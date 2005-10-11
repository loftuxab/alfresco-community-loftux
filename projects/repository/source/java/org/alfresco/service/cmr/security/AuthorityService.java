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
package org.alfresco.service.cmr.security;

import java.util.Set;

/**
 * The service that encapsulates authorities granted to users. 
 * 
 * @author Andy Hind
 */
public interface AuthorityService
{
    /**
     * Check of the current user has admin authority.
     * There is no contract for who should have this authority, only that it cna be tested here.
     * It could be determined by group membership, role, authentication mechanism, ... 
     * 
     * @return true if the currently authenticated user has the admin authority
     */
    public boolean hasAdminAuthority();
    
    /**
     * Get the authorities for the current user
   
     * @return
     */
    public Set<String> getAuthorities();
}
