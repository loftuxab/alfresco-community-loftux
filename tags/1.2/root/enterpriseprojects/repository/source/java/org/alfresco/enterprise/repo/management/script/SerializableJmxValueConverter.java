/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import java.io.Serializable;

import org.alfresco.repo.jscript.ValueConverter;
import org.mozilla.javascript.Scriptable;

/**
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
//TODO Repackage all this into ./conversion
public class SerializableJmxValueConverter extends AbstractJmxValueConverter
{
    private final static ValueConverter DEFAULT_CONVERTER = new ValueConverter();
    
    @Override public boolean canConvertToJavaScript(Object javaObject)
    {
        return javaObject instanceof Serializable;
    }
    
    @Override public Object convertToJavaScriptImpl(Object javaObject, Scriptable scope)
    {
        return DEFAULT_CONVERTER.convertValueForScript(null, scope, null, (Serializable)javaObject);
    }
}