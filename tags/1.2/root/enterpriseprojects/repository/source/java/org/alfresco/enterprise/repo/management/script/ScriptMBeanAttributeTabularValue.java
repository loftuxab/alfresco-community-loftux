/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

import org.alfresco.repo.jscript.Scopeable;
import org.alfresco.scripts.ScriptException;
import org.mozilla.javascript.Scriptable;

/**
 * This class provides a JavaScript-friendly API for MBean attribute values of type TabularValue.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class ScriptMBeanAttributeTabularValue implements Scopeable
{
    private Scriptable                        javaScriptScope;
    private final JmxValueConversionChain     conversionChain;
    
    private final TabularData         tabularData;
    private final TabularType         tabularType;
    
    public ScriptMBeanAttributeTabularValue(Scriptable scope, TabularData tabularData, JmxValueConversionChain conversionChain)
    {
        if (tabularData == null)         { throw new ScriptException("Null tabularData"); }
        if (conversionChain == null)     { throw new ScriptException("Null conversionChain"); }
        
        this.javaScriptScope = scope;
        this.conversionChain = conversionChain;
        
        this.tabularData = tabularData;
        this.tabularType = tabularData.getTabularType();
    }
    
    /**
     * Gets the Java type name of the components of this TabularValue.
     */
    public String getComponentJavaTypeName() { return this.tabularType.getTypeName(); }

    /**
     * Gets the Java type name of the component data types.
     * @return e.g. "javax.management.openmbean.CompositeData"
     */
    public String getComponentJavaClassName() { return this.tabularType.getClassName(); }
    
    /** Gets the description of this attribute type. */
    public String getComponentDescription() { return this.tabularType.getDescription(); }
    
    
    /** Returns a boolean indicating whether the data values within this composite value are arrays. */
    public boolean getComponentIsArray() { return this.tabularType.isArray(); }
    
    public String getTabularTypeDescription() { return this.tabularType.getDescription(); }
    
    /** Returns an unmodifiable List of the names of the items the values of which are used to
     * uniquely index each row element of tabular data values described by this TabularType instance.
     * 
     * @see TabularType#getRowType()#getItemNames()
     */
    public Object getItemNames()
    {
        // Wrap this up in an ArrayList instance because otherwise Rhino can't convert the List.
        List<String> result = new ArrayList<String>();
        result.addAll(this.tabularType.getRowType().keySet());
        
        return conversionChain.convertToJavaScript(result, javaScriptScope);
    }
    
    public Object getItemTypes()
    {
        List<String> result = new ArrayList<String>();
        for (String itemName : this.tabularType.getRowType().keySet())
        {
            result.add(this.tabularType.getRowType().getType(itemName).getClassName());
        }
        return conversionChain.convertToJavaScript(result, javaScriptScope);
    }
    
    public boolean getIsArray() { return this.tabularType.isArray(); }
    
    /**
     * Returns true if this attribute value has no data (no rows).
     * @see TabularData#isEmpty()
     */
    public boolean isEmpty() { return this.tabularData.isEmpty(); }
    
    /**
     * Returns the number of CompositeData values (rows) within this attribute value.
     * @see TabularData#size()
     */
    public int getSize() { return this.tabularData.size(); }
    
    /**
     * @see TabularData#values()
     * @return NativeArray of ScriptCompositeValue references
     */
    public Object getRows()
    {
        //TODO Cache this in this class
        @SuppressWarnings("unchecked")
        Collection<CompositeData> dataValues = (Collection<CompositeData>) tabularData.values();
        
        CompositeData[] dataArray = dataValues.toArray(new CompositeData[0]);
        
        if (conversionChain.canConvertToJavaScript(dataArray))
        {
            return conversionChain.convertToJavaScript(dataArray, javaScriptScope);
        }
        else
        {
            throw new ScriptException("Can't handle data type of : " + dataValues.getClass());
        }
    }
    
    @Override public void setScope(Scriptable scope) { this.javaScriptScope = scope; }
    
    @Override public int hashCode() { return tabularData.hashCode(); }
    
    @Override public boolean equals(Object otherObj)
    {
        if (this == otherObj) return true;
        if (otherObj == null) return false;
        if (getClass() != otherObj.getClass()) return false;
        
        ScriptMBeanAttributeTabularValue otherValue = (ScriptMBeanAttributeTabularValue) otherObj;
        return tabularData.equals(otherValue.tabularData);
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