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
package org.alfresco.repo.webservice.node;

import org.alfresco.repo.webservice.QueryResult;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * Public version of the NodeService interface used to expose the Node Service as a web service  
 * 
 * @author gavinc
 */
public class NodeWebService
{
   private NodeService nodeService;
   
   /**
    * Sets the node service instance to use in this web service
    * 
    * @param nodeService The NodeService implmentation
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }

   /**
     * @see org.alfresco.service.cmr.repository.NodeService#createStore(String, String)
     */
    public StoreRef createStore(String protocol, String identifier) // throws NodeServiceFault;
    {
       return this.nodeService.createStore(protocol, identifier);
    }
    
    /**
     * @see org.alfresco.service.cmr.repository.NodeService#exists(StoreRef)
     */
    public boolean storeExists(StoreRef storeRef)
    {
       return this.nodeService.exists(storeRef);
    }
    
    /**
     * @see org.alfresco.service.cmr.repository.NodeService#getRootNode(StoreRef)
     */
    public NodeRef getRootNode(StoreRef storeRef) // throws NodeServiceFault;
    {
       return this.nodeService.getRootNode(storeRef);
    }
    
    public QueryResult getChildren(StoreRef storeRef, String id)
    {
       NodeRef node1 = new NodeRef(storeRef, "1");
       NodeRef node2 = new NodeRef(storeRef, "2");
       NodeRef node3 = new NodeRef(storeRef, "3");
       NodeRef node4= new NodeRef(storeRef, "4");
       
       NodeRef[] nodes = new NodeRef[] {node1, node2, node3, node4};
       
       return new QueryResult(nodes);
    }
}
