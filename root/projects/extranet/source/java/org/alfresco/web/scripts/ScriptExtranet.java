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
package org.alfresco.web.scripts;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.alfresco.extranet.ExtranetHelper;
import org.alfresco.extranet.jira.JIRAClient;
import org.alfresco.extranet.jira.JIRAService;
import org.alfresco.web.framework.cache.BasicCache;
import org.alfresco.web.framework.cache.ContentCache;
import org.alfresco.web.site.HttpRequestContext;
import org.alfresco.web.site.RequestContext;

import com.dolby.jira.net.soap.jira.RemoteIssue;

/**
 * @author muzquiano
 */
public final class ScriptExtranet extends ScriptBase
{
    private static final String WEBSCRIPTS_REGISTRY = "webframework.webscripts.registry";
    
    public ScriptExtranet(RequestContext context)
    {
        super(context);
    }
    
    // no properties
    public ScriptableMap buildProperties()
    {
        return null;
    }
    
    public JIRAClient getJIRAClient()
    {
        JIRAClient jiraClient = null;
        
        JIRAService jiraService = ExtranetHelper.getJIRAService(((HttpRequestContext)context).getRequest());
        if(jiraService != null)
        {
            jiraClient = jiraService.getClient();
        }
        
        return jiraClient;
    }
    
    public static ContentCache checkInsCache = null;
    
    public synchronized Object[] getCheckIns(String filterId, int start, int end)
    {
        if(checkInsCache == null)
        {
            checkInsCache = new BasicCache(1000*60*5);  // five minutes
        }
        
        // cache key
        String key = filterId;
        
        // check to see if we already have this in the cache        
        RemoteIssue[] issues = (RemoteIssue[]) checkInsCache.get(key);
        if(issues == null)
        {
            String token = getJIRAClient().getToken();
            try
            {
                issues = getJIRAClient().getJiraSOAPService().getIssuesFromFilter(token, filterId);
                checkInsCache.put(key, issues);                
            }
            catch(RemoteException re)
            {
                re.printStackTrace();
            }
        }
        
        RemoteIssue[] array = null;
        if(issues != null)
        {        
            ArrayList arrayList = new ArrayList();
            for(int i = 0; i < (end - start); i++)
            {
                if(issues.length > start + i)
                {
                    arrayList.add(issues[start+i]);
                }
            }
            
            array = (RemoteIssue[]) arrayList.toArray(new RemoteIssue[arrayList.size()]);
        }
        
        return array;
    }
    
}
