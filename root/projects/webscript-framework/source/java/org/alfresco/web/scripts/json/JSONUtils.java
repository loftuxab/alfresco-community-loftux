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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts.json;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.JSONException;
import org.json.JSONStringer;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

/**
 * Collection of JSON Utility methods.
 * 
 * @author Roy Wetherall
 */
public class JSONUtils
{
    /**
     * Converts a given JavaScript native object and converts it to the relevant JSON string.
     * 
     * @param object            JavaScript object
     * @return String           JSON      
     * @throws JSONException
     */
    public String toJSONString(Object object)
        throws JSONException
    {
        JSONStringer json = new JSONStringer();

        if (object instanceof NativeArray)
        {
            nativeArrayToJSONString((NativeArray)object, json);
        }
        else if (object instanceof NativeObject)
        { 
            nativeObjectToJSONString((NativeObject)object, json);
        }
        else
        {
            // TODO what else should this support?
            throw new AlfrescoRuntimeException("Only native objects and arrays are currently supported by the toJSONString method.");
        }        
        
        return json.toString();
    }
    
    /**
     * Build a JSON string for a native object
     * 
     * @param nativeObject
     * @param json
     * @throws JSONException
     */
    private void nativeObjectToJSONString(NativeObject nativeObject, JSONStringer json)
        throws JSONException
    {
        json.object();
        
        Object[] ids = nativeObject.getIds();
        for (Object id : ids)
        {
            String key = id.toString();
            json.key(key);
            
            Object value = nativeObject.get(key, nativeObject);
            valueToJSONString(value, json);
        }
        
        json.endObject();
    }
    
    /**
     * Build JSON string for a native array
     * 
     * @param nativeArray
     * @param json
     */
    private void nativeArrayToJSONString(NativeArray nativeArray, JSONStringer json)
        throws JSONException
    {
        Object[] propIds = nativeArray.getIds();
        if (isArray(propIds) == true)
        {      
            json.array();
            
            for (int i=0; i<propIds.length; i++)
            {
                Object propId = propIds[i];
                if (propId instanceof Integer)
                {
                    Object value = nativeArray.get((Integer)propId, nativeArray);
                    valueToJSONString(value, json);
                }
            }
            
            json.endArray();
        }
        else
        {
            json.object();
            
            for (Object propId : propIds)
            {
                Object value = nativeArray.get(propId.toString(), nativeArray);
                json.key(propId.toString());
                valueToJSONString(value, json);    
            }            
            
            json.endObject();
        }
    }
    
    /**
     * Look at the id's of a native array and try to determine whether it's actually an Array or a HashMap
     * 
     * @param ids       id's of the native array
     * @return boolean  true if it's an array, false otherwise (ie it's a map)
     */
    private boolean isArray(Object[] ids)
    {
        boolean result = true;
        for (Object id : ids)
        {
            if (id instanceof Integer == false)
            {
               result = false;
               break;
            }
        }
        return result;
    }
    
    /**
     * Convert value to JSON string
     * 
     * @param value
     * @param json
     * @throws JSONException
     */
    private void valueToJSONString(Object value, JSONStringer json)
        throws JSONException
    {
        if (value instanceof IdScriptableObject &&
            ((IdScriptableObject)value).getClassName().equals("Date") == true)
        {
            // Get the UTC values of the date
            Object year = NativeObject.callMethod((IdScriptableObject)value, "getUTCFullYear", null);
            Object month = NativeObject.callMethod((IdScriptableObject)value, "getUTCMonth", null);
            Object date = NativeObject.callMethod((IdScriptableObject)value, "getUTCDate", null);
            Object hours = NativeObject.callMethod((IdScriptableObject)value, "getUTCHours", null);
            Object minutes = NativeObject.callMethod((IdScriptableObject)value, "getUTCMinutes", null);
            Object seconds = NativeObject.callMethod((IdScriptableObject)value, "getUTCSeconds", null);
            Object milliSeconds = NativeObject.callMethod((IdScriptableObject)value, "getUTCMilliseconds", null);
            
            // Build the JSON object to represent the UTC date
            json.object()
                    .key("zone").value("UTC")
                    .key("year").value(year)
                    .key("month").value(month)
                    .key("date").value(date)
                    .key("hours").value(hours)
                    .key("minutes").value(minutes)
                    .key("seconds").value(seconds)
                    .key("milliseconds").value(milliSeconds)
                .endObject();
            
        }
        else if (value instanceof NativeArray)
        {
            // Output the native object
            nativeArrayToJSONString((NativeArray)value, json);
        }
        else if (value instanceof NativeObject)
        {
            // Output the native array
            nativeObjectToJSONString((NativeObject)value, json);
        }
        else
        {
            json.value(value);
        }
    }
    
    // TODO commented out for now since further investigation is required to ensure this is the best 
    //      JSON escaping strategy
    
//    public String escapeJSONString(String s)
//    {
//        String result = null;
//        
//        if (s != null)
//        {
//            StringBuffer sb=new StringBuffer();
//            for(int i=0;i<s.length();i++)
//            {
//                char ch=s.charAt(i);
//                switch(ch)
//                {
//                    case '"':
//                        sb.append("\\\"");
//                        break;
//                    case '\\':
//                        sb.append("\\\\");
//                        break;
//                    case '\b':
//                        sb.append("\\b");
//                        break;
//                    case '\f':
//                        sb.append("\\f");
//                        break;
//                    case '\n':
//                        sb.append("\\n");
//                        break;
//                    case '\r':
//                        sb.append("\\r");
//                        break;
//                    case '\t':
//                        sb.append("\\t");
//                        break;
//                    case '/':
//                        sb.append("\\/");
//                        break;
//                    default:
//                        if(ch>='\u0000' && ch<='\u001F')
//                        {
//                            String ss=Integer.toHexString(ch);
//                            sb.append("\\u");
//                            for(int k=0;k<4-ss.length();k++)
//                            {
//                                sb.append('0');
//                            }
//                            sb.append(ss.toUpperCase());
//                        }
//                        else
//                        {
//                            sb.append(ch);
//                        }
//                }
//            }
//        
//            // Set result string
//            result = sb.toString();
//        }
//        
//        return result;
//    }
    
}
