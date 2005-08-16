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

import java.util.List;

import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Store;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * Helper class used by the web services
 * 
 * @author gavinc
 */
public class Utils
{   
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
    * Resolves the given predicate into a list of NodeRefs that can be acted upon
    * 
    * @param predicate The predicate passed from the client
    * @return A List of NodeRef objects
    */
   public List<NodeRef> resolvePredicate(Predicate predicate)
   {
      return null;
   }
}
