/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryXmlConfig;
import com.hazelcast.config.TcpIpConfig;

/**
 * FactoryBean used to create Hazelcast {@link Config} objects. A configuration file is supplied
 * in the form of a Spring {@link Resource} and a set of {@link Properties} can also be provided. The
 * XML file is processed so that property placeholders of the form ${property.name} are substitued for
 * the corresponding property value before the XML is parsed into the Hazelcast configuration object.
 *  
 * @author Matt Ward
 */
public class HazelcastConfigFactoryBean implements ApplicationContextAware
{
    private static final Log log = LogFactory.getLog(HazelcastConfigFactoryBean.class);
    private static final String PLACEHOLDER_END = "}";
    private static final String PLACEHOLDER_START = "${";
    private Resource configFile;
    private Config config;
    private Properties properties;
    private TcpIpConfig tcpIpConfig;
    private ClusterService clusterService;
    private boolean initialised;
    private final ReentrantReadWriteLock initRWLock = new ReentrantReadWriteLock();
    private ApplicationContext applicationContext;
    
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * Set the Hazelcast XML configuration file to use. This will be merged with the supplied
     * Properties and parsed to produce a final {@link Config} object. 
     * @param configFile the configFile to set
     */
    public void setConfigFile(Resource configFile)
    {
        this.configFile = configFile;
    }
    
    /**
     * Used to supply the set of Properties that the configuration file can reference.
     * 
     * @param properties the properties to set
     */
    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }

    /**
     * Initialise the config factory. This is not done as an initialising bean
     * as the services are not ready for use when Spring invokes that method. 
     */
    public void init() throws Exception
    {
        try
        {
            initRWLock.readLock().lock();
            if (initialised)
            {
                // Don't do anything, initialisation already performed.
                return;
            }
        }
        finally
        {
            initRWLock.readLock().unlock();
        }
        
        // Not initialised, so get a write lock and perform initialisation.
        try
        {
            // Double check that condition hasn't changed.
            if (!initialised)
            {
                initRWLock.writeLock().lock();
                
                tcpIpConfig = (TcpIpConfig) applicationContext.getBean("tcpIpConfig");
                clusterService = (ClusterService) applicationContext.getBean("ClusterService");
                
                PropertyCheck.mandatory(this, "tcpIpConfig", tcpIpConfig);
                PropertyCheck.mandatory(this, "configFile", configFile);
                PropertyCheck.mandatory(this, "clusterService", clusterService);
                if (properties == null)
                {
                    properties = new Properties();
                }
                
                createInferredProperties();
                
                // These configXML strings will be large and are therefore intended
                // to be thrown away. We only want to keep the final Config object.
                String rawConfigXML = getConfigFileContents();
                String configXML = substituteProperties(rawConfigXML);
                if (log.isTraceEnabled())
                {
                    log.trace("Post-processed Hazelcast XML configuration...\n" + configXML);
                }
                config = new InMemoryXmlConfig(configXML);
                
                // Inject the custom TcpIpConfig that will be used for member discovery.
                tcpIpConfig.setEnabled(true);
                config.getNetworkConfig().getJoin().setTcpIpConfig(tcpIpConfig);
                
                // Set the cluster name as reported by the ClusterService
                String clusterName = clusterService.getClusterName();
                if (clusterName == null)
                {
                    clusterName = clusterService.generateClusterName();
                }
                config.getGroupConfig().setName(clusterName);
                
                // Store that we've completed initialisation
                initialised = true;
            }
        }
        finally
        {
            initRWLock.writeLock().unlock();
        }
    }

    
    /**
     * Creates other properties as a result of the values of particular properties,
     * for example sets alfresco.cluster.specify.interface if alfresco.cluster.interface
     * has a non-empty value (this saves users from having to set two properties when one should do).
     */
    private void createInferredProperties()
    {
        String interfacePattern = properties.getProperty("alfresco.cluster.interface");
        Boolean specifyInterface = StringUtils.hasText(interfacePattern); 
        // Sets the property to "true" if specifyInterface is set, "false" otherwise.
        properties.setProperty("alfresco.cluster.specify.interface", specifyInterface.toString());
    }

    /**
     * For the method parameter <code>text</code>, replaces all occurrences of placeholders having
     * the form ${property.name} with the value of the property having the key "property.name". The
     * properties are supplied using {@link #setProperties(Properties)}.
     * 
     * @param text The String to apply property substitutions to.
     * @return String after substitutions have been applied.
     */
    private String substituteProperties(String text)
    {
        long startTimeMillis = System.currentTimeMillis();
        for (String propName : properties.stringPropertyNames())
        {
            String propValue = properties.getProperty(propName);
            String quotedPropName = Pattern.quote(PLACEHOLDER_START + propName + PLACEHOLDER_END);
            text = text.replaceAll(quotedPropName, propValue);
        }
        long endTimeMillis = System.currentTimeMillis();
        if (log.isDebugEnabled())
        {
            long timeTakenMillis = endTimeMillis - startTimeMillis;
            log.debug("Properties substituted in " + timeTakenMillis + "ms");
        }
        return text;
    }

    /**
     * Opens the configFile {@link Resource} and reads the contents into a String.
     * 
     * @return the contents of the configFile resource.
     */
    private String getConfigFileContents()
    {
        StringWriter writer = new StringWriter();
        InputStream inputStream = null;
        try
        {
            inputStream = configFile.getInputStream();
            IOUtils.copy(inputStream, writer, "UTF-8");
            return writer.toString();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Couldn't read configuration: " + configFile, e);
        }
        finally
        {    
            try
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException("Couldn't close stream", e);
            }
        }
    }

    /**
     * FactoryBean's factory method. Returns the config with the property key/value
     * substitutions in place.
     */
    public Config getConfig() throws Exception
    {
        init();
        return config;
    }
}
