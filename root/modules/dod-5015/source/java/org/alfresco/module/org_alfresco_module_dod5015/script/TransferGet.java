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

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementSearchBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.view.ExporterCrawlerParameters;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Streams the nodes of a transfer object to the client in the form of an
 * ACP file.
 * 
 * @author Gavin Cornwell
 */
public class TransferGet extends BaseTransferWebScript
{
    /** Logger */
    private static Log logger = LogFactory.getLog(TransferGet.class);
    
    @Override
    protected File executeTransfer(NodeRef transferNode,
                WebScriptRequest req, WebScriptResponse res, 
                Status status, Cache cache) throws IOException
    {
        // get all 'transferred' nodes
        NodeRef[] itemsToTransfer = getTransferNodes(transferNode);
        
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
        File tempFile = createACP(params, ZIP_EXTENSION, true);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Creating transfer archive for " + itemsToTransfer.length + 
                        " items into file: " + tempFile.getAbsolutePath());
        }
        
        // stream the archive back to the client as an attachment (forcing save as)
        streamContent(req, res, tempFile, true, tempFile.getName());
        
        // return the temp file for deletion
        return tempFile;
    }
}