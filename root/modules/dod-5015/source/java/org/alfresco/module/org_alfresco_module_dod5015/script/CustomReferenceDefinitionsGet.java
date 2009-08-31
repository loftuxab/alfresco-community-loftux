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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.DeclarativeWebScript;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides the implementation for the customrefdefinitions.get webscript.
 * 
 * @author Neil McErlean
 */
public class CustomReferenceDefinitionsGet extends DeclarativeWebScript
{
	private final static String PARAM_REF_ID = "refId";
    private static Log logger = LogFactory.getLog(CustomReferenceDefinitionsGet.class);
    
    private RecordsManagementAdminService rmAdminService;
    private NamespaceService namespaceService;

    public void setNamespaceService(NamespaceService namespaceService)
    {
    	this.namespaceService = namespaceService;
    }

    public void setRecordsManagementAdminService(RecordsManagementAdminService rmAdminService)
    {
        this.rmAdminService = rmAdminService;
    }

    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        String refId = templateVars.get(PARAM_REF_ID);
        
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("Getting custom reference definitions with refId: " + String.valueOf(refId));
    	}

    	Map<QName, AssociationDefinition> currentCustomRefs = rmAdminService.getCustomReferenceDefinitions();

    	// If refId has been provided then this is a request for a single custom-ref-defn.
        // else it is a request for them all.
        if (refId != null)
        {
            QName qn = rmAdminService.getQNameForClientId(refId);
        	
        	AssociationDefinition assDef = currentCustomRefs.get(qn);
        	if (assDef == null)
        	{
        		StringBuilder msg = new StringBuilder();
        		msg.append("Unable to find reference: ").append(refId);
        		if (logger.isDebugEnabled())
        		{
        		    logger.debug(msg.toString());
        		}
				throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND,
                		msg.toString());
        	}

        	currentCustomRefs = new HashMap<QName, AssociationDefinition>(1);
        	currentCustomRefs.put(qn, assDef);
        }

        List<Map<String, String>> listOfReferenceData = new ArrayList<Map<String, String>>();
        
        for (Entry<QName, AssociationDefinition> entry : currentCustomRefs.entrySet())
        {
    		Map<String, String> data = new HashMap<String, String>();

    		AssociationDefinition nextValue = entry.getValue();
            QName serverSideQName = nextValue.getName();
    		
    		String clientId = rmAdminService.getClientIdForQName(serverSideQName);

    		CustomReferenceType referenceType = nextValue instanceof ChildAssociationDefinition ?
    				CustomReferenceType.PARENT_CHILD : CustomReferenceType.BIDIRECTIONAL;
    		
			data.put("referenceType", referenceType.toString());

			String nextTitle = nextValue.getTitle();
            if (CustomReferenceType.PARENT_CHILD.equals(referenceType))
            {
                if (nextTitle != null)
                {
                    //TODO Can I actually use the title? It's not 'controlled'
                    String[] sourceAndTarget = rmAdminService.splitSourceTargetId(nextTitle);
                    data.put("source", sourceAndTarget[0]);
                    data.put("target", sourceAndTarget[1]);
                }
            }
            else if (CustomReferenceType.BIDIRECTIONAL.equals(referenceType))
            {
                if (nextTitle != null)
                {
                    data.put("label", nextTitle);
                }
            }
            else
            {
                //TODO throw
            }
        	
    		listOfReferenceData.add(data);
        }
        
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("Retrieved custom reference definitions: " + listOfReferenceData.size());
    	}

    	model.put("customRefs", listOfReferenceData);

        return model;
    }
}