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
package org.alfresco.module.vti.metadata.soap.dws;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.vti.metadata.soap.SoapUtils;

/**
 * @author AndreyAk
 *
 */
public class ListInfoBean implements Serializable
{

    private static final long serialVersionUID = 216886247863517038L;
    
    private String name;
    private boolean moderated;
    private List<Permission> permissionList;
    
    /**
     * @param name
     * @param moderated
     * @param permissionList
     */
    public ListInfoBean(String name, boolean moderated, List<Permission> permissionList)
    {
        super();
        this.name = name;
        this.moderated = moderated;
        this.permissionList = permissionList;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the moderated
     */
    public boolean isModerated()
    {
        return moderated;
    }
    /**
     * @param moderated the moderated to set
     */
    public void setModerated(boolean moderated)
    {
        this.moderated = moderated;
    }
    /**
     * @return the permissionList
     */
    public List<Permission> getPermissionList()
    {
        return permissionList;
    }
    /**
     * @param permissionList the permissionList to set
     */
    public void setPermissionList(List<Permission> permissionList)
    {
        this.permissionList = permissionList;
    }
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder("");
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("Name", name);
        
        result.append(SoapUtils.startTag("ListInfo", attributes));
        result.append(SoapUtils.proccesTag("Moderated", moderated));
        result.append(SoapUtils.startTag("ListPermissions"));
        for (Permission permission : permissionList)
        {
            result.append(SoapUtils.singleTag(permission.toString()));
        }
        result.append(SoapUtils.endTag("ListPermissions"));
        result.append(SoapUtils.endTag("ListInfo"));
        return result.toString();
    }
}
