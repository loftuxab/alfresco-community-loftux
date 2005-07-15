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
package org.alfresco.repo.domain;

import org.alfresco.repo.domain.StoreKey;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * Represents a store entity
 * 
 * @author Derek Hulley
 */
public interface Store
{
    /**
     * @return Returns the key for the class
     */
    public StoreKey getKey();

    /**
     * @param key the key uniquely identifying this store
     */
    public void setKey(StoreKey key);
    
    /**
     * @return Returns the root of the store
     */
    public Node getRootNode();
    
    /**
     * @param rootNode mandatory association to the root of the store
     */
    public void setRootNode(Node rootNode);
    
    /**
     * Convenience method to access the reference
     * @return Returns the reference to the store
     */
    public StoreRef getStoreRef();
}
