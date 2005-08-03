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

import org.alfresco.service.namespace.QName;

public class PermissionReferenceImpl implements PermissionReference
{
    private long id;
    
    private QName typeQName;
    
    private String name;

    public PermissionReferenceImpl()
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
    
    public QName getTypeQName()
    {
        return typeQName;
    }

    public void setTypeQName(QName typeQName)
    {
       this.typeQName = typeQName;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof PermissionReferenceImpl))
        {
            return false;
        }
        PermissionReferenceImpl other = (PermissionReferenceImpl)o;
        return this.name.equals(other.name) && this.typeQName.equals(other.typeQName);
    }

    @Override
    public int hashCode()
    {
        return typeQName.hashCode() * 37 + name.hashCode();
    }
    
    

}
