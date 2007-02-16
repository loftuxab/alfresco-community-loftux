/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.webservice.util;

/**
 * Helper class to contain web service authentication credentials
 * 
 * @author Roy Wetherall
 */
public class AuthenticationDetails
{
    /** The user name */
    private String userName;
    
    /** The ticket **/
    private String ticket;
    
    /** The session id **/
    private String sessionId;
    
    /**
     * Constructor
     * 
     * @param userName  the user name
     * @param ticket    the ticket
     * @param sessionId the session id
     */
    public AuthenticationDetails(String userName, String ticket, String sessionId)
    {
        this.userName = userName;
        this.ticket = ticket;
        this.sessionId = sessionId;
    }
    
    /**
     * Gets the user name
     * 
     * @return  the user name
     */
    public String getUserName()
    {
        return userName;
    }
    
    /**
     * Gets the ticket
     * 
     * @return  the ticket
     */
    public String getTicket()
    {
        return ticket;
    }
    
    /**
     * Gets the session id
     * 
     * @return  the sessio id, may return null if no session id is set
     */
    public String getSessionId()
    {
        return sessionId;
    }
}
