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
package org.alfresco.repo.webservice.repository;

import java.rmi.RemoteException;

import org.alfresco.repo.webservice.types.Store;
import org.alfresco.repo.webservice.types.StoreEnum;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * Web service implementation of the RepositoryService.
 * The WSDL for this service can be accessed from http://localhost:8080/web-client/remote-api/RepositoryService?wsdl
 * 
 * @author gavinc
 */
public class RepositoryWebService implements RepositoryServiceSoapPort
{
   private NodeService nodeService;
   
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
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#getStores()
    */
   public Store[] getStores() throws RemoteException, RepositoryFault
   {
      Store store1 = new Store(StoreEnum.workspace, "SpacesStore");
      Store store2 = new Store(StoreEnum.version, "LightweightVersionStore");
      
      return new Store[] {store1, store2};
   }
}
