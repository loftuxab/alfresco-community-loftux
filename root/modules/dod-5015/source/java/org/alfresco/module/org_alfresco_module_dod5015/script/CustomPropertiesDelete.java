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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation for Java backed webscript to remove RM custom properties from a record type.
 * 
 * @author Neil McErlean
 */
public class CustomPropertiesDelete extends AbstractRmWebScript
{
    private static Log logger = LogFactory.getLog(CustomPropertiesDelete.class);
    /*
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.Status, org.alfresco.web.scripts.Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> ftlModel = null;
        try
        {
            NodeRef recordNode = parseRequestForNodeRef(req);
        	QName propQName = getPropertyFromReq(req, recordNode);
        	if (logger.isDebugEnabled())
        	{
        		StringBuilder msg = new StringBuilder();
        		msg.append("Deleting property ")
        		    .append(propQName)
        		    .append(" from ")
        		    .append(recordNode);
        		logger.debug(msg.toString());
        	}
            ftlModel = removeProperty(recordNode, propQName);
        } 
        catch (JSONException je)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                        "Could not parse JSON from req.", je);
        }
        
        return ftlModel;
    }
    
    private QName getPropertyFromReq(WebScriptRequest req, NodeRef record)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        String propIdString = templateVars.get("property_qname");

        Map<QName, Serializable> nodeProps = nodeService.getProperties(record);
        QName propQName = QName.createQName(propIdString.replaceFirst("_", ":"), namespaceService);
        boolean propertyExists = nodeProps.containsKey(propQName);

        if (!propertyExists)
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, 
                        "Requested property (id:" + propIdString + ") does not exist");
        }
        
        return propQName;
    }
    
    /**
     * Applies custom properties to the specified record node.
     */
    protected Map<String, Object> removeProperty(NodeRef record, QName propQName) throws JSONException
    {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("nodeRef", record.toString());
        
        nodeService.removeProperty(record, propQName);

        result.put("propertyqname", propQName.toPrefixString(namespaceService));

        return result;
    }
}