/*
 * Created on 13-Jun-2005
 * 
 * TODO Comment this class
 * 
 * 
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
