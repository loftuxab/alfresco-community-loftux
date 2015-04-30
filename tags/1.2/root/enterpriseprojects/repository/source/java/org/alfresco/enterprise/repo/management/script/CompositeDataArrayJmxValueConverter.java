/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import java.io.Serializable;

import javax.management.openmbean.CompositeData;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
//TODO Repackage all this into ./conversion
public class CompositeDataArrayJmxValueConverter extends AbstractJmxValueConverter
{
    @Override public boolean canConvertToJavaScript(Object javaObject)
    {
        return javaObject instanceof CompositeData[];
    }
    
    @Override public Object convertToJavaScriptImpl(Object javaObject, Scriptable scope)
    {
        Object result;
        
        // recursively convert each value in the array
        CompositeData[] dataArray = (CompositeData[]) javaObject;
        Object[] array = new Object[dataArray.length];
        int index = 0;
        for (CompositeData cd : dataArray)
        {
            array[index++] = conversionChain.convertToJavaScript(cd, scope);
        }
        try
        {
            Context.enter();
            // Convert array to a native JavaScript Array
            // Note - a scope is usually required for this to work
            result = (Serializable)Context.getCurrentContext().newArray(scope, array);
        }
        finally
        {
            Context.exit();
        }
        
        return result;
    }
}