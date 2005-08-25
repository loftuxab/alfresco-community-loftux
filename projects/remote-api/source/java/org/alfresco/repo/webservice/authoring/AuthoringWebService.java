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
package org.alfresco.repo.webservice.authoring;

import java.rmi.RemoteException;

import org.alfresco.repo.webservice.types.ContentFormat;
import org.alfresco.repo.webservice.types.NamedValue;
import org.alfresco.repo.webservice.types.ParentReference;
import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Reference;
import org.alfresco.repo.webservice.types.VersionHistory;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Web service implementation of the AuthoringService.
 * The WSDL for this service can be accessed from http://localhost:8080/alfresco/api/AuthoringService?wsdl
 *  
 * @author gavinc
 */
public class AuthoringWebService implements AuthoringServiceSoapPort
{
   private static Log logger = LogFactory.getLog(AuthoringWebService.class);
   
   private NodeService nodeService;
   private ContentService contentService;
   
   /**
    * Sets the instance of the NodeService to be used
    * 
    * @param nodeService The NodeService
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * Sets the ContentService instance to use
    * 
    * @param contentSvc The ContentService
    */
   public void setContentService(ContentService contentSvc)
   {
      this.contentService = contentSvc;
   }

   /**
    * @see org.alfresco.repo.webservice.authoring.AuthoringServiceSoapPort#checkout(org.alfresco.repo.webservice.types.Predicate, org.alfresco.repo.webservice.types.ParentReference)
    */
   public CheckoutResult checkout(Predicate items, ParentReference destination) throws RemoteException, AuthoringFault
   {
      throw new AuthoringFault(1, "checkout() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.authoring.AuthoringServiceSoapPort#checkin(org.alfresco.repo.webservice.types.Predicate, org.alfresco.repo.webservice.types.NamedValue[], boolean)
    */
   public CheckinResult checkin(Predicate items, NamedValue[] comments, boolean keepCheckedOut) throws RemoteException, AuthoringFault
   {
      throw new AuthoringFault(1, "checkin() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.authoring.AuthoringServiceSoapPort#checkinExternal(org.alfresco.repo.webservice.types.Predicate, org.alfresco.repo.webservice.types.NamedValue[], boolean, org.alfresco.repo.webservice.types.ContentFormat, byte[])
    */
   public Reference checkinExternal(Predicate items, NamedValue[] comments, boolean keepCheckedOut, ContentFormat format, byte[] content) throws RemoteException, AuthoringFault
   {
      throw new AuthoringFault(1, "checkinExternal() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.authoring.AuthoringServiceSoapPort#cancelCheckout(org.alfresco.repo.webservice.types.Predicate)
    */
   public CancelCheckoutResult cancelCheckout(Predicate items) throws RemoteException, AuthoringFault
   {
      throw new AuthoringFault(1, "cancelCheckout() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.authoring.AuthoringServiceSoapPort#lock(org.alfresco.repo.webservice.types.Predicate, boolean, org.alfresco.repo.webservice.authoring.LockType)
    */
   public Reference[] lock(Predicate items, boolean lockChildren, LockType lockType) throws RemoteException, AuthoringFault
   {
      throw new AuthoringFault(1, "lock() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.authoring.AuthoringServiceSoapPort#unlock(org.alfresco.repo.webservice.types.Predicate, boolean)
    */
   public Reference[] unlock(Predicate items, boolean unlockChildren) throws RemoteException, AuthoringFault
   {
      throw new AuthoringFault(1, "unlock() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.authoring.AuthoringServiceSoapPort#getLockStatus(org.alfresco.repo.webservice.types.Predicate)
    */
   public LockStatus[] getLockStatus(Predicate items) throws RemoteException, AuthoringFault
   {
      throw new AuthoringFault(1, "getLockStatus() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.authoring.AuthoringServiceSoapPort#createVersion(org.alfresco.repo.webservice.types.Predicate, org.alfresco.repo.webservice.types.NamedValue[], boolean)
    */
   public VersionResult createVersion(Predicate items, NamedValue[] comments, boolean versionChildren) throws RemoteException, AuthoringFault
   {
      throw new AuthoringFault(1, "createVersion() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.authoring.AuthoringServiceSoapPort#getVersionHistory(org.alfresco.repo.webservice.types.Reference)
    */
   public VersionHistory getVersionHistory(Reference node) throws RemoteException, AuthoringFault
   {
      throw new AuthoringFault(1, "getVersionHistory() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.authoring.AuthoringServiceSoapPort#revertVersion(org.alfresco.repo.webservice.types.Reference, java.lang.String)
    */
   public void revertVersion(Reference node, String versionLabel) throws RemoteException, AuthoringFault
   {
      throw new AuthoringFault(1, "revertVersion() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.authoring.AuthoringServiceSoapPort#deleteAllVersions(org.alfresco.repo.webservice.types.Reference)
    */
   public VersionHistory deleteAllVersions(Reference node) throws RemoteException, AuthoringFault
   {
      throw new AuthoringFault(1, "deleteAllVersions() is not implemented yet!");
   }
}
