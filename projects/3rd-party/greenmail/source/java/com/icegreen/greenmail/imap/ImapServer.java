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
*
* 2012 - Alfresco Software, Ltd.
* Alfresco Software has modified source of this file
* The details of changes as svn diff can be found in svn at location root/projects/3rd-party/src
*/
package com.icegreen.greenmail.imap;

import com.icegreen.greenmail.AbstractServer;
import com.icegreen.greenmail.Managers;
import com.icegreen.greenmail.util.DummySSLServerSocketFactory;
import com.icegreen.greenmail.util.ServerSetup;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.SSLServerSocket;


public class ImapServer extends AbstractServer {

    private AtomicReference<Exception> serverOpeningExceptionRef;

    public ImapServer(ServerSetup setup, Managers managers) {
        super(setup, managers);
    }
    
    public ImapServer(ServerSetup setup, Managers managers, AtomicReference<Exception> serverOpeningExceptionRef ) {
        this(setup, managers);
        this.serverOpeningExceptionRef = serverOpeningExceptionRef;
    }
    
    protected synchronized ServerSocket openServerSocket() throws IOException {
        ServerSocket ret;
        if (setup.isSecure()) {
            ret = (SSLServerSocket) DummySSLServerSocketFactory.getDefault().createServerSocket(
                    setup.getPort(), 0, bindTo);
        } else {
            ret = new ServerSocket(setup.getPort(), 0, bindTo);
        }
        return ret;
    }


    public synchronized void quit() {
        try {
            List copyOfData = new ArrayList(handlers);
            for (Iterator iterator = copyOfData.iterator(); iterator.hasNext();)
            {
                ImapHandler imapHandler = (ImapHandler) iterator.next();
                imapHandler.resetHandler();
            }
            handlers.clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            if (null != serverSocket && !serverSocket.isClosed()) {
                serverSocket.close();
                serverSocket = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            IOException serverOpeningException = null;
            try {
                serverSocket = openServerSocket();
            } catch (IOException e) {
                serverOpeningException = e;
                throw new RuntimeException(e);
            } finally {
                if (serverOpeningExceptionRef != null){
                    synchronized (serverOpeningExceptionRef) {
                        serverOpeningExceptionRef.set(serverOpeningException);
                        serverOpeningExceptionRef.notify();
                    }
                }
            }

            while (keepOn()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ImapHandler imapHandler = new ImapHandler(managers.getUserManager(), managers.getImapHostManager(), clientSocket, this);
                    handlers.add(imapHandler);
                    imapHandler.start();
                } catch (IOException ignored) {
                    //ignored
                }
            }
        } finally{
            quit();
        }
    }
}