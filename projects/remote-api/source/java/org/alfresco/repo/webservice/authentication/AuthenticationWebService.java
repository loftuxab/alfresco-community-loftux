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
package org.alfresco.repo.webservice.authentication;

import java.rmi.RemoteException;

import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Web service implementation of the AuthenticationService.
 * The WSDL for this service can be accessed from http://localhost:8080/alfresco/api/AuthenticationService?wsdl
 *  
 * @author gavinc
 */
public class AuthenticationWebService implements AuthenticationServiceSoapPort
{
   private static Log logger = LogFactory.getLog(AuthenticationWebService.class);
   
   private AuthenticationService authenticationService;
   
   /**
    * Sets the AuthenticationService instance to use
    * 
    * @param authenticationSvc The AuthenticationService
    */
   public void setAuthenticationService(AuthenticationService authenticationSvc)
   {
      this.authenticationService = authenticationSvc;
   }

   /**
    * @see org.alfresco.repo.webservice.authentication.AuthenticationServiceSoapPort#authenticate(java.lang.String, java.lang.String)
    */
   public AuthenticationResult authenticate(String username, String password) throws RemoteException, AuthenticationFault
   {
      // TODO: This needs to be configured or passed in as a parameter
      StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
      
      try
      {
         UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
         this.authenticationService.authenticate(storeRef, token);
         String ticket = this.authenticationService.getCurrentTicket();
         
         if (logger.isDebugEnabled())
            logger.debug("Issued ticket '" + ticket + "' for '" + username + "'");
         
         return new AuthenticationResult(username, ticket);
      }
      catch (AuthenticationException ae)
      {
         throw new AuthenticationFault(100, ae.getMessage());
      }
      catch (Throwable e)
      {
         throw new AuthenticationFault(0, e.getMessage());
      }
   }
}
