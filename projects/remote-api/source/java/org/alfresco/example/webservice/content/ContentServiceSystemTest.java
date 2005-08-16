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
package org.alfresco.example.webservice.content;

import javax.xml.rpc.ServiceException;

import junit.framework.AssertionFailedError;

import org.alfresco.example.webservice.BaseWebServiceSystemTest;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ContentServiceSystemTest extends BaseWebServiceSystemTest
{
   private static Log logger = LogFactory.getLog(ContentServiceSystemTest.class);
   
   private ContentServiceSoapBindingStub contentSvc;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      try 
      {
         EngineConfiguration config = new FileProvider(getResourcesDir(), "client-deploy.wsdd");
         this.contentSvc = (ContentServiceSoapBindingStub)new ContentServiceLocator(config).getContentService();
      }
      catch (ServiceException jre) 
      {
         if (jre.getLinkedCause() != null)
         {
            jre.getLinkedCause().printStackTrace();
         }
         
         throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
      }
      
      assertNotNull("authSvc is null", this.contentSvc);
      
      // Time out after a minute
      this.contentSvc.setTimeout(60000);
   }

   /**
    * Tests the exists service method
    * 
    * @throws Exception
    */
   public void testExists() throws Exception
   {
      try
      {
         this.contentSvc.exists(null);
         fail("The exists method should have thrown a ContentFault");
      }
      catch (ContentFault cf)
      {
         // we expected this
      }
   }
}
