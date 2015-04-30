/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import java.io.IOException;
import java.io.Serializable;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.alfresco.repo.jscript.Scopeable;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.scripts.ScriptException;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Scriptable;

/**
 * This class provides a JavaScript-friendly API for MBean attribute objects.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class ScriptMBeanAttribute implements Scopeable
{
    private static Log log = LogFactory.getLog(ScriptMBeanAttribute.class);
    
    private final MBeanServerConnection       mbeanServerConnection;
    private final JmxValueConversionChain     conversionChain;
    private final ObjectName                  containingBean;
    private final String                      attributeName;
    private final MBeanAttributeInfo          attributeInfo;
    
    // JavaScript-related stuff
    private Scriptable            scope;
    
    /** A locally cached copy of the JMX attribute value. */
    private Object attributeValue = null;
    /**
     * A flag indicating whether the JMX attribute value is known to have changed.
     * Note that if this value is false, the attribute may still have been updated e.g. updates to array elements would bypass this flag.
     */
    private boolean attributeDirty;
    
    private final static ValueConverter DEFAULT_CONVERTER = new ValueConverter();
    
    public ScriptMBeanAttribute(ObjectName containingBean, MBeanAttributeInfo attributeInfo,
                                MBeanServerConnection mbeanServerConnection,
                                JmxValueConversionChain conversionChain,
                                Scriptable scope)
    {
        if (attributeInfo == null)         { throw new ScriptException("Null attributeInfo"); }
        if (mbeanServerConnection == null) { throw new ScriptException("Null mbeanServerConnection"); }
        if (conversionChain == null)       { throw new ScriptException("Null conversionChain"); }
        
        this.containingBean = containingBean;
        this.attributeName = attributeInfo.getName();
        this.attributeInfo = attributeInfo;
        this.mbeanServerConnection = mbeanServerConnection;
        this.conversionChain = conversionChain;

        this.scope = scope;
    }
    
    // I'm using a getter idiom in the next few methods to allow JavaScript client code to say myAttr.isReadable;
    /**
     * This method reports whether this MBean attribute is readable.
     * Note that readable in this context means that the JMX attribute is itself defined as readable.
     * User or role-based authorisation may prevent some users from actually reading from it.
     */
    public boolean getIsReadable()  { return this.attributeInfo.isReadable(); }
    /**
     * This method reports whether this MBean attribute is writable.
     * Note that writable in this context means that the JMX attribute is itself defined as writable.
     * User or role-based authorisation may prevent some users from actually writing to it.
     */
    public boolean getIsWritable() { return this.attributeInfo.isWritable(); }
    
    // TODO Fix this. Could be non-array?
    public boolean getIsComposite() { return this.attributeInfo.getType().equals("[Ljavax.management.openmbean.CompositeData;"); }
    
    public boolean getIsTabular() { return this.attributeInfo.getType().equals("javax.management.openmbean.TabularData"); }
    
    /** This method returns the name of the Java class of the attribute value. */
    public String getClassName() { return this.attributeInfo.getType(); }
    
    /** This method returns the name of this attribute. */
    public String getName() { return this.attributeName; }
    
    /** This method returns the description string of this attribute. */
    public String getDescription() { return this.attributeInfo.getDescription(); }
    
    /** This method returns the value of this attribute. */
    public Object getValue()
    {
        // We need to store a local (to this object) copy of the JMX attribute value.
        // We need this so that updates can be made to that object, allowing us to perform
        // a permissions check on jmx.save(bean)
        if (attributeValue != null)
        {
            return conversionChain.convertToJavaScript(attributeValue, scope);
        }
        // If it's null and dirty then it's been explicitly set to null by client code
        else if (attributeValue == null && attributeDirty)
        {
            return attributeValue;
        }
        else
        {
            // go to JMX to retrieve the value.
            try
            {
                Object currentValue = mbeanServerConnection.getAttribute(containingBean, attributeName);
                
                this.attributeValue = currentValue;
                
                return conversionChain.convertToJavaScript((Serializable)currentValue, scope);
            } catch (AttributeNotFoundException e)
            {
                throw new ScriptException("Attribute not found: " + attributeName, e);
            } catch (InstanceNotFoundException e)
            {
                throw new ScriptException("Bean not found: " + containingBean, e);
            } catch (MBeanException e)
            {
                throw new ScriptException("MBean exception", e);
            } catch (ReflectionException e)
            {
                throw new ScriptException("Reflection exception", e);
            } catch (IOException e)
            {
                throw new ScriptException("IOException in talking to MBean server", e);
            }
        }
    }
    
    public void setValue(Object newValue)
    {
        if ( !this.attributeInfo.isWritable())
        {
            throw new ScriptException("Cannot set value for unwritable attribute " + attributeName);
        }
        
        if (log.isTraceEnabled())
        {
            // Do not log the actual value as this may be sensitive.
            log.trace("Setting new value on attribute " + this);
        }
        
        // The newValue received in this method may be a JavaScript object. We may need to convert it to what JMX expects.
        // But Object[] (most often String[]) is a special case - we do not want to convert this to List<String> in all cases.
        if (newValue instanceof Serializable && !(newValue instanceof Object[]))
        {
            Serializable s = (Serializable)newValue;
            this.attributeValue = DEFAULT_CONVERTER.convertValueForRepo(s);
        }
        else
        {
            this.attributeValue = newValue;
        }
        
        // We do not currently provide conversion for CompositeData or TabularData as we only have read-only JMX attributes of these types.
        
        this.attributeDirty = true;
    }
    
    /**
     * This method gets the current uncommitted value of this JMX attribute.
     * If the value is not 'dirty', then <code>null</code> is returned.
     */
    // This method is intentionally package-private.
    Attribute getDirtyValue()
    {
        // Only return a value if we have any non-null attribute value - this would include unchanged values read from the JMX server.
        // Or if we believe the value to have changed - this would return attribute values which had been set to null.
        if (attributeValue != null || attributeDirty)
        {
            // We currently only support attribute value updates. e.g. no attribute description updates.
            
            // Now that we come to save the attribute value into JMX, we may need to convert its current Java class
            // into a different Java class. An example of this would be an attribute value whose type was char.
            // This would have come from the JavaScript layer as a Java String.
            Class<?> requiredType = getAttributeRequiredType();
            
            Object jmxTypedValue;
            try
            {
                jmxTypedValue = TypeConversionUtils.convert(requiredType, attributeValue);
            } catch (IllegalArgumentException iax)
            {
                throw new ScriptException("Could not convert attribute to " + requiredType, iax);
            }
            return new Attribute(attributeName, jmxTypedValue);
        }
        else
        {
            return null;
        }
    }
    
    private Class<?> getAttributeRequiredType()
    {
        Class<?> result = null;
        try
        {
            String type = attributeInfo.getType();
            result = ClassUtils.getClass(type);
        } catch (ClassNotFoundException cnfx)
        {
            // This should not happen in normal operation.
            throw new ScriptException("Unrecognised attribute value class: " + result);
        }
        return result;
    }
    
    void clearCachedData()
    {
        this.attributeValue = null;
        this.attributeDirty = false;
    }
    
    @Override public void setScope(Scriptable scope) { this.scope = scope; }
    
    @Override public int hashCode() { return containingBean.hashCode() + 7 * attributeName.hashCode(); }
    
    @Override public boolean equals(Object otherObj)
    {
        if (this == otherObj) return true;
        if (otherObj == null) return false;
        if (getClass() != otherObj.getClass()) return false;
        
        ScriptMBeanAttribute otherAttribute = (ScriptMBeanAttribute) otherObj;
        return attributeName.equals(otherAttribute.attributeName) && containingBean.equals(otherAttribute.containingBean);
    }
    
    @Override public String toString()
    {
        StringBuilder msg = new StringBuilder();
        
        msg.append(ScriptMBeanAttribute.class.getSimpleName())
           .append("[").append(containingBean).append(" ").append(attributeName).append("]");
           
        return msg.toString();
        
        // Note: do not add the attribute value to toString() - it may contain sensitive data.
    }
}