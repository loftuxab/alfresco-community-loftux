/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.scripts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Wrapper;

/**
 * @author Kevin Roast
 */
public class ScriptValueConverter
{
    private static final String TYPE_DATE = "Date";
    
    /**
     * Convert an object from a script wrapper value to a serializable value valid outside
     * of the Rhino script processor context.
     * 
     * This includes converting JavaScript Array objects to Lists of valid objects.
     * 
     * @param value     Value to convert from script wrapper object to external object value.
     * 
     * @return unwrapped and converted value.
     */
    public static Object unwrapValue(Object value)
    {
        if (value == null)
        {
            return null;
        }
        else if (value instanceof Wrapper)
        {
            // unwrap a Java object from a JavaScript wrapper
            // recursively call this method to convert the unwrapped value
            value = unwrapValue(((Wrapper)value).unwrap());
        }
        else if (value instanceof ScriptableObject)
        {
            // a scriptable object will probably indicate a multi-value property
            // set using a JavaScript Array object
            ScriptableObject values = (ScriptableObject)value;

            if (value instanceof IdScriptableObject)
            {
                if ("Date".equals(((IdScriptableObject)value).getClassName()))
                {
                    value = Context.jsToJava(value, Date.class);
                }
                else if (value instanceof NativeArray)
                {
                    // convert JavaScript array of values to a List of Serializable objects
                    Object[] propIds = values.getIds();
                    if (isArray(propIds) == true)
                    {                    
                        List<Object> propValues = new ArrayList<Object>(propIds.length);
                        for (int i=0; i<propIds.length; i++)
                        {
                            // work on each key in turn
                            Object propId = propIds[i];
                            
                            // we are only interested in keys that indicate a list of values
                            if (propId instanceof Integer)
                            {
                                // get the value out for the specified key
                                Object val = values.get((Integer)propId, values);
                                // recursively call this method to convert the value
                                propValues.add(unwrapValue(val));
                            }
                        }

                        value = propValues;
                    }
                    else
                    {
                        Map<String, Object> propValues = new HashMap<String, Object>(propIds.length);
                        for (Object propId : propIds)
                        {
                            if (propId instanceof String)
                            {
                                // Get the value and add to the map
                                Object val = values.get((String)propId, values);
                                propValues.put((String)propId, unwrapValue(val));
                            }
                        }
                        
                        value = propValues;
                    }
                }
                else
                {
                    // convert JavaScript map to values to a Map of objects
                    Object[] propIds = values.getIds();
                    Map<String, Object> propValues = new HashMap<String, Object>(propIds.length);
                    for (int i=0; i<propIds.length; i++)
                    {
                        // work on each key in turn
                        Object propId = propIds[i];

                        // we are only interested in keys that indicate a list of values
                        if (propId instanceof String)
                        {
                            // get the value out for the specified key
                            Object val = values.get((String)propId, values);
                            // recursively call this method to convert the value
                            propValues.put((String)propId, unwrapValue(val));
                        }
                    }
                    value = propValues;
                }
            }
        }
        else if (value instanceof Object[])
        {
            // convert back a list Object Java values
            Object[] array = (Object[])value;
            ArrayList<Object> list = new ArrayList<Object>(array.length);
            for (int i=0; i<array.length; i++)
            {
                list.add(unwrapValue(array[i]));
            }
            value = list;
        }
        return value;
    }
    
    /**
     * Convert an object from any repository serialized value to a valid script object.
     * This includes converting Collection multi-value properties into JavaScript Array objects.
     *
     * @param services  Repository Services Registry
     * @param scope     Scripting scope
     * @param qname     QName of the property value for conversion
     * @param value     Property value
     * 
     * @return Value safe for scripting usage
     */
    public Object wrapValue(Scriptable scope, Object value)
    {
        // perform conversions from Java objects to JavaScript scriptable instances
        if (value == null)
        {
            return null;
        }
        else if (value instanceof Date)
        {
            // convert Date to JavaScript native Date object
            // call the "Date" constructor on the root scope object - passing in the millisecond
            // value from the Java date - this will construct a JavaScript Date with the same value
            Date date = (Date)value;
            Object val = ScriptRuntime.newObject(
                    Context.getCurrentContext(), scope, TYPE_DATE, new Object[] {date.getTime()});
            value = (Serializable)val;
        }
        else if (value instanceof Collection)
        {
            // recursively convert each value in the collection
            Collection<Object> collection = (Collection<Object>)value;
            Object[] array = new Object[collection.size()];
            int index = 0;
            for (Object obj : collection)
            {
                array[index++] = wrapValue(scope, obj);
            }
            // convert array to a native JavaScript Array
            value = (Serializable)Context.getCurrentContext().newArray(scope, array);
        }
        
        // simple numbers and strings are wrapped automatically by Rhino
        
        return value;
    }
    
    /**
     * Look at the id's of a native array and try to determine whether it's actually an Array or a Hashmap
     * 
     * @param ids       id's of the native array
     * @return boolean  true if it's an array, false otherwise (ie it's a map)
     */
    private static boolean isArray(Object[] ids)
    {
        boolean result = true;
        for (int i=0; i<ids.length; i++)
        {
            if (ids[i] instanceof Integer == false)
            {
               result = false;
               break;
            }
        }
        return result;
    }
}
