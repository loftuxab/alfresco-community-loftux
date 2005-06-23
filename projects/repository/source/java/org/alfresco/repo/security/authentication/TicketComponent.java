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
 */
package org.alfresco.repo.security.authentication;

import net.sf.acegisecurity.Authentication;

/**
 * Manage authentication tickets
 * 
 * @author andyh
 * 
 */
public interface TicketComponent
{
    /**
     * Register a ticket
     * 
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    public Authentication addTicket(Authentication authentication) throws AuthenticationException;

    /**
     * Check that a certificate is valid and can be used in place of a login.
     * 
     * Tickets may be rejected because:
     * <ol>
     * <li> The certificate does not exists
     * <li> The status of the user has changed 
     * <ol>
     * <li> The user is locked
     * <li> The account has expired
     * <li> The credentials have expired
     * <li> The account is disabled
     * </ol>
     * <li> The ticket may have expired
     * <ol>
     * <li> The ticked my be invalid by timed expiry
     * <li> An attemp to reuse a once only ticket
     * </ol>
     * </ol>
     * 
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    public Authentication validateTicket(Authentication authentication) throws AuthenticationException;
    
    public Authentication validateTicket(String ticketString) throws AuthenticationException;
    
    public String extractTicket(Authentication authentication);
    
    public void invalidateTicket(String ticket);
    
    public void invalidateTicket(Authentication authentication);
}
