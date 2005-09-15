/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.service.cmr.repository.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.alfresco.util.CachingDateFormat;

public class ValueConverterTest extends TestCase
{

    public ValueConverterTest()
    {
        super();
    }

    public ValueConverterTest(String arg0)
    {
        super(arg0);
    }

    public void testPrimitives()
    {
        assertEquals(Boolean.valueOf(false), ValueConverter.convert(Boolean.class, false));
        assertEquals(Boolean.valueOf(true), ValueConverter.convert(Boolean.class, true));
        assertEquals(Character.valueOf('a'), ValueConverter.convert(Character.class, 'a'));
        assertEquals(Byte.valueOf("3"), ValueConverter.convert(Byte.class, (byte) 3));
        assertEquals(Short.valueOf("4"), ValueConverter.convert(Short.class, (short) 4));
        assertEquals(Integer.valueOf("5"), ValueConverter.convert(Integer.class, (int) 5));
        assertEquals(Long.valueOf("6"), ValueConverter.convert(Long.class, (long) 6));
        assertEquals(Float.valueOf("7.1"), ValueConverter.convert(Float.class, (float) 7.1));
        assertEquals(Double.valueOf("123.123"), ValueConverter.convert(Double.class, (double) 123.123));
    }

    public void testNoConversion()
    {
        assertEquals(Boolean.valueOf(false), ValueConverter.convert(Boolean.class, Boolean.valueOf(false)));
        assertEquals(Boolean.valueOf(true), ValueConverter.convert(Boolean.class, Boolean.valueOf(true)));
        assertEquals(Character.valueOf('w'), ValueConverter.convert(Character.class, Character.valueOf('w')));
        assertEquals(Byte.valueOf("3"), ValueConverter.convert(Byte.class, Byte.valueOf("3")));
        assertEquals(Short.valueOf("4"), ValueConverter.convert(Short.class, Short.valueOf("4")));
        assertEquals(Integer.valueOf("5"), ValueConverter.convert(Integer.class, Integer.valueOf("5")));
        assertEquals(Long.valueOf("6"), ValueConverter.convert(Long.class, Long.valueOf("6")));
        assertEquals(Float.valueOf("7.1"), ValueConverter.convert(Float.class, Float.valueOf("7.1")));
        assertEquals(Double.valueOf("123.123"), ValueConverter.convert(Double.class, Double.valueOf("123.123")));
        assertEquals(Double.valueOf("123.123"), ValueConverter.convert(Double.class, Double.valueOf("123.123")));
        assertEquals(new BigInteger("1234567890123456789"), ValueConverter.convert(BigInteger.class, new BigInteger("1234567890123456789")));
        assertEquals(new BigDecimal("12345678901234567890.12345678901234567890"), ValueConverter.convert(BigDecimal.class, new BigDecimal("12345678901234567890.12345678901234567890")));
        Date date = new Date();
        assertEquals(date, ValueConverter.convert(Date.class, date));
        assertEquals(new Duration("P25D"), ValueConverter.convert(Duration.class, new Duration("P25D")));
        assertEquals("woof", ValueConverter.convert(String.class, "woof"));
    }

    public void testToString()
    {
        assertEquals("true", ValueConverter.convert(String.class, new Boolean(true)));
        assertEquals("false", ValueConverter.convert(String.class, new Boolean(false)));
        assertEquals("v", ValueConverter.convert(String.class, Character.valueOf('v')));
        assertEquals("3", ValueConverter.convert(String.class, Byte.valueOf("3")));
        assertEquals("4", ValueConverter.convert(String.class, Short.valueOf("4")));
        assertEquals("5", ValueConverter.convert(String.class, Integer.valueOf("5")));
        assertEquals("6", ValueConverter.convert(String.class, Long.valueOf("6")));
        assertEquals("7.1", ValueConverter.convert(String.class, Float.valueOf("7.1")));
        assertEquals("123.123", ValueConverter.convert(String.class, Double.valueOf("123.123")));
        assertEquals("1234567890123456789", ValueConverter.convert(String.class, new BigInteger("1234567890123456789")));
        assertEquals("12345678901234567890.12345678901234567890", ValueConverter.convert(String.class, new BigDecimal("12345678901234567890.12345678901234567890")));
        Date date = new Date();
        assertEquals(CachingDateFormat.getDateFormat().format(date), ValueConverter.convert(String.class, date));
        assertEquals("P0Y25D", ValueConverter.convert(String.class, new Duration("P0Y25D")));
        assertEquals("woof", ValueConverter.convert(String.class, "woof"));
    }

    public void testFromString()
    {
        assertEquals(Boolean.valueOf(true), ValueConverter.convert(Boolean.class, "True"));
        assertEquals(Boolean.valueOf(false), ValueConverter.convert(Boolean.class, "woof"));
        assertEquals(Character.valueOf('w'), ValueConverter.convert(Character.class, "w"));
        assertEquals(Byte.valueOf("3"), ValueConverter.convert(Byte.class, "3"));
        assertEquals(Short.valueOf("4"), ValueConverter.convert(Short.class, "4"));
        assertEquals(Integer.valueOf("5"), ValueConverter.convert(Integer.class, "5"));
        assertEquals(Long.valueOf("6"), ValueConverter.convert(Long.class, "6"));
        assertEquals(Float.valueOf("7.1"), ValueConverter.convert(Float.class, "7.1"));
        assertEquals(Double.valueOf("123.123"), ValueConverter.convert(Double.class, "123.123"));
        assertEquals(new BigInteger("1234567890123456789"), ValueConverter.convert(BigInteger.class, "1234567890123456789"));
        assertEquals(new BigDecimal("12345678901234567890.12345678901234567890"), ValueConverter.convert(BigDecimal.class, "12345678901234567890.12345678901234567890"));
        assertEquals("2004-03-12T00:00:00", CachingDateFormat.getDateFormat().format(ValueConverter.convert(Date.class, "2004-03-12T00:00:00")));
        assertEquals(new Duration("P25D"), ValueConverter.convert(Duration.class, "P25D"));
        assertEquals("woof", ValueConverter.convert(String.class, "woof"));
    }

    public void testPrimativeAccessors()
    {
        assertEquals(false, ValueConverter.convert(Boolean.class, false).booleanValue());
        assertEquals(true, ValueConverter.convert(Boolean.class, true).booleanValue());
        assertEquals('a', ValueConverter.convert(Character.class, 'a').charValue());
        assertEquals((byte) 3, ValueConverter.convert(Byte.class, (byte) 3).byteValue());
        assertEquals((short) 4, ValueConverter.convert(Short.class, (short) 4).shortValue());
        assertEquals((int) 5, ValueConverter.convert(Integer.class, (int) 5).intValue());
        assertEquals((long) 6, ValueConverter.convert(Long.class, (long) 6).longValue());
        assertEquals((float) 7.1, ValueConverter.convert(Float.class, (float) 7.1).floatValue());
        assertEquals((double) 123.123, ValueConverter.convert(Double.class, (double) 123.123).doubleValue());
    }
    
    public void testInterConversions()
    {
        assertEquals(Byte.valueOf("1"), ValueConverter.convert(Byte.class, Byte.valueOf("1")));
        assertEquals(Short.valueOf("2"), ValueConverter.convert(Short.class, Byte.valueOf("2")));
        assertEquals(Integer.valueOf("3"), ValueConverter.convert(Integer.class, Byte.valueOf("3")));
        assertEquals(Long.valueOf("4"), ValueConverter.convert(Long.class, Byte.valueOf("4")));
        assertEquals(Float.valueOf("5"), ValueConverter.convert(Float.class, Byte.valueOf("5")));
        assertEquals(Double.valueOf("6"), ValueConverter.convert(Double.class, Byte.valueOf("6")));
        assertEquals(new BigInteger("7"), ValueConverter.convert(BigInteger.class, Byte.valueOf("7")));
        assertEquals(new BigDecimal("8"), ValueConverter.convert(BigDecimal.class, Byte.valueOf("8")));
        
        assertEquals(Byte.valueOf("1"), ValueConverter.convert(Byte.class, Short.valueOf("1")));
        assertEquals(Short.valueOf("2"), ValueConverter.convert(Short.class, Short.valueOf("2")));
        assertEquals(Integer.valueOf("3"), ValueConverter.convert(Integer.class, Short.valueOf("3")));
        assertEquals(Long.valueOf("4"), ValueConverter.convert(Long.class, Short.valueOf("4")));
        assertEquals(Float.valueOf("5"), ValueConverter.convert(Float.class, Short.valueOf("5")));
        assertEquals(Double.valueOf("6"), ValueConverter.convert(Double.class, Short.valueOf("6")));
        assertEquals(new BigInteger("7"), ValueConverter.convert(BigInteger.class, Short.valueOf("7")));
        assertEquals(new BigDecimal("8"), ValueConverter.convert(BigDecimal.class, Short.valueOf("8")));
        
        assertEquals(Byte.valueOf("1"), ValueConverter.convert(Byte.class, Integer.valueOf("1")));
        assertEquals(Short.valueOf("2"), ValueConverter.convert(Short.class, Integer.valueOf("2")));
        assertEquals(Integer.valueOf("3"), ValueConverter.convert(Integer.class, Integer.valueOf("3")));
        assertEquals(Long.valueOf("4"), ValueConverter.convert(Long.class, Integer.valueOf("4")));
        assertEquals(Float.valueOf("5"), ValueConverter.convert(Float.class, Integer.valueOf("5")));
        assertEquals(Double.valueOf("6"), ValueConverter.convert(Double.class, Integer.valueOf("6")));
        assertEquals(new BigInteger("7"), ValueConverter.convert(BigInteger.class, Integer.valueOf("7")));
        assertEquals(new BigDecimal("8"), ValueConverter.convert(BigDecimal.class, Integer.valueOf("8")));
        
        assertEquals(Byte.valueOf("1"), ValueConverter.convert(Byte.class, Long.valueOf("1")));
        assertEquals(Short.valueOf("2"), ValueConverter.convert(Short.class, Long.valueOf("2")));
        assertEquals(Integer.valueOf("3"), ValueConverter.convert(Integer.class, Long.valueOf("3")));
        assertEquals(Long.valueOf("4"), ValueConverter.convert(Long.class, Long.valueOf("4")));
        assertEquals(Float.valueOf("5"), ValueConverter.convert(Float.class, Long.valueOf("5")));
        assertEquals(Double.valueOf("6"), ValueConverter.convert(Double.class, Long.valueOf("6")));
        assertEquals(new BigInteger("7"), ValueConverter.convert(BigInteger.class, Long.valueOf("7")));
        assertEquals(new BigDecimal("8"), ValueConverter.convert(BigDecimal.class, Long.valueOf("8")));
        
        assertEquals(Byte.valueOf("1"), ValueConverter.convert(Byte.class, Float.valueOf("1")));
        assertEquals(Short.valueOf("2"), ValueConverter.convert(Short.class, Float.valueOf("2")));
        assertEquals(Integer.valueOf("3"), ValueConverter.convert(Integer.class, Float.valueOf("3")));
        assertEquals(Long.valueOf("4"), ValueConverter.convert(Long.class, Float.valueOf("4")));
        assertEquals(Float.valueOf("5"), ValueConverter.convert(Float.class, Float.valueOf("5")));
        assertEquals(Double.valueOf("6"), ValueConverter.convert(Double.class, Float.valueOf("6")));
        assertEquals(new BigInteger("7"), ValueConverter.convert(BigInteger.class, Float.valueOf("7")));
        assertEquals(new BigDecimal("8"), ValueConverter.convert(BigDecimal.class, Float.valueOf("8")));
        
        assertEquals(Byte.valueOf("1"), ValueConverter.convert(Byte.class, Double.valueOf("1")));
        assertEquals(Short.valueOf("2"), ValueConverter.convert(Short.class, Double.valueOf("2")));
        assertEquals(Integer.valueOf("3"), ValueConverter.convert(Integer.class, Double.valueOf("3")));
        assertEquals(Long.valueOf("4"), ValueConverter.convert(Long.class, Double.valueOf("4")));
        assertEquals(Float.valueOf("5"), ValueConverter.convert(Float.class, Double.valueOf("5")));
        assertEquals(Double.valueOf("6"), ValueConverter.convert(Double.class, Double.valueOf("6")));
        assertEquals(new BigInteger("7"), ValueConverter.convert(BigInteger.class, Double.valueOf("7")));
        assertEquals(new BigDecimal("8"), ValueConverter.convert(BigDecimal.class, Double.valueOf("8")));
        
        assertEquals(Byte.valueOf("1"), ValueConverter.convert(Byte.class, new BigInteger("1")));
        assertEquals(Short.valueOf("2"), ValueConverter.convert(Short.class, new BigInteger("2")));
        assertEquals(Integer.valueOf("3"), ValueConverter.convert(Integer.class, new BigInteger("3")));
        assertEquals(Long.valueOf("4"), ValueConverter.convert(Long.class, new BigInteger("4")));
        assertEquals(Float.valueOf("5"), ValueConverter.convert(Float.class, new BigInteger("5")));
        assertEquals(Double.valueOf("6"), ValueConverter.convert(Double.class, new BigInteger("6")));
        assertEquals(new BigInteger("7"), ValueConverter.convert(BigInteger.class, new BigInteger("7")));
        assertEquals(new BigDecimal("8"), ValueConverter.convert(BigDecimal.class, new BigInteger("8")));
        
        assertEquals(Byte.valueOf("1"), ValueConverter.convert(Byte.class, new BigDecimal("1")));
        assertEquals(Short.valueOf("2"), ValueConverter.convert(Short.class, new BigDecimal("2")));
        assertEquals(Integer.valueOf("3"), ValueConverter.convert(Integer.class, new BigDecimal("3")));
        assertEquals(Long.valueOf("4"), ValueConverter.convert(Long.class, new BigDecimal("4")));
        assertEquals(Float.valueOf("5"), ValueConverter.convert(Float.class, new BigDecimal("5")));
        assertEquals(Double.valueOf("6"), ValueConverter.convert(Double.class, new BigDecimal("6")));
        assertEquals(new BigInteger("7"), ValueConverter.convert(BigInteger.class, new BigDecimal("7")));
        assertEquals(new BigDecimal("8"), ValueConverter.convert(BigDecimal.class, new BigDecimal("8")));
    }
    
    public void testDate()
    {
        Date date = new Date(101);
        
        assertEquals(Byte.valueOf("101"), ValueConverter.convert(Byte.class, date));
        assertEquals(Short.valueOf("101"), ValueConverter.convert(Short.class, date));
        assertEquals(Integer.valueOf("101"), ValueConverter.convert(Integer.class, date));
        assertEquals(Long.valueOf("101"), ValueConverter.convert(Long.class, date));
        assertEquals(Float.valueOf("101"), ValueConverter.convert(Float.class, date));
        assertEquals(Double.valueOf("101"), ValueConverter.convert(Double.class, date));
        assertEquals(new BigInteger("101"), ValueConverter.convert(BigInteger.class, date));
        assertEquals(new BigDecimal("101"), ValueConverter.convert(BigDecimal.class, date));
        
        assertEquals(date, ValueConverter.convert(Date.class, (byte)101));
        assertEquals(date, ValueConverter.convert(Date.class, (short)101));
        assertEquals(date, ValueConverter.convert(Date.class, (int)101));
        assertEquals(date, ValueConverter.convert(Date.class, (long)101));
        assertEquals(date, ValueConverter.convert(Date.class, (float)101));
        assertEquals(date, ValueConverter.convert(Date.class, (double)101));
        
        assertEquals(date, ValueConverter.convert(Date.class, new BigInteger("101")));
        assertEquals(date, ValueConverter.convert(Date.class, (Object)(new BigDecimal("101"))));
        
        assertEquals(101, ValueConverter.intValue(date));
    }
    
    public void testMultiValue()
    {
        ArrayList<Object> list = makeList();
        
        assertEquals(true, ValueConverter.isMultiValued(list));
        assertEquals(14, ValueConverter.size(list));
        
        for(String stringValue: ValueConverter.getCollection(String.class, list))
        {
            System.out.println("Value is "+stringValue); 
        }
        
    }

    private ArrayList<Object> makeList()
    {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(Boolean.valueOf(true));
        list.add(Boolean.valueOf(false));
        list.add(Character.valueOf('q'));
        list.add(Byte.valueOf("1"));
        list.add(Short.valueOf("2"));
        list.add(Integer.valueOf("3"));
        list.add(Long.valueOf("4"));
        list.add(Float.valueOf("5"));
        list.add(Double.valueOf("6"));
        list.add(new BigInteger("7"));
        list.add(new BigDecimal("8"));
        list.add(new Date());
        list.add(new Duration("P5Y0M"));
        list.add("Hello mum");
        return list;
    }
    
    public void testSingleValuseAsMultiValue()
    {
        Integer integer = Integer.valueOf(43);
        
        assertEquals(false, ValueConverter.isMultiValued(integer));
        assertEquals(1, ValueConverter.size(integer));
        
        for(String stringValue: ValueConverter.getCollection(String.class, integer))
        {
            System.out.println("Value is "+stringValue); 
        }
        
    }
    
    public void testNullAndEmpty()
    {
        assertNull(ValueConverter.convert(Boolean.class, null));
        ArrayList<Object> list = new ArrayList<Object>();
        assertNotNull(ValueConverter.convert(Boolean.class, list));
        list.add(null);
        assertNotNull(ValueConverter.convert(Boolean.class, list));
        
    }
}
