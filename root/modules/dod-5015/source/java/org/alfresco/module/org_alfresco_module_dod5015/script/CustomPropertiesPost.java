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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Implementation for Java backed webscript to add RM custom properties to a record type.
 * 
 * @author Neil McErlean
 */
public class CustomPropertiesPost extends AbstractRmWebScript
{
    /*
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.Status, org.alfresco.web.scripts.Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        JSONObject json = null;
        Map<String, Object> ftlModel = null;
        try
        {
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            ftlModel = addCustomProperties(req, json);
        } 
        catch (IOException iox)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "Could not read content from req.", iox);
        }
        catch (JSONException je)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                        "Could not parse JSON from req.", je);
        }
        
        return ftlModel;
    }
    
    /**
     * Applies custom properties to the specified record node.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> addCustomProperties(WebScriptRequest req, JSONObject json) throws JSONException
    {
        NodeRef recordNode = parseRequestForNodeRef(req);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("nodeRef", recordNode.toString());
        
        List<Map<String, Object>> customPropData = new ArrayList<Map<String, Object>>();
        for (Iterator iter = json.keys(); iter.hasNext(); )
        {
            String nextKeyString = (String)iter.next();
            String nextValueString = (String)json.get(nextKeyString);
            
            QName propProperQName = QName.createQName(nextKeyString, namespaceService);
            Map<QName, Serializable> existingProps = nodeService.getProperties(recordNode);
            existingProps.put(propProperQName, nextValueString);
            // TODO Given the data dictionary probs, we may need to apply the aspect manually
            //      if it is not already there.
            nodeService.setProperties(recordNode, existingProps);
            
            Map<String, Object> nextPropData = new HashMap<String, Object>();
            nextPropData.put("qname", propProperQName.toPrefixString());
            nextPropData.put("value", nextValueString);
            customPropData.add(nextPropData);
        }

        result.put("properties", customPropData);

        return result;
    }
}