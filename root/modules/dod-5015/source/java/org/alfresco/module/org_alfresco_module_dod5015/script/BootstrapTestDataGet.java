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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.DeclarativeWebScript;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * BootstrapTestData GET WebScript implementation.
 */
public class BootstrapTestDataGet extends DeclarativeWebScript
                                  implements RecordsManagementModel
{
    private static Log logger = LogFactory.getLog(BootstrapTestDataGet.class);
    
    private static final String COMPONENT_DOCUMENT_LIBRARY = "documentLibrary";
    private static final String ARG_SITE_NAME = "site";
    private static final String ARG_IMPORT = "import";
    private static final String DEFAULT_SITE_NAME = "rm";
    
    private static final StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    
    private NodeService nodeService;
    private SearchService searchService;
    private RecordsManagementService recordsManagementService;
    private RecordsManagementActionService recordsManagementActionService;
    private ImporterService importerService;
    private SiteService siteService;
    
    
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
    
    public void setImporterService(ImporterService importerService) 
    {
        this.importerService = importerService;
    }
    
    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }
    
    
    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // resolve import argument
        boolean importData = false;
        if (req.getParameter(ARG_IMPORT) != null)
        {
            importData = Boolean.parseBoolean(req.getParameter(ARG_IMPORT));
        }
        
        // resolve rm site
        String siteName = DEFAULT_SITE_NAME;
        if (req.getParameter(ARG_SITE_NAME) != null)
        {
            siteName = req.getParameter(ARG_SITE_NAME);
        }
        SiteInfo site = siteService.getSite(siteName);
        if (site == null)
        {
            throw new AlfrescoRuntimeException("Records Management site does not exist: " + siteName);
        }
        
        // resolve documentLibrary (filePlan) container
        NodeRef filePlan = siteService.getContainer(siteName, COMPONENT_DOCUMENT_LIBRARY);
        if (filePlan == null)
        {
            filePlan = siteService.createContainer(siteName, COMPONENT_DOCUMENT_LIBRARY, DOD5015Model.TYPE_FILE_PLAN, null);
        }
        
        if (importData)
        {
            // import the RM test data ACP into the the provided filePlan node reference
            InputStream is = BootstrapTestDataGet.class.getClassLoader().getResourceAsStream(
                    "alfresco/module/org_alfresco_module_dod5015/bootstrap/DODExampleFilePlan.xml");
            if (is == null)
            {
                throw new AlfrescoRuntimeException("The DODExampleFilePlan.xml import file could not be found");
            }
            Reader viewReader = new InputStreamReader(is);
            Location location = new Location(filePlan);
            importerService.importView(viewReader, location, null, null);
        }
        
        // fix up the test dataset to fire initial events for disposition schedules
        ResultSet rs = searchService.query(SPACES_STORE, SearchService.LANGUAGE_LUCENE, "TYPE:\"rma:recordFolder\"");
        try
        {
            for (NodeRef recordFolder : rs.getNodeRefs())
            {
                if (nodeService.hasAspect(recordFolder, ASPECT_DISPOSITION_LIFECYCLE) == false)
                {
                    // See if the folder has a disposition schedule that needs to be applied
                    DispositionSchedule ds = recordsManagementService.getDispositionSchedule(recordFolder);
                    if (ds != null)
                    {
                        // Fire action to "set-up" the folder correctly
                        String folderName = (String)nodeService.getProperty(recordFolder, ContentModel.PROP_NAME);
                        logger.info("Setting up bootstraped record folder " + folderName);
                        recordsManagementActionService.executeRecordsManagementAction(recordFolder, "setupRecordFolder");
                    }
                }
            }
        }
        finally
        {
            rs.close();
        }
        
        Map<String, Object> model = new HashMap<String, Object>(1, 1.0f);
    	model.put("success", true);
    	
        return model;
    }
}