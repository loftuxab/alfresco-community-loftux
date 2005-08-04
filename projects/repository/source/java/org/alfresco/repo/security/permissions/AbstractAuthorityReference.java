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

public abstract class AbstractAuthorityReference implements AuthorityReference
{

    public AbstractAuthorityReference()
    {
        super();
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof AbstractAuthorityReference ))
        {
            return false;
        }
        AbstractAuthorityReference other = (AbstractAuthorityReference)o;
        return EqualsHelper.nullSafeEquals(this.getAuthority(), other.getAuthority());
    }

    @Override
    public int hashCode()
    {
        return getAuthority().hashCode();
    }


}
