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
    public PermissionReference getPermissionDefinition();

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
