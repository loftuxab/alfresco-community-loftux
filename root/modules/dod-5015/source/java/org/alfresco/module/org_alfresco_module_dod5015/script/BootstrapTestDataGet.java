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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.DeclarativeWebScript;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 */
public class BootstrapTestDataGet extends DeclarativeWebScript
                                  implements RecordsManagementModel
{
    private static Log logger = LogFactory.getLog(BootstrapTestDataGet.class);
    
    private static final StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    
    private NodeService nodeService;
    private SearchService searchService;
    private RecordsManagementService recordsManagementService;
    private RecordsManagementActionService recordsManagementActionService;
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    public void setRecordsManagementService(RecordsManagementService recordsManagementService)
    {
        this.recordsManagementService = recordsManagementService;
    }
    
    public void setRecordsManagementActionService(RecordsManagementActionService recordsManagementActionService)
    {
        this.recordsManagementActionService = recordsManagementActionService;
    }
    
    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        boolean result = true;
        Map<String, Object> model = new HashMap<String, Object>();
        
        try
        {
            ResultSet rs = searchService.query(SPACES_STORE, SearchService.LANGUAGE_LUCENE, "TYPE:\"rma:recordFolder\"");
            try
            {
                for (NodeRef recordFolder : rs.getNodeRefs())
                {
                    String folderName = null;
                    try
                    {
                        if (nodeService.hasAspect(recordFolder, ASPECT_DISPOSITION_LIFECYCLE) == false)
                        {
                            // See if the folder has a disposition schedule that needs to be applied
                            DispositionSchedule ds = recordsManagementService.getDispositionSchedule(recordFolder);
                            if (ds != null)
                            {
                                // Fire action to "set-up" the folder correctly
                                folderName = (String)nodeService.getProperty(recordFolder, ContentModel.PROP_NAME);
                                logger.info("Setting up bootstraped record folder " + folderName);
                                recordsManagementActionService.executeRecordsManagementAction(recordFolder, "setupRecordFolder");
                            }
                        }
                    }
                    catch (Throwable exception)
                    {
                        logger.info("Unable to bootstrap record folder " + folderName);
                        throw exception;
                    }
                }
            }
            finally
            {
                rs.close();
            }
        }
        catch (Throwable exception)
        {
            exception.printStackTrace();
            result = false;
        }
        
    	model.put("success", result);
    	
        return model;
    }
}