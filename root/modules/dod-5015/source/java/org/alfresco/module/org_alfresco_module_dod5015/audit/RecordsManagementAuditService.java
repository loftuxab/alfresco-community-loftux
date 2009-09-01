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

import java.util.Date;
import java.util.List;

/**
 * Records management audit service.
 * 
 * @author Gavin Cornwell
 */
public interface RecordsManagementAuditService
{
    public static final String RM_AUDIT_APPLICATION_NAME = "DOD5015";
    public static final String RM_AUDIT_PATH_ROOT = "/DOD5015";
    public static final String RM_AUDIT_PATH_ACTIONS = "/DOD5015/actions";
    public static final String RM_AUDIT_PATH_ACTIONS_NODE = "/node";
    public static final String RM_AUDIT_PATH_ACTIONS_PARAMS = "/parameters";
    
    /**
     * Starts RM auditing.
     */
    void start();
    
    /**
     * Stops RM auditing.
     */
    void stop();
    
    /**
     * Clears the RM audit trail.
     */
    void clear();
    
    /**
     * Determines whether the RM audit log is currently enabled.
     * 
     * @return true if RM auditing is active false otherwise
     */
    boolean isEnabled();
    
    /**
     * Returns the date the RM audit was last started.
     * 
     * @return Date the audit was last started
     */
    Date getDateLastStarted();
    
    /**
     * Returns the date the RM audit was last stopped.
     * 
     * @return Date the audit was last stopped
     */
    Date getDateLastStopped();
    
    /**
     * Retrieves a list of ALL audit log entries for the RM system.
     * 
     * @return List of RecordsManagementAuditEntry objects
     */
    List<RecordsManagementAuditEntry> getAuditTrail();
    
    /**
     * Retrieves a list of audit log entries using the provided parameters
     * represented by the RecordsManagementAuditQueryParameters instance.
     * <p>
     * The parameters are all optional, null or an empty RecordsManagementAuditQueryParameters
     * object will result in ALL audit log entries for the RM system being
     * returned. Setting the various parameters effectively filters the full
     * audit trail.
     * 
     * @param params Parameters to use to retrieve audit trail
     * @return List of RecordsManagementAuditEntry objects
     */
    List<RecordsManagementAuditEntry> getAuditTrail(RecordsManagementAuditQueryParameters params);
}
