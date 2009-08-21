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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditQueryParameters;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation for Java backed webscript to return audit
 * log of RM events, optionally scoped to an RM node.
 * 
 * @author Gavin Cornwell
 */
public class AuditLogGet extends BaseAuditLogWebScript
{
    /** Logger */
    private static Log logger = LogFactory.getLog(AuditLogGet.class);
    
    protected final static String PARAM_USER = "user";
    protected final static String PARAM_SIZE = "size";
    protected final static String PARAM_FROM = "from";
    protected final static String PARAM_TO = "to";
    protected final static String DATE_PATTERN = "yyyy-MM-dd";
    
    /*
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.Status, org.alfresco.web.scripts.Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // this webscript has a couple of different forms of url, work out
        // whether a nodeRef has been supplied or whether the whole audit
        // log should be displayed
        NodeRef nodeRef = null;
        
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        String storeType = templateVars.get("store_type");
        if (storeType != null && storeType.length() > 0)
        {
            // there is a store_type so all other params are likely to be present
            String storeId = templateVars.get("store_id");
            String nodeId = templateVars.get("id");
            
            // create the nodeRef
            nodeRef = new NodeRef(new StoreRef(storeType, storeId), nodeId);
        }
        
        // create model object with the audit model
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("auditlog", generateAuditModel(req, nodeRef));
        return model;
    }
    
    /**
     * Generates the audit log model for the optional node. If a node
     * is not supplied the audit log for the whole system is returned.
     * 
     * @param req The request
     * @param nodeRef The NodeRef to get audit log for
     * @return Map representing the audit log model
     */
    protected Map<String, Object> generateAuditModel(WebScriptRequest req, NodeRef nodeRef)
    {
        Map<String, Object> model = createAuditStatusModel();
        
        // gather all the common filtering parameters
        String size = req.getParameter(PARAM_SIZE);
        String user = req.getParameter(PARAM_USER);
        String from = req.getParameter(PARAM_FROM);
        String to = req.getParameter(PARAM_TO);

        // create parameters for audit trail retrieval
        RecordsManagementAuditQueryParameters params = new RecordsManagementAuditQueryParameters();
        params.setNodeRef(nodeRef);
        params.setUser(user);
        
        if (size != null && size.length() > 0)
        {
            try
            {
                params.setMaxEntries(Integer.parseInt(size));
            }
            catch (NumberFormatException nfe)
            {
                if (logger.isWarnEnabled())
                    logger.warn("Ignoring size parameter as '" + size + "' is not a number!");
            }
        }
        
        if (from != null && from.length() > 0)
        {
            try
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
                params.setDateFrom(dateFormat.parse(from));
            }
            catch (ParseException pe)
            {
                if (logger.isWarnEnabled())
                    logger.warn("Ignoring from parameter as '" + from + "' does not conform to the date pattern: " + DATE_PATTERN);
            }
        }
        
        if (to != null && to.length() > 0)
        {
            try
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
                params.setDateTo(dateFormat.parse(to));
            }
            catch (ParseException pe)
            {
                if (logger.isWarnEnabled())
                    logger.warn("Ignoring to parameter as '" + to + "' does not conform to the date pattern: " + DATE_PATTERN);
            }
        }
        
        // get the audit trail
        model.put("entries", this.rmAuditService.getAuditTrail(params));
        
        return model;
    }
}