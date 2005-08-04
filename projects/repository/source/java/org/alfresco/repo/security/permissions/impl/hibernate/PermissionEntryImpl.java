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
package org.alfresco.repo.security.permissions.impl.hibernate;

import org.alfresco.util.EqualsHelper;

public class PermissionEntryImpl implements PermissionEntry
{
    private long id;
    
    private NodePermissionEntry nodePermissionEntry;

    private PermissionReference permissionReference;

    private Recipient recipient;

    private boolean allowed;

    public PermissionEntryImpl()
    {
        super();
    }
    
    public long getId()
    {
        return id;
    }
    
    /* package */ void setId(long id)
    {
        this.id = id;
    }

    public NodePermissionEntry getNodePermissionEntry()
    {
        return nodePermissionEntry;
    }

    private void setNodePermissionEntry(NodePermissionEntry nodePermissionEntry)
    {
        this.nodePermissionEntry = nodePermissionEntry;
    }

    public PermissionReference getPermissionReference()
    {
        return permissionReference;
    }

    private void setPermissionReference(PermissionReference permissionReference)
    {
        this.permissionReference = permissionReference;
    }

    public Recipient getRecipient()
    {
        return recipient;
    }

    private void setRecipient(Recipient recipient)
    {
        this.recipient = recipient;
    }

    public boolean isAllowed()
    {
        return allowed;
    }

    public void setAllowed(boolean allowed)
    {
        this.allowed = allowed;
    }


    public static PermissionEntryImpl create(NodePermissionEntry nodePermissionEntry, PermissionReference permissionReference, Recipient recipient, boolean allowed)
    {
        PermissionEntryImpl permissionEntry = new PermissionEntryImpl();
        permissionEntry.setNodePermissionEntry(nodePermissionEntry);
        permissionEntry.setPermissionReference(permissionReference);
        permissionEntry.setRecipient(recipient);
        permissionEntry.setAllowed(allowed);
        nodePermissionEntry.getPermissionEntries().add(permissionEntry);
        return permissionEntry;
    }

    public void delete()
    {
        nodePermissionEntry.getPermissionEntries().remove(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof PermissionEntryImpl))
        {
            return false;
        }
        PermissionEntryImpl other = (PermissionEntryImpl) o;
        return EqualsHelper.nullSafeEquals(this.nodePermissionEntry,
                other.nodePermissionEntry)
                && EqualsHelper.nullSafeEquals(this.permissionReference,
                        other.permissionReference)
                && EqualsHelper.nullSafeEquals(this.recipient, other.recipient)
                && (this.allowed == other.allowed);
    }

    @Override
    public int hashCode()
    {
        int hashCode = nodePermissionEntry.hashCode();
        if (permissionReference != null)
        {
            hashCode = hashCode * 37 + permissionReference.hashCode();
        }
        if (recipient != null)
        {
            hashCode = hashCode * 37 + recipient.hashCode();
        }
        hashCode = hashCode * 37 + (allowed ? 1 : 0);
        return hashCode;
    }

}
