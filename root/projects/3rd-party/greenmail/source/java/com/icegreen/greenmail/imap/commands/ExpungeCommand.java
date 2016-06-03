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
import com.icegreen.greenmail.imap.ProtocolException;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.MailFolder;

/**
 * Handles processeing for the EXPUNGE imap command.
 *
 * @author Darrell DeBoer <darrell@apache.org>
 * @version $Revision: 109034 $
 */
class ExpungeCommand extends SelectedStateCommand implements UidEnabledCommand
{
    public static final String NAME = "EXPUNGE";
    public static final String ARGS = null;

    /**
     * @see CommandTemplate#doProcess
     */
    protected void doProcess(ImapRequestLineReader request,
                             ImapResponse response,
                             ImapSession session)
            throws ProtocolException, FolderException 
    {
        doProcessInternal(request, response, session, false);
    }
    
    /**
     * @see UidEnabledCommand#doProcess
     */
    public void doProcess(ImapRequestLineReader request,
                          ImapResponse response,
                          ImapSession session,
                          boolean useUids)
     throws ProtocolException, FolderException
    {
        doProcessInternal(request, response, session, useUids);
    }
    
    private void doProcessInternal(ImapRequestLineReader request,
            ImapResponse response,
            ImapSession session,
            boolean useUids) throws ProtocolException, FolderException
    { 
        IdRange[] idSet = new IdRange[0];
        if (useUids)
        {
            idSet = parser.parseIdRange(request);
        }
        parser.endLine(request);

        if (session.getSelected().isReadonly()) {
            response.commandFailed(this, "Mailbox selected read only.");
        }

        MailFolder folder = session.getSelected();
        if (useUids)
        {
            for (long uid : folder.getMessageUids())
            {
                if (includes(idSet, uid))
                {
                    response.expungeResponse(folder.getMsn(uid));
                    folder.expunge(uid);
                }
            }
        }
        else
        {
            folder.expunge();
        }

        session.unsolicitedResponses(response);
        response.commandComplete(this);    	
    }

    /**
     * @see ImapCommand#getName
     */
    public String getName() {
        return NAME;
    }

    /**
     * @see CommandTemplate#getArgSyntax
     */
    public String getArgSyntax() {
        return ARGS;
    }
}

/*
6.4.3.  EXPUNGE Command

   Arguments:  none

   Responses:  untagged responses: EXPUNGE

   Result:     OK - expunge completed
               NO - expunge failure: can't expunge (e.g. permission
                    denied)
               BAD - command unknown or arguments invalid

      The EXPUNGE command permanently removes from the currently
      selected mailbox all messages that have the \Deleted flag set.
      Before returning an OK to the client, an untagged EXPUNGE response
      is sent for each message that is removed.

   Example:    C: A202 EXPUNGE
               S: * 3 EXPUNGE
               S: * 3 EXPUNGE
               S: * 5 EXPUNGE
               S: * 8 EXPUNGE
               S: A202 OK EXPUNGE completed

      Note: in this example, messages 3, 4, 7, and 11 had the
      \Deleted flag set.  See the description of the EXPUNGE
      response for further explanation.
*/

