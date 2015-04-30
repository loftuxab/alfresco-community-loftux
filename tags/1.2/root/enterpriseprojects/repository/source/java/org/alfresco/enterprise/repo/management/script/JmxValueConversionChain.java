/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import java.util.List;

import org.alfresco.scripts.ScriptException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Scriptable;

/**
 * This class is responsible for the conversion of JMX values to and from JavaScript-friendly
 * wrapper objects. It does this by relying on various other pieces of conversion code.
 * It contains a Chain of Responsibility whereby each converter in a chain will be asked in order if it
 * can convert a given value and the request will be passed down the chain until a link in the chain can handle it.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class JmxValueConversionChain implements JmxValueConverter
{
    private static Log log = LogFactory.getLog(JmxValueConversionChain.class);
    
    private List<JmxValueConverter> converters;
    
    public void setValueConverters(List<JmxValueConverter> converters) { this.converters = converters; }
    
    @Override public boolean canConvertToJavaScript(Object javaObject)
    {
        final boolean result = this.getFirstCapableConverter(javaObject) != null;
        
        if (log.isTraceEnabled())
        {
            String objectClass = javaObject == null ? "<null>" : javaObject.getClass().getCanonicalName();
            log.trace("Can convert " + objectClass + " to JS: " + result);
        }
        
        return result;
    }
    
    @Override public Object convertToJavaScript(Object javaObject, Scriptable scope)
    {
        JmxValueConverter converter = getFirstCapableConverter(javaObject);
        
        if (converter != null)
        {
            return converter.convertToJavaScript(javaObject, scope);
        }
        else
        {
            throw new ScriptException("Cannot convert " + getObjectClass(javaObject));
        }
    }
    
    private String getObjectClass(Object o) { return o == null ? "<null>" : o.getClass().getCanonicalName(); }
    
    private JmxValueConverter getFirstCapableConverter(Object javaObject)
    {
        JmxValueConverter result = null;
        for (JmxValueConverter converter : converters)
        {
            if (converter.canConvertToJavaScript(javaObject))
            {
                result = converter;
                break;
            }
        }
        return result;
    }
}