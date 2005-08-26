/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.security.permissions.noop;

import java.util.HashSet;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessPermission;
import org.alfresco.repo.security.permissions.NodePermissionEntry;
import org.alfresco.repo.security.permissions.PermissionEntry;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.PermissionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


/**
 * Dummy implementation of Permissions Service
 *  
 */
public class PermissionServiceNOOPImpl
    implements PermissionService
{

    /**
     * ALL Permission
     */
    private static PermissionReference ALL_PERMISSION = new PermissionReference()
    {
        public String getName()
        {
            return "All";
        }
    
        public QName getQName()
        {
            return ContentModel.TYPE_BASE;
        }
    };    
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#getOwnerAuthority()
     */
    public String getOwnerAuthority()
    {
        return "owner";
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#getAllAuthorities()
     */
    public String getAllAuthorities()
    {
        return "all";
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#getAllPermission()
     */
    public PermissionReference getAllPermission()
    {
        return ALL_PERMISSION;
    }    
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#getPermissions(org.alfresco.service.cmr.repository.NodeRef)
     */
    public Set<AccessPermission> getPermissions(NodeRef nodeRef)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#getAllPermissions(org.alfresco.service.cmr.repository.NodeRef)
     */
    public Set<AccessPermission> getAllPermissions(NodeRef nodeRef)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#getSettablePermissions(org.alfresco.service.cmr.repository.NodeRef)
     */
    public Set<PermissionReference> getSettablePermissions(NodeRef nodeRef)
    {
        return getSettablePermissions((QName)null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#getSettablePermissions(org.alfresco.service.namespace.QName)
     */
    public Set<PermissionReference> getSettablePermissions(QName type)
    {
        HashSet<PermissionReference> permissions = new HashSet<PermissionReference>();
        permissions.add(ALL_PERMISSION);
        return permissions;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#getSetPermissions(org.alfresco.service.cmr.repository.NodeRef)
     */
    public NodePermissionEntry getSetPermissions(NodeRef nodeRef)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#hasPermission(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.repo.security.permissions.PermissionReference)
     */
    public boolean hasPermission(NodeRef nodeRef, PermissionReference perm)
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#explainPermission(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.repo.security.permissions.PermissionReference)
     */
    public NodePermissionEntry explainPermission(NodeRef nodeRef, PermissionReference perm)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#deletePermissions(org.alfresco.service.cmr.repository.NodeRef)
     */
    public void deletePermissions(NodeRef nodeRef)
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#deletePermissions(org.alfresco.repo.security.permissions.NodePermissionEntry)
     */
    public void deletePermissions(NodePermissionEntry nodePermissionEntry)
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#deletePermission(org.alfresco.repo.security.permissions.PermissionEntry)
     */
    public void deletePermission(PermissionEntry permissionEntry)
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#deletePermission(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, org.alfresco.repo.security.permissions.PermissionReference, boolean)
     */
    public void deletePermission(NodeRef nodeRef, String authority, PermissionReference perm, boolean allow)
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#setPermission(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, org.alfresco.repo.security.permissions.PermissionReference, boolean)
     */
    public void setPermission(NodeRef nodeRef, String authority, PermissionReference perm, boolean allow)
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#setPermission(org.alfresco.repo.security.permissions.PermissionEntry)
     */
    public void setPermission(PermissionEntry permissionEntry)
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#setPermission(org.alfresco.repo.security.permissions.NodePermissionEntry)
     */
    public void setPermission(NodePermissionEntry nodePermissionEntry)
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.security.permissions.PermissionService#setInheritParentPermissions(org.alfresco.service.cmr.repository.NodeRef, boolean)
     */
    public void setInheritParentPermissions(NodeRef nodeRef, boolean inheritParentPermissions)
    {
    }

}
