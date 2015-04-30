/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for {@link TypeConversionUtils}.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class TypeConversionUtilsTest
{
    // When JavaScript passes a number to Java as a method param of type java.lang.Object,
    // Rhino converts it to a java.lang.Double.
    // These Doubles cover the various ranges within Java's number system.
    private final static Double ZERO     = new Double(0);
    private final static Double BYTE     = new Double(Byte.MAX_VALUE);
    private final static Double SHORT    = new Double(Short.MAX_VALUE);
    private final static Double INT      = new Double(Integer.MAX_VALUE);
    private final static Double LONG     = new Double(Long.MAX_VALUE);
    private final static Double FLOAT    = new Double(Float.MAX_VALUE);
    private final static Double DOUBLE   = Double.MAX_VALUE;
    
    @Test public void convertNumbersToJavaPrimitives() throws Exception
    {
        assertEquals((byte)0,        (byte)TypeConversionUtils.convert(byte.class, ZERO));
        assertEquals(Byte.MAX_VALUE, (byte)TypeConversionUtils.convert(byte.class, BYTE));
        
        assertEquals((short)0,        (short)TypeConversionUtils.convert(short.class, ZERO));
        assertEquals(Byte.MAX_VALUE,  (short)TypeConversionUtils.convert(short.class, BYTE));
        assertEquals(Short.MAX_VALUE, (short)TypeConversionUtils.convert(short.class, SHORT));
        
        assertEquals(0,                 (int)TypeConversionUtils.convert(int.class, ZERO));
        assertEquals(Byte.MAX_VALUE,    (int)TypeConversionUtils.convert(int.class, BYTE));
        assertEquals(Short.MAX_VALUE,   (int)TypeConversionUtils.convert(int.class, SHORT));
        assertEquals(Integer.MAX_VALUE, (int)TypeConversionUtils.convert(int.class, INT));
        
        assertEquals(0L,                (long)TypeConversionUtils.convert(long.class, ZERO));
        assertEquals(Byte.MAX_VALUE,    (long)TypeConversionUtils.convert(long.class, BYTE));
        assertEquals(Short.MAX_VALUE,   (long)TypeConversionUtils.convert(long.class, SHORT));
        assertEquals(Integer.MAX_VALUE, (long)TypeConversionUtils.convert(long.class, INT));
        assertEquals(Long.MAX_VALUE,    (long)TypeConversionUtils.convert(long.class, LONG));
        
        assertEquals(0F,                (float)TypeConversionUtils.convert(float.class, ZERO),  Float.MIN_NORMAL);
        assertEquals(Byte.MAX_VALUE,    (float)TypeConversionUtils.convert(float.class, BYTE),  Float.MIN_NORMAL);
        assertEquals(Short.MAX_VALUE,   (float)TypeConversionUtils.convert(float.class, SHORT), Float.MIN_NORMAL);
        assertEquals(Integer.MAX_VALUE, (float)TypeConversionUtils.convert(float.class, INT),   Float.MIN_NORMAL);
        assertEquals(Long.MAX_VALUE,    (float)TypeConversionUtils.convert(float.class, LONG),  Float.MIN_NORMAL);
        assertEquals(Float.MAX_VALUE,   (float)TypeConversionUtils.convert(float.class, FLOAT), Float.MIN_NORMAL);
        
        assertEquals(0.0,               (double)TypeConversionUtils.convert(double.class, ZERO),   Double.MIN_NORMAL);
        assertEquals(Byte.MAX_VALUE,    (double)TypeConversionUtils.convert(double.class, BYTE),   Double.MIN_NORMAL);
        assertEquals(Short.MAX_VALUE,   (double)TypeConversionUtils.convert(double.class, SHORT),  Double.MIN_NORMAL);
        assertEquals(Integer.MAX_VALUE, (double)TypeConversionUtils.convert(double.class, INT),    Double.MIN_NORMAL);
        assertEquals(Long.MAX_VALUE,    (double)TypeConversionUtils.convert(double.class, LONG),   Double.MIN_NORMAL);
        assertEquals(Float.MAX_VALUE,   (double)TypeConversionUtils.convert(double.class, FLOAT),  Double.MIN_NORMAL);
        assertEquals(Double.MAX_VALUE,  (double)TypeConversionUtils.convert(double.class, DOUBLE), Double.MIN_NORMAL);
        
        // I don't think we need to test negative numbers in these normal cases.
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void convertOutOfRangePositiveByteShouldThrowException() throws Exception
    {
        TypeConversionUtils.convert(byte.class, SHORT);
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void convertOutOfRangeNegativeByteShouldThrowException() throws Exception
    {
        TypeConversionUtils.convert(byte.class, -SHORT);
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void convertOutOfRangePositiveShortShouldThrowException() throws Exception
    {
        TypeConversionUtils.convert(short.class, INT);
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void convertOutOfRangeNegativeShortShouldThrowException() throws Exception { TypeConversionUtils.convert(short.class, -INT); }
    
    @Test (expected=IllegalArgumentException.class)
    public void convertOutOfRangePositiveIntShouldThrowException() throws Exception
    {
        TypeConversionUtils.convert(int.class, LONG);
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void convertOutOfRangeNegativeIntShouldThrowException() throws Exception
    {
        TypeConversionUtils.convert(int.class, -LONG);
    }
    
    @Test public void convertOutOfRangeLongs() throws Exception
    {
        // Out of range longs are truncated silently. Rhino does this too.
        // http://www-archive.mozilla.org/js/liveconnect/lc3_method_overloading.html#InvocationConversion
        assertEquals(Long.MAX_VALUE, (long)TypeConversionUtils.convert(long.class, DOUBLE));
        assertEquals(-Long.MAX_VALUE - 1, (long)TypeConversionUtils.convert(long.class, -DOUBLE));
    }
    
    @Test public void convertOutOfRangeFloats() throws Exception
    {
        // Out of range floats are set to +/- infinity silently. Rhino does this too.
        // http://www-archive.mozilla.org/js/liveconnect/lc3_method_overloading.html#InvocationConversion
        assertEquals(Float.POSITIVE_INFINITY, (float)TypeConversionUtils.convert(float.class, DOUBLE),  Float.MIN_NORMAL);
        assertEquals(Float.NEGATIVE_INFINITY, (float)TypeConversionUtils.convert(float.class, -DOUBLE), Float.MIN_NORMAL);
    }
    
    // I don't believe we can receive an out-of-range java.lang.Double from JavaScript as JS itself can't represent
    // a number which exceeds Java's Double.MAX_VALUE limit.
    //
    // Therefore there is no convertoutOfRangeDoubles() method.
    
    @Test public void convertNullNumber() throws Exception
    {
        assertNull(TypeConversionUtils.convert(Long.class, null));
    }
    
    @Test public void convertBooleanStrings() throws Exception
    {
        assertTrue(TypeConversionUtils.convert(boolean.class, "true"));
        assertTrue(TypeConversionUtils.convert(boolean.class, "TRUE"));
        
        assertFalse(TypeConversionUtils.convert(boolean.class, "false"));
        assertFalse(TypeConversionUtils.convert(boolean.class, "FALSE"));
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void convertInvalidBooleanStringShouldThrowException() throws Exception
    {
        // Alfresco's default ValueConverter would convert "hello" to false. We don't want that here.
        TypeConversionUtils.convert(boolean.class, "hello");
    }
    
    @Test public void convertStringToChar() throws Exception
    {
        assertEquals('x', (char)TypeConversionUtils.convert(char.class, "x"));
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void convertTooLongStringToCharShouldThrowException() throws Exception
    {
        TypeConversionUtils.convert(char.class, "xyz");
    }
}