/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.alfresco.error.AlfrescoRuntimeException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.hazelcast.config.Config;
import com.hazelcast.config.TcpIpConfig;
import static org.mockito.Mockito.*;

/**
 * Tests for the HazelcastConfigFactoryBean class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class HazelcastConfigFactoryBeanTest
{
    private HazelcastConfigFactoryBean configFactory;
    private Resource resource;
    private Properties properties;
    private @Mock TcpIpConfig tcpIpConfig;
    private @Mock ClusterService clusterService;
    private @Mock ApplicationContext appCtx;
    
    @Before
    public void setUp() throws Exception
    {
        configFactory = new HazelcastConfigFactoryBean();
        resource = new ClassPathResource("cluster-test/placeholder-test.xml");
        configFactory.setConfigFile(resource);
        
        properties = new Properties();
        properties.setProperty("alfresco.hazelcast.password", "let-me-in");
        properties.setProperty("alfresco.hazelcast.port", "1234");
        configFactory.setProperties(properties);
        configFactory.setApplicationContext(appCtx);
        
        // Prime the mock ClusterService
        when(clusterService.getClusterName()).thenReturn("the-cluster-name");
    }

    private Config getConfigFromFactory() throws Exception
    {
        when(appCtx.getBean("tcpIpConfig")).thenReturn(tcpIpConfig);
        when(appCtx.getBean("ClusterService")).thenReturn(clusterService);
        // Trigger the spring post-bean creation lifecycle method
        configFactory.init();
        Config config = configFactory.getConfig();
        return config;
    }
    
    @Test
    public void testConfigHasNewPropertyValues() throws Exception
    {
        // Invoke the factory method.
        Config config = getConfigFromFactory();
        assertEquals("let-me-in", config.getGroupConfig().getPassword());
        assertEquals(1234, config.getNetworkConfig().getPort());
    }

    @Test
    public void injectedTcpIpConfigIsUsed() throws Exception
    {
        Config config = getConfigFromFactory();
        assertSame(tcpIpConfig, config.getNetworkConfig().getJoin().getTcpIpConfig());
    }
    
    @Test
    public void clusterWillUseNameSuppliedByClusterService() throws Exception
    {
        Config config = getConfigFromFactory();   
        assertSame("the-cluster-name", config.getGroupConfig().getName());
    }

    @Test
    public void checkConfigFileIsManadatory() throws Exception
    {
        checkPropertyIsMandatory(null, tcpIpConfig, clusterService, "configFile");
    }
    
    @Test
    public void checkTcpIpConfigIsMandatory() throws Exception
    {
        checkPropertyIsMandatory(resource, null, clusterService, "tcpIpConfig");
    }
    
    @Test
    public void checkClusterServiceIsMandatory() throws Exception
    {        
        checkPropertyIsMandatory(resource, tcpIpConfig, null, "clusterService");
    }
    
    @Test
    public void specifyInterfacePropertyFalse() throws Exception
    {
        properties.setProperty("alfresco.cluster.interface", "");
        getConfigFromFactory();
        assertFalse(Boolean.valueOf(properties.getProperty("alfresco.cluster.specify.interface")));
    }
    
    @Test
    public void specifyInterfacePropertyTrue() throws Exception
    {
        properties.setProperty("alfresco.cluster.interface", "192.168.1.*");
        getConfigFromFactory();
        assertTrue(Boolean.valueOf(properties.getProperty("alfresco.cluster.specify.interface")));
    }
    
    private void checkPropertyIsMandatory(Resource resource,
                TcpIpConfig tcpIpConfig,
                ClusterService clusterService,
                String propertyName)
                            throws Exception
                            {
        try
        {
            configFactory.setConfigFile(resource);            
            when(appCtx.getBean("tcpIpConfig")).thenReturn(tcpIpConfig);
            when(appCtx.getBean("ClusterService")).thenReturn(clusterService);
            configFactory.init();
            fail("Exception should have been thrown");
        }
        catch (AlfrescoRuntimeException e)
        {
            Object[] params = e.getMsgParams();
            assertEquals(propertyName, params[0]);
        }
    }
}
