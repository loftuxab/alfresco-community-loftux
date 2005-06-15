/*
 * Created on 13-Jun-2005
 *
 */
package org.alfresco.repo.security.authentication;

import net.sf.acegisecurity.Authentication;

import org.alfresco.service.cmr.repository.StoreRef;

/**
 * The authentication service defines the API for managing authentication information 
 * against a userid. 
 * 
 * This service follows the acegi pattern for authentication.
 * It makes the acegi authentication available as a service.
 *  
 * @author andyh
 *
 */
public interface AuthenticationService
{
    /**
     * Create an authentication entry based upon the supplied authentication object if supported
     * GrantedAuthorities will be found from the repository. 
     *  
     * @param authentication
     * @throws AuthenticationException
     */
    public void createAuthentication(StoreRef storeRef, Authentication authentication) throws AuthenticationException;
    
    /**
     * Update the credentials held. The GrantedAuthorities held in the repository will be unaffected.
     * 
     * @param authentication
     * @throws AuthenticationException
     */
    public void updateAuthentication(StoreRef storeRef, Authentication authentication) throws AuthenticationException;
    
    /**
     * Delete the authentication entry. Only the authentication entry is removed - the person information in the repository 
     * will remain unchanged. 
     * 
     * @param authentication
     * @throws AuthenticationException
     */
    public void deleteAuthentication(StoreRef storeRef, Authentication authentication) throws AuthenticationException;
    
    /**
     * Wrap an ACEGI authentication call
     * 
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    public Authentication authenticate(StoreRef storeRef, Authentication authentication) throws AuthenticationException;
    
    /**
     * Support to get the current authentication object
     * 
     * @return
     * @throws AuthenticationException
     */
    public Authentication getCurrrentAuthentication() throws AuthenticationException;
    
    /**
     * Invalidate any ticket associated with the given authentication
     *  
     * @param authentication
     * @throws AuthenticationException
     */
    public void invalidate(Authentication authentication) throws AuthenticationException;
    
    /**
     * Invalidate any ticket associated with the given authentication
     *  
     * @param authentication
     * @throws AuthenticationException
     */
    public void invalidate(String ticket) throws AuthenticationException;
    
    /**
     * Invalidate any ticket associated with the given authentication
     *  
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    public Authentication validate(String ticket) throws AuthenticationException;
    
    /**
     * Invalidate any ticket associated with the given authentication
     *  
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    public Authentication validate(Authentication authentication) throws AuthenticationException;
    
    /**
     * Get the current ticket as a string
     * @return
     */
    public String getCurrentTicket();
    
    /**
     * Remove the current security information
     *
     */
    public void clearCurrentSecurityContext();
}

