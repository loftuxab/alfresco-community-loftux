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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.CustomAssociation;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptRequest;

/**
 * Implementation for Java backed webscript to return list of custom references
 * from a record.
 * 
 * @author Neil McErlean
 */
public class CustomAssociationsGet extends AbstractRmWebScript
{
    private RecordsManagementAdminService recordsManagementAdminService;
    
    public void setRecordsManagementAdminService(RecordsManagementAdminService recordsManagementAdminService)
    {
        this.recordsManagementAdminService = recordsManagementAdminService;
    }

    /*
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.Status, org.alfresco.web.scripts.Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        NodeRef record = parseRequestForNodeRef(req);
        if (rmService.isRecord(record) == false)
        {
            // TODO Bad request?
        }
        
        //TODO Use this filter for 'rmc:' assocs only.
        RegexQNamePattern customElementsPattern = new RegexQNamePattern("http://www.alfresco.org/model/rmcustom/1.0", ".*");
        
        List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(record, RegexQNamePattern.MATCH_ALL);
        List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(record, RegexQNamePattern.MATCH_ALL);
        
        Map<QName, CustomAssociation> allCustomAssocDefs = recordsManagementAdminService.getAvailableCustomAssociations();
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("nodeRef", record.toString());
        
        List<Map<String, Object>> customSrcAssocs = new ArrayList<Map<String, Object>>();
        for (AssociationRef assRef : sourceAssocs)
        {
            Map<String, Object> nextAssRefData = new HashMap<String, Object>();
            nextAssRefData.put("sourceRef", assRef.getSourceRef());
            nextAssRefData.put("targetRef", assRef.getTargetRef());
            QName assRefQName = assRef.getTypeQName();
            nextAssRefData.put("assocTypeQName", assRefQName);
            nextAssRefData.put("associationDefinition", allCustomAssocDefs.get(assRefQName));
            
            customSrcAssocs.add(nextAssRefData);
        }
        List<Map<String, Object>> customTargetAssocs = new ArrayList<Map<String, Object>>();
        for (AssociationRef assRef : targetAssocs)
        {
            Map<String, Object> nextAssRefData = new HashMap<String, Object>();
            nextAssRefData.put("sourceRef", assRef.getSourceRef());
            nextAssRefData.put("targetRef", assRef.getTargetRef());
            QName assRefQName = assRef.getTypeQName();
            nextAssRefData.put("assocTypeQName", assRefQName);
            nextAssRefData.put("associationDefinition", allCustomAssocDefs.get(assRefQName));
            
            customTargetAssocs.add(nextAssRefData);
        }

        //TODO Remove this dummy data
//        Map<String, Object> dummyData = new HashMap<String, Object>();
//        dummyData.put("sourceRef", "dummySource");
//        dummyData.put("targetRef", "dummyTarget");
//        dummyData.put("assocTypeQName", "dummyTypeQName1");
//        final CustomAssociation dummyCA = new CustomAssociation("dummy1");
//        dummyData.put("associationDefinition", dummyCA);
//        customSrcAssocs.add(dummyData);
//        
//        Map<String, Object> dummyTargetData = new HashMap<String, Object>();
//        dummyTargetData.put("sourceRef", "dummySource");
//        dummyTargetData.put("targetRef", "dummyTarget");
//        dummyTargetData.put("assocTypeQName", "dummyTypeQName2");
//        final CustomAssociation dummyCA2 = new CustomAssociation("dummy2");
//        dummyTargetData.put("associationDefinition", dummyCA2);
//        customTargetAssocs.add(dummyTargetData);
        // End dummy data

        result.put("sourceassocs", customSrcAssocs);
        result.put("targetassocs", customTargetAssocs);
        
        return result;
    }
}