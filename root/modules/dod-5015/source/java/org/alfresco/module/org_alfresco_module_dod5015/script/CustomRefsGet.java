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

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides the implementation for the customrefs.get webscript.
 * 
 * @author Neil McErlean
 */
public class CustomRefsGet extends AbstractRmWebScript
{
    private static Log logger = LogFactory.getLog(CustomRefsGet.class);
    private RecordsManagementAdminService rmAdminService;
    
    public void setRecordsManagementAdminService(RecordsManagementAdminService rmAdminService)
    {
        this.rmAdminService = rmAdminService;
    }

    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> ftlModel = new HashMap<String, Object>();
        
        NodeRef node = parseRequestForNodeRef(req);
        
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("Getting custom reference instances for " + node);
    	}

    	List<Map<String, String>> listOfReferenceData = new ArrayList<Map<String, String>>();
    	
    	List<AssociationRef> assocs = this.rmAdminService.getCustomReferencesFor(node);
    	for (AssociationRef assRef : assocs)
    	{
    		Map<String, String> data = new HashMap<String, String>();

    		QName typeQName = assRef.getTypeQName();
    		
    		data.put("sourceRef", assRef.getSourceRef().toString());
    		data.put("targetRef", assRef.getTargetRef().toString());

    		String clientId = rmAdminService.getClientIdForQName(typeQName);
    		
    		AssociationDefinition assDef = rmAdminService.getAvailableCustomReferences().get(typeQName);
    		data.put("label", clientId);
			data.put("referenceType", CustomReferenceType.BIDIRECTIONAL.toString());
    		
    		listOfReferenceData.add(data);
    	}
    	
    	List<ChildAssociationRef> childAssocs = this.rmAdminService.getCustomChildReferencesFor(node);
    	for (ChildAssociationRef childAssRef : childAssocs)
    	{
    		Map<String, String> data = new HashMap<String, String>();

    		QName typeQName = childAssRef.getTypeQName();
    		
    		data.put("childRef", childAssRef.getChildRef().toString());
    		data.put("parentRef", childAssRef.getParentRef().toString());

            String clientId = rmAdminService.getClientIdForQName(typeQName);

            String[] sourceAndTarget = rmAdminService.splitSourceTargetId(clientId);
            data.put("source", sourceAndTarget[0]);
            data.put("target", sourceAndTarget[1]);
			data.put("referenceType", CustomReferenceType.PARENT_CHILD.toString());
    		
    		listOfReferenceData.add(data);
    	}
    	
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("Retrieved custom reference instances: " + assocs);
    	}
    	
    	ftlModel.put("customRefs", listOfReferenceData);

        return ftlModel;
    }
}