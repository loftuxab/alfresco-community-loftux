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
import org.alfresco.repo.exporter.ACPExportPackageHandler;
import org.alfresco.repo.web.scripts.content.StreamACP;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.view.ExporterCrawlerParameters;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Creates an RM specific ACP file of nodes to export then streams it back
 * to the client.
 * 
 * @author Gavin Cornwell
 */
public class ExportPost extends StreamACP
{
    /** Logger */
    private static Log logger = LogFactory.getLog(ExportPost.class);

    protected static final String PARAM_TRANSFER_FORMAT = "transferFormat";
            
    /**
     * @see org.alfresco.web.scripts.WebScript#execute(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        File tempACPFile = null;
        try
        {
            NodeRef[] nodeRefs = null;
            boolean transferFormat = false;
            String contentType = req.getContentType();
            if (MULTIPART_FORMDATA.equals(contentType))
            {
                // get nodeRefs parameter from form
                nodeRefs = getNodeRefs(req.getParameter(PARAM_NODE_REFS));
                
                // look for the transfer format
                String transferFormatParam = req.getParameter(PARAM_TRANSFER_FORMAT);
                if (transferFormatParam != null && transferFormatParam.length() > 0)
                {
                    transferFormat = Boolean.parseBoolean(transferFormatParam);
                }
            }
            else
            {
                // presume the request is a JSON request so get nodeRefs from JSON body
                JSONObject json = new JSONObject(new JSONTokener(req.getContent().getContent()));
                nodeRefs = getNodeRefs(json);
                
                if (json.has(PARAM_TRANSFER_FORMAT))
                {
                    transferFormat = json.getBoolean(PARAM_TRANSFER_FORMAT);
                }
            }
            
            // setup the ACP parameters
            ExporterCrawlerParameters params = new ExporterCrawlerParameters();
            params.setCrawlSelf(true);
            params.setCrawlChildNodes(true);
            params.setExportFrom(new Location(nodeRefs));
            
            // if transfer format has been requested we need to exclude certain aspects
            if (transferFormat)
            {
                // restrict specific aspects from being returned
                QName[] excludedAspects = new QName[] { 
                            ContentModel.ASPECT_THUMBNAILED, 
                            RecordsManagementModel.ASPECT_DISPOSITION_LIFECYCLE};
                params.setExcludeAspects(excludedAspects);
            }
            
            // create an ACP of the nodes
            tempACPFile = createACP(params, 
                        transferFormat ? ZIP_EXTENSION : ACPExportPackageHandler.ACP_EXTENSION, 
                        // TODO: change keepFolderStructure parameter to true once auth issues are sorted
                        false);
                
            // stream the ACP back to the client as an attachment (forcing save as)
            streamContent(req, res, tempACPFile, true, tempACPFile.getName());
        } 
        catch (IOException ioe)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "Could not read content from req.", ioe);
        }
        catch (JSONException je)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                        "Could not parse JSON from req.", je);
        }
        finally
        {
           // try and delete the temporary file
           if (tempACPFile != null)
           {
               tempACPFile.delete();
               
               if (logger.isDebugEnabled())
                   logger.debug("Deleted temporary archive: " + tempACPFile.getAbsolutePath());
           }
        }
    }
}