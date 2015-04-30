/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management.subsystems;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

import org.alfresco.enterprise.repo.management.MBeanSupport;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.management.subsystems.AbstractPropertyBackedBean;
import org.alfresco.repo.management.subsystems.ChildApplicationContextFactory;
import org.alfresco.repo.management.subsystems.PropertyBackedBean;
import org.alfresco.repo.management.subsystems.PropertyBackedBeanWithMonitor;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * A class that adapts a {@link PropertyBackedBean} as writable {@link DynamicMBean}, backed by a persistent store. If
 * any of the bean's properties are edited its {@link PropertyBackedBean#stop()} method will be called before applying
 * the changes.
 * 
 * @author dward
 */
@SuppressWarnings("unchecked")
public class PropertyBackedBeanAdapter extends MBeanSupport implements DynamicMBean, ApplicationListener
{
    /** The logger. */
    private static Log logger = LogFactory.getLog(PropertyBackedBeanAdapter.class);
    /**
     * The name of the method corresponding to the revert operation.
     */
    private static final String OPERATION_REVERT = "revert";

    /**
     * The name of the method corresponding to the stop operation.
     */
    private static final String OPERATION_STOP = "stop";

    /**
     * The name of the method corresponding to the start operation.
     */
    private static final String OPERATION_START = "start";

    /** The path in the attribute service below which we persist attributes. */
    private static final String ROOT_ATTRIBUTE_PATH = ".PropertyBackedBeans";

    /** The bean. */
    private final PropertyBackedBean bean;
    
    /** The attribute service. */
    private final AttributeService attributeService;

    /** The last component of the path within the attribute service. */
    private final String name;

    /**
     * Used to control setting of properties from either an JMX client or
     * by code in the local Alfresco node calling
     * {@link AbstractPropertyBackedBean#setProperties(Map)} or
     * {@link AbstractPropertyBackedBean#setProperty(String, String)}.
     * Is <code>true</code> when the call starts from this class.
     * This is the case when being set via a JMX client.
     */
    private ThreadLocal<Boolean> callingSetProperties = new ThreadLocal<Boolean>()
    {
        @Override
        protected Boolean initialValue()
        {
            return false;
        }
    };

    /** The operations we will expose on the MBean. */
    private static final MBeanOperationInfo[] EXPORTED_OPERATIONS;
    
    static
    {
        try
        {
            EXPORTED_OPERATIONS = new MBeanOperationInfo[]
            {
                new MBeanOperationInfo(PropertyBackedBeanAdapter.OPERATION_START, PropertyBackedBean.class
                        .getMethod(PropertyBackedBeanAdapter.OPERATION_START)),
                new MBeanOperationInfo(PropertyBackedBeanAdapter.OPERATION_STOP, PropertyBackedBean.class
                        .getMethod(PropertyBackedBeanAdapter.OPERATION_STOP)),
                new MBeanOperationInfo(PropertyBackedBeanAdapter.OPERATION_REVERT, PropertyBackedBean.class
                        .getMethod(PropertyBackedBeanAdapter.OPERATION_REVERT))
            };
        }
        catch (NoSuchMethodException e)
        {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Instantiates a new property backed bean adapter.
     * 
     * @param transactionService
     *            the transaction service
     * @param attributeService
     *            the attribute service
     * @param bean
     *            the bean
     * @param channel
     *            channel to broadcast events on
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public PropertyBackedBeanAdapter(TransactionService transactionService, AttributeService attributeService,
            PropertyBackedBean bean) throws IOException
    {
        super(transactionService);
        this.attributeService = attributeService;
        this.bean = bean;
        // 'Flatten' the ID into a String name separated by $
        StringBuilder name = new StringBuilder(100);
        for (String component : bean.getId())
        {
            if (name.length() > 0)
            {
                name.append('$');
            }
            // Use URLEncoder to escape $ and / characters
            name.append(URLEncoder.encode(component, "UTF-8"));
        }
        this.name = name.toString();

        // Load any persisted changes and configure the bean
        Map<String, String> persistedProperties = doWork(new RetryingTransactionCallback<Map<String, String>>()
        {
            public Map<String, String> execute() throws Throwable
            {
                Map<String, String> persistedProperties = (Map<String, String>) PropertyBackedBeanAdapter.this.attributeService.getAttribute(
                        PropertyBackedBeanAdapter.ROOT_ATTRIBUTE_PATH,
                        PropertyBackedBeanAdapter.this.name);
                return persistedProperties == null ? new HashMap<String, String>() : persistedProperties;
            }
        }, true);

        // Override the properties - never set read only properties
        for (Map.Entry<String, String> e : persistedProperties.entrySet())
        {
            if(this.bean.isUpdateable(e.getKey()))
            {
                this.bean.setProperty(e.getKey(), e.getValue());
            }
        }
              
       logger.debug("at end of constructor");
        
    }
    

	/**
	 * getMonitor adapter.   The monitor adapter presents an object to be blended into this
	 * property backed bean.
	 * 
	 * @return the MonitorAdapter or null if there is not a monitor adapter
	 */
	private MonitorAdapter getMonitor() {
		MonitorAdapter monitor = null;
		if (bean instanceof PropertyBackedBeanWithMonitor) 
		{
			logger.debug("property backed bean with monitor");

			if (bean instanceof ChildApplicationContextFactory) 
			{
				if(logger.isDebugEnabled())
				{
				    ChildApplicationContextFactory factory = (ChildApplicationContextFactory) bean;
				    logger.debug("factory.getTypeName:" + factory.getTypeName());
				    logger.debug("factory.getId:" + factory.getId());
				}
			}
			


			PropertyBackedBeanWithMonitor beanToBeMonitored = (PropertyBackedBeanWithMonitor) bean;
			Object monitorObject = (beanToBeMonitored.getMonitorObject());
			if (monitorObject != null) 
			{
			    try 
			    {
			        monitor = new MonitorAdapter(
						getTransactionService(), beanToBeMonitored);
					
			        logger.debug("got a monitor");
				} 
			    catch (Throwable t) 
				{
				    // We can go on without the monitor however
					// something bad happened so log it
					logger.error("error reading application context", t);
				}
			}
		}

		return monitor;
	}

    public synchronized Object getAttribute(final String attribute) throws AttributeNotFoundException, MBeanException,
            ReflectionException
    {
    	MonitorAdapter monitor = getMonitor();
        if(monitor != null && monitor.hasAttribute(attribute))
        {
            return monitor.getAttribute(attribute);
        }
        
        return this.bean.getProperty(attribute);
            
    }

    public AttributeList getAttributes(String[] attributes)
    {
        AttributeList list = new AttributeList();
        for (String attribute : attributes)
        {
            Object value;
            try
            {
                value = getAttribute(attribute);
            }
            catch (ReflectionException e)
            {
                throw new RuntimeException(e);
            }
            catch (MBeanException e)
            {
                throw new RuntimeException(e);
            }
            catch (AttributeNotFoundException e)
            {
                throw new RuntimeException(e);
            }
            if (value != null)
            {
                list.add(new Attribute(attribute, value));
            }
        }
        return list;
    }

    public synchronized MBeanInfo getMBeanInfo()
    {
        logger.debug("getMBeanInfo");
        MonitorAdapter monitor = getMonitor();
        try
        {
            Set<String> properties = this.bean.getPropertyNames();
            
            int attrsSize = properties.size(); 
            if(monitor != null)
            {
                attrsSize += monitor.getReadOnlyProperties().size();
            }

            MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[attrsSize];
            int i = 0;
            boolean isReadOnly = isReadOnly();
            for (String property : properties)
            {
                attrs[i++] = new MBeanAttributeInfo(property, String.class.getName(), this.bean.getDescription(property),
                        true, // isReadable
                        !isReadOnly && this.bean.isUpdateable(property), // isWritable
                        false); // isIs
            }
            
            if(monitor != null)
            {                
                for (String property : monitor.getReadOnlyProperties())
                {
                     attrs[i++] = new MBeanAttributeInfo(property, 
                        String.class.getName(), 
                        monitor.getPropertyDescripton(property),
                        true, // isReadable
                        false, // isWritable
                        monitor.getIsIs(property)); // isIs
                }
            }

            List<MBeanOperationInfo> operations = new ArrayList<MBeanOperationInfo>();
            for(MBeanOperationInfo operation : PropertyBackedBeanAdapter.EXPORTED_OPERATIONS)
            {
                operations.add(operation);
            }
            
            if(monitor != null)
            {
                
                Method[] methods = monitor.getDeclaredMethods();
                
                for(Method method : methods)
                {
                    logger.debug("monitor method found" + method.getName());
                    MBeanOperationInfo info = new MBeanOperationInfo(name, method);
                    operations.add(info);
                }
            }
                

            return new MBeanInfo(this.name, "Persistent Managed Bean", 
                 attrs, 
                 null,
                (MBeanOperationInfo[])operations.toArray(new MBeanOperationInfo[operations.size()]), 
                 null);

        } 
        catch (Exception e)
        {
            logger.error("Unable to get bean info", e);
            throw new AlfrescoRuntimeException("unable to get bean info", e);
        }
    }
    
    public Object invoke(final String actionName, final Object[] params, final String[] signature)
            throws MBeanException, ReflectionException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("invoke :" + actionName);
        }
        
        final MonitorAdapter monitor = getMonitor();
        
        // Do the work
        return doWork(new RetryingTransactionCallback<Object>()
        {

            public Object execute() throws Throwable
            {
                try
                {
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                    Class<?>[] parameterTypes = new Class<?>[signature.length];
                    int i = 0;
                    for (String type : signature)
                    {
                        parameterTypes[i++] = Class.forName(type, true, classLoader);
                    }
                    
                    if(monitor != null && monitor.hasMethod(actionName, parameterTypes))
                    {
                        // method is on the monitor bean
                        return monitor.invokeMethod(actionName, parameterTypes, params);
                    }
                    else
                    {   
                        // method is on the property backed bean adapter
                        return PropertyBackedBeanAdapter.this.bean.getClass().getMethod(actionName, parameterTypes)
                            .invoke(PropertyBackedBeanAdapter.this.bean, params);
                    }
                }
                catch (InvocationTargetException e)
                {
                    throw new RuntimeException(e.getTargetException());
                }
                catch (NoSuchMethodException e)
                {
                    throw new ReflectionException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new ReflectionException(e);
                }
            }
        }, false);
    }
    
    private Map<String, String> getPersistedValues()
    {
        // Load the current property values
        Map<String, String> attributeMap = (Map<String, String>) attributeService.getAttribute(
                PropertyBackedBeanAdapter.ROOT_ATTRIBUTE_PATH,
                PropertyBackedBeanAdapter.this.name);
        attributeMap = (attributeMap == null ? new HashMap<String, String>()
                : attributeMap);
        
        return attributeMap;
    }
    
    /**
     * @param attributes
     * @param persistedValues
     * @return true, a persistedValue needs to be changed
     */
    private boolean isChanged(List<Attribute> attributes, Map<String, String> persistedValues)
    {
        for (Attribute attribute : attributes)
        {
            if(this.bean.isUpdateable(attribute.getName()))
            {
                // only check updatable values
                String newValue = getStringValue(attribute);
                String oldValue = getStringValue(persistedValues, attribute.getName())
                ;persistedValues.get(attribute.getName());
                if(oldValue == null)
                {
                    oldValue = "";
                }
                
                if(!oldValue.equals(newValue))
                {
                    // value is different
                    return true;
                }
            }
        }
        
        return false;
    }
    

    private Map<String, String> combineWithPersisted(List<Attribute> attributes, Map<String, String> persistedValues)
    {
        Map<String, String> attributeMap = new HashMap<String, String>(persistedValues);

        // Apply the updates
        for (Attribute attribute : attributes)
        {
            // only persist properties that can be updated
            if(this.bean.isUpdateable(attribute.getName()))
            {
                attributeMap.put(attribute.getName(), getStringValue(attribute));
            }
        }
        
        return attributeMap;
    }
    
    
    public synchronized void setAttribute(final Attribute attribute) throws AttributeNotFoundException,
            InvalidAttributeValueException, MBeanException, ReflectionException
    {
    	MonitorAdapter monitor = getMonitor();
    	
        if(monitor != null && monitor.hasAttribute(attribute.getName()))
        {
            // Do nothing - read only property
            return;
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" setAttribute("+attribute.getName()+','+attribute.getValue()+")");
        }
        
        doWork(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                callingSetProperties.set(true);
                try
                {
                    // Combine the new values with the current persisted values
                    Map<String, String> persistedValues = getPersistedValues();
                    List<Attribute> valueToSet = Collections.singletonList(attribute);
                    Map<String, String> attributeMap = combineWithPersisted(valueToSet, persistedValues);

                    // Work out whether anything has changed
                    boolean changed = isChanged(valueToSet, persistedValues);
                    
                    if (changed)
                    {
                        // Update the bean
                        String attributeName = attribute.getName(); 
                        bean.stop();
                        bean.setProperty(attributeName, attributeMap.get(attributeName));

                        // Persist the updates
                        if (logger.isDebugEnabled())
                        {
                            logger.debug(" setAttribute() persist ("+attributeMap+")");
                        }
                        attributeService.setAttribute((Serializable) attributeMap,
                            PropertyBackedBeanAdapter.ROOT_ATTRIBUTE_PATH, name);
                        
                        // NOTE: you might expect a bean.start() method call here. This is intentionally
                        // not included so that multiple calls to setAttribute to do not result
                        // in as many stop/start cycles of the subsystem.
                    }

                    // Done
                    logger.debug(" setAttribute() done");
                    return null;
                }
                finally
                {
                    callingSetProperties.set(false);
                }
            }
        }, false);
    }

    public AttributeList setAttributes(final AttributeList attributes)
    {
        final List<Attribute> valuesToSet = attributes.asList();
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" setAttributes("+attributes+")");
        }
        return doWork(new RetryingTransactionCallback<AttributeList>()
        {
            public AttributeList execute() throws Throwable
            {
                callingSetProperties.set(true);
                try
                {
                    // Combine the new values with the current persisted values
                    logger.debug(" setAttributes() combineWithPersisted");
                    Map<String, String> persistedValues = getPersistedValues();
                    Map<String, String> attributeMap = combineWithPersisted(valuesToSet, persistedValues);
                    
                    // Work out whether anything has changed
                    boolean changed = isChanged(valuesToSet, persistedValues );
                    
                    if (changed)
                    {
                        // Bean has changed, update it
                        logger.debug(" setAttributes() bean.stop");
                        bean.stop();
                        if (logger.isDebugEnabled())
                        {
                            logger.debug(" setAttributes() bean.setProperties ("+attributeMap+")");
                        }
                        bean.setProperties(attributeMap);

                        // Persist the updates
                        if (logger.isDebugEnabled())
                        {
                            logger.debug(" setAttributes() persist ("+attributeMap+")");
                        }
                        attributeService.setAttribute(
                            (Serializable) attributeMap,
                            PropertyBackedBeanAdapter.ROOT_ATTRIBUTE_PATH, name);

                        logger.debug(" setAttributes() bean.start");
                        bean.start();
                    }
                    // Done
                    logger.debug(" setAttributes() done");
                    return attributes;
                }
                finally
                {
                    callingSetProperties.set(false);
                }
            }
        }, false);
    }
    
    public void setProperty(String name, String value)
    {
        if (!callingSetProperties.get())
        {
            // This is NOT a call back, so was an AbstractPropertyBackedBean.setProperty() call
            // made locally on this node. Must save the values to the database.
            try
            {
                setAttribute(new Attribute(name, value));
            }
            catch (Exception e)
            {
                throw new AlfrescoRuntimeException("Error setting JMX bean property from local node.", e);
            }
        }
    }

    public void setProperties(Map<String, String> properties)
    {
        if (!callingSetProperties.get())
        {
            // This is NOT a call back, so was an AbstractPropertyBackedBean.setProperties() call
            // made locally on this node. Must save the values to the database.
            setAttributes(toAttributes(properties));
        }
    }

    private AttributeList toAttributes(Map<String, String> properties)
    {
        AttributeList attributes = new AttributeList(properties.size());
        for (Entry<String, String> entry: properties.entrySet())
        {
            attributes.add(new Attribute(entry.getKey(), entry.getValue()));
        }
        return attributes;
    }

    private void removeAttributes(final Collection<String> attributes)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("removeAttributes() Attributes("+attributes+")");
        }
        doWork(new RetryingTransactionCallback<Collection<String>>()
        {
            public Collection<String> execute() throws Throwable
            {
                callingSetProperties.set(true);
                try
                {
                    // Get the current persisted values
                    logger.debug(" removeAttributes() currentPersisted");
                    boolean changed = false;
                    Map<String, String> persistedValues = getPersistedValues();
                    Map<String, String> attributeMap = combineWithPersisted(new ArrayList<Attribute>(), persistedValues);
                    for (String attribute: attributes)
                    {
                        if (attributeMap.remove(attribute) != null)
                        {
                        	changed = true;
                        }
                    }
                
                    if (changed)
                    {
                        // Bean has changed, update it
                        logger.debug(" removeAttributes() bean.stop");
                        bean.stop();
                        if (logger.isDebugEnabled())
                        {
                            logger.debug(" removeAttributes() bean.removeProperties ("+attributes+")");
                        }
                        bean.removeProperties(attributes);

                        // Persist the updates
                        if (logger.isDebugEnabled())
                        {
                            logger.debug(" removeAttributes() persist ("+attributeMap+")");
                        }
                        attributeService.setAttribute(
                                (Serializable) attributeMap,
                                PropertyBackedBeanAdapter.ROOT_ATTRIBUTE_PATH, name);

                        logger.debug(" removeAttributes() bean.start");
                        bean.start();
                    }

                    // Done
                    logger.debug(" removeAttributes() done");
                    return attributes;
                }
                finally
                {
                    callingSetProperties.set(false);
                }
            }
        }, false);
    }

    public void removeProperties(Collection<String> properties)
    {
        if (!callingSetProperties.get())
        {
            // This is NOT a call back, so was an AbstractPropertyBackedBean.removeProperties() call
            // made locally on this node. Must remove the values from the database.
            removeAttributes(properties);
        }
    }

    /**
     * Converts an attribute to a string value, treating nulls as empty strings
     * 
     * @param attribute
     *            the attribute
     * @return the string value
     */
    private String getStringValue(Attribute attribute)
    {
        Object value = attribute.getValue();
        return value == null ? "" : value.toString();
    }
    
    /**
     * Converts an map to a string value, treating nulls as empty strings
     * 
     * @param persistedValues
     *            the persistedValues
     * @return the string value
     */
    private String getStringValue(Map<String, String> persistedValues, String name)
    {
        String value = persistedValues.get(name);
        return value == null ? "" : value.toString();
    }

    
    /**
     * Removes persisted properties for this bean from storage.
     */
    public void purge()
    {
        PropertyBackedBeanAdapter.this.attributeService.removeAttribute(PropertyBackedBeanAdapter.ROOT_ATTRIBUTE_PATH, name);
    }

    public void onApplicationEvent(final ApplicationEvent event)
    {
        if (this.bean instanceof ApplicationListener)
        {
            doWork(new RetryingTransactionCallback<Object>()
            {
                public Object execute() throws Throwable
                {
                    ((ApplicationListener) PropertyBackedBeanAdapter.this.bean).onApplicationEvent(event);
                    return null;
                }
            }, false);
        }
    }
} // End Property Backed Bean Adapter
