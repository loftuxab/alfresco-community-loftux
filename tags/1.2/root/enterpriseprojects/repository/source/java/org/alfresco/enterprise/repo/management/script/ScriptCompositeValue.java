/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import java.io.Serializable;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;

import org.alfresco.repo.jscript.Scopeable;
import org.alfresco.repo.jscript.ScriptableHashMap;
import org.alfresco.scripts.ScriptException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Scriptable;

/**
 * This class provides a JavaScript-friendly API for MBean values of type CompositeValue.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class ScriptCompositeValue implements Scopeable
{
    private static Log log = LogFactory.getLog(ScriptCompositeValue.class);
    
    private final CompositeData                     compositeData;
    private final CompositeType                     compositeType;
    
    private Scriptable                              javaScriptScope;
    private final ScriptableHashMap<String, Object> dataValues;
    
    private final JmxValueConversionChain           conversionChain;
    
    public ScriptCompositeValue(Scriptable scope, CompositeData compositeData, JmxValueConversionChain conversionChain)
    {
        if (compositeData == null)   { throw new ScriptException("Null compositeData"); }
        if (conversionChain == null) { throw new ScriptException("Null conversionChain"); }
        
        this.javaScriptScope = scope;
        
        this.compositeData = compositeData;
        this.compositeType = compositeData.getCompositeType();
        
        this.conversionChain = conversionChain;
        
        // Extract and store the data values.
        final Set<String> keySet = compositeType.keySet();
        dataValues = new ScriptableHashMap<String, Object>();
        for (String key : keySet)
        {
            Object nextValue = compositeData.get(key);
            
            if (log.isTraceEnabled())
            {
                log.trace("key,value.class = " + key + "," + (nextValue == null ? "null" : nextValue.getClass().getSimpleName()) );
            }
            
            final Object convertedValue = conversionChain.convertToJavaScript((Serializable) nextValue, this.javaScriptScope);
            dataValues.put(key, convertedValue);
        }
    }
    
    /**
     * Gets the Java type name of the components of this CompositeValue.
     * @return e.g. "org.alfresco.repo.action.ActionStatistics"
     */
    public String getComponentJavaTypeName() { return this.compositeType.getTypeName(); }
    
    /**
     * Gets the Java type name of the component data types.
     * @return e.g. "javax.management.openmbean.CompositeData"
     */
    public String getComponentJavaClassName() { return this.compositeType.getClassName(); }
    
    /** Gets the description of this attribute type. */
    public String getComponentDescription() { return this.compositeType.getDescription(); }
    
    
    /** Returns a boolean indicating whether the data values within this composite value are arrays. */
    public boolean getComponentIsArray() { return this.compositeType.isArray(); }
    
    // TODO How to prevent updates to this map? Or do we ignore them?
    public ScriptableHashMap<String, Object> getDataMap() { return this.dataValues; }
    
    /**
     * This method returns a script-friendly String[] of the keys in the {@link #getDataMap() dataMap}.
     * They are not in any particular order, as they come from Map.keySet() which returns a Set.
     */
    public String[] getDataKeys()
    {
        Set<String> keys = this.dataValues.keySet();
        String[] result = new String[keys.size()];
        
        int i = 0;
        for (String key : keys)
        {
            result[i] = key;
            i++;
        }
        
        return result;
    }
    
    @Override public void setScope(Scriptable scope) { this.javaScriptScope = scope; }
    
    @Override public int hashCode() { return compositeData.hashCode(); }
    
    @Override public boolean equals(Object otherObj)
    {
        if (this == otherObj) return true;
        if (otherObj == null) return false;
        if (getClass() != otherObj.getClass()) return false;
        
        ScriptCompositeValue otherValue = (ScriptCompositeValue) otherObj;
        return compositeData.equals(otherValue.compositeData);
    }
    
    @Override public String toString()
    {
        StringBuilder msg = new StringBuilder();
        
        msg.append(this.getClass().getSimpleName())
           .append('[')
           .append(this.getComponentJavaTypeName())
           .append(']');
           
        return msg.toString();
        
        // Note: do not add the attribute value to toString() - it may contain sensitive data & may also be very long.
    }
}