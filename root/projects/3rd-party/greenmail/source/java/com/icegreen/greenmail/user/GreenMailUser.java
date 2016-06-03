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
 * Copyright (C) 2006 Wael Chatila. All Rights Reserved.
 * Permission to use, read, view, run, copy, compile, link or any
 * other way use this code without prior permission
 * of the copyright holder is not permitted.
 */
package com.icegreen.greenmail.user;

import com.icegreen.greenmail.mail.MovingMessage;

import javax.mail.internet.MimeMessage;


public interface GreenMailUser {
    String getEmail();
    String getLogin();

    void deliver(MovingMessage msg) throws UserException;
    void deliver(MimeMessage msg) throws UserException;

    void create() throws UserException;

    void delete()
            throws UserException;

    String getPassword();

    void setPassword(String password);

    void authenticate(String password)
            throws UserException;

    String getQualifiedMailboxName();

}