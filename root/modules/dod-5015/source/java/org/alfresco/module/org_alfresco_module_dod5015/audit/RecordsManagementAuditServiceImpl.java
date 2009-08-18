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
package org.alfresco.module.org_alfresco_module_dod5015.audit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.audit.AuditInfo;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Records Management Audit Service Implementation.
 * 
 * @author Gavin Cornwell
 */
public class RecordsManagementAuditServiceImpl implements RecordsManagementAuditService
{
	/** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementAuditServiceImpl.class);

    private NodeService nodeService;
    private AuditService auditService;

	public void setAuditService(AuditService auditService)
	{
		this.auditService = auditService;
	}
	
	public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

	public boolean isEnabled()
    {
        return true;
    }

    public void start()
    {
    }

    public void stop()
    {
    }
    
    public void clear()
    {
    }

    public Date getDateLastStarted()
    {
        // TODO: return proper date, for now it's today's date
        return new Date();
    }

    public Date getDateLastStopped()
    {
        // TODO: return proper date, for now it's today's date
        return new Date();
    }

    public List<RecordsManagementAuditEntry> getAuditTrail()
    {
        return getAuditTrail(null);
    }

    public List<RecordsManagementAuditEntry> getAuditTrail(
                RecordsManagementAuditQueryParameters params)
    {
        List<RecordsManagementAuditEntry> entries = new ArrayList<RecordsManagementAuditEntry>();
        
        if (params != null)
        {
            // examine parameters and build up query? or call relevant service methods
            
            if (params.getNodeRef() != null)
            {
                List<AuditInfo> auditLog = this.auditService.getAuditTrail(params.getNodeRef());
                for (AuditInfo entry: auditLog)
                {
                    RecordsManagementAuditEntry rmEntry = createRMAuditEntry(entry, params.getNodeRef());
                    if (rmEntry != null)
                    {
                        entries.add(rmEntry);
                    }
                }
            }
        }
        else
        {
            // TODO: return whole RM audit trail
        }
        
        return entries;
    }
    
    protected RecordsManagementAuditEntry createRMAuditEntry(AuditInfo entry, NodeRef nodeRef)
    {
        RecordsManagementAuditEntry rmEntry = null;
        
        String service = entry.getAuditService();
        String method = entry.getAuditMethod();
        if (service != null && method != null)
        {
            String event = service + "." + method;
            String userName = entry.getUserIdentifier();
            String fullName = userName;
            String userRole = "Records Manager";
            String nodeName = (String)nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
            
            rmEntry = new RecordsManagementAuditEntry(entry.getDate(), userName, fullName, 
                        userRole, nodeRef, nodeName, event);
        }
        
        return rmEntry;
    }
}