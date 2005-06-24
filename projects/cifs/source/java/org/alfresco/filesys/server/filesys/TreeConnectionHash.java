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
package org.alfresco.filesys.server.filesys;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Tree Connection Hash Class
 * <p>
 * Hashtable of TreeConnections for the available disk shared devices. TreeConnections are indexed
 * using the hash of the share name to allow mounts to be persistent across server restarts.
 */
public class TreeConnectionHash
{

    // Share name hash to tree connection

    private Hashtable<Integer, TreeConnection> m_connections;

    /**
     * Class constructor
     */
    public TreeConnectionHash()
    {
        m_connections = new Hashtable<Integer, TreeConnection>();
    }

    /**
     * Return the number of tree connections in the hash table
     * 
     * @return int
     */
    public final int numberOfEntries()
    {
        return m_connections.size();
    }

    /**
     * Add a connection to the list of available connections
     * 
     * @param tree TreeConnection
     */
    public final void addConnection(TreeConnection tree)
    {
        m_connections.put(tree.getSharedDevice().getName().hashCode(), tree);
    }

    /**
     * Delete a connection from the list
     * 
     * @param shareName String
     * @return TreeConnection
     */
    public final TreeConnection deleteConnection(String shareName)
    {
        return (TreeConnection) m_connections.get(shareName.hashCode());
    }

    /**
     * Find a connection for the specified share name
     * 
     * @param shareName String
     * @return TreeConnection
     */
    public final TreeConnection findConnection(String shareName)
    {

        // Get the tree connection for the associated share name

        TreeConnection tree = m_connections.get(shareName.hashCode());

        // Return the tree connection

        return tree;
    }

    /**
     * Find a connection for the specified share name hash code
     * 
     * @param hashCode int
     * @return TreeConnection
     */
    public final TreeConnection findConnection(int hashCode)
    {

        // Get the tree connection for the associated share name

        TreeConnection tree = m_connections.get(hashCode);

        // Return the tree connection

        return tree;
    }

    /**
     * Enumerate the connections
     * 
     * @return Enumeration<TreeConnection>
     */
    public final Enumeration<TreeConnection> enumerateConnections()
    {
        return m_connections.elements();
    }
}
