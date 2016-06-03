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
package com.icegreen.greenmail.user;

import com.icegreen.greenmail.imap.ImapHostManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class UserManager {
    Map _users = Collections.synchronizedMap(new HashMap());
    private ImapHostManager imapHostManager;

    public UserManager(ImapHostManager imapHostManager) {
        this.imapHostManager = imapHostManager;
    }

    public GreenMailUser getUser(String login) {
        return (GreenMailUser) _users.get(login);
    }

    public GreenMailUser getUserByEmail(String email) {
        GreenMailUser ret = getUser(email);
        if (null == ret) {
            for (Iterator it = _users.values().iterator(); it.hasNext();) {
                GreenMailUser u = (GreenMailUser) it.next();
                if (u.getEmail().trim().equalsIgnoreCase(email.trim())) {
                    return u;
                }
            }
        }
        return ret;
    }

    public GreenMailUser createUser(String name, String login, String password) throws UserException {
        GreenMailUser user = new UserImpl(name, login, password, imapHostManager);
        user.create();
        addUser(user);
        return user;
    }

    private void addUser(GreenMailUser user) {
        _users.put(user.getLogin(), user);
    }

    public void deleteUser(GreenMailUser user)
            throws UserException {
        user = (GreenMailUser) _users.remove(user.getLogin());
        if (user != null)
            user.delete();
    }

    public boolean test(String userid, String password) {
        GreenMailUser u = getUser(userid);
        if (null == u) {
            return false;
        }

        return u.getPassword().equals(password);
    }

    public ImapHostManager getImapHostManager() {
        return imapHostManager;
    }


}