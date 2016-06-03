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
package com.icegreen.greenmail.pop3;


import com.icegreen.greenmail.pop3.commands.Pop3Command;
import com.icegreen.greenmail.pop3.commands.Pop3CommandRegistry;
import com.icegreen.greenmail.user.UserManager;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.StringTokenizer;


public class Pop3Handler extends Thread {
    Pop3CommandRegistry _registry;
    Pop3Connection _conn;
    UserManager _manager;
    Pop3State _state;
    boolean _quitting;
    String _currentLine;
    private Socket _socket;

    public Pop3Handler(Pop3CommandRegistry registry,
                       UserManager manager, Socket socket) {
        _registry = registry;
        _manager = manager;
        _socket = socket;
    }

    public void run() {
        try {
            _conn = new Pop3Connection(this, _socket);
            _state = new Pop3State(_manager);

            _quitting = false;

            sendGreetings();

            while (!_quitting) {
                handleCommand();
            }

            _conn.close();
        } catch (SocketTimeoutException ste) {
            _conn.println("421 Service shutting down and closing transmission channel");

        } catch (Exception e) {
        } finally {
            try {
                _socket.close();
            } catch (IOException ioe) {
            }
        }

    }

    void sendGreetings() {
        _conn.println("+OK POP3 GreenMail Server ready");
    }

    void handleCommand()
            throws IOException {
        _currentLine = _conn.readLine();

        if (_currentLine == null) {
            quit();

            return;
        }

        String commandName = new StringTokenizer(_currentLine, " ").nextToken()
                .toUpperCase();

        Pop3Command command = _registry.getCommand(commandName);

        if (command == null) {
            _conn.println("-ERR Command not recognized");

            return;
        }

        if (!command.isValidForState(_state)) {
            _conn.println("-ERR Command not valid for this state");

            return;
        }

        command.execute(_conn, _state, _currentLine);
    }

    public void quit() {
         _quitting = true;
        try {
            if (_socket != null && !_socket.isClosed()) {
                _socket.close();
            }
        } catch(IOException ignored) {
            //empty
        } 
    }
}