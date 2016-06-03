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

import com.icegreen.greenmail.util.InternetPrintWriter;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class SmtpConnection {

    // TODO: clean up getting localhost name
    private static final int TIMEOUT_MILLIS = 1000 * 30;
    private InetAddress serverAddress;


    {
        try {
            serverAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException uhe) {
        }
    }

    // networking/io stuff
    Socket sock;
    InetAddress clientAddress;
    InternetPrintWriter out;
    BufferedReader in;
    SmtpHandler handler;
    String heloName;

    public SmtpConnection(SmtpHandler handler, Socket sock)
            throws IOException {
        this.sock = sock;
        sock.setSoTimeout(TIMEOUT_MILLIS);
        clientAddress = sock.getInetAddress();
        OutputStream o = sock.getOutputStream();
        InputStream i = sock.getInputStream();
        out = new InternetPrintWriter(o, true);
        in = new BufferedReader(new InputStreamReader(i));

        this.handler = handler;
    }

    /**
     * For testing only
     */
    SmtpConnection() {
    }

    public void println(String line) {

        // System.err.println("S: " + line);
        out.println(line);
    }

    public BufferedReader getReader() {

        return in;
    }

    public String readLine()
            throws IOException {
        String line = in.readLine();

        // System.err.println("C: " + line);
        return line;
    }

    public String getClientAddress() {

        return clientAddress.getHostName();
    }

    public InetAddress getServerAddress() {

        return serverAddress;
    }

    public String getServerGreetingsName() {
        InetAddress serverAddress = getServerAddress();

        if (serverAddress != null)

            return serverAddress.toString();
        else

            return System.getProperty("user.name");
    }

    public String getHeloName() {

        return heloName;
    }

    public void setHeloName(String n) {
        heloName = n;
    }

    public void quit() {
        handler.quit();
    }
}