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
 * Created on 02-Aug-2005
 */
package org.alfresco.repo.security.permissions.impl;

import org.alfresco.repo.security.permissions.NodePermissionEntry;
import org.alfresco.repo.security.permissions.PermissionEntry;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.service.cmr.repository.NodeRef;

public interface PermissionsDAO
{

    public NodePermissionEntry getPermissions(NodeRef nodeRef);

    public void deletePermissions(NodeRef nodeRef);

    public void deletePermissions(NodePermissionEntry nodePermissionEntry);

    public void deletePermissions(PermissionEntry permissionEntry);

    public void deletePermissions(NodeRef nodeRef, String authority, PermissionReference perm,  boolean allow);

    public void setPermission(NodeRef nodeRef, String authority, PermissionReference perm, boolean allow);

    public void setPermission(PermissionEntry permissionEntry);

    public void setPermission(NodePermissionEntry nodePermissionEntry);

    public void setInheritParentPermissions(NodeRef nodeRef, boolean inheritParentPermissions);

}
