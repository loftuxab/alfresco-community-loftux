/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.module.vti.handler.alfresco.v2;

import java.util.List;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoUserGroupServiceHandler;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.model.UserBean;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.PermissionService;

/**
 * Alfresco implementation of UserGroupServiceHandler and AbstractAlfrescoUserGroupServiceHandler
 * 
 * @author Dmitry Lazurkin
 */
public class AlfrescoUserGroupServiceHandler extends AbstractAlfrescoUserGroupServiceHandler
{
    private PermissionService permissionService;
    private VtiPathHelper pathHelper;

    /**
     * Set permission service
     * 
     * @param permissionService the permission service to set
     */
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    /**
     * Set path helper
     * 
     * @param pathHelper the path helper to set
     */
    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    /**
     * @see org.alfresco.module.vti.handler.UserGroupServiceHandler#addUserCollectionToRole(java.lang.String, java.lang.String, java.util.List)
     */
    public void addUserCollectionToRole(String dwsUrl, String roleName, List<UserBean> usersList)
    {
        NodeRef dwsNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, dwsUrl);

        FileInfo dwsFileInfo = pathHelper.getFileFolderService().getFileInfo(dwsNodeRef);

        if (dwsFileInfo == null)
        {
            throw new VtiHandlerException(VtiHandlerException.NOT_FOUND);
        }

        if (dwsFileInfo.isFolder() == false)
        {
            throw new VtiHandlerException(VtiHandlerException.BAD_URL);
        }

        for (UserBean userBean : usersList)
        {
            if (personService.personExists(userBean.getLoginName().substring("ALFRESCO\\".length())))
            {
                permissionService.setPermission(dwsFileInfo.getNodeRef(), userBean.getLoginName().substring("ALFRESCO\\".length()), roleName, true);
            }
            else
            {
                // The user does not have sufficient rights
                throw new VtiHandlerException(VtiHandlerException.NOT_PERMISSIONS);
            }
        }
    }

    /**
     * @see org.alfresco.module.vti.handler.UserGroupServiceHandler#isUserMember(java.lang.String, java.lang.String)
     */
    public boolean isUserMember(String dwsUrl, String username)
    {
        return true;
    }
}
