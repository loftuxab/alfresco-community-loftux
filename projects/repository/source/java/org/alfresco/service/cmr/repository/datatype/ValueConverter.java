/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.dictionary.PropertyTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.CachingDateFormat;
import org.alfresco.util.ParameterCheck;

/**
 * Support for generic conversion between types.
 * 
 * Additional conversions may be added. Basic interoperabikitynos supported.
 * 
 * Direct conversion and two stage conversions via Number are supported. We do
 * not support conversion by any route at the moment
 * 
 * TODO: Add conversion for binary as byte[] for UTF8 String encoding 
 * 
 * TODO: Add support for QName 
 * 
 * TODO: Add support for Path 
 * 
 * TODO: Add support for lucene
 * 
 * TODO: Add suport to check of a type is convertable
 * 
 * TODO: Support for dynamically manging converions
 * 
 * @author andyh
 * 
 */
public class ValueConverter
{
    /**
     * General conversion method to Object types (note it cannot support
     * conversion to primary types due the restrictions of reflection. Use the
     * static conversion methods to primitive types)
     * 
     * @param propertyType - the target property type
     * @param value - the value to be converted
     * @return - the converted value as the correct type
     */
    public static Object convert(PropertyTypeDefinition propertyType, Object value)
    {
        ParameterCheck.mandatory("Property type definition", propertyType);
        
        // Convert property type to java class
        Class javaClass = null;
        String javaClassName = propertyType.getJavaClassName();
        try
        {
            javaClass = Class.forName(javaClassName);
        }
        catch (ClassNotFoundException e)
        {
            throw new DictionaryException("Java class " + javaClassName + " of property type " + propertyType.getName() + " is invalid", e);
        }
        
        return convert(javaClass, value);
    }
    
    
    /**
     * General conversion method to Object types (note it cannot support
     * conversion to primary types due the restrictions of reflection. Use the
     * static conversion methods to primitive types)
     * 
     * @param <T> The target type for the result of the conversion
     * @param c - a class for the target type
     * @param value - the value to be converted
     * @return - the converted value as the correct type
     */
    public static <T> T convert(Class<T> c, Object value)
    {
        // If multi value then default to using the first
        if (value instanceof Collection)
        {
            Object valueObject = null;
            for (Object o : (Collection) value)
            {
                valueObject = o;
                break;
            }
            return convert(c, valueObject);
        }
        
        if(value == null)
        {
            return null;
        }

        // Primative types
        if (c.isPrimitive())
        {
            // We can not suport primitive type conversion
            throw new UnsupportedOperationException("Can not convert direct to primitive type " + c.getName());
        }

        // Check if we already have the correct type
        if (c.isInstance(value))
        {
            return c.cast(value);
        }

        // Find the correct conversion - if available and do the converiosn
        Converter converter = getConversion(value.getClass(), c);
        return (T) converter.convert(value);

    }

    /**
     * Is the value multi valued
     * 
     * @param value
     * @return true - if the underlyinf is a collection of values and not a singole value
     */
    public static boolean isMultiValued(Object value)
    {
        return ((value instanceof Collection) && (((Collection) value).size() > 1));
    }

    /**
     * Get the number of values represented
     * @param value
     * @return 1 for norma values and the size of the collection for MVPs
     */
    public static int size(Object value)
    {
        if (value instanceof Collection)
        {
            return ((Collection) value).size();
        }
        else
        {
            return 1;
        }
    }

    /**
     * Get a typed iterator over the values in the multi valued collection
     * Also works for single value 
     * 
     * next() may fail with conversion failures as we convert as we go.
     * 
     * @param <T> The target type
     * @param c - the traget class
     * @param value - the value to convert
     * @return - a simple iterator over the values
     */
    public static <T> Iterator<T> iterator(Class<T> c, Object value)
    {
        Collection coll = createCollection(value);
        Iterator it = coll.iterator();

        Iterator<T> retit = new ConvertingIterator<T>(it, c);

        return retit;
    }

    private static Collection createCollection(Object value)
    {
        Collection coll;
        if (ValueConverter.isMultiValued(value))
        {
            coll = (Collection) value;
        }
        else
        {
            ArrayList<Object> list = new ArrayList<Object>(1);
            list.add(value);
            coll = list;
        }
        return coll;
    }

    /**
     * Get a collection for the values witha converting iterator
     * 
     * @param c
     * @param value
     * @return
     */
    public static <T> Collection<T> getCollection(Class<T> c, Object value)
    {
        Collection coll = createCollection(value);
        return new ConvertingCollection<T>(coll, c);
    }
    
    /**
     * Helper call for converting as iteratong over a collection
     * @author andyh
     *
     * @param <T>
     */
    private static class ConvertingIterator<T> implements Iterator<T>
    {
        Iterator it;

        Class type;

        ConvertingIterator(Iterator it, Class type)
        {
            this.it = it;
            this.type = type;
        }

        public boolean hasNext()
        {
            return it.hasNext();
        }

        public T next()
        {
            return (T) ValueConverter.convert(type, it.next());
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

    }
    
    private static class ConvertingCollection<T> implements Collection<T>
    {
        private Collection<?> base;
        private Class c;
        
        ConvertingCollection(Collection<?> base, Class c)
        {
            super();
            this.base = base;
            this.c = c;
        }

        public boolean add(T o)
        {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection c)
        {
            throw new UnsupportedOperationException();
        }

        public void clear()
        {
            throw new UnsupportedOperationException();
        }

        public boolean contains(Object o)
        {
            return base.contains(o);
        }

        public boolean containsAll(Collection<?> c)
        {
            return base.containsAll(c);
        }

        public boolean equals(Object o)
        {
           return base.equals(o);
        }

        public int hashCode()
        {
           return base.hashCode();
        }

        public boolean isEmpty()
        {
           return base.isEmpty();
        }

        public Iterator<T> iterator()
        {
            Iterator it = base.iterator();
            Iterator<T> retit = new ConvertingIterator<T>(it, c);
            return retit;
        }

        public boolean remove(Object o)
        {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection c)
        {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection c)
        {
            throw new UnsupportedOperationException();
        }

        public int size()
        {
            return base.size();
        }

        public Object[] toArray()
        {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public <O> O[] toArray(O[] a)
        {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }
        
        
    }
    
    /**
     * Get the boolean value for the value object
     * May have conversion failure
     * 
     * @param value
     * @return
     */
    public static boolean booleanValue(Object value)
    {
        return convert(Boolean.class, value).booleanValue();
    }

    
    /**
     * Get the char value for the value object
     * May have conversion failure
     * 
     * @param value
     * @return
     */
    public static char charValue(Object value)
    {
        return convert(Character.class, value).charValue();
    }

    
    /**
     * Get the byte value for the value object
     * May have conversion failure
     * 
     * @param value
     * @return
     */
    public static byte byteValue(Object value)
    {
        if (value instanceof Number)
        {
            return ((Number) value).byteValue();
        }
        return convert(Number.class, value).byteValue();
    }

    /**
     * Get the short value for the value object
     * May have conversion failure
     * 
     * @param value
     * @return
     */
    public static short shortValue(Object value)
    {
        if (value instanceof Number)
        {
            return ((Number) value).shortValue();
        }
        return convert(Number.class, value).shortValue();
    }

    
    /**
     * Get the int value for the value object
     * May have conversion failure
     * 
     * @param value
     * @return
     */
    public static int intValue(Object value)
    {
        if (value instanceof Number)
        {
            return ((Number) value).intValue();
        }
        return convert(Number.class, value).intValue();
    }

    
    /**
     * Get the long value for the value object
     * May have conversion failure
     * 
     * @param value
     * @return
     */
    public static long longValue(Object value)
    {
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        return convert(Number.class, value).longValue();
    }

    /**
     * Get the bollean value for the value object
     * May have conversion failure
     * 
     * @param float
     * @return
     */
    public static float floatValue(Object value)
    {
        if (value instanceof Number)
        {
            return ((Number) value).floatValue();
        }
        return convert(Number.class, value).floatValue();
    }

    
    /**
     * Get the bollean value for the value object
     * May have conversion failure
     * 
     * @param double
     * @return
     */
    public static double doubleValue(Object value)
    {
        if (value instanceof Number)
        {
            return ((Number) value).doubleValue();
        }
        return convert(Number.class, value).doubleValue();
    }

    /**
     * Find a conversion 
     * 
     * @param <F>
     * @param <T>
     * @param source
     * @param dest
     * @return
     */
    private static <F, T> Converter getConversion(Class<F> source, Class<T> dest)
    {
        Converter<?, ?> converter = null;
        Class clazz = source;
        do
        {
            Map<Class, Converter> map = conversions.get(clazz);
            if (map == null)
            {
                continue;
            }
            converter = map.get(dest);
            if (converter == null)
            {
                Converter<?, ?> first = map.get(Number.class);
                Converter<?, ?> second = null;
                if (first != null)
                {
                    map = conversions.get(Number.class);
                    if (map != null)
                    {
                        second = map.get(dest);
                    }
                }
                if (second != null)
                {
                    converter = new TwoStageConverter<F, T, Number>(first, second);
                }

            }
        }
        while ((converter == null) && ((clazz = clazz.getSuperclass()) != null));

        if (converter == null)
        {
            throw new UnsupportedOperationException("There are is no conversion registered from source type " + source.getName() + " to " + dest);

        }
        return converter;
    }

    /**
     * Add a converter to the list of those available
     * 
     * @param <F>       
     * @param <T>
     * @param source
     * @param destination
     * @param converter
     */
    public static <F, T> void addConverter(Class<F> source, Class<T> destination, Converter<F, T> converter)
    {
        Map<Class, Converter> map = conversions.get(source);
        if (map == null)
        {
            map = new HashMap<Class, Converter>();
            conversions.put(source, map);
        }
        map.put(destination, converter);
    }
    
    /**
     * Map of conversion
     */
    static Map<Class, Map<Class, Converter>> conversions = new HashMap<Class, Map<Class, Converter>>();

    /**
     * Initialise the starting conversions
     */
    static
    {
        //
        // From string
        //

        Map<Class, Converter> map = new HashMap<Class, Converter>();
        conversions.put(String.class, map);
        map.put(Boolean.class, new Converter<String, Boolean>()
        {
            public Boolean convert(String source)
            {
                return Boolean.valueOf(source);
            }

        });

        map.put(Character.class, new Converter<String, Character>()
        {
            public Character convert(String source)
            {
                if ((source == null) || (source.length() == 0))
                {
                    return null;
                }
                return Character.valueOf(source.charAt(0));
            }

        });

        map.put(Number.class, new Converter<String, Number>()
        {
            public Number convert(String source)
            {
                try
                {
                    return DecimalFormat.getNumberInstance().parse(source);
                }
                catch (ParseException e)
                {
                    throw new RuntimeException(e);
                }
            }

        });

        map.put(Byte.class, new Converter<String, Byte>()
        {
            public Byte convert(String source)
            {
                return Byte.valueOf(source);
            }

        });

        map.put(Short.class, new Converter<String, Short>()
        {
            public Short convert(String source)
            {
                return Short.valueOf(source);
            }

        });

        map.put(Integer.class, new Converter<String, Integer>()
        {
            public Integer convert(String source)
            {
                return Integer.valueOf(source);
            }

        });

        map.put(Long.class, new Converter<String, Long>()
        {
            public Long convert(String source)
            {
                return Long.valueOf(source);
            }

        });

        map.put(Float.class, new Converter<String, Float>()
        {
            public Float convert(String source)
            {
                return Float.valueOf(source);
            }

        });

        map.put(Double.class, new Converter<String, Double>()
        {
            public Double convert(String source)
            {
                return Double.valueOf(source);
            }

        });

        map.put(BigInteger.class, new Converter<String, BigInteger>()
        {
            public BigInteger convert(String source)
            {
                return new BigInteger(source);
            }

        });

        map.put(BigDecimal.class, new Converter<String, BigDecimal>()
        {
            public BigDecimal convert(String source)
            {
                return new BigDecimal(source);
            }

        });

        map.put(Date.class, new Converter<String, Date>()
        {
            public Date convert(String source)
            {
                try
                {
                    return CachingDateFormat.getDateFormat().parse(source);
                }
                catch (ParseException e)
                {
                    throw new RuntimeException(e);
                }
            }

        });

        map.put(Duration.class, new Converter<String, Duration>()
        {
            public Duration convert(String source)
            {
                return new Duration(source);
            }

        });
        
        map.put(QName.class, new Converter<String, QName>()
                {
                    public QName convert(String source)
                    {
                        return QName.createQName(source);
                    }

                });
        
        map.put(NodeRef.class, new Converter<String, NodeRef>()
                {
                    public NodeRef convert(String source)
                    {
                        return new NodeRef(source);
                    }

                });

        //
        // Number to Subtypes and Date
        //

        map = new HashMap<Class, Converter>();
        conversions.put(Number.class, map);

        map.put(Byte.class, new Converter<Number, Byte>()
        {
            public Byte convert(Number source)
            {
                return Byte.valueOf(source.byteValue());
            }

        });

        map.put(Short.class, new Converter<Number, Short>()
        {
            public Short convert(Number source)
            {
                return Short.valueOf(source.shortValue());
            }

        });

        map.put(Integer.class, new Converter<Number, Integer>()
        {
            public Integer convert(Number source)
            {
                return Integer.valueOf(source.intValue());
            }

        });

        map.put(Long.class, new Converter<Number, Long>()
        {
            public Long convert(Number source)
            {
                return Long.valueOf(source.longValue());
            }

        });

        map.put(Float.class, new Converter<Number, Float>()
        {
            public Float convert(Number source)
            {
                return Float.valueOf(source.floatValue());
            }

        });

        map.put(Double.class, new Converter<Number, Double>()
        {
            public Double convert(Number source)
            {
                return Double.valueOf(source.doubleValue());
            }

        });

        map.put(Date.class, new Converter<Number, Date>()
        {
            public Date convert(Number source)
            {
                return new Date(source.longValue());
            }

        });

        map.put(String.class, new Converter<Number, String>()
        {
            public String convert(Number source)
            {
                return source.toString();
            }

        });

        map.put(BigInteger.class, new Converter<Number, BigInteger>()
        {
            public BigInteger convert(Number source)
            {
                if (source instanceof BigDecimal)
                {
                    return ((BigDecimal) source).toBigInteger();
                }
                else
                {
                    return BigInteger.valueOf(source.longValue());
                }
            }

        });

        map.put(BigDecimal.class, new Converter<Number, BigDecimal>()
        {
            public BigDecimal convert(Number source)
            {
                if (source instanceof BigInteger)
                {
                    return new BigDecimal((BigInteger) source);
                }
                else
                {
                    return BigDecimal.valueOf(source.longValue());
                }
            }

        });

        //
        // Date ->
        //

        map = new HashMap<Class, Converter>();
        conversions.put(Date.class, map);

        map.put(Number.class, new Converter<Date, Number>()
        {
            public Number convert(Date source)
            {
                return Long.valueOf(source.getTime());
            }

        });

        map.put(String.class, new Converter<Date, String>()
        {
            public String convert(Date source)
            {
                return CachingDateFormat.getDateFormat().format(source);
            }

        });

        //
        // Boolean ->
        //

        map = new HashMap<Class, Converter>();
        conversions.put(Boolean.class, map);

        map.put(String.class, new Converter<Boolean, String>()
        {
            public String convert(Boolean source)
            {
                return source.toString();
            }

        });

        //
        // Character ->
        //

        map = new HashMap<Class, Converter>();
        conversions.put(Character.class, map);

        map.put(String.class, new Converter<Character, String>()
        {
            public String convert(Character source)
            {
                return source.toString();
            }

        });

        //
        // Duration ->
        //

        map = new HashMap<Class, Converter>();
        conversions.put(Duration.class, map);

        map.put(String.class, new Converter<Duration, String>()
        {
            public String convert(Duration source)
            {
                return source.toString();
            }

        });

        //
        // Byte
        //
        
        
        map = new HashMap<Class, Converter>();
        conversions.put(Byte.class, map);

        map.put(String.class, new Converter<Byte, String>()
        {
            public String convert(Byte source)
            {
                return source.toString();
            }

        });

        //
        // Short
        //

        map = new HashMap<Class, Converter>();
        conversions.put(Short.class, map);

        map.put(String.class, new Converter<Short, String>()
        {
            public String convert(Short source)
            {
                return source.toString();
            }

        });

        //
        // Integer
        //

        map = new HashMap<Class, Converter>();
        conversions.put(Integer.class, map);

        map.put(String.class, new Converter<Integer, String>()
        {
            public String convert(Integer source)
            {
                return source.toString();
            }

        });

        //
        // Long
        //

        map = new HashMap<Class, Converter>();
        conversions.put(Long.class, map);

        map.put(String.class, new Converter<Long, String>()
        {
            public String convert(Long source)
            {
                return source.toString();
            }

        });

        //
        // Float
        //

        map = new HashMap<Class, Converter>();
        conversions.put(Float.class, map);

        map.put(String.class, new Converter<Float, String>()
        {
            public String convert(Float source)
            {
                return source.toString();
            }

        });

        //
        // Double
        //

        map = new HashMap<Class, Converter>();
        conversions.put(Double.class, map);

        map.put(String.class, new Converter<Double, String>()
        {
            public String convert(Double source)
            {
                return source.toString();
            }

        });

        //
        // BigInteger
        //

        map = new HashMap<Class, Converter>();
        conversions.put(BigInteger.class, map);

        map.put(String.class, new Converter<BigInteger, String>()
        {
            public String convert(BigInteger source)
            {
                return source.toString();
            }

        });

        //
        // BigDecimal
        //

        map = new HashMap<Class, Converter>();
        conversions.put(BigDecimal.class, map);

        map.put(String.class, new Converter<BigDecimal, String>()
        {
            public String convert(BigDecimal source)
            {
                return source.toString();
            }

        });
        
        //
        // QName
        //

        map = new HashMap<Class, Converter>();
        conversions.put(QName.class, map);

        map.put(String.class, new Converter<QName, String>()
        {
            public String convert(QName source)
            {
                return source.toString();
            }

        });
        
        //
        // NodeRef
        //
        
        map = new HashMap<Class, Converter>();
        conversions.put(NodeRef.class, map);

        map.put(String.class, new Converter<NodeRef, String>()
        {
            public String convert(NodeRef source)
            {
                return source.toString();
            }

        });
        
        //
        // Path
        //
        
        map = new HashMap<Class, Converter>();
        conversions.put(Path.class, map);

        map.put(String.class, new Converter<Path, String>()
        {
            public String convert(Path source)
            {
                return source.toString();
            }

        });

    }

    // Support for pluggable conversions
    
    /**
     * Conversion interface
     * @author andyh
     *
     * @param <F> From type
     * @param <T> To type
     */
    public static interface Converter<F, T>
    {
        public T convert(F source);
    }

    /**
     * Support for chaining conversions
     * 
     * @author andyh
     *
     * @param <F> From Type
     * @param <T> To Type
     * @param <M> Intermediate type
     */
    public static class TwoStageConverter<F, T, M> implements Converter<F, T>
    {
        Converter first;

        Converter second;

        TwoStageConverter(Converter first, Converter second)
        {
            this.first = first;
            this.second = second;
        }

        public T convert(F source)
        {
            return (T) second.convert((M) first.convert(source));
        }
    }

}
