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
package org.alfresco.web.scripts;

import org.alfresco.connector.Connector;
import org.alfresco.connector.exception.ConnectorServiceException;
import org.alfresco.web.framework.ImportTask;
import org.alfresco.web.framework.Task;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.studio.WebStudio;

/**
 * Utility for importing Surf assets from a remote location into the
 * Surf environment
 * 
 * @author muzquiano
 */
public final class ScriptImporter extends ScriptBase
{
    /**
     * Constructs a new ScriptImporter object.
     * 
     * @param context The RequestContext instance for the current
     *            request
     */
    public ScriptImporter(RequestContext context)
    {
        super(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.ScriptBase#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
        }

        return this.properties;
    }

    // --------------------------------------------------------------
    // JavaScript Properties
    //    

    /**
     * Imports a Surf archive into the given store from a URL location
     * 
     * A task id is returned so that the task can be monitored asynchrously 
     * 
     * @param store
     * @param webappId empty or webapp id
     * @param url
     * 
     * @return task id
     */
    public String importArchive(String store, String webappId, String url)
    {
        String taskId = null;
        
        String taskName = "Import from: " + url;

        // allow for resolution of relative url's
        if (url.startsWith("/"))
        {
            String baseUrl = context.getRequest().getScheme() + "://"
                    + context.getRequest().getServerName();

            if (context.getRequest().getServerPort() != 80)
            {
                baseUrl += ":" + context.getRequest().getServerPort();
            }            
            baseUrl += context.getRequest().getContextPath();
            
            url = baseUrl + url;
        }
        
        // create the alfresco connector ahead of time
        Connector alfrescoConnector = null;
        try
        {
            alfrescoConnector = FrameworkHelper.getConnector(context, "alfresco");        

            // create the task
            ImportTask task = new ImportTask(taskName);
            task.setAlfrescoConnector(alfrescoConnector);
            task.setStoreId(store);
            task.setWebappId(webappId);
            task.setUrl(url);
            
            // add the task to the task manager
            taskId = WebStudio.getTaskManager().addTask(task);
            
        }
        catch(ConnectorServiceException cse)
        {
            FrameworkHelper.getLogger().warn("Unable to load 'alfresco' endpoint connector for script import", cse);
        }
        
        return taskId;
    }
    
    /**
     * Returns a task with the given task id
     * 
     * @param taskId
     * 
     * @return task
     */
    public Task getTask(String taskId)
    {
        return WebStudio.getTaskManager().getTask(taskId);
    }
}
