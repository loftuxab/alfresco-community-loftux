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
package org.alfresco.repo.security.authentication;

import net.sf.acegisecurity.UserDetails;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Extension to the acegi user details to access the password salt
 * 
 * @author andyh
 *
 */
public interface RepositoryUserDetails extends UserDetails
{
    /**
     * Get the salt
     * 
     * @return
     */
    public String getSalt();
   
    /**
     * A noderef for the user 
     * 
     * @return
     */
    public NodeRef getUserNodeRef();
    
    /**
     * A node ref for the person 
     * 
     * @return
     */
    public NodeRef getPersonNodeRef();
}
