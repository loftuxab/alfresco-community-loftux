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

import java.util.Set;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.AuthenticationManager;
import net.sf.acegisecurity.UserDetails;
import net.sf.acegisecurity.context.Context;
import net.sf.acegisecurity.context.ContextHolder;
import net.sf.acegisecurity.context.security.SecureContext;
import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;

public class AuthenticationServiceImpl implements AuthenticationService
{
    MutableAuthenticationDao authenticationDao;

    AuthenticationComponent authenticationComponent;

    AuthenticationManager authenticationManager;

    TicketComponent ticketComponent;

    public AuthenticationServiceImpl()
    {
        super();
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

    public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }

    public void createAuthentication(String userName, char[] password) throws AuthenticationException
    {
        try
        {
            authenticationDao.createUser(userName, new String(password));
        }
        catch (net.sf.acegisecurity.AuthenticationException ae)
        {
            throw new AuthenticationException(ae.getMessage(), ae);
        }
    }

    public void updateAuthentication(String userName, char[] oldPassword, char[] newPassword)
            throws AuthenticationException
    {
        try
        {
            authenticationDao.updateUser(userName, new String(newPassword));
        }
        catch (net.sf.acegisecurity.AuthenticationException ae)
        {
            throw new AuthenticationException(ae.getMessage(), ae);
        }
    }

    public void setAuthentication(String userName, char[] newPassword) throws AuthenticationException
    {
        try
        {
            authenticationDao.updateUser(userName, new String(newPassword));
        }
        catch (net.sf.acegisecurity.AuthenticationException ae)
        {
            throw new AuthenticationException(ae.getMessage(), ae);
        }
    }

    public void deleteAuthentication(String userName) throws AuthenticationException
    {
        try
        {
            authenticationDao.deleteUser(userName);
        }
        catch (net.sf.acegisecurity.AuthenticationException ae)
        {
            throw new AuthenticationException(ae.getMessage(), ae);
        }
    }

    public void authenticate(String userName, char[] password) throws AuthenticationException
    {
        try
        {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
                    new String(password));
            authenticationManager.authenticate(authentication);
            authenticationComponent.setCurrentUser(userName);

        }
        catch (net.sf.acegisecurity.AuthenticationException ae)
        {
            throw new AuthenticationException(ae.getMessage(), ae);
        }
    }

    public String getCurrentUserName() throws AuthenticationException
    {
        Context context = ContextHolder.getContext();
        if ((context == null) || !(context instanceof SecureContext))
        {
            return null;
        }
        return getUserName(((SecureContext) context).getAuthentication());
    }

    public void invalidateUserSession(String userName) throws AuthenticationException
    {
        ticketComponent.invalidateTicketByUser(userName);
    }

    public void invalidateTicket(String ticket) throws AuthenticationException
    {
        ticketComponent.invalidateTicketById(ticket);
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

    public void validate(String ticket) throws AuthenticationException
    {
        authenticationComponent.setCurrentUser(ticketComponent.validateTicket(ticket));
    }

    public String getCurrentTicket()
    {
        return ticketComponent.getTicket(getCurrentUserName());
    }

    public void clearCurrentSecurityContext()
    {
        authenticationComponent.clearCurrentSecurityContext();
    }

    public Set<String> getCurrentUserRoles()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> getCurrentUserGroups()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> getUserRoles(String userName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> getUserGroups(String userName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> getAllUserRoles()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> getAllUserGroups()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> getAllUserNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public NodeRef synchronisePerson(String userName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isCurrentUserTheSystemUser()
    {
        String userName = getCurrentUserName();
        if ((userName != null) && userName.equals(authenticationComponent.getSystemUserName()))
        {
            return true;
        }
        return false;
    }

}
