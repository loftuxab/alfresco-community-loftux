/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jmx.export.MBeanExportOperations;

/**
 * A base class for objects that export JMX MBeans in response to particular ApplicationEvents.
 * 
 * @author dward
 */
public abstract class AbstractManagedResourceExporter<T extends ApplicationEvent> implements ApplicationListener
{

    /** The event class. */
    private Class<T> eventClass;

    /**
     * Provides runtime type information for the class of ApplicationEvent that this abstract managed resource exporter
     * will listen for.
     * 
     * @param eventClass
     *            the event class
     */
    public AbstractManagedResourceExporter(Class<T> eventClass)
    {
        this.eventClass = eventClass;
    }

    /** The JMX exporter. */
    private MBeanExportOperations exporter;

    /**
     * Sets the JMX exporter.
     * 
     * @param exporter
     *            the JMX exporter
     */
    public void setJmxExporter(MBeanExportOperations exporter)
    {
        this.exporter = exporter;
    }

    /**
     * Returns the objects to export in response to an event of the appropriate type. Subclasses must implement this.
     * 
     * @param event
     *            the event
     * @return a Map of objects to export, keyed by ObjectName
     * @throws MalformedObjectNameException
     *             if an object name is malformed
     */
    public abstract Map<ObjectName, ?> getObjectsToExport(T event) throws MalformedObjectNameException;

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (this.eventClass.isAssignableFrom(event.getClass()))
        {
            try
            {
                for (Map.Entry<ObjectName, ?> e : getObjectsToExport((T) event).entrySet())
                {
                    this.exporter.registerManagedResource(e.getValue(), e.getKey());
                }
                postRegister();
            }
            catch (MalformedObjectNameException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Overridable hook. Perform any additional configuration necessary post-registration.
     */
    protected void postRegister()
    {
    }
}
