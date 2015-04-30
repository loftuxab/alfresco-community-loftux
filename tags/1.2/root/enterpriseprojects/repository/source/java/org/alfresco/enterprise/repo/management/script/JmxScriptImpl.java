/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.scripts.ScriptException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Scriptable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This class provides the basic implementation of {@link JmxScript}, performing queries against the {@link MBeanServerConnection}
 * and creating result objects for those queries. It is responsible for wrapping the results in script-friendly types such as {@link ScriptMBean}.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class JmxScriptImpl implements JmxScript, ApplicationContextAware
{
    private static Log log = LogFactory.getLog(JmxScriptImpl.class);
    
    private ApplicationContext          applicationContext;
    private MBeanServerConnection       mbeanServer;
    private Scriptable                  scope;
    private JmxValueConversionChain     conversionChain;
    
    public void setApplicationContext(ApplicationContext appContext) { this.applicationContext = appContext; }
    
    public void setMBeanServer(MBeanServerConnection mBeanServer) { this.mbeanServer = mBeanServer; }
    
    public void setValueConverter(JmxValueConversionChain conversionChain) { this.conversionChain = conversionChain; }
    
    public void setScope(Scriptable scope) { this.scope = scope; }
    
    @Override public ScriptMBean[] queryMBeans(String objectName)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Querying for MBeans with object name: " + objectName);
        }
        
        // Using a TreeSet in order to obtain sorted results.
        SortedSet<ScriptMBean> results = new TreeSet<ScriptMBean>();
        
        try
        {
            ObjectName jmxObjName = objectName == null ? null : new ObjectName(objectName);
            Set<ObjectName> matchingMBeanNames = mbeanServer.queryNames(jmxObjName, null);
            
            if (log.isTraceEnabled())
            {
                log.trace("MBeanServer returned " + matchingMBeanNames.size() + " matching MBeans.");
            }
            
            for (Iterator<ObjectName> iter = matchingMBeanNames.iterator(); iter.hasNext(); )
            {
                ObjectName nextMBeanName = iter.next();
                
                // We get a new instance of a ScriptMBean from the spring application context.
                // It will be pre-wired with any required dependencies and configured by spring.
                ScriptMBean scriptMBean = (ScriptMBean) this.applicationContext.getBean("scriptMBean", nextMBeanName);
                
                // We just need to set its JavaScript scope.
                scriptMBean.setScope(this.scope);
                
                results.add(scriptMBean);
            }
        }
        catch(MalformedObjectNameException monx)
        {
            throw new ScriptException("Cannot access MBean '" + objectName + "'", monx);
        }
        catch(IOException iox)
        {
            throw new ScriptException("Comms problem with MBean Server", iox);
        }
        
        List<ScriptMBean> beansList = new ArrayList<ScriptMBean>(results);
        
        return beansList.toArray(new ScriptMBean[0]);
    }
    
    @Override public void save(ScriptMBean updatedBean)
    {
        updatedBean.saveImpl();
    }
}