/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.tools.ObjectGUID;
import org.alfresco.web.framework.model.Component;

/**
 * Static Utility class that provides reflection against the public
 * properties of a given object.
 * 
 * This class provides methods for checking whether a given property name
 * is declared as a model variable.
 * 
 * Model variables are defined on ModelObject derived types as variables
 * that begin with a "PROP_" prefix.
 * 
 * The code walks the class chain and picks out which variables are model
 * specific and which ones are custom.  The results are stored in look-up
 * tables so that subsequent lookups will simply hit the cache.  This is
 * perfectly fine since model definitions do not change at runtime.
 * 
 * @author muzquiano
 */
public final class ModelHelper
{
    private static Map<String, Set> classMap = new HashMap<String, Set>();
    
    /**
     * Determines whether the given property is a custom property for 
     * the given object
     * 
     * @param object the object
     * @param propertyName the property name
     * 
     * @return true, if is custom property
     */
    public static boolean isCustomProperty(ModelObject object, String propertyName)
    {
        return !(isModelProperty(object, propertyName));
    }

    /**
     * Determines whether the given property is a non-custom (or model)
     * property for the given object.
     * 
     * @param object the object
     * @param propertyName the property name
     * 
     * @return true, if is model property
     */
    public static boolean isModelProperty(ModelObject object, String propertyName)
    {
        if (object == null || propertyName == null)
        {
            throw new IllegalArgumentException("ModelObject and PropertyName are mandatory.");
        }
        
        Class modelClass = object.getClass();
        
        // grab the cache of property keys
        Set<String> propertyMap;
        // NOTE: synchronizing on a static object! this is bad!
        // TODO: refactor this - should not require sync on static object
        synchronized (classMap)
        {
            propertyMap = classMap.get(modelClass.getName());
            if (propertyMap == null)
            {
                // we need to build the property map cache
                propertyMap = new HashSet<String>();
                classMap.put(modelClass.getName(), propertyMap);
                
                // reflect on the class
                try
                {
                    Class klass = modelClass;
                    do
                    {
                        Field[] fields = klass.getFields();
                        for (int i = 0; i < fields.length; i++)
                        {
                            // is it a declared property?
                            if (fields[i].getName().startsWith("PROP_"))
                            {
                                String fieldValue = (String) fields[i].get(object);
                                
                                // mark it
                                propertyMap.add(fieldValue);
                            }
                        }
                        
                        klass = klass.getSuperclass();
                    }
                    while (klass != null);
                }
                catch (IllegalAccessException iae)
                {
                	throw new AlfrescoRuntimeException("Unable to inspect properties on model object class: " +
                            modelClass.getName());
                }
            }
        }
        
        // look up property in property map cache
        return propertyMap.contains(propertyName);
    }

    /**
     * Builds a new GUID
     * 
     * @return the string
     */
    public static String newGUID()
    {
        return new ObjectGUID().toString();
    }   
    
    /**
     * Allows model object ids to be set manually
     * 
     * @param object
     * @param id
     */
    public static void resetId(ModelObject object, String id)
    {
        if (object instanceof AbstractModelObject)
        {
            // not for components
            if (object instanceof Component)
            {
                return;
            }
            
            ((AbstractModelObject)object).id = id;
        }        
    }
}
