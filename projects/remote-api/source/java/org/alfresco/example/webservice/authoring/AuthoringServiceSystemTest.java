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
package org.alfresco.example.webservice.authoring;

import javax.xml.rpc.ServiceException;

import junit.framework.AssertionFailedError;

import org.alfresco.example.webservice.BaseWebServiceSystemTest;
import org.alfresco.example.webservice.content.ContentServiceLocator;
import org.alfresco.example.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.example.webservice.types.Content;
import org.alfresco.example.webservice.types.ContentFormat;
import org.alfresco.example.webservice.types.ParentReference;
import org.alfresco.example.webservice.types.Predicate;
import org.alfresco.example.webservice.types.Reference;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuthoringServiceSystemTest extends BaseWebServiceSystemTest
{
   private static Log logger = LogFactory.getLog(AuthoringServiceSystemTest.class);
   
   private static String versionedNodeId;
   private static final String INITIAL_VERSION_CONTENT = "Content of the initial version";
   private static final String FIRST_VERSION_CONTENT = "Content of the first version";
   private static final String SECOND_VERSION_CONTENT = "The content for the second version is completely different";
   private static final String THIRD_VERSION_CONTENT = "The third version is short!";
   
   private AuthoringServiceSoapBindingStub authoringService;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      try 
      {
         EngineConfiguration config = new FileProvider(getResourcesDir(), "client-deploy.wsdd");
         this.authoringService = (AuthoringServiceSoapBindingStub)new AuthoringServiceLocator(config).getAuthoringService();
      }
      catch (ServiceException jre) 
      {
         if (jre.getLinkedCause() != null)
         {
            jre.getLinkedCause().printStackTrace();
         }
         
         throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
      }
      
      assertNotNull("authoringService is null", this.authoringService);
      
      // Time out after a minute
      this.authoringService.setTimeout(60000);
   }
   
   public void testCreateNode() throws Exception
   {
      ContentServiceSoapBindingStub contentService = null;
      try 
      {
         EngineConfiguration config = new FileProvider(getResourcesDir(), "client-deploy.wsdd");
         contentService = (ContentServiceSoapBindingStub)new ContentServiceLocator(config).getContentService();
         assertNotNull("contentService is null", contentService);
         contentService.setTimeout(60000);
      }
      catch (Exception e) 
      {
         fail("Could not instantiate the content service" + e.toString());
      }
      
      // get the root node (hard code for now until we have a way to query for the root node)
      ParentReference root = new ParentReference();
      root.setStore(STORE);
      root.setUuid(companyHomeId);
      
      String mimetype = "text/plain";
      Content content = contentService.create(root, "version-test.txt", new ContentFormat(mimetype, "UTF-8"), 
            INITIAL_VERSION_CONTENT.getBytes());
      assertNotNull("returned content should not be null", content);
      assertNotNull("format should not be null", content.getFormat());
      assertEquals("Mimetype should match what was sent", mimetype, content.getFormat().getMimetype());
      versionedNodeId = content.getReference().getUuid();
      logger.debug("created new content with id: " + versionedNodeId);
   }
   
   /**
    * Tests the checkout service method
    * 
    * @throws Exception
    */
   public void testCheckout() throws Exception
   {
      Reference ref = new Reference();
      ref.setStore(STORE);
      ref.setUuid(versionedNodeId);
      Predicate predicate = new Predicate();
      predicate.setNodes(new Reference[] {ref});
      
      CheckoutResult result = this.authoringService.checkout(predicate, null);
      assertNotNull("The result should not be null", result);
      assertEquals("There should only be 1 original reference", 1, result.getOriginals().length);
      assertEquals("There should only be 1 working copy reference", 1, result.getWorkingCopies().length);
   }
   
   /**
    * Tests the checkout service method passing a destination for the working copy
    * 
    * @throws Exception
    */
   public void xtestCheckoutWithDestination() throws Exception
   {
      Reference ref = new Reference();
      ref.setStore(STORE);
      ref.setUuid(versionedNodeId);
      Predicate predicate = new Predicate();
      predicate.setNodes(new Reference[] {ref});
      
      // define the parent reference as a path based reference
      ParentReference checkoutLocation = new ParentReference();
      checkoutLocation.setStore(STORE);
      checkoutLocation.setPath("//*[@cm:name = 'Alfresco Tutorial']");
      
      CheckoutResult result = this.authoringService.checkout(predicate, checkoutLocation);
      assertNotNull("The result should not be null", result);
      assertEquals("There should only be 1 original reference", 1, result.getOriginals().length);
      assertEquals("There should only be 1 working copy reference", 1, result.getWorkingCopies().length);
   }
   
   /**
    * Tests the checkin service method
    * 
    * @throws Exception
    */
   public void testCheckin() throws Exception
   {
      try
      {
         this.authoringService.checkin(null, null, false);
         fail("This method should have thrown an authoring fault");
      }
      catch (AuthoringFault af)
      {
         // expected to get this
      }
   }

   /**
    * Tests the checkinExternal service method
    * 
    * @throws Exception
    */
   public void testCheckinExternal() throws Exception
   {
      try
      {
         this.authoringService.checkinExternal(null, null, false, null, null);
         fail("This method should have thrown an authoring fault");
      }
      catch (AuthoringFault af)
      {
         // expected to get this
      }
   }

   /**
    * Tests the cancelCheckout service method
    * 
    * @throws Exception
    */
   public void testCancelCheckout() throws Exception
   {
      try
      {
         this.authoringService.cancelCheckout(null);
         fail("This method should have thrown an authoring fault");
      }
      catch (AuthoringFault af)
      {
         // expected to get this
      }
   }

   /**
    * Tests the lock service method
    * 
    * @throws Exception
    */
   public void testLock() throws Exception
   {
      try
      {
         this.authoringService.lock(null, false, null);
         fail("This method should have thrown an authoring fault");
      }
      catch (AuthoringFault af)
      {
         // expected to get this
      }
   }

   /**
    * Tests the unlock service method
    * 
    * @throws Exception
    */
   public void testUnlock() throws Exception
   {
      try
      {
         this.authoringService.unlock(null, false);
         fail("This method should have thrown an authoring fault");
      }
      catch (AuthoringFault af)
      {
         // expected to get this
      }
   }

   /**
    * Tests the getLockStatus service method
    * 
    * @throws Exception
    */
   public void testGetLockStatus() throws Exception
   {
      try
      {
         this.authoringService.getLockStatus(null);
         fail("This method should have thrown an authoring fault");
      }
      catch (AuthoringFault af)
      {
         // expected to get this
      }
   }

   /**
    * Tests the createVersion service method
    * 
    * @throws Exception
    */
   public void testCreateVersion() throws Exception
   {
      try
      {
         this.authoringService.createVersion(null, null, false);
         fail("This method should have thrown an authoring fault");
      }
      catch (AuthoringFault af)
      {
         // expected to get this
      }
   }

   /**
    * Tests the getVersionHistory service method
    * 
    * @throws Exception
    */
   public void testGetVersionHistory() throws Exception
   {
      try
      {
         this.authoringService.getVersionHistory(null);
         fail("This method should have thrown an authoring fault");
      }
      catch (AuthoringFault af)
      {
         // expected to get this
      }
   }

   /**
    * Tests the revertVersion service method
    * 
    * @throws Exception
    */
   public void testRevertVersion() throws Exception
   {
      try
      {
         this.authoringService.revertVersion(null, null);
         fail("This method should have thrown an authoring fault");
      }
      catch (AuthoringFault af)
      {
         // expected to get this
      }
   }

   /**
    * Tests the deleteAllVersions service method
    * 
    * @throws Exception
    */
   public void testDeleteAllVersions() throws Exception
   {
      try
      {
         this.authoringService.deleteAllVersions(null);
         fail("This method should have thrown an authoring fault");
      }
      catch (AuthoringFault af)
      {
         // expected to get this
      }
   }
}
