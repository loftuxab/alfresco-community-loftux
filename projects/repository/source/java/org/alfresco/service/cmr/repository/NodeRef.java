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
package org.alfresco.service.cmr.repository;

import java.io.Serializable;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Reference to a node
 * 
 * @author Derek Hulley
 */
public class NodeRef implements EntityRef, Serializable
{
   
    private static final long serialVersionUID = 3760844584074227768L;
    private static final String URI_FILLER = "/";
    
    private StoreRef storeRef;
    private String id;

    /**
     * Construct a Node Reference from a Store Reference and Node Id
     * 
     * @param storeRef store reference
     * @param id the manually assigned identifier of the node
     */
    public NodeRef(StoreRef storeRef, String id)
    {
        if (storeRef == null)
        {
            throw new IllegalArgumentException(
                    "Store reference may not be null");
        }
        if (id == null)
        {
            throw new IllegalArgumentException("Node id may not be null");
        }

        this.storeRef = storeRef;
        this.id = id;
    }

    /**
     * Construct a Node Reference from a string representation of a Node Reference.
     * <p>
     * The string representation of a Node Reference is as follows:
     * <p>
     * <pre><storeref>/<nodeId></pre>
     * 
     * @param nodeRef the string representation of a node ref
     */
    public NodeRef(String nodeRef)
    {
        int lastForwardSlash = nodeRef.lastIndexOf('/');
        if(lastForwardSlash == -1)
        {
            throw new AlfrescoRuntimeException("Invalid node ref - does not contain forward slash");
        }
         this.storeRef = new StoreRef(nodeRef.substring(0, lastForwardSlash));
         this.id = nodeRef.substring(lastForwardSlash+1);
    }

    public String toString()
    {
        return storeRef.toString() + URI_FILLER + id;
    }

    /**
     * Override equals for this ref type
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof NodeRef)
        {
            NodeRef that = (NodeRef) obj;
            return (this.id.equals(that.id)
                    && this.storeRef.equals(that.storeRef));
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Hashes on ID alone.  As the number of copies of a particular node will be minimal, this is acceptable
     */
    public int hashCode()
    {
        return id.hashCode();
    }

    /**
     * @return The StoreRef part of this reference
     */
    public final StoreRef getStoreRef()
    {
        return storeRef;
    }

    /**
     * @return The Node Id part of this reference
     */
    public final String getId()
    {
        return id;
    }
}