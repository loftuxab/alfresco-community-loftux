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
package org.alfresco.sample;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.alfresco.jcr.api.JCRNodeRef;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



/**
 * Simple client example demonstrating the use of the Alfresco JCR (JSR-170) API.
 * 
 * The client creates a content node in the "Company Home" folder.  The content
 * may be viewed and operated on within the Alfresco Web Client.  Note: the web client
 * will need to be re-started after executing this sample to see the changes in
 * effect.
 *
 * This client demonstrates the "Embedded Repository" deployment option as described
 * in the Alfresco Respotiory Architecture docucment - 
 * http://wiki.alfresco.com/wiki/Alfresco_Repository_Architecture
 */
public class FirstJCRClient
{
	
    public static void main(String[] args)
	    throws Exception
	{
	    // access the Alfresco JCR Repository (here it's via programmatic approach, but it could also be injected)
	    ApplicationContext context = new ClassPathXmlApplicationContext("classpath:alfresco/application-context.xml");
	    Repository repository = (Repository)context.getBean("JCR.Repository");
	
	    // login to workspace (here we rely on the default workspace defined by JCR.Repository bean)
	    Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
	
	    try
	    {
	        // first, access the company home
	        Node rootNode = session.getRootNode();
	        Node companyHome = rootNode.getNode("app:company_home");
	
	        // create the content node
	        String name = "Created by JCR at " + System.currentTimeMillis();
	        Node content = companyHome.addNode("cm:" + name, "cm:content");
	        content.setProperty("cm:name", name);

	        // add titled aspect (for Web Client display)
	        content.addMixin("cm:titled");
	        content.setProperty("cm:title", name);
	        content.setProperty("cm:description", name);

	        //
	        // write some content to new node
	        //
	        content.setProperty("cm:content", "The quick brown fox jumps over the lazy dog");
	        
	        //
	        // To set the content mime type, we need to use an Alfresco native service
	        // as there isn't an equivalent call in JCR
	        //
	        setMimeType(context, content, MimetypeMap.MIMETYPE_TEXT_PLAIN);

	        // save changes
	        session.save();
	    }
	    finally
	    {
	        session.logout();
	        System.exit(0);
	    }
	}    

    
    /**
     * Demonstrates the mixed use of JCR API calls and Alfresco Foundation API calls.
     * 
     * Here, the Foundation API is used to set the mimetype of the content.
     * 
     * @param context  application context
     * @param node   the JCR Node to adjust
     * @param mimeType  the mimetype to set
     * @throws RepositoryException
     */
	private static void setMimeType(ApplicationContext context, Node node, String mimeType)
	    throws RepositoryException
	{
		// retrieve service registry
        ServiceRegistry serviceRegistry = (ServiceRegistry) context.getBean(ServiceRegistry.SERVICE_REGISTRY);
        NodeService nodeService = serviceRegistry.getNodeService();
		
	    // convert the JCR Node to an Alfresco Node Reference
	    NodeRef nodeRef = JCRNodeRef.getNodeRef(node);
	
	    // retrieve the Content Property (represented as a ContentData object in Alfresco)
	    ContentData content = (ContentData)nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
	    
	    // update the Mimetype
	    content = ContentData.setMimetype(content, mimeType);
	    nodeService.setProperty(nodeRef, ContentModel.PROP_CONTENT, content);
	}

}
