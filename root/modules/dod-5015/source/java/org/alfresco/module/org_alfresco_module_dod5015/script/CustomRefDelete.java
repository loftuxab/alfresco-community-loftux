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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation for Java backed webscript to remove RM custom reference instances
 * from a node.
 * 
 * @author Neil McErlean
 */
public class CustomRefDelete extends AbstractRmWebScript
{
    private static Log logger = LogFactory.getLog(CustomRefDelete.class);
    
    private RecordsManagementAdminService rmAdminService;

    public void setRecordsManagementAdminService(RecordsManagementAdminService rmAdminService)
    {
		this.rmAdminService = rmAdminService;
	}

    /*
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.Status, org.alfresco.web.scripts.Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> ftlModel = removeCustomReferenceInstance(req);
        
        return ftlModel;
    }
    
    /**
     * Removes custom reference.
     */
    protected Map<String, Object> removeCustomReferenceInstance(WebScriptRequest req)
    {
        NodeRef fromNodeRef = parseRequestForNodeRef(req);

        // Get the toNode from the URL query string.
        String storeType = req.getParameter("st");
        String storeId = req.getParameter("si");
        String nodeId = req.getParameter("id");
        
        // create the NodeRef and ensure it is valid
        StoreRef storeRef = new StoreRef(storeType, storeId);
        NodeRef toNodeRef = new NodeRef(storeRef, nodeId);
        
        if (!this.nodeService.exists(toNodeRef))
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Unable to find to-node: " + 
            		toNodeRef.toString());
        }

        Map<String, Object> result = new HashMap<String, Object>();
        
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        String clientsRefId = templateVars.get("refId");
        QName qn = rmAdminService.getQNameForClientId(clientsRefId);
        if (qn == null)
        {
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            		"Unable to find reference type: " + clientsRefId);
        }
        
        if (logger.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Removing reference ").append(qn).append(" from ")
                .append(fromNodeRef).append(" to ").append(toNodeRef);
            logger.debug(msg.toString());
        }
        
        rmAdminService.removeCustomReference(fromNodeRef, toNodeRef, qn);
        
        result.put("success", true);

        return result;
    }
}