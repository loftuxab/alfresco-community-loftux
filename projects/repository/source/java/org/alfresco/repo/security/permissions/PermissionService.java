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
 * Created on 29-Jul-2005
 */
package org.alfresco.repo.security.permissions;

import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import net.sf.acegisecurity.Authentication;

/**
 * The public API for a permission service
 * 
 * The implementation may be changed in the application configuration
 * 
 * @author andyh
 */
public interface PermissionService
{
    /**
     * Get all the AccessPermissions that are granted to the given
     * authentication for the given node
     * 
     * @param nodeRef -
     *            the reference to the node
     * @param auth -
     *            the authentication
     * @return the set of allowed permissions
     */
    public Set<AccessPermission> getPermissions(NodeRef nodeRef, Authentication auth);

    /**
     * Get all the AccessPermissions that are granted/denied to the given
     * authentication for the given node
     * 
     * @param nodeRef -
     *            the reference to the node
     * @param auth -
     *            the authentication
     * @return the set of allowed permissions
     */
    public Set<AccessPermission> getAllPermissions(NodeRef nodeRef, Authentication auth);

    /**
     * Get the permissions that can be set for a given node
     * 
     * @param nodeRef
     * @return
     */
    public Set<PermissionReference> getSettablePermissions(NodeRef nodeRef);
    
    /**
     * Get the permissions that can be set for a given type
     * 
     * @param nodeRef
     * @return
     */
    public Set<PermissionReference> getSettablePermissions(QName type);

    /**
     * Get the permissions that have been set on the given node (it knows
     * nothing of the parent permissions)
     * 
     * @param nodeRef
     * @return
     */
    public NodePermissionEntry getSetPermissions(NodeRef nodeRef);

    /**
     * Check that the given authentication has a particular permission for the
     * given node. (The default behaviour is ot inherit permissions)
     * 
     * @param nodeRef
     * @param auth
     * @param perm
     * @return
     */
    public boolean hasPermission(NodeRef nodeRef, Authentication auth, PermissionReference perm);

    /**
     * Where is the permission set that controls the behaviour for the givent
     * permission for the given authentication to access the specified name.
     * 
     * @param nodeRef
     * @param auth
     * @param perm
     * @return
     */
    public NodePermissionEntry explainPermission(NodeRef nodeRef, Authentication auth, PermissionReference perm);

    /**
     * Delete all the permission assigned to the node
     * @param nodeRef
     */
    public void deletePermissions(NodeRef nodeRef);
    
    /**
     * Delete the permissions defined by the nodePermissionEntry
     * @param nodePermissionEntry
     */
    public void deletePermissions(NodePermissionEntry nodePermissionEntry);
    
    /**
     * Delete a single permission entry
     * @param permissionEntry
     */
    public void deletePermission(PermissionEntry permissionEntry);

    /**
     * Find and delete a permission by node, authentication and permission definition.
     * 
     * @param nodeRef
     * @param authority
     * @param perm
     */
    public void deletePermission(NodeRef nodeRef, String authority, PermissionReference perm, boolean allow);
    
    /**
     * Set a specific permission on a node.
     * 
     * @param nodeRef
     * @param authority
     * @param perm
     * @param allow
     */
    public void setPermission(NodeRef nodeRef, String authority, PermissionReference perm, boolean allow);

    /**
     * Add or set a permission entry on a node.
     * 
     * @param permissionEntry
     */
    public void setPermission(PermissionEntry permissionEntry);
    
    /**
     * Set the permissions on a node.
     * 
     * @param nodePermissionEntry
     */
    public void setPermission(NodePermissionEntry nodePermissionEntry);

    /**
     * Set the global inheritance behaviour for permissions on a node.
     * 
     * @param nodeRef
     * @param inheritParentPermissions
     */
    public void setInheritParentPermissions(NodeRef nodeRef, boolean inheritParentPermissions);
}
