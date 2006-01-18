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
package org.alfresco.webservice.test;

import junit.framework.TestCase;

import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.types.StoreEnum;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for all web service system tests that need to authenticate. The
 * setUp method calls the AuthenticationService and authenticates as
 * admin/admin, the returned ticket is then stored in
 * <code>TicketHolder.ticket</code> so that all subclass implementations can
 * use it to call other services.
 * 
 * @see junit.framework.TestCase#setUp()
 * @author gavinc
 */
public abstract class BaseWebServiceSystemTest extends TestCase
{
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(BaseWebServiceSystemTest.class);

    protected static final String USERNAME = "admin";
    protected static final String PASSWORD = "admin";
    
    public static final String FOLDER_NAME = "test folder";
    protected static final String CONTENT_NAME = "test content";
    
    protected static final String TEST_CONTENT = "This is some test content.  This is some test content.";
    
    protected static Store store;
    protected static Reference rootReference;    
    protected static Reference contentReference;
    protected static Reference folderReference;
    
    protected RepositoryServiceSoapBindingStub repositoryService;
    protected ContentServiceSoapBindingStub contentService;
    
    public BaseWebServiceSystemTest()
    {
        this.repositoryService = WebServiceFactory.getRepositoryService();
        this.contentService = WebServiceFactory.getContentService();
    }
    
    /**
     * Calls the AuthenticationService to retrieve a ticket for all tests to
     * use.
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();        
        
        // Start a new session
        AuthenticationUtils.startSession(USERNAME, PASSWORD);
        
        // Create the store
        if (BaseWebServiceSystemTest.store == null)
        {
            // Create the store
            BaseWebServiceSystemTest.store = WebServiceFactory.getRepositoryService().createStore(StoreEnum.workspace, "Test" + System.currentTimeMillis());
            
            // Get the root node reference
            Predicate predicate = new Predicate(null, BaseWebServiceSystemTest.store, null);
            Node[] nodes = WebServiceFactory.getRepositoryService().get(predicate);
            if (nodes.length == 1)
            {
                BaseWebServiceSystemTest.rootReference = nodes[0].getReference();
            }
            else
            {
                throw new Exception("Unable to get the root not of the created sotre.");
            }

            // Create test content
            ParentReference contentParentRef = new ParentReference(Constants.ASSOC_CHILDREN, "{test}testContent");
            contentParentRef.setStore(BaseWebServiceSystemTest.store);
            contentParentRef.setUuid(BaseWebServiceSystemTest.rootReference.getUuid());
            NamedValue[] contentProperties = new NamedValue[]{new NamedValue(Constants.PROP_NAME, CONTENT_NAME)};
            CMLCreate createContent = new CMLCreate("testContent", contentParentRef, Constants.TYPE_CONTENT, contentProperties);
            
            // Create test folder
            ParentReference folderParentRef = new ParentReference(Constants.ASSOC_CHILDREN, "{test}testFolder");
            folderParentRef.setStore(BaseWebServiceSystemTest.store);
            folderParentRef.setUuid(BaseWebServiceSystemTest.rootReference.getUuid());
            NamedValue[] folderProperties = new NamedValue[]{new NamedValue(Constants.PROP_NAME, FOLDER_NAME)};
            CMLCreate createFolder = new CMLCreate("testFolder", folderParentRef, Constants.TYPE_FOLDER, folderProperties);
            
            CML cml = new CML();
            cml.setCreate(new CMLCreate[]{createContent, createFolder});
            
            UpdateResult[] updateResult = this.repositoryService.update(cml);
            BaseWebServiceSystemTest.contentReference = updateResult[0].getDestination();
            BaseWebServiceSystemTest.folderReference = updateResult[1].getDestination();
            
            // Write the test content to the reference
            this.contentService.write(BaseWebServiceSystemTest.contentReference, Constants.PROP_CONTENT, TEST_CONTENT.getBytes(), new ContentFormat(Constants.MIMETYPE_TEXT_PLAIN, "UTF-8"));
        }        
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        // End the current session
        AuthenticationUtils.endSession();
    }

      
    protected ParentReference getFolderParentReference(String assocName)
    {
        ParentReference parentReference = new ParentReference();
        parentReference.setStore(BaseWebServiceSystemTest.store);
        parentReference.setUuid(BaseWebServiceSystemTest.folderReference.getUuid());
        parentReference.setAssociationType(Constants.ASSOC_CONTAINS);
        parentReference.setChildName(assocName);
        return parentReference;
    }

    protected Reference createContentAtRoot(String name, String contentValue) throws Exception
    {
        ParentReference parentRef = new ParentReference();
        parentRef.setStore(BaseWebServiceSystemTest.store);
        parentRef.setUuid(BaseWebServiceSystemTest.rootReference.getUuid());
        parentRef.setAssociationType(Constants.ASSOC_CHILDREN);
        parentRef.setChildName("{test}test" + System.currentTimeMillis());
        
        NamedValue[] properties = new NamedValue[]{new NamedValue(Constants.PROP_NAME, name)};
        CMLCreate create = new CMLCreate("1", parentRef, Constants.TYPE_CONTENT, properties);
        CML cml = new CML();
        cml.setCreate(new CMLCreate[]{create});
        UpdateResult[] result = this.repositoryService.update(cml);     
        
        Reference newContentNode = result[0].getDestination();
        
        Content content = this.contentService.write(newContentNode, Constants.PROP_CONTENT, contentValue.getBytes(), new ContentFormat("text/plain", "UTF-8"));
                
        assertNotNull(content);
        assertNotNull(content.getFormat());
        assertEquals("text/plain", content.getFormat().getMimetype());
        
        return content.getNode();        
    }    
    
    protected Predicate convertToPredicate(Reference reference)
    {
        Predicate predicate = new Predicate();
        predicate.setNodes(new Reference[] {reference});
        return predicate;
    }

}
