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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService;
import org.springframework.extensions.surf.util.ISO8601DateFormat;
import org.springframework.extensions.webscripts.DeclarativeWebScript;

/**
 * Base class for all audit administration webscripts.
 * 
 * @author Gavin Cornwell
 */
public class BaseAuditAdminWebScript extends DeclarativeWebScript
{
    protected RecordsManagementAuditService rmAuditService;
    
    /**
     * Sets the RecordsManagementAuditService instance
     * 
     * @param auditService The RecordsManagementAuditService instance
     */
    public void setRecordsManagementAuditService(RecordsManagementAuditService rmAuditService)
    {
        this.rmAuditService = rmAuditService;
    }
    
    /**
     * Creates a model to represent the current status of the RM audit log.
     * 
     * @return Map of RM audit log status
     */
    protected Map<String, Object> createAuditStatusModel()
    {
        Map<String, Object> auditStatus = new HashMap<String, Object>(3);
        
        auditStatus.put("started", ISO8601DateFormat.format(rmAuditService.getDateLastStarted()));
        auditStatus.put("stopped", ISO8601DateFormat.format(rmAuditService.getDateLastStopped()));
        auditStatus.put("enabled", Boolean.valueOf(rmAuditService.isEnabled()));
        
        return auditStatus;
    }
}