/*
 * #%L
 * Alfresco greenmail implementation
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
/* -------------------------------------------------------------------
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 * This file has been modified by the copyright holder. Original file can be found at http://james.apache.org
 * -------------------------------------------------------------------
 */
package com.icegreen.greenmail.imap.commands;

import com.icegreen.greenmail.imap.ImapRequestLineReader;
import com.icegreen.greenmail.imap.ImapResponse;
import com.icegreen.greenmail.imap.ImapSession;
import com.icegreen.greenmail.imap.ImapSessionState;

/**
 * Represents a processor for a particular Imap command. Implementations of this
 * interface should encpasulate all command specific processing.
 *
 * @author Darrell DeBoer <darrell@apache.org>
 * @version $Revision: 109034 $
 */
public interface ImapCommand {
    /**
     * @return the name of the command, as specified in rfc2060.
     */
    String getName();

    /**
     * Specifies if this command is valid for the given session state.
     *
     * @param state The current {@link com.icegreen.greenmail.imap.ImapSessionState state} of the {@link com.icegreen.greenmail.imap.ImapSession}
     * @return <code>true</code> if the command is valid in this state.
     */
    boolean validForState(ImapSessionState state);

    /**
     * Performs all processing of the current Imap request. Reads command
     * arguments from the request, performs processing, and writes responses
     * back to the request object, which are sent to the client.
     *
     * @param request  The current client request
     * @param response The current server response
     * @param session  The current session
     */
    void process(ImapRequestLineReader request,
                 ImapResponse response,
                 ImapSession session);
    
    boolean isLoginCommand();
}
