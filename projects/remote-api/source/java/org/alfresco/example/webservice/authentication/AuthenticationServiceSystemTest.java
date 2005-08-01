/**
 * AuthenticationServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.alfresco.example.webservice.authentication;

import javax.xml.rpc.ServiceException;

import junit.framework.AssertionFailedError;

import org.alfresco.util.BaseTest;

public class AuthenticationServiceSystemTest extends BaseTest 
{
   private AuthenticationServiceSoapBindingStub binding;
   
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      try 
      {
         this.binding = (AuthenticationServiceSoapBindingStub)new AuthenticationServiceLocator().getAuthenticationService();
      }
      catch (ServiceException jre) 
      {
         if (jre.getLinkedCause() != null)
         {
            jre.getLinkedCause().printStackTrace();
         }
         
         throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
      }
      
      assertNotNull("binding is null", this.binding);
      
      // Time out after a minute
      binding.setTimeout(60000);
   }

   /**
    * Tests whether the authentication service is working correctly
    * 
    * @throws Exception
    */
   public void testSuccessfulLogin() throws Exception 
   {
      try 
      {
         AuthenticationResult value = this.binding.authenticate("admin", "admin");
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
         AuthenticationResult result = this.binding.authenticate("wrong", "credentials");
         fail("The credentials are incorrect so an AuthenticationFault should have been thrown");
      }
      catch (AuthenticationFault error) 
      {
         // we expected this
      }
   }
}
