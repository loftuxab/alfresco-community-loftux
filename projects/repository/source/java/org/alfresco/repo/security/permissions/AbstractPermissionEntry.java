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
package org.alfresco.repo.security.permissions;

import org.alfresco.util.EqualsHelper;

/**
 * This class provides common support for hash code and equality.
 * 
 * @author andyh
 */
public abstract class AbstractPermissionEntry implements PermissionEntry
{

    public AbstractPermissionEntry()
    {
        super();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof AbstractPermissionEntry))
        {
            return false;
        }
        AbstractPermissionEntry other = (AbstractPermissionEntry) o;
        return EqualsHelper.nullSafeEquals(this.getNodeRef(),
                other.getNodeRef())
                && EqualsHelper.nullSafeEquals(this.getPermissionReference(),
                        other.getPermissionReference())
                && EqualsHelper.nullSafeEquals(this.getAuthority(), other.getAuthority())
                && EqualsHelper.nullSafeEquals(this.getAccessStatus(), other.getAccessStatus());
    }

    @Override
    public int hashCode()
    {
        int hashCode = getNodeRef().hashCode();
        if (getPermissionReference() != null)
        {
            hashCode = hashCode * 37 + getPermissionReference().hashCode();
        }
        if (getAuthority() != null)
        {
            hashCode = hashCode * 37 + getAuthority().hashCode();
        }
        if(getAccessStatus() != null)
        {
           hashCode = hashCode * 37 + getAccessStatus().hashCode();
        }
        return hashCode;
    }

    

}
