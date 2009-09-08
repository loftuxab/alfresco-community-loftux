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
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementCustomModel;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Abstract base class for PUT and POST for custom property definitions.
 * 
 * @author Neil McErlean
 */
public abstract class AbstractCustomPropertyDefnWrite extends AbstractRmWebScript
{
    private static final String NAME = "name";

    protected RecordsManagementAdminService rmAdminService;

    public static final String PARAM_DATATYPE = "dataType";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_DESCRIPTION = "description";
    public static final String PARAM_DEFAULT_VALUE = "defaultValue";
    public static final String PARAM_MULTI_VALUED = "multiValued";
    public static final String PARAM_MANDATORY = "mandatory";
    public static final String PARAM_PROTECTED = "protected";
    public static final String PARAM_CONSTRAINT_REF = "constraintRef";

    protected static final String PARAM_ELEMENT = "element";
    
    protected static final String PROP_ID = "propId";
    protected static final String URL = "url";
    
    public void setRecordsManagementAdminService(RecordsManagementAdminService rmAdminService)
    {
        this.rmAdminService = rmAdminService;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        JSONObject json = null;
        Map<String, Object> ftlModel = null;
        try
        {
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            
            ftlModel = handlePropertyRequest(req, json);
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
    protected Map<String, Object> handlePropertyRequest(WebScriptRequest req, JSONObject json)
            throws JSONException
    {
        Map<String, Object> result = new HashMap<String, Object>();
        
        Map<String, Serializable> params = getParamsFromUrlAndJson(req, json);
        
        QName propertyQName;
        if (isRequestToCreateNewProp(params))
        {
            propertyQName = createNewPropertyDefinition(params);
        }
        else
        {
            propertyQName = updatePropertyDefinition(params);
        }
        String localName = propertyQName.getLocalName();
        
        result.put("success", true);
        result.put(PROP_ID, localName);

        String urlResult = getUrlResult(req, propertyQName);
        result.put(URL, urlResult);
    
        return result;
    }
    
    /**
     * This method returns the url of the resulting property definition, whether created or
     * already existing.
     */
    protected abstract String getUrlResult(WebScriptRequest req, QName propQName);

    protected QName createNewPropertyDefinition(Map<String, Serializable> params)
    {
        // Need to select the correct aspect in the customModel to which we'll add the property.
        String customisableElement = (String)params.get(PARAM_ELEMENT);
        CustomisableRmElement ce = CustomisableRmElement.getEnumFor(customisableElement);
        String aspectName = ce.getCorrespondingAspect();
        
        String label = (String)params.get(NAME);
        
        //According to the wireframes, type here can only be date|text|number
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
        
        // if propId is specified, use it.
        QName proposedQName = null;
        String propId = (String)params.get(PROP_ID);
        if (propId != null)
        {
            proposedQName = QName.createQName(RecordsManagementCustomModel.RM_CUSTOM_PREFIX, propId, namespaceService);
        }
        return rmAdminService.addCustomPropertyDefinition(proposedQName, aspectName, label, type,
            title, description, defaultValue, multiValued, mandatory, isProtected, constraintRef);
    }

    protected QName updatePropertyDefinition(Map<String, Serializable> params)
    {
        String label = (String)params.get(NAME);
        String propId = (String)params.get(PROP_ID);
        QName propQName = rmAdminService.getQNameForClientId(propId);
        
        if (propQName == null)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND,
                    "Could not find property definition for: " + propId);
        }

        return rmAdminService.updateCustomPropertyDefinition(propQName, label);
    }

    protected abstract boolean isRequestToCreateNewProp(Map<String, Serializable> params);

    @SuppressWarnings("unchecked")
    protected Map<String, Serializable> getParamsFromUrlAndJson(
            WebScriptRequest req, JSONObject json) throws JSONException
    {
        Map<String, Serializable> params;
        params = new HashMap<String, Serializable>();
        params.put(PARAM_ELEMENT, req.getParameter(PARAM_ELEMENT));
        
        // PUT has propId
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        String propId = templateVars.get(PROP_ID);
        if (propId != null)
        {
            params.put(PROP_ID, (Serializable)propId);
        }
        
        for (Iterator iter = json.keys(); iter.hasNext(); )
        {
            String nextKeyString = (String)iter.next();
            String nextValueString = json.getString(nextKeyString);
            
            params.put(nextKeyString, nextValueString);
        }
        
        return params;
    }

}