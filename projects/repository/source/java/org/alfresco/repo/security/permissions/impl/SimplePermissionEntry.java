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
 * Created on 03-Aug-2005
 */
package org.alfresco.repo.security.permissions.impl;

import org.alfresco.repo.security.permissions.AbstractPermissionEntry;
import org.alfresco.repo.security.permissions.AccessStatus;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public class SimplePermissionEntry extends AbstractPermissionEntry
{
    private NodeRef nodeRef;
    private SimplePermissionReference permissionReference;
    private String authority;
    private AccessStatus accessStatus;
    
    public static final String ALL_AUTHORITIES = "\u0000";
    
    public static final SimplePermissionReference ALL_PERMISSIONS = new SimplePermissionReference(QName.createQName("\u0000", "\u0000"), "\u0000");
    
    public SimplePermissionEntry(NodeRef nodeRef, SimplePermissionReference permissionReference, String authority, AccessStatus accessStatus)
    {
        super();
        this.nodeRef = nodeRef;
        this.permissionReference = permissionReference;
        this.authority = authority;
        this.accessStatus = accessStatus;
    }

    public PermissionReference getPermissionReference()
    {
        return permissionReference;
    }

    public String getAuthority()
    {
       return authority;
    }

    public NodeRef getNodeRef()
    {
        return nodeRef;
    }

    public boolean isDenied()
    {
        return accessStatus == AccessStatus.DENIED;
    }

    public boolean isAllowed()
    {
        return accessStatus == AccessStatus.ALLOWED;
    }

    public AccessStatus getAccessStatus()
    {
        return accessStatus;
    }

}
