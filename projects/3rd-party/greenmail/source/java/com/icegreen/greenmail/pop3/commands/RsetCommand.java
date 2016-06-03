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
package com.icegreen.greenmail.pop3.commands;

import java.util.List;
import javax.mail.Flags;

import com.icegreen.greenmail.pop3.Pop3Connection;
import com.icegreen.greenmail.pop3.Pop3State;
import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.store.SimpleStoredMessage;

/**
 * Handles the RSET command.
 *
 * See http://www.ietf.org/rfc/rfc1939.txt:
 *
 * Arguments: none
 *
 * Restrictions:
 *   May only be given in the TRANSACTION state.
 *
 * Discussion:
 *   If any messages have been marked as deleted by the POP3
 *   server, they are unmarked.  The POP3 server then replies
 *   with a positive response.
 *
 * Possible Responses:
 *   +OK
 *
 * Examples:
 *   C: RSET
 *   S: +OK maildrop has 2 messages (320 octets)
 *
 * @author Marcel May
 * @version $Id: $
 * @since Dec 21, 2006
 */
public class RsetCommand extends Pop3Command {
    public boolean isValidForState(Pop3State state) {
        return true;
    }

    public void execute(Pop3Connection conn, Pop3State state, String cmd) {
        conn.println("+OK");
        try {
            MailFolder inbox = state.getFolder();
            List msgList = inbox.getMessages();
            int count = 0;
            for(int i=0;i<msgList.size();i++) {
                SimpleStoredMessage msg = (SimpleStoredMessage) msgList.get(i);
                Flags flags = msg.getFlags();
                if (flags.contains(Flags.Flag.DELETED)) {
                    count++;
                    flags.remove(Flags.Flag.DELETED);
                }
            }

            conn.println("+OK maildrop has "+count+" messages undeleted.");
        } catch (Exception e) {
            conn.println("-ERR " + e);
        }
    }
}
