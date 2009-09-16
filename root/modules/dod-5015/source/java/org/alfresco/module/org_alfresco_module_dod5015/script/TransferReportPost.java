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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Files a transfer report as a record.
 * 
 * @author Gavin Cornwell
 */
public class TransferReportPost extends TransferReportGet
{
    /** Logger */
    private static Log logger = LogFactory.getLog(TransferReportPost.class);
    
    protected static final String PARAM_DESTINATION = "destination";
    protected static final String RESPONSE_SUCCESS = "success";
    protected static final String RESPONSE_RECORD = "record";
    protected static final String RESPONSE_RECORD_NAME = "recordName";
    protected static final String FILE_ACTION = "file";
    
    protected RecordsManagementActionService rmActionService;
    
    /**
     * Sets the RecordsManagementActionService instance
     * 
     * @param rmActionService RecordsManagementActionService instance
     */
    public void setRecordsManagementActionService(RecordsManagementActionService rmActionService)
    {
        this.rmActionService = rmActionService;
    }
    
    @Override
    protected File executeTransfer(NodeRef transferNode,
                WebScriptRequest req, WebScriptResponse res, 
                Status status, Cache cache) throws IOException
    {
        File report = null;
        
        // retrieve requested format
        String format = req.getFormat();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("status", status);
        model.put("cache", cache);
        
        try
        {
            // extract the destination parameter, ensure it's present and it is
            // a record folder
            JSONObject json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            if (!json.has(PARAM_DESTINATION))
            {
                status.setCode(HttpServletResponse.SC_BAD_REQUEST, 
                            "Mandatory '" + PARAM_DESTINATION + "' parameter has not been supplied");
                Map<String, Object> templateModel = createTemplateParameters(req, res, model);
                sendStatus(req, res, status, cache, format, templateModel);
                return null;
            }
            
            String destinationParam = json.getString(PARAM_DESTINATION);
            NodeRef destination = new NodeRef(destinationParam);
            
            if (!this.nodeService.exists(destination))
            {
                status.setCode(HttpServletResponse.SC_NOT_FOUND, 
                            "Node " + destination.toString() + " does not exist");
                Map<String, Object> templateModel = createTemplateParameters(req, res, model);
                sendStatus(req, res, status, cache, format, templateModel);
                return null;
            }
            
            // ensure the node is a filePlan object
            if (!RecordsManagementModel.TYPE_RECORD_FOLDER.equals(this.nodeService.getType(destination)))
            {
                status.setCode(HttpServletResponse.SC_BAD_REQUEST, 
                            "Node " + destination.toString() + " is not a record folder");
                Map<String, Object> templateModel = createTemplateParameters(req, res, model);
                sendStatus(req, res, status, cache, format, templateModel);
                return null;
            }
            
            if (logger.isDebugEnabled())
                logger.debug("Filing transfer report as record in record folder: " + destination);
        
            // generate the report (will be in JSON format)
            report = generateTransferReport(transferNode);
            
            // file the report as a record
            NodeRef record = fileTransferReport(report, destination);
            
            if (logger.isDebugEnabled())
                logger.debug("Filed transfer report as new record: " + record);
            
            // return success flag and record noderef as JSON
            JSONObject responseJSON = new JSONObject();
            responseJSON.put(RESPONSE_SUCCESS, (record != null));
            if (record != null)
            {
                responseJSON.put(RESPONSE_RECORD, record.toString());
                responseJSON.put(RESPONSE_RECORD_NAME, 
                            (String)nodeService.getProperty(record, ContentModel.PROP_NAME));
            }
            
            // setup response
            String jsonString = responseJSON.toString();
            res.setContentType(MimetypeMap.MIMETYPE_JSON);
            res.setContentEncoding("UTF-8");
            res.setHeader("Content-Length", Long.toString(jsonString.length()));
            
            // write the JSON response
            res.getWriter().write(jsonString);
        }
        catch (JSONException je)
        {
            throw createStatusException(je, req, res);
        }
        
        // return the file for deletion
        return report;
    }
    
    /**
     * Files the given transfer report as a record in the given record folder.
     * 
     * @param report Report to file
     * @param destination The destination record folder
     * @return NodeRef of the created record
     */
    protected NodeRef fileTransferReport(File report, NodeRef destination)
    {
        ParameterCheck.mandatory("report", report);
        ParameterCheck.mandatory("destination", destination);
        
        NodeRef record = null;
        
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
        properties.put(ContentModel.PROP_NAME, report.getName());
        
        // file the transfer report as an undeclared record
        record = this.nodeService.createNode(destination, 
                    ContentModel.ASSOC_CONTAINS, 
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, 
                                QName.createValidLocalName(report.getName())), 
                    ContentModel.TYPE_CONTENT, properties).getChildRef();

        // Set the content
        ContentWriter writer = this.contentService.getWriter(record, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_JSON);
        writer.setEncoding("UTF-8");
        writer.putContent(report);
        
        // file the node as a record
        this.rmActionService.executeRecordsManagementAction(record, FILE_ACTION); 
        
        return record;
    }
}