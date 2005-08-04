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
package org.alfresco.example.webservice.repository;

import javax.xml.rpc.ServiceException;

import junit.framework.AssertionFailedError;

import org.alfresco.example.webservice.BaseWebServiceSystemTest;
import org.alfresco.example.webservice.types.Store;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RepositoryServiceSystemTest extends BaseWebServiceSystemTest
{
   private static Log logger = LogFactory.getLog(RepositoryServiceSystemTest.class);
   
   private RepositoryServiceSoapBindingStub repSvc;
   
   /**
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      try 
      {
         EngineConfiguration config = new FileProvider(getResourcesDir(), "client-deploy.wsdd");
         this.repSvc = (RepositoryServiceSoapBindingStub)new RepositoryServiceLocator(config).getRepositoryService();
      }
      catch (ServiceException jre) 
      {
         if (jre.getLinkedCause() != null)
         {
            jre.getLinkedCause().printStackTrace();
         }
         
         throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
      }
      
      assertNotNull("authSvc is null", this.repSvc);
      
      // Time out after a minute
      this.repSvc.setTimeout(60000);
   }

   /**
    * Tests the getStores method
    * 
    * @throws Exception
    */
   public void testGetStores() throws Exception
   {
      Store[] stores = this.repSvc.getStores();
      assertNotNull("stores array should not be null", stores);
      assertTrue("There should be 2 stores", stores.length == 2);
      logger.info("store1 = " + stores[0].getScheme() + ":" + stores[0].getAddress());
      logger.info("store2 = " + stores[1].getScheme() + ":" + stores[1].getAddress());
   }
   
   
}
