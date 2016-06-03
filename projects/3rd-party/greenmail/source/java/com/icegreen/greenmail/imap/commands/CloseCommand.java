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
 * Handles processeing for the CHECK imap command.
 *
 * @author Darrell DeBoer <darrell@apache.org>
 * @version $Revision: 109034 $
 */
class CloseCommand extends SelectedStateCommand {
    public static final String NAME = "CLOSE";
    public static final String ARGS = null;

    /**
     * @see CommandTemplate#doProcess
     */
    protected void doProcess(ImapRequestLineReader request,
                             ImapResponse response,
                             ImapSession session)
            throws ProtocolException, FolderException {
        parser.endLine(request);

        if (!session.getSelected().isReadonly()) {
            MailFolder folder = session.getSelected();
            folder.expunge();
        }
        session.deselect();
        
//      Don't send unsolicited responses on close.
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
6.4.2.  CLOSE Command

   Arguments:  none

   Responses:  no specific responses for this command

   Result:     OK - close completed, now in authenticated state
               NO - close failure: no mailbox selected
               BAD - command unknown or arguments invalid

      The CLOSE command permanently removes from the currently selected
      mailbox all messages that have the \Deleted flag set, and returns
      to authenticated state from selected state.  No untagged EXPUNGE
      responses are sent.

      No messages are removed, and no error is given, if the mailbox is
      selected by an EXAMINE command or is otherwise selected read-only.

      Even if a mailbox is selected, a SELECT, EXAMINE, or LOGOUT
      command MAY be issued without previously issuing a CLOSE command.
      The SELECT, EXAMINE, and LOGOUT commands implicitly close the
      currently selected mailbox without doing an expunge.  However,
      when many messages are deleted, a CLOSE-LOGOUT or CLOSE-SELECT

      sequence is considerably faster than an EXPUNGE-LOGOUT or
      EXPUNGE-SELECT because no untagged EXPUNGE responses (which the
      client would probably ignore) are sent.

   Example:    C: A341 CLOSE
               S: A341 OK CLOSE completed
*/
