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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminServiceImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptRequest;

/**
 * Implementation for Java backed webscript to return list of custom properties
 * from a record series, record category, record folder or record.
 * 
 * @author Neil McErlean
 */
public class CustomPropertiesGet extends AbstractRmWebScript
{
    /*
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.Status, org.alfresco.web.scripts.Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        NodeRef record = parseRequestForNodeRef(req);
        //TODO This request only makes sense for Record Series, Record Categories, Record Folders and Records.
        
        Map<QName, Serializable> allProperties = nodeService.getProperties(record);
        Map<QName, Serializable> customProperties = new HashMap<QName, Serializable>();
        for (QName qn : allProperties.keySet())
        {
            if (qn.toPrefixString().startsWith(RecordsManagementAdminServiceImpl.CUSTOM_MODEL_PREFIX))
            {
                customProperties.put(qn, allProperties.get(qn));
            }
        }
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("nodeRef", record.toString());
        
        List<Map<String, Object>> customPropData = new ArrayList<Map<String, Object>>();
        for (QName qn : customProperties.keySet())
        {
            Map<String, Object> nextPropData = new HashMap<String, Object>();
            nextPropData.put("qname", qn.toPrefixString());
            nextPropData.put("value", customProperties.get(qn));
            customPropData.add(nextPropData);
        }

        result.put("properties", customPropData);
        
        return result;
    }
}