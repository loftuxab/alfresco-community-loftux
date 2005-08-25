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
package org.alfresco.example.webservice.authoring;

import javax.xml.rpc.ServiceException;

import junit.framework.AssertionFailedError;

import org.alfresco.example.webservice.BaseWebServiceSystemTest;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuthoringServiceSystemTest extends BaseWebServiceSystemTest
{
   private static Log logger = LogFactory.getLog(AuthoringServiceSystemTest.class);
   
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
   
   /**
    * Tests the checkout service method
    * 
    * @throws Exception
    */
   public void testCheckout() throws Exception
   {
      try
      {
         this.authoringService.checkout(null, null);
         fail("This method should have thrown an authoring fault");
      }
      catch (AuthoringFault af)
      {
         // expected to get this
      }
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
