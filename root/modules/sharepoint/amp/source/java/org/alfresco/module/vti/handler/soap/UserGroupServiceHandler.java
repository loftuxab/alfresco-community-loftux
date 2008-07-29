/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * and Open Source Software ("FLOSS") applications as described in Alfresco's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.vti.handler.soap;

import java.util.List;

import org.alfresco.module.vti.metadata.soap.usergroup.UserBean;

/**
 * @author AndreyAk
 *
 */
public interface UserGroupServiceHandler
{

    /**
     * Returns a user name based on the specified e-mail address.
     * @param dwsUrl dws url
     * @param emailList  list that specifies the e-mail address of the user
     *
     * @return UserBean
     */
    List<UserBean> getUserLoginFromEmail(String dwsUrl, List<String> emailList);

    /**
     * Adds the collection of users to the specified site group.
     * @param dwsUrl  dws url
     * @param roleName name of the site group to add users to
     * @param usersList list that contains information about the users to add
     */
    void addUserCollectionToRole(String dwsUrl, String roleName, List<UserBean> usersList);

}
