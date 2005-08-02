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
package org.alfresco.repo.security.permissions.impl;

import java.util.Set;

import net.sf.acegisecurity.Authentication;

import org.alfresco.repo.security.permissions.AccessPermission;
import org.alfresco.repo.security.permissions.NodePermissionEntry;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.PermissionEntry;
import org.alfresco.repo.security.permissions.PermissionService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

public class PermissionServiceImpl implements PermissionService
{
    private ModelDAO modelDAO;

    private PermissionsDAO permissionsDAO;

    private NodeService nodeService;

    private DictionaryService dictionaryService;

    public PermissionServiceImpl()
    {
        super();
    }

    //
    // Inversion of control
    //
    
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setModelDAO(ModelDAO modelDAO)
    {
        this.modelDAO = modelDAO;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setPermissionsDAO(PermissionsDAO permissionsDAO)
    {
        this.permissionsDAO = permissionsDAO;
    }

    //
    // Permissions Service
    //
    
    public Set<AccessPermission> getPermissions(NodeRef nodeRef, Authentication auth)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<AccessPermission> getAllPermissions(NodeRef nodeRef, Authentication auth)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<PermissionReference> getSettablePermissions(NodeRef nodeRef)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<PermissionReference> getSettablePermissions(QName type)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public NodePermissionEntry getSetPermissions(NodeRef nodeRef)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasPermision(NodeRef nodeRef, Authentication auth, PermissionReference perm)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public NodePermissionEntry explainPermission(NodeRef nodeRef, Authentication auth, PermissionReference perm)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void deletePermissions(NodeRef nodeRef)
    {
        // TODO Auto-generated method stub

    }

    public void deletePermissions(NodePermissionEntry nodePermissionEntry)
    {
        // TODO Auto-generated method stub

    }

    public void deletePermission(PermissionEntry permissionEntry)
    {
        // TODO Auto-generated method stub

    }

    public void deletePermission(NodeRef nodeRef, Authentication auth, PermissionReference perm)
    {
        // TODO Auto-generated method stub

    }

    public void setPermission(NodeRef nodeRef, Authentication auth, PermissionReference perm, boolean allow)
    {
        // TODO Auto-generated method stub

    }

    public void setPermission(PermissionEntry permissionEntry)
    {
        // TODO Auto-generated method stub

    }

    public void setPermission(NodePermissionEntry nodePermissionEntry)
    {
        // TODO Auto-generated method stub

    }

    public void setInheritParentPermissions(NodeRef nodeRef, boolean inheritParentPermissions)
    {
        // TODO Auto-generated method stub

    }

}
