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

import java.util.Date;
import java.util.HashMap;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.GrantedAuthorityImpl;
import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.alfresco.service.cmr.repository.datatype.Duration;
import org.alfresco.util.GUID;
public class InMemoryTicketComponentImpl implements TicketComponent
{
    public static final String GRANTED_AUTHORITY_TICKET_PREFIX = "TICKET_";

    private boolean ticketsExpire;

    private Duration validDuration;
    
    private boolean oneOff;

    private HashMap<String, Ticket> tickets = new HashMap<String, Ticket>();

    public InMemoryTicketComponentImpl()
    {
        super();
    }

    public Authentication addTicket(Authentication authentication) throws AuthenticationException
    {
        if (!authentication.isAuthenticated())
        {
            throw new AuthenticationException("Tickets may not be issued for unauthenticated users!");
        }
        //Object details = authentication.getDetails();
        if (authentication.getPrincipal() instanceof RepositoryUserDetails)
        {
            RepositoryUserDetails userDetails = (RepositoryUserDetails) authentication.getPrincipal();
            Date expiryDate = null;
            if (ticketsExpire)
            {
                expiryDate = Duration.add(new Date(), validDuration);
            }
            Ticket ticket = new Ticket(ticketsExpire, expiryDate, authentication);
            tickets.put(ticket.getTicketId(), ticket);
            GrantedAuthority[] gas = new GrantedAuthority[authentication.getAuthorities().length + 1];
            System.arraycopy(authentication.getAuthorities(), 0, gas, 0, authentication.getAuthorities().length);
            gas[gas.length - 1] = new GrantedAuthorityImpl(GRANTED_AUTHORITY_TICKET_PREFIX + ticket.getTicketId());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), gas);
            auth.setDetails(userDetails);
            auth.setAuthenticated(authentication.isAuthenticated());
            return auth;
        }
        else
        {
            throw new AuthenticationException("Tickets may only be generated for RepositoryUserDetails not " + authentication.getPrincipal().getClass().getName());
        }
    }

    public Authentication validateTicket(Authentication authentication) throws AuthenticationException
    {
        String ticketString = extractTicket(authentication);
        return validateTicket(ticketString);
    }

    public String extractTicket(Authentication authentication)
    {
        GrantedAuthority[] gas = authentication.getAuthorities();

        for (GrantedAuthority ga : gas)
        {
            if (ga.getAuthority().startsWith(GRANTED_AUTHORITY_TICKET_PREFIX))
            {
                return ga.getAuthority();
            }
        }
        throw new AuthenticationException("No ticket information present under granted authority " + GRANTED_AUTHORITY_TICKET_PREFIX + " ...");
    }

    public Authentication validateTicket(String ticketString)
    {
        if (ticketString.length() < GRANTED_AUTHORITY_TICKET_PREFIX.length())
        {
           throw new AuthenticationException(ticketString  + " is an invalid ticket format");
        }
        
        String key = ticketString.substring(GRANTED_AUTHORITY_TICKET_PREFIX.length());
        Ticket ticket = tickets.get(key);
        if (ticket == null)
        {
            throw new AuthenticationException("Missing ticket for " + ticketString);
        }
        if (ticket.hasExpired())
        {
            throw new TicketExpiredException("Ticket expired for " + ticketString);
        }
        // TODO: Recheck the user details here
        // TODO: Strengthen ticket as GUID is predicatble
        if(oneOff)
        {
            tickets.remove(key);
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(ticket.getAuthentication().getPrincipal(), ticket.getAuthentication().getCredentials(),
                ticket.getAuthentication().getAuthorities());
        auth.setDetails(ticket.getAuthentication().getDetails());
        auth.setAuthenticated(ticket.getAuthentication().isAuthenticated());
        return auth;
    }

    public void invalidateTicket(Authentication authentication)
    {
        String ticketString = extractTicket(authentication);
        invalidateTicket(ticketString);
    }
    
    public void invalidateTicket(String ticketString)
    {
        String key = ticketString.substring(GRANTED_AUTHORITY_TICKET_PREFIX.length());
        tickets.remove(key);
    }
    
    
    
    private static class Ticket
    {
        private boolean expires;

        private Date expiryDate;

        private Authentication authentication;

        private String ticketId;

        Ticket(boolean expires, Date expiryDate, Authentication authentication)
        {
            this.expires = expires;
            this.expiryDate = expiryDate;
            this.authentication = authentication;
            this.ticketId = GUID.generate();
        }

        /**
         * Has the tick expired
         * 
         * @return
         */
        boolean hasExpired()
        {
            if (expires && (expiryDate != null) && (expiryDate.compareTo(new Date()) < 0))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        public boolean equals(Object o)
        {
            if (o == this)
            {
                return true;
            }
            if (!(o instanceof Ticket))
            {
                return false;
            }
            Ticket t = (Ticket) o;
            return (this.expires == t.expires) && this.expiryDate.equals(t.expiryDate) && this.authentication.equals(t.authentication) && this.ticketId.equals(t.ticketId);
        }

        public int hashCode()
        {
            return ticketId.hashCode();
        }

        protected boolean getExpires()
        {
            return expires;
        }

        protected Date getExpiryDate()
        {
            return expiryDate;
        }

        protected String getTicketId()
        {
            return ticketId;
        }

        protected Authentication getAuthentication()
        {
            return authentication;
        }

    }



    public void setOneOff(boolean oneOff)
    {
        this.oneOff = oneOff;
    }
    

    public void setTicketsExpire(boolean ticketsExpire)
    {
        this.ticketsExpire = ticketsExpire;
    }
    

    public void setValidDuration(String validDuration)
    {
        this.validDuration = new Duration(validDuration);
    }
    
}
