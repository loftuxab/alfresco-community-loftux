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
/*
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 * This file has been used and modified. Original file can be found on http://foedus.sourceforge.net
 */
package com.icegreen.greenmail.smtp.commands;

import java.util.HashMap;
import java.util.Map;


public class SmtpCommandRegistry {
    private static Map commands = new HashMap();
    private static Object[][] COMMANDS = new Object[][]
    {
        {"HELO", new HeloCommand()}, {"EHLO", new HeloCommand()},
        {"NOOP", new NoopCommand()}, {"RSET", new RsetCommand()},
        {"QUIT", new QuitCommand()}, {"MAIL", new MailCommand()},
        {"RCPT", new RcptCommand()}, {"DATA", new DataCommand()},
        {"VRFY", new VrfyCommand()}
    };

    public void load()
            throws Exception {
        for (int i = 0; i < COMMANDS.length; i++) {
            String name = COMMANDS[i][0].toString();

            if (commands.containsKey(name))

                continue;

            try {
                SmtpCommand command = (SmtpCommand) COMMANDS[i][1];
                registerCommand(name, command);
            } catch (Exception e) {
                throw e;
            }
        }
    }

    private void registerCommand(String name, SmtpCommand command) {
        commands.put(name, command);
    }

    public SmtpCommand getCommand(String name) {
        if (commands.size() == 0) {
            try {
                load();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (SmtpCommand) commands.get(name);
    }
}