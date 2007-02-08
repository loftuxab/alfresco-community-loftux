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
