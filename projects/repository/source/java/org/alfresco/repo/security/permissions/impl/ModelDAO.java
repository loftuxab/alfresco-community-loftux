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

import java.util.Set;

import org.alfresco.repo.security.permissions.AccessStatus;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public interface ModelDAO
{

    public Set<PermissionReference> getPermissions(QName type);

    public Set<PermissionReference> getPermissions(NodeRef nodeRef);

    public Set<PermissionReference> getGrantingPermissions(PermissionReference perm);

    public Set<PermissionReference> getRequiredNodePermissions(PermissionReference required, QName qName, Set<QName> aspectQNames);

    public Set<PermissionReference> getRequiredParentPermissions(PermissionReference required, QName qName, Set<QName> aspectQNames);

    public AccessStatus getDefaultPermission(PermissionReference required);

    public Set<PermissionReference> getGranteePermissions(PermissionReference permissionReference);

}
