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
package com.icegreen.greenmail.smtp;

import com.icegreen.greenmail.AbstractServer;
import com.icegreen.greenmail.Managers;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.smtp.commands.SmtpCommandRegistry;
import com.icegreen.greenmail.foedus.util.InMemoryWorkspace;

import java.io.IOException;
import java.net.SocketException;
import java.net.Socket;
import java.util.Vector;
import java.util.Iterator;

public class SmtpServer extends AbstractServer {

    public SmtpServer(ServerSetup setup, Managers managers) {
        super(setup, managers);
    }

    public synchronized void quit() {
        try {
            for (Iterator iterator = handlers.iterator(); iterator.hasNext();) {
                SmtpHandler smtpHandler = (SmtpHandler) iterator.next();
                smtpHandler.quit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            if (null != serverSocket && !serverSocket.isClosed()) {
                serverSocket.close();
                serverSocket = null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            try {
                serverSocket = openServerSocket();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (keepOn()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    SmtpHandler smtpHandler = new SmtpHandler(new SmtpCommandRegistry(), managers.getSmtpManager(), new InMemoryWorkspace(), clientSocket);
                    handlers.add(smtpHandler);
                    smtpHandler.start();
                } catch (SocketException ignored) {
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } finally{
            quit();
        }
    }
}