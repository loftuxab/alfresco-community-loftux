/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Static Helper methods for instantiating objects from reflection.
 * 
 * @author muzquiano
 */
public class ReflectionHelper
{
    private static Log logger = LogFactory.getLog(ReflectionHelper.class);
    
    private ReflectionHelper()
    {
    }

    /**
     * Constructs a new object for the given class name.
     * The construction takes no arguments.
     * 
     * If an exception occurs during construction, null is returned.
     * 
     * All exceptions are written to the Log instance for this class.
     * 
     * @param className
     * @return
     */
    public static Object newObject(String className)
    {
        Object o = null;

        try
        {
            Class clazz = Class.forName(className);
            o = clazz.newInstance();
        }
        catch (ClassNotFoundException cnfe)
        {
            logger.debug(cnfe);
        }
        catch (InstantiationException ie)
        {
            logger.debug(ie);
        }
        catch (IllegalAccessException iae)
        {
            logger.debug(iae);
        }
        return o;
    }

    /**
     * Constructs a new object for the given class name and with the given
     * arguments.  The arguments must be specified in terms of their Class[]
     * types and their Object[] values.
     * 
     * Example:
     * 
     *   String s = newObject("java.lang.String", new Class[] { String.class},
     *              new String[] { "test"});
     *              
     * is equivalent to:
     * 
     *   String s = new String("test");
     * 
     * If an exception occurs during construction, null is returned.
     * 
     * All exceptions are written to the Log instance for this class.

     * @param className
     * @param argTypes
     * @param args
     * @return
     */
    public static Object newObject(String className, Class[] argTypes, Object[] args)
    {
        /**
         * We have some mercy here - if they called and did not pass in any
         * arguments, then we will call through to the pure newObject() method.
         */
        if (args == null || args.length == 0)
        {
            return newObject(className);
        }

        /**
         * Try to build the object
         * 
         * If an exception occurs, we log it and return null.
         */
        Object o = null;
        try
        {
            // base class
            Class clazz = Class.forName(className);

            Constructor c = clazz.getDeclaredConstructor(argTypes);
            o = c.newInstance(args);
        }
        catch (ClassNotFoundException cnfe)
        {
            logger.debug(cnfe);
        }
        catch (InstantiationException ie)
        {
            logger.debug(ie);
        }
        catch (IllegalAccessException iae)
        {
            logger.debug(iae);
        }
        catch (NoSuchMethodException nsme)
        {
            logger.debug(nsme);
        }
        catch (InvocationTargetException ite)
        {
            logger.debug(ite);
        }
        return o;
    }

    /**
     * Invokes a method on the given object by passing the given arguments
     * into the method.
     * 
     * @param obj
     * @param method
     * @param argTypes
     * @param args
     * @return
     */
    public static Object invoke(Object obj, String method, Class[] argTypes, Object[] args)
    {
        if (obj == null || method == null)
        {
            throw new IllegalArgumentException("Object and Method must be supplied.");
        }
        
        /**
         * Try to invoke the method.
         * 
         * If the method is unable to be invoked, we log and return null.
         */
        try
        {
            Method m = obj.getClass().getMethod(method, argTypes);
            if(m != null)
            {
                return m.invoke(obj, args);
            }
        }
        catch(NoSuchMethodException nsme)
        {
            logger.debug(nsme);
        }
        catch(IllegalAccessException iae)
        {
            logger.debug(iae);
        }
        catch(InvocationTargetException ite)
        {
            logger.debug(ite);
        }
        
        return null;
    }
}
