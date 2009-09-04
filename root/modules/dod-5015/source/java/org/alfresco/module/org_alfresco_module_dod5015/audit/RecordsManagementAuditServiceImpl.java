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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.audit.AuditService.AuditQueryCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ParameterCheck;
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

    private AuditService auditService;

    // temporary field to hold imaginary enabled flag
    private boolean enabled = false;
    
    /**
     * Sets the AuditService instance
     */
	public void setAuditService(AuditService auditService)
	{
		this.auditService = auditService;
	}
	
    /**
     * {@inheritDoc}
     */
	public boolean isEnabled()
    {
        return this.enabled;
    }
	
    /**
     * {@inheritDoc}
     */
    public void start()
    {
        // TODO: Start RM auditing properly!
        this.enabled = true;
        
        if (logger.isInfoEnabled())
            logger.info("Started Records Management auditing");
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        // TODO: Stop RM auditing properly!
        this.enabled = false;
        
        if (logger.isInfoEnabled())
            logger.info("Stopped Records Management auditing");
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear()
    {
        // TODO: Clear the RM audit trail
        
        if (logger.isInfoEnabled())
            logger.debug("Records Management audit log has been cleared");
    }
    
    /**
     * {@inheritDoc}
     */
    public Date getDateLastStarted()
    {
        // TODO: return proper date, for now it's today's date
        return new Date();
    }
    
    /**
     * {@inheritDoc}
     */
    public Date getDateLastStopped()
    {
        // TODO: return proper date, for now it's today's date
        return new Date();
    }
    
    /**
     * {@inheritDoc}
     */
    public List<RecordsManagementAuditEntry> getAuditTrail(
                RecordsManagementAuditQueryParameters params)
    {
        ParameterCheck.mandatory("params", params);
        
        if (logger.isDebugEnabled())
            logger.debug("Retrieving audit trail using parameters: " + params);
        
        // TODO: Add node-based filtering
        if (params.getNodeRef() != null)
        {
            logger.error("TODO: Node-based filtering is not enabled, yet.");
        }
        
        // The callback will populate this
        final List<RecordsManagementAuditEntry> entries = new ArrayList<RecordsManagementAuditEntry>(50);
        
        AuditQueryCallback callback = new AuditQueryCallback()
        {
            public boolean handleAuditEntry(
                    Long entryId,
                    String applicationName,
                    String user,
                    long time,
                    Map<String, Serializable> values)
            {
                Date timestamp = new Date(time);
                String fullName = (String) values.get(RecordsManagementAuditService.RM_AUDIT_DATA_PERSON_FULLNAME);
                if (fullName == null)
                {
                    logger.warn(
                            "RM Audit: No value for '" +
                            RecordsManagementAuditService.RM_AUDIT_DATA_PERSON_FULLNAME + "': " + entryId);
                }
                String userRole = (String) values.get(RecordsManagementAuditService.RM_AUDIT_DATA_PERSON_ROLE);
                if (userRole == null)
                {
                    logger.warn(
                            "RM Audit: No value for '" +
                            RecordsManagementAuditService.RM_AUDIT_DATA_PERSON_ROLE + "': " + entryId);
                }
                NodeRef nodeRef = (NodeRef) values.get(RecordsManagementAuditService.RM_AUDIT_DATA_NODE_NODEREF);
                if (nodeRef == null)
                {
                    logger.warn(
                            "RM Audit: No value for '" +
                            RecordsManagementAuditService.RM_AUDIT_DATA_NODE_NODEREF + "': " + entryId);
                }
                String nodeName = (String) values.get(RecordsManagementAuditService.RM_AUDIT_DATA_NODE_NAME);
                if (nodeName == null)
                {
                    logger.warn(
                            "RM Audit: No value for '" +
                            RecordsManagementAuditService.RM_AUDIT_DATA_NODE_NAME + "': " + entryId);
                }
                String description = (String) values.get(RecordsManagementAuditService.RM_AUDIT_DATA_ACTIONDESCRIPTION_VALUE);
                if (description == null)
                {
                    logger.warn(
                            "RM Audit: No value for '" +
                            RecordsManagementAuditService.RM_AUDIT_DATA_ACTIONDESCRIPTION_VALUE + "': " + entryId);
                }
                RecordsManagementAuditEntry entry = new RecordsManagementAuditEntry(
                        timestamp,
                        user,
                        fullName,
                        userRole,
                        nodeRef,
                        nodeName,
                        description);
                // Add it to the output
                entries.add(entry);
                if (logger.isDebugEnabled())
                {
                    logger.debug("   " + entry);
                }
                // Keep going
                return true;
            }
        };
        
        String user = params.getUser();
        Long fromTime = (params.getDateFrom() == null ? null : new Long(params.getDateFrom().getTime()));
        Long toTime = (params.getDateTo() == null ? null : new Long(params.getDateTo().getTime()));
        int maxEntries = params.getMaxEntries();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("RM Audit: Issuing query: " + params);
        }
        
        auditService.auditQuery(
                callback,
                RecordsManagementAuditService.RM_AUDIT_APPLICATION_NAME,
                user,
                fromTime,
                toTime,
                maxEntries);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("RM Audit: Got " + entries.size() + " query results for params: " + params);
        }
        return entries;
    }
}