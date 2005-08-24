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
package org.alfresco.repo.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.UserTransaction;

import org.alfresco.repo.webservice.axis.QueryConfigHandler;
import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Reference;
import org.alfresco.repo.webservice.types.Store;
import org.alfresco.repo.webservice.types.StoreEnum;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.transaction.TransactionService;
import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Helper class used by the web services
 * 
 * @author gavinc
 */
public class Utils
{
   public static final String REPOSITORY_SERVICE_NAMESPACE = "http://www.alfresco.org/ws/service/repository/1.0"; 
   
   private Utils()
   {
      // don't allow construction
   }
   
   /**
    * Converts the web service Store type to a StoreRef used by the repository
    * 
    * @param store The Store to convert
    * @return The converted StoreRef
    */
   public static StoreRef convertToStoreRef(Store store)
   {
      return new StoreRef(store.getScheme().getValue(), store.getAddress());
   }
   
   /**
    * Converts the given Reference web service type into a repository NodeRef
    *  
    * @param ref The Reference to convert
    * @return The NodeRef representation of the Reference
    */
   public static NodeRef convertToNodeRef(Reference ref)
   {
      // TODO: Also support creation from the path
      if (ref.getPath() != null)
      {
         throw new IllegalArgumentException("Paths in References are not supported yet!");
      }
      
      return new NodeRef(convertToStoreRef(ref.getStore()), ref.getUuid());
   }
   
   /**
    * Converts the given repository NodeRef object into a web service Reference type
    * 
    * @param node The node to create a Reference for
    * @return The Reference
    */
   public static Reference convertToReference(NodeRef node)
   {
      Reference ref = new Reference();
      Store store = new Store(StoreEnum.fromValue(node.getStoreRef().getProtocol()), 
            node.getStoreRef().getIdentifier());
      ref.setStore(store);
      ref.setUuid(node.getId());
      return ref;
   }
   
   /**
    * Resolves the given predicate into a list of NodeRefs that can be acted upon
    * 
    * @param predicate The predicate passed from the client
    * @return A List of NodeRef objects
    */
   public static List<NodeRef> resolvePredicate(Predicate predicate)
   {
      if (predicate.getQuery() != null)
      {
         throw new IllegalArgumentException("Queries in predicates are not supported yet!");
      }
      
      Reference[] nodes = predicate.getNodes();
      ArrayList<NodeRef> nodeRefs = new ArrayList<NodeRef>(nodes.length);

      for (int x = 0; x < nodes.length; x++)
      {
         nodeRefs.add(convertToNodeRef(nodes[x]));
      }
      
      return nodeRefs;
   }
   
   /**
    * Returns the current Spring WebApplicationContext object 
    * 
    * @param msgContext SOAP message context
    * @return The Spring WebApplicationContext
    */
   public static WebApplicationContext getSpringContext(MessageContext msgContext)
   {
      // get hold of the web application context via the message context
      HttpServletRequest req = (HttpServletRequest)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
      ServletContext servletCtx = req.getSession().getServletContext();
      return WebApplicationContextUtils.getRequiredWebApplicationContext(servletCtx);
   }
   
   /**
    * Returns a UserTransaction that can be used within a service call
    * 
    * @param msgContext SOAP message context
    * @return a UserTransaction
    */
   public static UserTransaction getUserTransaction(MessageContext msgContext)
   {
      // get the service regsistry
      ServiceRegistry svcReg = (ServiceRegistry)getSpringContext(msgContext).
         getBean(ServiceRegistry.SERVICE_REGISTRY);
      
      TransactionService transactionService = svcReg.getTransactionService();
      return transactionService.getUserTransaction();
   }
   
   /**
    * Returns the value of the <code>fetchSize</code> from the QueryConfiguration
    * SOAP header (if present)
    * 
    * @param msgContext The SOAP MessageContext
    * @return The current batch size or -1 if the header is not present
    */
   public static int getBatchSize(MessageContext msgContext)
   {
      int batchSize = -1;
      
      Integer batchConfigSize = (Integer)MessageContext.getCurrentContext().getProperty(QueryConfigHandler.ALF_FETCH_SIZE);
      if (batchConfigSize != null)
      {
         batchSize = batchConfigSize.intValue();
      }
      
      return batchSize;
   }
}
