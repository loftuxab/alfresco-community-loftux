/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.Map;
import java.util.TreeMap;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

/**
 * A class that adapts a {@link Map} as a read-only {@link DynamicMBean}.
 * 
 * @author dward
 */
public class PropertiesDynamicMBean implements DynamicMBean
{

    /** The properties. */
    private final Map<String, Object> properties;

    /**
     * Instantiates a new DynamicMBean using the System properties.
     */
    @SuppressWarnings("unchecked")
    public PropertiesDynamicMBean()
    {
        this((Map) System.getProperties());
    }

    /**
     * Instantiates a new DynamicMBean using the supplied Map.
     * 
     * @param properties
     *            a map of properties
     */
    public PropertiesDynamicMBean(Map<String, Object> properties)
    {
        this.properties = properties;
    }

    /*
     * (non-Javadoc)
     * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
     */
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException
    {
        return this.properties.get(attribute);
    }

    /*
     * (non-Javadoc)
     * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
     */
    public AttributeList getAttributes(String[] attributes)
    {
        AttributeList list = new AttributeList();
        for (String attribute : attributes)
        {
            Object value = this.properties.get(attribute);
            if (value != null)
            {
                list.add(new Attribute(attribute, value));
            }
        }
        return list;
    }

    /*
     * (non-Javadoc)
     * @see javax.management.DynamicMBean#getMBeanInfo()
     */
    public MBeanInfo getMBeanInfo()
    {
        // Created a sorted snapshot of the properties
        Map<String, Object> properties;
        synchronized (this.properties)
        {
            properties = new TreeMap<String, Object>();
            for (Map.Entry<String, Object> entry : this.properties.entrySet())
            {
                properties.put(entry.getKey(), entry.getValue());
            }
        }

        MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[properties.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : properties.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();
            attrs[i++] = new MBeanAttributeInfo(key, value == null ? String.class.getName() : value.getClass()
                    .getName(), "Property " + key, true, // isReadable
                    false, // isWritable
                    false); // isIs
        }
        ;
        return new MBeanInfo(this.getClass().getName(), "Property Dynamic MBean", attrs, null, null, null);
    }

    /*
     * (non-Javadoc)
     * @see javax.management.DynamicMBean#invoke(java.lang.String, java.lang.Object[], java.lang.String[])
     */
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException,
            ReflectionException
    {
        throw new ReflectionException(new NoSuchMethodException(actionName));
    }

    /*
     * (non-Javadoc)
     * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
     */
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException,
            MBeanException, ReflectionException
    {
        throw new ReflectionException(new IllegalAccessException());
    }

    /*
     * (non-Javadoc)
     * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
     */
    public AttributeList setAttributes(AttributeList attributes)
    {
        throw new IllegalStateException();
    }

}
