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
import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.alfresco.repo.security.authentication.AuthenticationException;
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
        try
        {
            StoreContextHolder.setContext(storeRef);
            authenticationDao.createUser(getUserName(authentication), getPassword(authentication));
        }
        catch (net.sf.acegisecurity.AuthenticationException ae)
        {
            throw new AuthenticationException(ae.getMessage(), ae);
        }
    }

    public void updateAuthentication(StoreRef storeRef, Authentication authentication) throws AuthenticationException
    {
        try
        {
           StoreContextHolder.setContext(storeRef);
           authenticationDao.updateUser(getUserName(authentication), getPassword(authentication));
        }
        catch (net.sf.acegisecurity.AuthenticationException ae)
        {
            throw new AuthenticationException(ae.getMessage(), ae);
        }
    }

    public void deleteAuthentication(StoreRef storeRef, Authentication authentication) throws AuthenticationException
    {
        try
        {
            StoreContextHolder.setContext(storeRef);
            authenticationDao.deleteUser(getUserName(authentication));
        }
        catch (net.sf.acegisecurity.AuthenticationException ae)
        {
            throw new AuthenticationException(ae.getMessage(), ae);
        }
    }

    public Authentication authenticate(StoreRef storeRef, Authentication authentication) throws AuthenticationException
    {
        try
        {
            StoreContextHolder.setContext(storeRef);
            return setCurrentAuthentication(authenticationManager.authenticate(authentication));
        }
        catch (net.sf.acegisecurity.AuthenticationException ae)
        {
            throw new AuthenticationException(ae.getMessage(), ae);
        }
    }

    public Authentication getCurrentAuthentication() throws AuthenticationException
    {
        Context context = ContextHolder.getContext();
        if ((context == null) || !(context instanceof SecureContext))
        {
            return null;
        }
        return ((SecureContext) context).getAuthentication();
    }

    private Authentication setCurrentAuthentication(Authentication authentication) throws AuthenticationException
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
        return setCurrentAuthentication(ticketComponent.validateTicket(ticket));
    }

    public Authentication validate(Authentication authentication) throws AuthenticationException
    {
        return setCurrentAuthentication(ticketComponent.validateTicket(authentication));
    }

    public String getCurrentTicket()
    {
        setCurrentAuthentication(ticketComponent.addTicket(getCurrentAuthentication()));
        return ticketComponent.extractTicket(getCurrentAuthentication());
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

    public Authentication setAuthenticatedUser(String userName)
    {
        RepositoryUserDetails ud = (RepositoryUserDetails)authenticationDao.loadUserByUsername(userName);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(ud, "", ud.getAuthorities());
        auth.setDetails(ud);
        auth.setAuthenticated(true);
        return setCurrentAuthentication(auth);
    }
}
