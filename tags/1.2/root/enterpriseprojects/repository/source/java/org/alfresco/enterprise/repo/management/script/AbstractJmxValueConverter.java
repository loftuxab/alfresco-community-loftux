/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import org.mozilla.javascript.Scriptable;

/**
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public abstract class AbstractJmxValueConverter implements JmxValueConverter
{
    protected JmxValueConversionChain conversionChain;
    
    public void setConversionChain(JmxValueConversionChain conversionChain) { this.conversionChain = conversionChain; }
    
    @Override public Object convertToJavaScript(Object javaObject, Scriptable scope)
    {
        if ( !canConvertToJavaScript(javaObject))
        {
            throw new IllegalArgumentException("Cannot convert object of type " + getObjectClass(javaObject));
        }
        return convertToJavaScriptImpl(javaObject, scope);
    }
    
    protected abstract Object convertToJavaScriptImpl(Object javaObject, Scriptable scope);
    
    private String getObjectClass(Object o) { return o == null ? "<null>" : o.getClass().getCanonicalName(); }
}