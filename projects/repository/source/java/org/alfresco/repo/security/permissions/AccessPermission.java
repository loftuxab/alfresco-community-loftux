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
package org.alfresco.repo.security.permissions;

/**
 * The interface used to support reporting back if permissions are allowed or
 * denied.
 * 
 * @author andyh
 */
public interface AccessPermission
{   
    /**
     * The permission defintion.
     * 
     * @return
     */
    public PermissionReference getPermssionDefintion();

    /**
     * Is this permission allowed?
     * @return
     */
    public boolean isAllowed();

    /**
     * Is this permission denied?
     * @return
     */
    public boolean isDenied();
    
    /**
     * Get the Access enum value
     * 
     * @return
     */
    public AccessStatus getAccessStatus();
}
