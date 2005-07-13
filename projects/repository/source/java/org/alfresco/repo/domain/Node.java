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

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Specific instances of nodes are unique, but may share GUIDs across stores.
 * 
 * @author Derek Hulley
 */
public interface Node
{
    /**
     * @return Returns the unique key for this node
     */
    public NodeKey getKey();

    /**
     * @param key the unique node key
     */
    public void setKey(NodeKey key);
    
    public Store getStore();
    
    public void setStore(Store store);
    
    public QName getTypeQName();
    
    public void setTypeQName(QName qname);
    
    public Set<QName> getAspects();
    
    /**
     * @return Returns all the regular associations for which this node is a target 
     */
    public Set<NodeAssoc> getSourceNodeAssocs();

    /**
     * @return Returns all the regular associations for which this node is a source 
     */
    public Set<NodeAssoc> getTargetNodeAssocs();

    public Set<ChildAssoc> getChildAssocs();

    public Set<ChildAssoc> getParentAssocs();

    public Map<String, Serializable> getProperties();

    /**
     * Convenience method to get the reference to the node
     * 
     * @return Returns the reference to this node
     */
    public NodeRef getNodeRef();
}
