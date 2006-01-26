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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.alfresco.webservice.authentication.AuthenticationFault;
import org.alfresco.webservice.authentication.AuthenticationResult;
import org.alfresco.webservice.authentication.AuthenticationServiceSoapBindingStub;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * Tests the AuthenticationService by trying to login as admin/admin and  
 * attempting to login with incorrect credentials.
 * 
 * @author gavinc
 */
public class AuthenticationServiceSystemTest extends TestCase 
{
   /**
    * Tests whether the authentication service is working correctly
    * 
    * @throws Exception
    */
   public void testSuccessfulLogin() throws Exception 
   {
      try 
      {
         AuthenticationResult value = WebServiceFactory.getAuthenticationService().startSession("admin", "admin");
         assertNotNull("result must not be null", value);
         System.out.println("ticket = " + value.getTicket());
      }
      catch (AuthenticationFault error) 
      {
         throw new AssertionFailedError("AuthenticationFault Exception caught: " + error);
      }
   }
   
   /**
    * Tests that a failed authentication attempt fails as expected
    * 
    * @throws Exception
    */
   public void testFailedLogin() throws Exception
   {
      try
      {
          WebServiceFactory.getAuthenticationService().startSession("wrong", "credentials");
         fail("The credentials are incorrect so an AuthenticationFault should have been thrown");
      }
      catch (AuthenticationFault error) 
      {
         // we expected this
      }
   }
   
   /**
    * Tests endSession
    * 
    * @throws Exception
    */
   public void testEndSession() throws Exception
   {
       AuthenticationServiceSoapBindingStub authenticationService = WebServiceFactory.getAuthenticationService();
       
       // Create and end a session
       AuthenticationResult result = authenticationService.startSession("admin", "admin");
       authenticationService.endSession(result.getTicket());
       
       try
       {
           // Try and end an invalid session
           authenticationService.endSession("badSessionId");
           fail("An exception should have been thrown since we are trying to end an invalid session");
       }
       catch (Throwable exception)
       {
           // Web are expecting this exception
       }
   }
}
