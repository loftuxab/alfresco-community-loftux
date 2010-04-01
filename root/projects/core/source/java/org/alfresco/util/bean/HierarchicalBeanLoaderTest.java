/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.util.bean;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @see HierarchicalBeanLoader
 * 
 * @author Derek Hulley
 * @since 3.2SP1
 */
public class HierarchicalBeanLoaderTest extends TestCase
{
    private ClassPathXmlApplicationContext ctx;
    
    private String getBean(Class<?> clazz, boolean setBeforeInit) throws Exception
    {
        if (setBeforeInit)
        {
            System.setProperty("hierarchy-test.dialect", clazz.getName());
        }
        ctx = new ClassPathXmlApplicationContext("bean-loader/hierarchical-bean-loader-test-context.xml");
        if (!setBeforeInit)
        {
            System.setProperty("hierarchy-test.dialect", clazz.getName());
        }
        return (String) ctx.getBean("test.someString");
    }
    
    public void tearDown()
    {
        try
        {
            ctx.close();
        }
        catch (Throwable e)
        {
        }
    }
    
    public void testSuccess1() throws Throwable
    {
        String str = getBean(TreeSet.class, true);
        assertEquals("Bean value incorrect", "TreeSet", str);
    }
    
    public void testSuccess2() throws Throwable
    {
        String str = getBean(AbstractList.class, true);
        assertEquals("Bean value incorrect", "AbstractList", str);
    }
    
    public void testSuccess3() throws Throwable
    {
        String str = getBean(AbstractCollection.class, true);
        assertEquals("Bean value incorrect", "AbstractCollection", str);
    }
    
    public void testFailure1() throws Throwable
    {
        try
        {
            getBean(Collection.class, true);
            fail("Should not be able to retrieve bean using class " + Collection.class);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
}
