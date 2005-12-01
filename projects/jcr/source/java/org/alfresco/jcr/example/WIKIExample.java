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
package org.alfresco.jcr.example;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.transaction.UserTransaction;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.transaction.TransactionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;


/**
 * Example that demonstrates read and write of a simple WIKI model
 *
 * Please refer to http://www.alfresco.org/mediawiki/index.php/Introducing_the_Alfresco_Java_Content_Repository_API
 * for a complete description of this example.
 * 
 * @author David Caruana
 */
public class WIKIExample
{

    public static void main(String[] args)
        throws Exception
    {
        // setup Spring and Transaction Service
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:org/alfresco/jcr/example/wiki-context.xml");
        ServiceRegistry registry = (ServiceRegistry)context.getBean(ServiceRegistry.SERVICE_REGISTRY);
        TransactionService trxService = (TransactionService)registry.getTransactionService();
        
        // retrieve Repository (here it's via programmatic approach, but it could also be injected)
        Repository repository = (Repository)context.getBean("JCR.Repository");
    
        // login to workspace
        // Note: Default workspace is the one used by Alfresco Web Client which contains all the Spaces
        //       and their documents
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));

        // start a transaction for creating the wiki structure
        UserTransaction trx1 = trxService.getUserTransaction();
        trx1.begin();
    
        try
        {
            // first, access the company home
            Node rootNode = session.getRootNode();
            System.out.println("Root node: path=" + rootNode.getPath() + ", type=" + rootNode.getPrimaryNodeType().getName());
            Node companyHome = rootNode.getNode("app:company_home");
            System.out.println("Company home node: path=" + companyHome.getPath() + ", type=" + companyHome.getPrimaryNodeType().getName());
            
            // remove the WIKI structure if it already exists
            try
            {
                Node encyclopedia = companyHome.getNode("wiki:encyclopedia");
                encyclopedia.remove();
            }
            catch(PathNotFoundException e)
            {
               // doesn't exist, no need to remove                
            }

            // create WIKI structure
            Node encyclopedia = companyHome.addNode("wiki:encyclopedia", "cm:folder");
            encyclopedia.setProperty("cm:name", "WIKI Encyclopedia");
            encyclopedia.setProperty("cm:description", "");
            Node page1 = encyclopedia.addNode("wiki:entry1", "wiki:page");
            page1.setProperty("cm:name", "Rose");
            page1.setProperty("cm:description", "");
            page1.setProperty("cm:title", "The rose");
            page1.setProperty("cm:content", "A rose is a flowering shrub.");
            page1.setProperty("wiki:category", new String[] {"flower", "plant", "rose"});
            Node page2 = encyclopedia.addNode("wiki:entry2", "wiki:page");
            page2.setProperty("cm:name", "Shakespeare");
            page2.setProperty("cm:description", "");
            page2.setProperty("cm:title", "William Shakespeare");
            page2.setProperty("cm:content", "A famous poet who likes roses.");
            page2.setProperty("wiki:category", new String[] {"poet"});
            
            // create a WIKI image
            Node contentNode = encyclopedia.addNode("wiki:image", "cm:content");
            contentNode.setProperty("cm:name", "Dog");
            contentNode.setProperty("cm:description", "");
            contentNode.setProperty("cm:title", "My dog at New Year party");
            ClassPathResource resource = new ClassPathResource("org/alfresco/jcr/example/wikiImage.gif");
            contentNode.setProperty("cm:content", resource.getInputStream());
            
            session.save();
            trx1.commit();
        }
        catch(Throwable e /* note: catch throwable for demonstration purposes only */)
        {
            trx1.rollback();
        }

        // start a transaction for accessing the wiki structure
        UserTransaction trx2 = trxService.getUserTransaction();
        trx2.begin();
    
        try
        {
            // access the wiki directly from root node
            Node rootNode = session.getRootNode();
            Node encyclopedia = rootNode.getNode("app:company_home/wiki:encyclopedia");
            Node direct = session.getNodeByUUID(encyclopedia.getUUID());
            System.out.println("Found encyclopedia correctly: " + encyclopedia.equals(direct));

            // access a wiki property directly from root node
            Node entry1 = rootNode.getNode("app:company_home/wiki:encyclopedia/wiki:entry1");
            String title = entry1.getProperty("cm:title").getString();
            System.out.println("Entry 1 Title: " + title);
            Calendar modified = entry1.getProperty("cm:modified").getDate();
            System.out.println("Entry 1 Last modified: " + modified.getTime());

            // browse all wiki entries
            System.out.println("Browse results:");
            NodeIterator entries = encyclopedia.getNodes();
            while (entries.hasNext())
            {
                Node entry = entries.nextNode();

                System.out.println(entry.getName());
                System.out.println(entry.getProperty("cm:title").getString());
                System.out.println(entry.getPath());
                if (entry.getPrimaryNodeType().getName().equals("wiki:page"))
                {
                    System.out.println(entry.getProperty("cm:content").getString());
                    Property categoryProperty = entry.getProperty("wiki:category");
                    Value[] categories = categoryProperty.getValues();
                    for (Value category : categories)
                    {
                        System.out.println("Category: " + category.getString());
                    }
                }
            }            

            // perform a search
            System.out.println("Search results:");
            Workspace workspace = session.getWorkspace();
            QueryManager queryManager = workspace.getQueryManager();
            Query query = queryManager.createQuery("//app:company_home/wiki:encyclopedia/*[@cm:title = 'The rose']", Query.XPATH);
            //Query query = queryManager.createQuery("//app:company_home/wiki:encyclopedia/*[jcr:contains(., 'rose')]", Query.XPATH);
            QueryResult result = query.execute();
            NodeIterator it = result.getNodes();
            while (it.hasNext())
            {
                Node n = it.nextNode();
                System.out.println(n.getName());
                System.out.println(n.getProperty("cm:title").getString());
                if (n.getPrimaryNodeType().getName().equals("wiki:page"))
                {
                    System.out.println(n.getProperty("cm:content").getString());
                }
            }            

            // export content
            File outputFile = new File("systemview.xml");
            FileOutputStream out = new FileOutputStream(outputFile);
            session.exportSystemView("/app:company_home/wiki:encyclopedia", out, false, false);
            System.out.println("Encyclopedia exported");
        }
        finally
        {
            trx2.rollback();
        }

        // logout
        session.logout();
    }

}
