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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementSearchBehaviour;
import org.alfresco.repo.web.scripts.content.StreamACP;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.view.ExporterCrawlerParameters;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Streams the nodes of a transfer object to the client in the form of an
 * ACP file.
 * 
 * @author Gavin Cornwell
 */
public class TransferGet extends StreamACP
{
    /** Logger */
    private static Log logger = LogFactory.getLog(TransferGet.class);
    
    /**
     * @see org.alfresco.web.scripts.WebScript#execute(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        File tempArchiveFile = null;
        try
        {
            // retrieve requested format
            String format = req.getFormat();
            
            // construct model for template
            Status status = new Status();
            Cache cache = new Cache(getDescription().getRequiredCache());
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("status", status);
            model.put("cache", cache);
            
            // get the parameters that represent the NodeRef, we know they are present
            // otherwise this webscript would not have matched
            Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
            String storeType = templateVars.get("store_type");
            String storeId = templateVars.get("store_id");
            String nodeId = templateVars.get("id");
            String transferId = templateVars.get("transfer_id");
            
            // create the NodeRef and ensure it is valid
            StoreRef storeRef = new StoreRef(storeType, storeId);
            NodeRef nodeRef = new NodeRef(storeRef, nodeId);
            
            if (logger.isDebugEnabled())
                logger.debug("Retrieving transfer '" + transferId + "' from file plan: " + nodeRef);
            
            if (!this.nodeService.exists(nodeRef))
            {
                status.setCode(HttpServletResponse.SC_NOT_FOUND, 
                            "Node " + nodeRef.toString() + " does not exist");
                Map<String, Object> templateModel = createTemplateParameters(req, res, model);
                sendStatus(req, res, status, cache, format, templateModel);
                return;
            }
            
            // ensure the node is a filePlan object
            if (!DOD5015Model.TYPE_FILE_PLAN.equals(this.nodeService.getType(nodeRef)))
            {
                status.setCode(HttpServletResponse.SC_BAD_REQUEST, 
                            "Node " + nodeRef.toString() + " is not a file plan");
                Map<String, Object> templateModel = createTemplateParameters(req, res, model);
                sendStatus(req, res, status, cache, format, templateModel);
                return;
            }
            
            // get all the transfer nodes and find the one we need
            List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(nodeRef, 
                        RecordsManagementModel.ASSOC_TRANSFERS, RegexQNamePattern.MATCH_ALL);
            NodeRef transferNodeRef = null;
            for (ChildAssociationRef child : assocs)
            {
                if (child.getChildRef().getId().equals(transferId))
                {
                    transferNodeRef = child.getChildRef();
                    break;
                }
            }
            
            // send 404 if the transfer is not found
            if (transferNodeRef == null)
            {
                status.setCode(HttpServletResponse.SC_NOT_FOUND, 
                            "Could not locate transfer with id: " + transferId);
                Map<String, Object> templateModel = createTemplateParameters(req, res, model);
                sendStatus(req, res, status, cache, format, templateModel);
                return;
            }
            
            // get all 'transferred' nodes and create as an array of NodeRefs
            assocs = this.nodeService.getChildAssocs(transferNodeRef, 
                        RecordsManagementModel.ASSOC_TRANSFERRED, RegexQNamePattern.MATCH_ALL);
            NodeRef[] itemsToTransfer = new NodeRef[assocs.size()];
            for (int idx = 0; idx < assocs.size(); idx++)
            {
                itemsToTransfer[idx] = assocs.get(idx).getChildRef();
            }
            
            // setup the ACP parameters
            ExporterCrawlerParameters params = new ExporterCrawlerParameters();
            params.setCrawlSelf(true);
            params.setCrawlChildNodes(true);
            params.setExportFrom(new Location(itemsToTransfer));
            QName[] excludedAspects = new QName[] { 
                        ContentModel.ASPECT_THUMBNAILED, 
                        RecordsManagementModel.ASPECT_DISPOSITION_LIFECYCLE,
                        RecordsManagementSearchBehaviour.ASPECT_RM_SEARCH};
            params.setExcludeAspects(excludedAspects);
            
            // create an archive of all the nodes to transfer
            tempArchiveFile = createACP(params, ZIP_EXTENSION, true);
            
            // stream the archive back to the client as an attachment (forcing save as)
            streamContent(req, res, tempArchiveFile, true, tempArchiveFile.getName());
        }
        catch (Throwable e)
        {
            throw createStatusException(e, req, res);
        }
        finally
        {
           // try and delete the temporary file
           if (tempArchiveFile != null)
           {
               tempArchiveFile.delete();
               
               if (logger.isDebugEnabled())
                   logger.debug("Deleted temporary archive: " + tempArchiveFile.getAbsolutePath());
           }
        }
    }
}