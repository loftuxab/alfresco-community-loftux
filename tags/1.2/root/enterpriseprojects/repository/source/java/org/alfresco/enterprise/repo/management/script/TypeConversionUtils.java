/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management.script;

import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.repository.datatype.TypeConversionException;
import org.alfresco.service.cmr.repository.datatype.TypeConverter;
import org.alfresco.service.cmr.repository.datatype.TypeConverter.Converter;
import org.apache.commons.lang.ClassUtils;
import org.springframework.util.NumberUtils;

/**
 * This class performs type conversion between JMX-related objects that have come from the JavaScript layer &amp; objects
 * that are to be written into JMX.
 * <p/>
 * Note that many of the conversions performed by this class are done automatically by Rhino for normal Java method invocation
 * from JavaScript. However in JMX we must deal with method parameters typed as java.lang.Object on the JS API and convert to
 * a required type within the Java code.
 * <p/>
 * Also, in handling JMX operations, we must support the reflective invocation of Java methods from JavaScript rather than the
 * normal method despatch e.g.
 * <pre>
 *    // JavaScript code
 *    javaObj.invoke('method', [p1, p2, p3]);
 * </pre>
 * rather than
 * <pre>
 *    // JavaScript code
 *    javaObj.method(p1, p2, p3);
 * </pre>
 * This approach is supported by utility methods for type conversion in this class.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class TypeConversionUtils
{
    private TypeConversionUtils() { /* Intentionally empty to prevent instantiation. */ }
    
    private final static TypeConverter STRICTER_CONVERTER = DefaultTypeConverter.INSTANCE;
    static {
        // Replace the default String to Boolean type converter, with one that requires a stricter match.
        STRICTER_CONVERTER.addConverter(String.class, Boolean.class, new Converter<String, Boolean>()
        {
            public Boolean convert(String source)
            {
                if ("true".equalsIgnoreCase(source))
                {
                    return Boolean.TRUE;
                }
                else if ("false".equalsIgnoreCase(source))
                {
                    return Boolean.FALSE;
                }
                else
                {
                    throw new IllegalArgumentException("Cannot convert '" + source + "' to Boolean");
                }
            }
        });
    }
    
    /**
     * This method converts the provided object to an instance of the requiredType.
     * It handles primitive required types, conversion from String (of length 1) to char and narrowing conversions of in-range numerical types.
     * 
     * @throws IllegalArgumentException if the object cannot be safely converted to the required type.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Object> T convert(Class<T> requiredType, Object unconvertedValue)
    {
        if (unconvertedValue == null) { return null; }
        
        // First of all, our lives will be much easier here if we convert primitive values to their wrapped, Object equivalents.
        // If we produce, for example, a java.lang.Boolean and the required type is actually a boolean primitive, it will
        // be unboxed by Java when the JMX attribute value is written.
        Class<?> nonPrimitiveRequiredType;
        if (requiredType.isPrimitive())
        {
            nonPrimitiveRequiredType = ClassUtils.primitiveToWrapper(requiredType);
        }
        else
        {
            nonPrimitiveRequiredType = requiredType;
        }
        
        
        T result;
        // The TypeConverter below can't handle String to char, so we'll explicitly handle it
        if (Character.class.equals(nonPrimitiveRequiredType) && String.class.equals(unconvertedValue.getClass()))
        {
            String stringValue = (String) unconvertedValue;
            if (stringValue.length() != 1)
            {
                throw new IllegalArgumentException("Cannot persist String of length " + stringValue.length() + " as a char");
            }
            else
            {
                result = (T) new Character(stringValue.charAt(0));
            }
        }
        // The TypeConverter below won't allow narrowing numerical conversions, but all numbers in JavaScript are doubles
        // and for double values that are within the range of their required (narrower) type, we'll allow them through.
        else if (Number.class.isAssignableFrom(nonPrimitiveRequiredType) && Double.class.equals(unconvertedValue.getClass()))
        {
            Class<? extends Number> nonPrimitiveRequiredNumericalType = (Class<? extends Number>) nonPrimitiveRequiredType;
            final Double doubleValue = (Double)unconvertedValue;
            
            result = (T) NumberUtils.convertNumberToTargetClass(doubleValue, nonPrimitiveRequiredNumericalType);
        }
        else
        {
            try
            {
                result = (T) STRICTER_CONVERTER.convert(nonPrimitiveRequiredType, unconvertedValue);
            } catch (TypeConversionException tcx)
            {
                throw new IllegalArgumentException("No convertor defined to convert attribute value from " + unconvertedValue.getClass().getSimpleName() +
                        " to " + requiredType.getSimpleName(), tcx);
            }
        }
        
        return result;
    }
}