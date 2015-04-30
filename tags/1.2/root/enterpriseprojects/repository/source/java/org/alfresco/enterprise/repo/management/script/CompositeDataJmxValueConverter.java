/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import javax.management.openmbean.CompositeData;

import org.mozilla.javascript.Scriptable;

/**
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class CompositeDataJmxValueConverter extends AbstractJmxValueConverter
{
    @Override public boolean canConvertToJavaScript(Object javaObject)
    {
        return javaObject instanceof CompositeData;
    }
    
    @Override public Object convertToJavaScriptImpl(Object javaObject, Scriptable scope)
    {
        return new ScriptCompositeValue(scope, (CompositeData)javaObject, conversionChain);
    }
}