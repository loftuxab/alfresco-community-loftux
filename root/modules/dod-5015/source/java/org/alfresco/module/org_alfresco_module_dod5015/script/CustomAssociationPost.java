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
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.CustomAssociation;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
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
 * Implementation for Java backed webscript to add a reference between two record types.
 * 
 * @author Neil McErlean
 */
public class CustomAssociationPost extends AbstractRmWebScript
{
    private static final String TARGET_ID = "targetId";
    private static final String TARGET_STORE_ID = "targetStoreId";
    private static final String TARGET_STORE_TYPE = "targetStoreType";
    private static final String ASSOC_TYPE_Q_NAME = "assocTypeQName";
    
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
        JSONObject json = null;
        CustomAssociation assocDef = null;
        try
        {
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            assocDef = applyCustomReference(req, json);
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
        
        // create model object with just the action data
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("customassociation", assocDef);
        return model;
    }
    
    /**
     * Applies a custom reference between the specified record nodes.
     */
    protected CustomAssociation applyCustomReference(WebScriptRequest req, JSONObject json) throws JSONException
    {
        // Ensure all mandatory parameters are present in JSON request.
        this.checkMandatoryJsonParam(json, ASSOC_TYPE_Q_NAME);
        this.checkMandatoryJsonParam(json, TARGET_STORE_TYPE);
        this.checkMandatoryJsonParam(json, TARGET_STORE_ID);
        this.checkMandatoryJsonParam(json, TARGET_ID);

        NodeRef sourceNode = parseRequestForNodeRef(req);
        
        String assocTypeShortQName = json.getString(ASSOC_TYPE_Q_NAME);
        String targetStoreType = json.getString(TARGET_STORE_TYPE);
        String targetStoreId = json.getString(TARGET_STORE_ID);
        String targetId = json.getString(TARGET_ID);
        
        QName assocTypeProperQName = QName.createQName(assocTypeShortQName, namespaceService);
        NodeRef targetNode = new NodeRef(targetStoreType, targetStoreId, targetId);

        // Need to handle child and standard assocs differently.
        Map<QName, CustomAssociation> allCustomAssocs = rmAdminService.getAvailableCustomAssociations();
        CustomAssociation assocDefinition = allCustomAssocs.get(assocTypeProperQName);
        
        //TODO This call isn't working yet. Getting IllegalArgumentException as custom type
        //     is not in the data dictionary. Investigating model reload...
        
        if (assocDefinition.isChildAssociation())
        {
            //TODO Is this the right nodeService call?
            nodeService.addChild(sourceNode, targetNode, assocTypeProperQName, assocTypeProperQName);
        }
        else 
        {
            nodeService.createAssociation(sourceNode, targetNode, assocTypeProperQName);
        }
        
        return assocDefinition;
    }
}