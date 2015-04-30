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
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.alfresco.repo.jscript.Scopeable;
import org.alfresco.repo.jscript.ScriptableHashMap;
import org.alfresco.scripts.ScriptException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Scriptable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This class provides a JavaScript-friendly API for MBean objects.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class ScriptMBean implements Scopeable, Comparable<ScriptMBean>, ApplicationContextAware
{
    private static Log log = LogFactory.getLog(ScriptMBean.class);
    
    private ApplicationContext          applicationContext;
    private MBeanServerConnection       mbeanServerConnection;
    private JmxValueConversionChain     conversionChain;
    
    private final ObjectName            objectName;
    
    private Scriptable                  scope;
    
    /**
     * A cache of the attributes read from JMX. We must cache these attribute objects so that if the values are updated (as they will
     * be by JavaScript code normally), we will have the same instances along with their contained updates.
     * It should be safe to cache them here as this object is instantiated once for each query and therefore there should be
     * no collision between different users running the same script.
     */
    private ScriptableHashMap<String, ScriptMBeanAttribute> cachedAttributeInstances = null;
    
    private ScriptableMBeanOperations cachedOperationsBean = null;
    
    /**
     * This factory method can be used to instantiate ScriptMBean objects.
     */
    public static ScriptMBean create(ObjectName objectName) { return new ScriptMBean(objectName); }
    
    /**
     * Instantiates this class with an object representing the specified MBean.
     * This class is intended to represent specific MBean instances and as such does not
     * support object name 'patterns' as constructor args.
     * 
     * @throws ScriptException if the specified objectName or mbeanServerConnection are <tt>null</tt>.
     * @throws ScriptException if the specified objectName is a 'pattern'.
     * @see ObjectName#isPattern()
     */
    private ScriptMBean(ObjectName objectName)
    {
        if (objectName == null)            { throw new ScriptException("Null objectName"); }
        if (objectName.isPattern())        { throw new ScriptException("objectName is a pattern rather than a single MBean"); }
        
        this.objectName = objectName;
    }
    
    public void setApplicationContext(ApplicationContext appContext) { this.applicationContext = appContext; }
    
    public void setMBeanServer(MBeanServerConnection mbeanServerConnection) { this.mbeanServerConnection = mbeanServerConnection; }
    
    public void setJmxValueConversionChain(JmxValueConversionChain conversionChain) { this.conversionChain = conversionChain; }
    
    /** Returns the name of this MBean. */
    public String getName() { return this.objectName.getCanonicalName(); }
    
    /** Returns the domain part of this MBean's name. */
    public String getDomain() { return objectName.getDomain(); }
    
    /** Returns a description of this MBean. */
    public String getDescription()
    {
        MBeanInfo beanInfo = getMBeanInfo();
        
        return beanInfo.getDescription();
    }
    
    /** Returns the name of the Java class backed by this MBean. */
    public String getClassName()
    {
        MBeanInfo beanInfo = getMBeanInfo();
        return beanInfo.getClassName();
    }
    
    private MBeanInfo getMBeanInfo()
    {
        if (log.isTraceEnabled())
        {
            log.trace("Reading MBeanInfo from JMX for objectName: " + objectName);
        }
        
        MBeanInfo beanInfo;
        try
        {
            beanInfo = mbeanServerConnection.getMBeanInfo(objectName);
        } catch (InstanceNotFoundException e)
        {
            throw new ScriptException("MBean not found: " + objectName, e);
        } catch (IntrospectionException e)
        {
            throw new ScriptException("MBean introspection failed: " + objectName, e);
        } catch (ReflectionException e)
        {
            throw new ScriptException("MBean reflection failed: " + objectName, e);
        } catch (IOException e)
        {
            throw new ScriptException("Comms error with MBean Server", e);
        }
        return beanInfo;
    }
    
    /**
     * This method gets the attributes of this MBean, if any.
     * These are returned as a {@link ScriptableHashMap} for JavaScript-friendliness.
     */
    public ScriptableHashMap<String, ScriptMBeanAttribute> getAttributes()
    {
        if (this.cachedAttributeInstances == null)
        {
            MBeanInfo beanInfo = getMBeanInfo();
            MBeanAttributeInfo[] attributeInfos = beanInfo.getAttributes();
            
            cachedAttributeInstances = new ScriptableHashMap<String, ScriptMBeanAttribute>();
            if (attributeInfos != null)
            {
                for (MBeanAttributeInfo attributeInfo : attributeInfos)
                {
                    String attributeName = attributeInfo.getName();
                    cachedAttributeInstances.put(attributeName,
                                                 new ScriptMBeanAttribute(objectName, attributeInfo, mbeanServerConnection, conversionChain, scope));
                }
            }
        }
        return cachedAttributeInstances;
    }
    
    /**
     * This method gets the operations of this MBean, if any.
     */
    public ScriptableMBeanOperations getOperations()
    {
        if (this.cachedOperationsBean == null)
        {
            Object operationsBean = applicationContext.getBean("scriptableMBeanOperations", objectName, getMBeanInfo(), scope);
            cachedOperationsBean = (ScriptableMBeanOperations) operationsBean;
        }
        return this.cachedOperationsBean;
    }
    
    /**
     * @see JmxScript#save(ScriptMBean)
     */
    void saveImpl()
    {
        // Note: this save() method must be package-private to prevent it appearing in the JavaScript API.
        // We cannot allow that as we have role-based authentication on the JmxScript.save() method
        if (log.isTraceEnabled()) { log.trace("Saving updates for objectName: " + objectName); }
        
        // We currently only support JMX attribute updates - e.g. no bean description updates yet.
        
        final AttributeList dirtyAttributes = new AttributeList();
        for (Map.Entry<String, ScriptMBeanAttribute> entry : getAttributes().entrySet())
        {
            Attribute nextAttr = entry.getValue().getDirtyValue();
            if (nextAttr != null)
            {
                dirtyAttributes.add(nextAttr);
            }
        }
        
        
        if (log.isDebugEnabled()) { log.debug("Saving " + dirtyAttributes.size() + " attribute update(s)."); }
        
        if ( !dirtyAttributes.isEmpty())
        {
            AttributeList updatedAttributes;
            try
            {
                updatedAttributes = mbeanServerConnection.setAttributes(objectName, dirtyAttributes);
            } catch (InstanceNotFoundException e)
            {
                throw new ScriptException("Bean not found: " + objectName, e);
            } catch (ReflectionException e)
            {
                throw new ScriptException("ReflectionException", e);
            } catch (IOException e)
            {
                throw new ScriptException("IOException", e);
                
            }
            
            // The above mbeanServerConnection.setAttributes call may not persist all updates - see its javadoc.
            // Let's determine which attributes were not actually updated.
            final List<String> updatedAttributeNames = new ArrayList<String>();
            for (Attribute nextUpdatedAttr : updatedAttributes.asList())
            {
                updatedAttributeNames.add(nextUpdatedAttr.getName());
            }
            
            final AttributeList unhandledAttributes = new AttributeList();
            for (Attribute nextDirtyAttr: dirtyAttributes.asList())
            {
                if ( !updatedAttributeNames.contains(nextDirtyAttr.getName()))
                {
                    unhandledAttributes.add(nextDirtyAttr);
                }
            }
            if ( !unhandledAttributes.isEmpty())
            {
                // TODO How can we report this to the user in a more useful way?
                if (log.isDebugEnabled()) { log.debug("MBeanServerConnection declined to accept " + unhandledAttributes.size() + " attribute updates."); }
                if (log.isTraceEnabled())
                {
                    for (Attribute attr : unhandledAttributes.asList()) { log.trace("    " + attr.getName()); }
                }
            }
            
            // Only clear the caches of the attributes successfully written.
            for (Attribute persistedAttribute : updatedAttributes.asList())
            {
                ScriptMBeanAttribute scriptPersistedAttr = getAttributes().get(persistedAttribute.getName());
                if (scriptPersistedAttr != null)
                {
                    scriptPersistedAttr.clearCachedData();
                }
            }
        }
        
        clearCache();
    }
    
    private void clearCache()
    {
        if (log.isTraceEnabled())
        {
            log.trace("Clearing cached attributes for objectName: " + objectName);
        }
        
        this.cachedAttributeInstances = null;
    }
    
    @Override public int hashCode() { return objectName == null ? 0 : objectName.hashCode(); }
    
    @Override public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        if (!objectName.equals(((ScriptMBean)obj).objectName)) return false;
        return true;
    }
    
    @Override public String toString() { return String.valueOf(objectName); }
    
    @Override public int compareTo(ScriptMBean that)
    {
        if (that == null)
        {
            return 0;
        }
        else
        {
            return this.objectName.compareTo(that.objectName);
        }
    }
    
    @Override public void setScope(Scriptable scope) { this.scope = scope; }
}