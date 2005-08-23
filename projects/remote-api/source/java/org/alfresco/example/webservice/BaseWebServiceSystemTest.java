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
package org.alfresco.example.webservice;

import javax.xml.rpc.ServiceException;

import junit.framework.AssertionFailedError;

import org.alfresco.example.webservice.authentication.AuthenticationResult;
import org.alfresco.example.webservice.authentication.AuthenticationServiceLocator;
import org.alfresco.example.webservice.authentication.AuthenticationServiceSoapBindingStub;
import org.alfresco.example.webservice.types.Store;
import org.alfresco.example.webservice.types.StoreEnum;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.util.BaseTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for all web service system tests that need to authenticate.
 * The setUp method calls the AuthenticationService and authenticates as admin/admin,
 * the returned ticket is then stored in <code>TicketHolder.ticket</code> so that 
 * all subclass implementations can use it to call other services.
 * 
 * @see junit.framework.TestCase#setUp()
 * @author gavinc
 */
public abstract class BaseWebServiceSystemTest extends BaseTest
{
   private static Log logger = LogFactory.getLog(BaseWebServiceSystemTest.class);
   
   private static final String USERNAME = "admin";
   private static final String PASSWORD = "admin";
   
   protected Store STORE = new Store(StoreEnum.workspace, "SpacesStore");
   protected StoreRef STORE_REF = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
   
   /**
    * Calls the AuthenticationService to retrieve a ticket for all tests to use.
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      AuthenticationServiceSoapBindingStub authSvc = null;
      try 
      {
         authSvc = (AuthenticationServiceSoapBindingStub)new AuthenticationServiceLocator().getAuthenticationService();
      }
      catch (ServiceException jre) 
      {
         if (jre.getLinkedCause() != null)
         {
            jre.getLinkedCause().printStackTrace();
         }
         
         throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
      }
      
      assertNotNull("authSvc is null", authSvc);
      
      // Time out after a minute
      authSvc.setTimeout(60000);
      
      // call the authenticate method and retrieve the ticket
      AuthenticationResult result = authSvc.authenticate(USERNAME, PASSWORD);
      assertNotNull("result is null", result);
      String ticket = result.getTicket();
      assertNotNull("ticket is null", ticket);
      TicketHolder.ticket = ticket;
      if (logger.isDebugEnabled())
         logger.debug("Retrieved and stored ticket: " + TicketHolder.ticket);
   }
}
