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
   
   // ********************************************************************
   //
   // NOTE: The rootId and companyHomeId variables have to be set for your
   //       database for the tests to run successfully, once there is a way
   //       to query for the root node this can be removed.
   //       Perform a MySQL query to get the ids for the rootId execute:
   //       "select * from node where type_local_name = 'store_root';" and 
   //       pick the row with an identifier of 'SpacesStore'.
   //       For the company home id execute the following query:
   //       "select * from child_assoc where parent_guid = '<your-root-id>' and local_name = 'Company_Home';"
   //
   // ********************************************************************
   
   protected static String rootId = "fddc4ffb-1ace-11da-b3ba-e981aa43b126";
   protected static String companyHomeId = "fe289b3d-1ace-11da-b3ba-e981aa43b126";
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
