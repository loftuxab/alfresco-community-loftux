/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.mozilla.javascript.Scriptable;

/**
 * @author Roy Wetherall
 */
public class ScriptRecordFolder extends ScriptNode
{
    private static final long serialVersionUID = 1801006559401292415L;

    /**
     * Constructor
     * 
     * @param nodeRef
     * @param services
     * @param scope
     */
    public ScriptRecordFolder(NodeRef nodeRef, ServiceRegistry services, Scriptable scope)
    {
        super(nodeRef, services, scope);
    }
    
    /**
     * Constructor
     * 
     * @param nodeRef
     * @param services
     */
    public ScriptRecordFolder(NodeRef nodeRef, ServiceRegistry services)
    {
        super(nodeRef, services);
    }

    /**
     * Get all the records filed within the record folder
     * 
     * @return
     */
    public ScriptRecord[] getRecords()
    {
        List<ScriptRecord> records = new ArrayList<ScriptRecord>(10);
        List<ChildAssociationRef> assocs = this.services.getNodeService().getChildAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
        for (ChildAssociationRef assoc : assocs)
        {
            NodeRef record = assoc.getChildRef();
            if (this.services.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_RECORD) == true)
            {
                // Add to the list to return
                records.add(new ScriptRecord(record, services, scope));
            }
        }
        return (ScriptRecord[])records.toArray(new ScriptRecord[records.size()]);
    }
    
}
