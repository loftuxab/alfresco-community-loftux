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
 * This interface defines the contract for classes which can convert JMX values to and from JavaScript-friendly wrapper objects.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public interface JmxValueConverter
{
    /** Returns <tt>true</tt> if the implementing class is able to convert objects of the type given. */
    boolean canConvertToJavaScript(Object javaObject);
    
    /** This method performs a conversion of the given Java object into a JavaScript-friendly analogue, if one exists. */
    Object convertToJavaScript(Object javaObject, Scriptable scope);
    
    /**
     * This class is a {@link JmxValueConverter} which actually does no conversion.
     * It just returns the java object parameter unchanged.
     * 
     * @author Neil Mc Erlean
     * @since 4.2
     */
    public class NullJmxValueConverter implements JmxValueConverter
    {
        @Override public boolean canConvertToJavaScript(Object javaObject) { return javaObject == null; }
        
        @Override public Object convertToJavaScript(Object javaObject, Scriptable scope) { return javaObject; }
    }
    
    /**
     * This class is a {@link JmxValueConverter} which actually does no conversion.
     * It just returns the java object parameter unchanged.
     * 
     * @author Neil Mc Erlean
     * @since 4.2
     */
    public class IdentityJmxValueConverter implements JmxValueConverter
    {
        @Override public boolean canConvertToJavaScript(Object javaObject) { return true; }
        
        @Override public Object convertToJavaScript(Object javaObject, Scriptable scope) { return javaObject; }
    }
}