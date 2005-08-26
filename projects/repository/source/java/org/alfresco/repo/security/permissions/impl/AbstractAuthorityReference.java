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
package org.alfresco.repo.security.permissions.impl;

import org.alfresco.repo.security.permissions.AuthorityReference;
import org.alfresco.util.EqualsHelper;

/**
 * This class provides common support for hash code and equality.
 * 
 * @author andyh
 */
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
