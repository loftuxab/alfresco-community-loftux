/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.web.site;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.alfresco.connector.User;
import org.alfresco.extranet.ExtranetHelper;
import org.alfresco.extranet.database.DatabaseGroup;
import org.alfresco.extranet.database.DatabaseUser;
import org.alfresco.web.site.exception.PageMapperException;

/**
 * @author muzquiano
 */
public class ExtranetPageMapper extends DefaultPageMapper
{
    public ExtranetPageMapper()
    {
        super();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.AbstractPageMapper#execute(org.alfresco.web.site.RequestContext, javax.servlet.ServletRequest)
     */
    public void execute(RequestContext context, ServletRequest request)
        throws PageMapperException
    {
        super.execute(context, request);
        
        // based on the user's properties, flip the theme
        User user = context.getUser();
        if(user != null)
        {
            String userId = user.getId();
            if(!"guest".equals(userId))
            {
                // non-guest user
                
                // get the user's groups
                List groupsList = ExtranetHelper.getGroupService((HttpServletRequest)request).getGroupsForUser(userId);
                
                // build a map
                HashMap groupsMap = new HashMap<String, String>();
                for(int i = 0; i < groupsList.size(); i++)
                {
                    DatabaseGroup dbGroup = (DatabaseGroup) groupsList.get(i);
                    if(dbGroup != null)
                    {
                        groupsMap.put(dbGroup.getEntityId(), dbGroup.getName());
                    }
                } 
                
                // store the groups onto the request context
                context.setValue("groups", groupsMap);
                
                if(groupsMap.get("enterprise") != null)
                {
                    // they are an enterprise user
                    ThemeUtil.setCurrentThemeId((HttpServletRequest)request, "enterprise");
                    context.setThemeId("enterprise");
                }
            }
        }
    }    
}
