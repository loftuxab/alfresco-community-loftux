/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Adapts a Java Bean to look like a {@link Map}. Remaps types to JMX Open types, so the bean can be converted to a
 * {@link CompositeData} via {@link CompositeDataSupport}. Also provides a static utility method for converting Maps to
 * CompositeData.
 * 
 * @author dward
 */
public class BeanMap extends AbstractMap<String, Object>
{
    /** The Constant logger. */
    private static final Log logger = LogFactory.getLog(BeanMap.class);

    /** The Constant simpleTypeMap. */
    private static final Map<Class<?>, SimpleType> simpleTypeMap = new HashMap<Class<?>, SimpleType>(17);

    // Create a handy reference map of Java class names to SimpleType instances by looping through all the public
    // SimpleType fields!
    static
    {
        Field[] typeFields = SimpleType.class.getFields();
        for (Field field : typeFields)
        {
            if (SimpleType.class.isAssignableFrom(field.getType()))
            {
                try
                {
                    SimpleType t = (SimpleType) field.get(null);
                    Class<?> c = Class.forName(t.getClassName());
                    BeanMap.simpleTypeMap.put(c, t);
                    // Attempt to 'unbox' boxed types
                    try
                    {
                        Field typeField = c.getField("TYPE");
                        c = (Class<?>) typeField.get(null);
                        BeanMap.simpleTypeMap.put(c, t);
                    }
                    catch (NoSuchFieldException e)
                    {
                        // Ignore
                    }
                }
                catch (IllegalAccessException e)
                {
                    throw new ExceptionInInitializerError(e);
                }
                catch (ClassNotFoundException e)
                {
                    throw new ExceptionInInitializerError(e);
                }
            }
        }
    }

    /** A static cache of property descriptors for all encountered bean classes. */
    private final static Map<Class<?>, Map<String, PropertyDescriptor>> descriptorCache = new HashMap<Class<?>, Map<String, PropertyDescriptor>>(
            17);

    /** A static cache of composite types for all encountered bean classes. */
    private final static Map<Class<?>, CompositeType> compositeTypeCache = new HashMap<Class<?>, CompositeType>(17);

    /** The bean. */
    private final Object bean;

    /** Its property descriptors. */
    private final Map<String, PropertyDescriptor> descriptors;

    /**
     * Instantiates a new bean map.
     * 
     * @param bean
     *            the bean
     */
    public BeanMap(Object bean)
    {
        this.bean = bean;
        this.descriptors = getPropertyDescriptors(bean.getClass());
    }

    /**
     * Gets the property descriptors for a given class, using the cache if possible.
     * 
     * @param c
     *            the class
     * @return its property descriptors
     */
    private static synchronized Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> c)
    {
        Map<String, PropertyDescriptor> descriptors = BeanMap.descriptorCache.get(c);
        if (descriptors == null)
        {
            descriptors = new TreeMap<String, PropertyDescriptor>();
            BeanMap.descriptorCache.put(c, descriptors);
            PropertyDescriptor[] descriptorArr;
            try
            {
                descriptorArr = Introspector.getBeanInfo(c, Object.class).getPropertyDescriptors();
            }
            catch (IntrospectionException e)
            {
                BeanMap.logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
            if (descriptorArr != null)
            {
                for (PropertyDescriptor descriptor : descriptorArr)
                {
                    if (descriptor.getReadMethod() != null && !descriptor.getPropertyType().isArray())
                    {
                        descriptors.put(descriptor.getName(), descriptor);
                    }
                }
            }
        }
        return descriptors;
    }

    /**
     * Gets the JMX composite type for a given bean class, using the cache if possible.
     * 
     * @param c
     *            the class
     * @return the composite type
     */
    private static synchronized CompositeType getCompositeType(Class<?> c)
    {
        CompositeType retVal = BeanMap.compositeTypeCache.get(c);
        if (retVal == null)
        {
            Map<String, PropertyDescriptor> descriptors = getPropertyDescriptors(c);
            OpenType[] itemTypes = new OpenType[descriptors.size()];
            int i = 0;
            for (PropertyDescriptor descriptor : descriptors.values())
            {
                itemTypes[i] = BeanMap.simpleTypeMap.get(descriptor.getPropertyType());
                // Convert all unmapped types to strings
                if (itemTypes[i] == null)
                {
                    itemTypes[i] = BeanMap.simpleTypeMap.get(String.class);
                }
                i++;
            }
            String[] itemNames = new String[descriptors.size()];
            descriptors.keySet().toArray(itemNames);
            try
            {
                retVal = new CompositeType(c.getName(), c.getName(), itemNames, itemNames, itemTypes);
                BeanMap.compositeTypeCache.put(c, retVal);
            }
            catch (OpenDataException e)
            {
                BeanMap.logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return retVal;
    }

    /**
     * Converts a {link Map} to a {@link CompositeData} object.
     * 
     * @param map
     *            the map
     * @return the composite data
     */
    public static CompositeData getCompositeData(Map<String, ?> map)
    {
        Map<String, Object> convertedMap = new TreeMap<String, Object>(map);
        OpenType[] itemTypes = new OpenType[convertedMap.size()];
        int i = 0;
        for (Map.Entry<String, Object> e : convertedMap.entrySet())
        {
            itemTypes[i] = BeanMap.simpleTypeMap.get(e.getValue().getClass());
            if (itemTypes[i] == null)
            {
                // Handle date subclasses
                if (e.getValue() instanceof Date)
                {
                    itemTypes[i] = BeanMap.simpleTypeMap.get(Date.class);
                    e.setValue(new Date(((Date) e.getValue()).getTime()));
                }
                // Convert all unmapped types to strings
                else
                {
                    itemTypes[i] = BeanMap.simpleTypeMap.get(String.class);
                    e.setValue(e.getValue().toString());
                }
            }
            i++;
        }
        String[] itemNames = new String[convertedMap.size()];
        convertedMap.keySet().toArray(itemNames);
        String className = convertedMap.getClass().getName();
        try
        {
            return new CompositeDataSupport(new CompositeType(className, className, itemNames, itemNames, itemTypes),
                    convertedMap);
        }
        catch (OpenDataException e)
        {
            BeanMap.logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts this object to a JMX composite data object.
     * 
     * @return a composite data object
     */
    public CompositeData toCompositeData()
    {
        try
        {
            return new CompositeDataSupport(getCompositeType(this.bean.getClass()), this);
        }
        catch (RuntimeException e)
        {
            BeanMap.logger.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e)
        {
            BeanMap.logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see java.util.AbstractMap#entrySet()
     */
    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet()
    {
        final Set<java.util.Map.Entry<String, PropertyDescriptor>> entrySet = this.descriptors.entrySet();
        return new AbstractSet<java.util.Map.Entry<String, Object>>()
        {
            @Override
            public int size()
            {
                return entrySet.size();
            }

            @Override
            public Iterator<Entry<String, Object>> iterator()
            {
                final Iterator<Entry<String, PropertyDescriptor>> i = entrySet.iterator();
                return new Iterator<Entry<String, Object>>()
                {

                    public boolean hasNext()
                    {
                        return i.hasNext();
                    }

                    public Entry<String, Object> next()
                    {
                        final Entry<String, PropertyDescriptor> e = i.next();
                        return new Entry<String, Object>()
                        {

                            public String getKey()
                            {
                                return e.getKey();
                            }

                            public Object getValue()
                            {
                                return get(e.getKey());
                            }

                            public Object setValue(Object value)
                            {
                                return put(e.getKey(), value);
                            }
                        };
                    }

                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /*
     * (non-Javadoc)
     * @see java.util.AbstractMap#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key)
    {
        return this.descriptors.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * @see java.util.AbstractMap#get(java.lang.Object)
     */
    @Override
    public Object get(Object key)
    {
        try
        {
            PropertyDescriptor descriptor = this.descriptors.get(key);
            Method method = descriptor.getReadMethod();
            // Lets read from getters on classes we wouldn't strictly be able to see, e.g. on private inner classes
            method.setAccessible(true);
            Object value = method.invoke(this.bean);
            // Workaround for the fact that CompositeDataSupport doesn't like subclasses of simple types!
            if (value instanceof Date && !value.getClass().equals(Date.class))
            {
                value = new Date(((Date) value).getTime());
            }
            // Convert unmapped types to strings
            else if (!BeanMap.simpleTypeMap.containsKey(descriptor.getPropertyType()))
            {
                value = value.toString();
            }
            return value;
        }
        catch (IllegalAccessException e)
        {
            BeanMap.logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            Throwable target = e.getTargetException();
            BeanMap.logger.error(target.getMessage(), target);
            if (target instanceof RuntimeException)
            {
                throw (RuntimeException) target;
            }
            else
            {
                throw new RuntimeException(target);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see java.util.AbstractMap#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public Object put(String key, Object value)
    {
        PropertyDescriptor descriptor = this.descriptors.get(key);
        if (descriptor == null || descriptor.getWriteMethod() == null)
        {
            throw new UnsupportedOperationException();
        }
        try
        {
            Object previousValue = get(key);
            descriptor.getWriteMethod().invoke(this.bean, value);
            return previousValue;
        }
        catch (IllegalAccessException e)
        {
            BeanMap.logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            Throwable target = e.getTargetException();
            BeanMap.logger.error(target.getMessage(), target);
            if (target instanceof RuntimeException)
            {
                throw (RuntimeException) target;
            }
            else
            {
                throw new RuntimeException(target);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see java.util.AbstractMap#remove(java.lang.Object)
     */
    @Override
    public Object remove(Object key)
    {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see java.util.AbstractMap#clear()
     */
    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see java.util.AbstractMap#keySet()
     */
    @Override
    public Set<String> keySet()
    {
        return this.descriptors.keySet();
    }
}
