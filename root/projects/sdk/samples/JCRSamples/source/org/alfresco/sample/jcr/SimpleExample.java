/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.sample.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



/**
 * Simple Example that demonstrate login and retrieval of top-level Spaces
 * under Company Home.
 * 
 * @author David Caruana
 */
public class SimpleExample
{

    public static void main(String[] args)
        throws Exception
    {
        // Setup Spring and Transaction Service
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:alfresco/application-context.xml");
        
        // Retrieve Repository
        Repository repository = (Repository)context.getBean("JCR.Repository");

        // Login to workspace
        // Note: Default workspace is the one used by Alfresco Web Client which contains all the Spaces
        //       and their documents
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));

        try
        {
            // Retrieve Company Home
            Node root = session.getRootNode();
            Node companyHome = root.getNode("app:company_home");
            
            // Iterator through children of Company Home
            NodeIterator iterator = companyHome.getNodes();
            while(iterator.hasNext())
            {
                Node child = iterator.nextNode();
                System.out.println(child.getName());

                PropertyIterator propIterator = child.getProperties();
                while(propIterator.hasNext())
                {
                    Property prop = propIterator.nextProperty();
                    if (!prop.getDefinition().isMultiple())
                    {
                        System.out.println(" " + prop.getName() + " = " + prop.getString());
                    }
                }
            }
        }
        finally
        {
            session.logout();
            System.exit(0);
        }
        
    }
    
}
