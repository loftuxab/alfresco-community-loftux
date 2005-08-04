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
 *
 * Created on 01-Aug-2005
 */
package org.alfresco.repo.security.permissions;

import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Encapsulate how permissions are globally inherited between nodes.
 * 
 * @author andyh
 */
public interface NodePermissionEntry
{
    /**
     * Get the node ref.
     * 
     * @return
     */
    public NodeRef getNodeRef();
    
    /**
     * Does the node inherit permissions from its primary parent?
     * 
     * @return
     */
    public boolean inheritPermissions();
    
    
    /**
     * Get the permission entries set for this node.
     * 
     * @return
     */
    public Set<? extends PermissionEntry> getPermissionEntries();
}
