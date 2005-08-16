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

import java.io.Serializable;

/**
 * The interface against which permission references are persisted in hibernate.
 * 
 * @author andyh
 */
public interface PermissionReference extends Serializable
{
   /**
    * Get the URI for the type to which this permission applies.
    * 
    * @return
    */ 
    public String getTypeUri();
    
    /**
     * Set the URI for the type to which this permission applies.
     * 
     * @param typeUri
     */
    public void setTypeUri(String typeUri);
    
    /**
     * Get the local name of the type to which this permission applies.
     * 
     * @return
     */
    public String getTypeName();
    
    /**
     * Set the local name of the type to which this permission applies.
     * 
     * @param typeName
     */
    public void setTypeName(String typeName);
    
    /**
     * Get the name of the permission.
     * 
     * @return
     */
    public String getName();
    
    /**
     * Set the name of the permission.
     * 
     * @param name
     */
    public void setName(String name);
}
