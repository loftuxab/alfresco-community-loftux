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
package org.alfresco.repo.security.permissions.impl.model;

import org.alfresco.repo.security.permissions.impl.PermissionReferenceImpl;
import org.alfresco.service.namespace.QName;

/**
 * Store and read the definition of a required permission.
 * 
 * @author andyh
 */
public class RequiredPermission extends PermissionReferenceImpl
{
    public enum On {
        PARENT, NODE, CHILDREN
    };

    private On on;

    boolean implies;

    public RequiredPermission(QName qName, String name, On on, boolean implies)
    {
        super(qName, name);
        this.on = on;
        this.implies = implies;
    }

    public boolean isImplies()
    {
        return implies;
    }

    public On getOn()
    {
        return on;
    }

}
