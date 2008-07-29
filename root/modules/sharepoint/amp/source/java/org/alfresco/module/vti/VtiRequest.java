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

package org.alfresco.module.vti;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.alfresco.module.vti.httpconnector.VtiServletContainer;
import org.alfresco.module.vti.metadata.DocMetaInfo;
import org.alfresco.module.vti.metadata.DocsMetaInfo;
import org.alfresco.module.vti.metadata.Document;


/**
 * 
 * @author Michael Shavnev
 *
 */
public class VtiRequest extends HttpServletRequestWrapper
{
    // Syntax Delimiters
    protected static final String OBRACKET = "[";
    protected static final String CBRACKET = "]";
    protected static final String LISTSEP = ";";
    protected static final String COMMASEP = ",";
    
    // METADICT-CONSTRAINT-CHAR
    protected static final String METADICT_CONSTRAINT_IGNORE = "X"; // ignore
    protected static final String METADICT_CONSTRAINT_RO = "R";     // read only 
    protected static final String METADICT_CONSTRAINT_RW = "W";     // read/write
    
    //METADICT-VALUE
    protected static final String METADICT_VALUE_TIME = "T";            // TIME
    protected static final String METADICT_VALUE_STRING_VECTOR = "V";   // METADICT-STRING-VECTOR
    protected static final String METADICT_VALUE_BOOLEAN = "B";         // BOOLEAN
    protected static final String METADICT_VALUE_INT_VECTOR = "U";      // METADICT-INT-VECTOR
    protected static final String METADICT_VALUE_DOUBLE = "D";          // DOUBLE
    protected static final String METADICT_VALUE_STRING = "S";          // STRING
    
    private Map<String, String[]> supplementParamMap;
    
    // context name (folder where alfresco application is deployed on server) 
    private String alfrescoContextName = null;

    public VtiRequest(HttpServletRequest request)
    {
        super(request);
        supplementParamMap = new HashMap<String, String[]>();
        alfrescoContextName = (String) request.getAttribute(VtiServletContainer.VTI_ALFRESCO_CONTEXT);
    }
    
    public String getParameter(String name)
    {
        String param = null;
        String[] params = getParameterValues(name);
        if (params != null && params.length > 0)
        {
            param = checkForLineFeed(params[0]);
        }
        
        return param;
    }
    
    public String[] getParameterValues(String name)
    {
        String[] params = null;
        if (supplementParamMap.containsKey(name)) 
        {
            params = supplementParamMap.get(name);
        }
        else
        {
            params = super.getParameterValues(name);
        }
        return params;
    }

    public void setParameterValues(String name, String[] values)
    {
        supplementParamMap.put(name, values);
    }
    
    public void setParameter(String name, String value)
    {
        setParameterValues(name, new String[] {value});
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, String[]> getParameterMap()
    {
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.putAll(super.getParameterMap());
        paramMap.putAll(supplementParamMap);
        return paramMap;
    }
    
    @SuppressWarnings("unchecked")
    public Enumeration<String> getParameterNames()
    {
        Set<String> paramNameSet = new HashSet<String>(); 
        for (Enumeration<String> enumeration = super.getParameterNames(); enumeration.hasMoreElements(); )
        {
            paramNameSet.add(enumeration.nextElement());
        }
        paramNameSet.addAll(supplementParamMap.keySet());
        return new IteratorEnumeration<String>(paramNameSet.iterator());
    }
    
    private static class IteratorEnumeration<E> implements Enumeration<E> {

        private Iterator<E> iterator;
        
        public IteratorEnumeration(Iterator<E> iterator)
        {
            this.iterator = iterator;
        }

        public boolean hasMoreElements()
        {
            return iterator.hasNext();
        }

        public E nextElement()
        {
            return iterator.next();
        }
    };
    
    
    //----------------------------------------------------------------------------------------------
    
    /**
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return boolean parameter from request or defaultValue if not present
     */ 
    public boolean getParameter(String paramName, boolean defaultValue)
    {
        boolean value = defaultValue;
        String stringValue = getParameter(paramName);
        stringValue = checkForLineFeed(stringValue);
        if (stringValue != null)
        {
            if ("true".equalsIgnoreCase(stringValue))
            {
                value = true;
            }
            else
            {
                value = false;
            }
        }
        return value;
    }
    
    /**
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return Date parameter from request or defaultValue if not present
     */ 
    public Date getParameter(String paramName, Date defaultValue)
    {
        Date value = defaultValue;
        String stringValue = getParameter(paramName);
        stringValue = checkForLineFeed(stringValue);
        if (stringValue != null)
        {
           try
           {
               value = DateFormat.getDateInstance().parse(stringValue);
           }
           catch (ParseException e)
           {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
        }        
        return value;
    }
    
    /**
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return int parameter from request or defaultValue if not present
     */
    public int getParameter(String paramName, int defaultValue)
    {
        int value = defaultValue;
        String stringValue = getParameter(paramName);
        stringValue = checkForLineFeed(stringValue);
        if (stringValue != null || !stringValue.equals(""))
        {
            value = Integer.valueOf(stringValue);
        }
        return value;
    }
    
    /**
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return String parameter from request or defaultValue if not present
     */
    public String getParameter(String paramName, String defaultValue) throws UnsupportedEncodingException
    {
        String value = getParameter(paramName);
        value = checkForLineFeed(value);
        if (value == null)
        {
            value = defaultValue;
        }
        return URLDecoder.decode(value, "UTF-8");
    }
    
    /**
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return List<String> parameter from request or defaultValue if not present
     */
    public List<String> getParameter(String paramName, List<String> defaultValue)
    {
        String vectorString = getParameter(paramName);
        vectorString = checkForLineFeed(vectorString);
        List<String> vector = null;
        
        if (vectorString != null)
        {
            if (vectorString.indexOf(OBRACKET) == 0 && vectorString.lastIndexOf(CBRACKET) == (vectorString.length() - 1))
            {
                vectorString = vectorString.substring(1, vectorString.length() - 1);
                String[] urls = vectorString.split(";");
                vector = new ArrayList<String>();
                for (String url : urls)
                {
                    vector.add(url);
                }
                return vector;
            }            
        }
        return defaultValue;
    }
    
    /**
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return DocsMetaInfo parameter from request or defaultValue if not present
     */
    public DocsMetaInfo getParameter(String paramName, DocsMetaInfo defaultValue)
    {      
        String mapString = checkForLineFeed(getParameter(paramName));
        
        DocsMetaInfo result = new DocsMetaInfo();
        
        if (mapString.indexOf(OBRACKET) == 0 && mapString.lastIndexOf(CBRACKET) == (mapString.length() - 1))
        {
            mapString = mapString.substring(1, mapString.length() - 1); 
            String[] urls = mapString.split("\\]\\]");   
            for (String url:urls)
            {
                DocMetaInfo folder = new DocMetaInfo(true);
                String folder_name = url.substring(0, url.indexOf(LISTSEP)).substring(url.indexOf("=") + 1);
                folder.setPath(folder_name);
                String meta_info = url.substring(url.indexOf(LISTSEP) + 1);
                meta_info = meta_info.substring(meta_info.indexOf("[") + 1);
                if (!meta_info.equals(""))                
                {
                    Map<String, String> properties = new HashMap<String, String>();
                    String[] meta_keys = meta_info.split(LISTSEP);  
                    for (int i = 0; i < meta_keys.length; i+=2)
                    {
                        properties.put(meta_keys[i], meta_keys[i+1].substring(meta_keys[i+1].indexOf("|") + 1));                        
                    }
                    folder.setDocInfoProperties(properties);
                } 
                result.getFolderMetaInfoList().add(folder);
            }
            return result;
        }        
        return defaultValue;
    }
    
    /**
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return Document parameter from request or defaultValue if not present
     */    
    public Document getParameter(String paramName, Document defaultValue)throws IOException
    {   
        String mapString = checkForLineFeed(getParameter(paramName));
              
        Document result = new Document();
        if (mapString != null && mapString.length() > 0 )
        {
            if (mapString.indexOf(OBRACKET) == 0 && mapString.lastIndexOf(CBRACKET) == (mapString.length() - 1))
            {
                result.setInputStream(this.getInputStream());
                mapString = mapString.substring(1, mapString.length() - 1);  
                String document_name = mapString.substring(0, mapString.indexOf(LISTSEP)).substring(mapString.indexOf("=") + 1);
                result.setPath(document_name);
                String meta_info = mapString.substring(mapString.indexOf(LISTSEP) + 1);
                meta_info = meta_info.substring(meta_info.indexOf("[") + 1, meta_info.indexOf("]"));
                if (!meta_info.equals(""))                
                {
                    Map<String, String> properties = new HashMap<String, String>();
                    String[] meta_keys = meta_info.split(LISTSEP);  
                    for (int i = 0; i < meta_keys.length; i+=2)
                    {
                        properties.put(meta_keys[i], meta_keys[i+1].substring(meta_keys[i+1].indexOf("|") + 1));                        
                    }
                    result.setDocInfoProperties(properties);
                }
                return result;
            }
        }        
        return defaultValue;                
    }
    
    /**
     * @param paramName name of parameter 
     * @return Map<String, String> parameter from request
     */
    public Map<String, String> getDictionary(String paramName)
    {
        String dictionaryString = getParameter(paramName);
        Map<String, String> dictionary = new HashMap<String, String>();

        if (dictionaryString != null)
        {
            dictionary = new HashMap<String, String>();
            if (dictionaryString.indexOf(OBRACKET) == 0 && dictionaryString.lastIndexOf(CBRACKET) == (dictionaryString.length() - 1))
            {                
                StringTokenizer tokenizer = new StringTokenizer(dictionaryString, LISTSEP);
                while (true)
                {
                    String key, value;
                    if (tokenizer.hasMoreTokens())
                    {
                        key = tokenizer.nextToken();
                        if (tokenizer.hasMoreTokens())
                        {
                            value = tokenizer.nextToken();
                            dictionary.put(key, value);
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }

        return dictionary;
    }

    /**
     * @param paramName name of parameter 
     * @return Map<String, Object> parameter from request
     */
    public Map<String, Object> getMetaDictionary(String paramName)
    {
        String metaDictionaryString = getParameter(paramName);
        Map<String, Object> metaDictionary = new HashMap<String, Object>();

        if (metaDictionaryString != null)
        {
            metaDictionary = new HashMap<String, Object>();
            if (metaDictionaryString.indexOf(OBRACKET) == 0 && metaDictionaryString.lastIndexOf(CBRACKET) == (metaDictionaryString.length() - 1))
            {                
                StringTokenizer tokenizer = new StringTokenizer(metaDictionaryString, LISTSEP);
                while (true)
                {
                    String key, valueWithMetaDataString;
                    if (tokenizer.hasMoreTokens())
                    {
                        key = tokenizer.nextToken();
                        if (tokenizer.hasMoreTokens())
                        {
                            valueWithMetaDataString = tokenizer.nextToken();

                            // METADICT-VALUE = constraint type "|" value
                            if (valueWithMetaDataString.length() >= 3 && valueWithMetaDataString.substring(1, 2).equals(METADICT_CONSTRAINT_IGNORE) == false)
                            {
                                String typeString = valueWithMetaDataString.substring(0, 1);
                                String valueString = valueWithMetaDataString.substring(3);

                                if (typeString.equals(METADICT_VALUE_TIME))
                                {
                                    try
                                    {
                                        metaDictionary.put(key, DateFormat.getDateInstance().parse(valueString));
                                    }
                                    catch (ParseException e)
                                    {
                                        // TODO: getRequestParamMetaDictionary logging
                                    }
                                }
                                else if (typeString.equals(METADICT_VALUE_STRING))
                                {
                                    metaDictionary.put(key, valueString);
                                }
                                else if (typeString.equals(METADICT_VALUE_DOUBLE))
                                {
                                    try
                                    {
                                        metaDictionary.put(key, new Double(Double.parseDouble(valueString)));
                                    }
                                    catch (NumberFormatException e)
                                    {
                                        // TODO: getRequestParamMetaDictionary logging
                                    }
                                }
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

            }
        }

        return metaDictionary;
    }    
    
    private String checkForLineFeed(String value)
    {
        if (value != null && value.endsWith("\n"))
        {
            value = value.substring(0, value.length()-1);
        }
        return value;
    }

    /**
     * 
     * @return context name
     */
    public String getAlfrescoContextName()
    {
        return alfrescoContextName;
    }

}
