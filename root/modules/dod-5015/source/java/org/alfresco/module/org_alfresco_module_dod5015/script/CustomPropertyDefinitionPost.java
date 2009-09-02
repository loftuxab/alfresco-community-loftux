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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.CustomisableRmElement;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Implementation for Java backed webscript to add RM custom property definitions
 * to the custom model.
 * 
 * @author Neil McErlean
 */
public class CustomPropertyDefinitionPost extends AbstractRmWebScript
{
    private static Log logger = LogFactory.getLog(CustomPropertyDefinitionPost.class);
    
    public static final String PARAM_DATATYPE = "dataType";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_DESCRIPTION = "description";
    public static final String PARAM_DEFAULT_VALUE = "defaultValue";
    public static final String PARAM_MULTI_VALUED = "multiValued";
    public static final String PARAM_MANDATORY = "mandatory";
    public static final String PARAM_PROTECTED = "protected";
    public static final String PARAM_CONSTRAINT_REF = "constraintRef";

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
     * Applies custom properties.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> addCustomProperties(WebScriptRequest req, JSONObject json) throws JSONException
    {
        Map<String, Object> result = new HashMap<String, Object>();
        
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("element", req.getParameter("element"));
        
        for (Iterator iter = json.keys(); iter.hasNext(); )
        {
            String nextKeyString = (String)iter.next();
            String nextValueString = json.getString(nextKeyString);
            
            params.put(nextKeyString, nextValueString);
        }
        
        
        
        
        
        // Need to select the correct aspect in the customModel to which we'll add the property.
        String customisableElement = (String)params.get("element");
        CustomisableRmElement ce = CustomisableRmElement.getEnumFor(customisableElement);
        String aspectName = ce.getCorrespondingAspect();
        
        String clientSideName = (String)params.get("name");
        
        //TODO According to the wireframes, type here can only be date|text|number
        Serializable serializableParam = params.get(PARAM_DATATYPE);
        QName type = null;
        if (serializableParam != null)
        {
            if (serializableParam instanceof String)
            {
                type = QName.createQName((String)serializableParam);
            }
            else if (serializableParam instanceof QName)
            {
                type = (QName)serializableParam;
            }
            else
            {
                throw new AlfrescoRuntimeException("Unexpected type of dataType param: "+serializableParam+" (expected String or QName)");
            }
        }
        
        // The title is actually generated, so this parameter will be ignored
        // by the RMAdminService
        String title = (String)params.get(PARAM_TITLE);
        String description = (String)params.get(PARAM_DESCRIPTION);
        String defaultValue = (String)params.get(PARAM_DEFAULT_VALUE);
        
        // 'mandatory' should be an available parameter.
        boolean mandatory = false;
        serializableParam = params.get(PARAM_MANDATORY);
        if (serializableParam != null)
        {
            mandatory = Boolean.valueOf(serializableParam.toString());
        }
        
        boolean isProtected = false;
        serializableParam = params.get(PARAM_PROTECTED);
        if (serializableParam != null)
        {
            isProtected = Boolean.valueOf(serializableParam.toString());
        }
        
        boolean multiValued = false;
        serializableParam = params.get(PARAM_MULTI_VALUED);
        if (serializableParam != null)
        {
            multiValued = Boolean.valueOf(serializableParam.toString());
        }
        
        serializableParam = params.get(PARAM_CONSTRAINT_REF);
        QName constraintRef = null;
        if (serializableParam != null)
        {
            if (serializableParam instanceof String)
            {
                constraintRef = QName.createQName((String)serializableParam);
            }
            else if (serializableParam instanceof QName)
            {
                constraintRef = (QName)serializableParam;
            }
            else
            {
                throw new AlfrescoRuntimeException("Unexpected type of constraintRef param: "+serializableParam+" (expected String or QName)");
            }
        }
        
        QName generatedQName = rmAdminService.addCustomPropertyDefinition(aspectName, clientSideName, type,
                title, description, defaultValue, multiValued, mandatory, isProtected, constraintRef);

        
        
        
        result.put("propId", generatedQName);
        result.put("success", true);

        return result;
    }
}