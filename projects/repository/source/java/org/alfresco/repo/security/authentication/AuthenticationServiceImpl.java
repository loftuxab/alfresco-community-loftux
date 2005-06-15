/*
 * Created on 13-Jun-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.security.authentication;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.AuthenticationManager;
import net.sf.acegisecurity.UserDetails;
import net.sf.acegisecurity.context.Context;
import net.sf.acegisecurity.context.ContextHolder;
import net.sf.acegisecurity.context.security.SecureContext;
import net.sf.acegisecurity.context.security.SecureContextImpl;

import org.alfresco.service.cmr.repository.StoreRef;

public class AuthenticationServiceImpl implements AuthenticationService
{
    MutableAuthenticationDao authenticationDao;

    AuthenticationManager authenticationManager;

    TicketComponent ticketComponent;

    public AuthenticationServiceImpl()
    {
        super();
    }

    public void createAuthentication(StoreRef storeRef, Authentication authentication) throws AuthenticationException
    {
        StoreContextHolder.setContext(storeRef);
        authenticationDao.createUser(getUserName(authentication), getPassword(authentication));
    }

    public void updateAuthentication(StoreRef storeRef, Authentication authentication) throws AuthenticationException
    {
        StoreContextHolder.setContext(storeRef);
        authenticationDao.updateUser(getUserName(authentication), getPassword(authentication));
    }

    public void deleteAuthentication(StoreRef storeRef, Authentication authentication) throws AuthenticationException
    {
        StoreContextHolder.setContext(storeRef);
        authenticationDao.deleteUser(getUserName(authentication));
    }

    public Authentication authenticate(StoreRef storeRef, Authentication authentication) throws AuthenticationException
    {
        StoreContextHolder.setContext(storeRef);
        return setCurrrentAuthentication(authenticationManager.authenticate(authentication));

    }

    public Authentication getCurrrentAuthentication() throws AuthenticationException
    {
        Context context = ContextHolder.getContext();
        if ((context == null) || !(context instanceof SecureContext))
        {
            return null;
        }
        return ((SecureContext) context).getAuthentication();
    }

    private Authentication setCurrrentAuthentication(Authentication authentication) throws AuthenticationException
    {
        Context context = ContextHolder.getContext();
        SecureContext sc = null;
        if ((context == null) || !(context instanceof SecureContext))
        {
            sc = new SecureContextImpl();
            ContextHolder.setContext(sc);
        }
        else
        {
            sc = (SecureContext) context;
        }
        authentication.setAuthenticated(true);
        sc.setAuthentication(authentication);
        return authentication;
    }

    public void invalidate(Authentication authentication) throws AuthenticationException
    {
        ticketComponent.invalidateTicket(authentication);
    }

    private String getUserName(Authentication authentication)
    {
        String username = authentication.getPrincipal().toString();

        if (authentication.getPrincipal() instanceof UserDetails)
        {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return username;
    }

    private String getPassword(Authentication authentication)
    {
        return authentication.getCredentials().toString();
    }

    public void invalidate(String ticket) throws AuthenticationException
    {
        ticketComponent.invalidateTicket(ticket);
    }

    public Authentication validate(String ticket) throws AuthenticationException
    {
        return setCurrrentAuthentication(ticketComponent.validateTicket(ticket));
    }

    public Authentication validate(Authentication authentication) throws AuthenticationException
    {
        return setCurrrentAuthentication(ticketComponent.validateTicket(authentication));
    }

    public String getCurrentTicket()
    {
        setCurrrentAuthentication(ticketComponent.addTicket(getCurrrentAuthentication()));
        return ticketComponent.extractTicket(getCurrrentAuthentication());
    }

    public void setAuthenticationDao(MutableAuthenticationDao authenticationDao)
    {
        this.authenticationDao = authenticationDao;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }

    public void setTicketComponent(TicketComponent ticketComponent)
    {
        this.ticketComponent = ticketComponent;
    }

    public void clearCurrentSecurityContext()
    {
        ContextHolder.setContext(null);
    }
}
