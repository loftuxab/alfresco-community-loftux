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
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

import javax.net.ssl.SSLServerSocket;

public class ImapServer extends AbstractServer {

    public ImapServer(ServerSetup setup, Managers managers) {
        super(setup, managers);
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

            try {
                serverSocket = openServerSocket();
            } catch (IOException e) {
                throw new RuntimeException(e);
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