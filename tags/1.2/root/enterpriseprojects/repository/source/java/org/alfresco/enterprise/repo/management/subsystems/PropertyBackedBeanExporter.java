/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management.subsystems;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.enterprise.repo.cluster.messenger.MessageReceiver;
import org.alfresco.enterprise.repo.cluster.messenger.MessageSendingException;
import org.alfresco.enterprise.repo.cluster.messenger.Messenger;
import org.alfresco.enterprise.repo.cluster.messenger.MessengerFactory;
import org.alfresco.repo.management.DynamicMBeanExportOperations;
import org.alfresco.repo.management.subsystems.PropertyBackedBean;
import org.alfresco.repo.management.subsystems.PropertyBackedBeanEvent;
import org.alfresco.repo.management.subsystems.PropertyBackedBeanRegisteredEvent;
import org.alfresco.repo.management.subsystems.PropertyBackedBeanRegistry;
import org.alfresco.repo.management.subsystems.PropertyBackedBeanRemovePropertiesEvent;
import org.alfresco.repo.management.subsystems.PropertyBackedBeanSetPropertiesEvent;
import org.alfresco.repo.management.subsystems.PropertyBackedBeanSetPropertyEvent;
import org.alfresco.repo.management.subsystems.PropertyBackedBeanStartedEvent;
import org.alfresco.repo.management.subsystems.PropertyBackedBeanStoppedEvent;
import org.alfresco.repo.management.subsystems.PropertyBackedBeanUnregisteredEvent;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Exports {@link PropertyBackedBean}s as persistent MBeans in response to events emitted by a
 * {@link PropertyBackedBeanRegistry}. Ensures start and stop operations on these beans are reflected across the cluster
 * using a {@link Messenger}. In the event of a {@link PropertyBackedBeanRegisteredEvent} an instance of this class will
 * export the source {@link PropertyBackedBean} as a persistent MBean. In the event of a
 * {@link PropertyBackedBeanUnregisteredEvent} the MBean will be unexported. If the
 * {@link PropertyBackedBeanUnregisteredEvent} has its <code>isPermanent</code> flag set, the persisted properties of
 * the bean will also be purged. On receiving a {@link PropertyBackedBeanStartedEvent} or
 * {@link PropertyBackedBeanStoppedEvent} from a remote node, will target the event at the corresponding bean to ensure
 * consistency across a cluster.
 * 
 * @author dward
 */
public class PropertyBackedBeanExporter implements InitializingBean, MessageReceiver<ApplicationEvent>
{
    private static Log logger = LogFactory.getLog(PropertyBackedBeanExporter.class);

    /** The JMX exporter. */
    private DynamicMBeanExportOperations jmxExporter;

    /** The registry. */
    private PropertyBackedBeanRegistry registry;

    /** The transaction service. */
    private TransactionService transactionService;

    /** The attribute service. */
    private AttributeService attributeService;

    /** Map of IDs to object names. */
    private Map<List<String>, ObjectName> objectNameMap = new HashMap<List<String>, ObjectName>(17);

    /** Map of IDs to adapters. */
    private Map<List<String>, PropertyBackedBeanAdapter> adapterMap =
            Collections.synchronizedMap(new HashMap<List<String>, PropertyBackedBeanAdapter>(17));

    /** Creates communication channels within a cluster */
    private MessengerFactory messengerFactory; 

    /** Provides communications within a cluster */
    private Messenger<ApplicationEvent> messenger;
    
    /**
     * Sets the JMX exporter.
     * 
     * @param jmxExporter
     *            the JMX exporter to set
     */
    public void setJmxExporter(DynamicMBeanExportOperations jmxExporter)
    {
        this.jmxExporter = jmxExporter;
    }

    /**
     * Sets the registry.
     * 
     * @param registry
     *            the registry to set
     */
    public void setRegistry(PropertyBackedBeanRegistry registry)
    {
        this.registry = registry;
    }

    /**
     * Sets the transaction service.
     * 
     * @param transactionService
     *            the transaction service to set
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    /**
     * Sets the attribute service.
     * 
     * @param attributeService
     *            the attribute service to set
     */
    public void setAttributeService(AttributeService attributeService)
    {
        this.attributeService = attributeService;
    }

    public synchronized void afterPropertiesSet() throws Exception
    {
        messenger = messengerFactory.createMessenger(getClass().getName());
        
        // Use an inner ApplicationListener rather than implementing it directly so that we don't have to handle all the
        // application context events
        this.registry.addListener(new ApplicationListener<ApplicationEvent>()
        {

            public void onApplicationEvent(ApplicationEvent event)
            {
                try
                {
                    if (event instanceof PropertyBackedBeanRegisteredEvent)
                    {
                        PropertyBackedBean bean = (PropertyBackedBean) event.getSource();
                        List<String> id = bean.getId();
                        
                        // Initialize a new adapter (causing persisted property overrides to be loaded and applied)
                        PropertyBackedBeanAdapter adapter = new PropertyBackedBeanAdapter(
                                PropertyBackedBeanExporter.this.transactionService,
                                PropertyBackedBeanExporter.this.attributeService, bean);
                        PropertyBackedBeanExporter.this.adapterMap.put(id, adapter);

                        // Generate a hierarchical object name
                        Iterator<String> i = id.iterator();
                        StringBuilder name = new StringBuilder(200).append("Alfresco:Type=Configuration,Category=")
                                .append(i.next());
                        int count = 0;
                        boolean hideFromJmx = false;
                        while (i.hasNext())
                        {
                            String next = i.next();
                            if ("hidden".equals(next))
                            {
                                hideFromJmx = true;
                                break;
                            }
                            name.append(",id").append(++count).append('=').append(next);
                        }
                        
                        // Deprecated or unsupported subsystems are hidden from JMX
                        if (!hideFromJmx)
                        {
                            if (logger.isDebugEnabled())
                            {
                                logger.debug("register JMX bean:" + name.toString());
                            }

                            // MNT-693: Synchronize here to make registration atomic and prevent any sneaky threads
                            // trying to interact with this MBean and deadlocking whilst we still hold locks during
                            // initialization
                            synchronized (adapter)
                            {
                                ObjectName result = PropertyBackedBeanExporter.this.jmxExporter.registerMBean(adapter,
                                        new ObjectName(name.toString()));
                                PropertyBackedBeanExporter.this.objectNameMap.put(id, result);
                            }
                        }
                    }
                    else if (event instanceof PropertyBackedBeanUnregisteredEvent)
                    {
                        List<String> id = ((PropertyBackedBeanUnregisteredEvent) event).getSourceId();
                        ObjectName objectName = PropertyBackedBeanExporter.this.objectNameMap.remove(id);
                        if (objectName != null)
                        {
                            if(logger.isDebugEnabled())
                            {
                                logger.debug("un-register JMX bean:" + objectName.toString());
                            }
                            PropertyBackedBeanExporter.this.jmxExporter.unregisterMBean(objectName);
                        }
                        // Purge persisted information if required
                        if (((PropertyBackedBeanUnregisteredEvent) event).isPermanent())
                        {
                            PropertyBackedBeanAdapter adapter = PropertyBackedBeanExporter.this.adapterMap.remove(id);
                            if (adapter != null)
                            {
                                adapter.purge();
                            }
                        }
                    }
                    else if (event instanceof PropertyBackedBeanSetPropertyEvent)
                    {
                        List<String> id = ((PropertyBackedBeanSetPropertyEvent) event).getSourceId();
                        // Purge persisted information if required
                        PropertyBackedBeanAdapter adapter = PropertyBackedBeanExporter.this.adapterMap.get(id);
                        if (adapter != null)
                        {
                            String name = ((PropertyBackedBeanSetPropertyEvent) event).getName();
                            String value = ((PropertyBackedBeanSetPropertyEvent) event).getValue();
                            adapter.setProperty(name, value);
                        }
                    }
                    else if (event instanceof PropertyBackedBeanSetPropertiesEvent)
                    {
                        List<String> id = ((PropertyBackedBeanSetPropertiesEvent) event).getSourceId();
                        
                    	if(logger.isDebugEnabled())
                    	{
                    		logger.debug("setProperties called for id" + id);
                    	}

                        // Purge persisted information if required
                        PropertyBackedBeanAdapter adapter = PropertyBackedBeanExporter.this.adapterMap.get(id);
                        if (adapter != null)
                        {
                            Map<String, String> properties = ((PropertyBackedBeanSetPropertiesEvent) event).getProperties();
                            adapter.setProperties(properties);
                        }
                    }
                    else if (event instanceof PropertyBackedBeanRemovePropertiesEvent)
                    {
                        List<String> id = ((PropertyBackedBeanRemovePropertiesEvent) event).getSourceId();
                        // Purge persisted information if required
                        PropertyBackedBeanAdapter adapter = PropertyBackedBeanExporter.this.adapterMap.get(id);
                        if (adapter != null)
                        {
                            Collection<String> properties = ((PropertyBackedBeanRemovePropertiesEvent) event).getProperties();
                            adapter.removeProperties(properties);
                        }
                    }
                    else if (event instanceof PropertyBackedBeanEvent)
                    {
                        // Broadcast these events across the cluster
                        if (messenger.isConnected())
                        {
                            messenger.send(event);
                        }
                    }
                }
                catch (MalformedObjectNameException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                catch (MessageSendingException e)
                {
                    // Let's just log these errors and continue
                    logger.error("Error broadcasting message to cluster", e);
                }
            }
        });
        messenger.setReceiver(this);
    }


    @Override
    public void onReceive(ApplicationEvent event)
    {
        if (event == null)
        {
            return;
        }
        
        // Target the broadcast event at the appropriate bean
        if (event instanceof PropertyBackedBeanEvent)
        {
            List<String> sourceId = ((PropertyBackedBeanEvent) event).getSourceId();
            PropertyBackedBeanAdapter adapter = this.adapterMap.get(sourceId);
            if (adapter == null)
            {
                logger.warn("Received remote message for property-backed bean ID that doesn't exist (yet): " + sourceId);
                return;
            }
            adapter.onApplicationEvent(event);
        }
    }
    
    /**
     * @param messengerFactory the messengerFactory to set
     */
    public void setMessengerFactory(MessengerFactory messengerFactory)
    {
        this.messengerFactory = messengerFactory;
    }
}
