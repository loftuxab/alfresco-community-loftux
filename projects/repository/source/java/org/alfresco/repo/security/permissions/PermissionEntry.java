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

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * A single permission entry defined against a node.
 * 
 * @author andyh
 */
public interface PermissionEntry
{
    /**
     * Get the permission definition.
     * 
     * This may be null. Null implies that the settings apply to all permissions
     * 
     * @return
     */
    public PermissionReference getPermissionReference();

    /**
     * Get the authority to which this entry applies This could be the string
     * value of a username, group, role or any other authority assigned to the
     * authorisation.
     * 
     * If null then this applies to all.
     * 
     * @return
     */
    public String getAuthority();

    /**
     * Get the node ref for the node to which this permission applies.
     * 
     * This can not be null.
     * 
     * @return
     */
    public NodeRef getNodeRef();

    /**
     * Is permissions denied?
     *
     */
    public boolean isDenied();

    /**
     * Is permission allowed?
     *
     */
    public boolean isAllowed();
    
    /**
     * Get the Access enum value
     * 
     * @return
     */
    public AccessStatus getAccessStatus();
}
