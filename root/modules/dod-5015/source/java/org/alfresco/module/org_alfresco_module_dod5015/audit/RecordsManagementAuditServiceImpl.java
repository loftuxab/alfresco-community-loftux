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
import org.alfresco.service.cmr.security.PersonService;
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

    protected NodeService nodeService;
    protected AuditService auditService;
    protected PersonService personService;

    // temporary field to hold imaginary enabled flag
    private boolean enabled = false;
    
    /**
     * Sets the AuditService instance
     * 
     * @param auditService AuditService instance
     */
	public void setAuditService(AuditService auditService)
	{
		this.auditService = auditService;
	}
	
	/**
     * Sets the NodeService instance
     * 
     * @param nodeService NodeService instance
     */
	public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
	
	/**
     * Sets the PersonService instance
     * 
     * @param personService PersonService instance
     */
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }
	
	/*
	 * @see org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService#isEnabled()
	 */
	public boolean isEnabled()
    {
        return this.enabled;
    }
	
	/*
	 * @see org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService#start()
	 */
    public void start()
    {
        // TODO: Start RM auditing properly!
        this.enabled = true;
        
        if (logger.isInfoEnabled())
            logger.info("Started Records Management auditing");
    }

    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService#stop()
     */
    public void stop()
    {
        // TODO: Stop RM auditing properly!
        this.enabled = false;
        
        if (logger.isInfoEnabled())
            logger.info("Stopped Records Management auditing");
    }
    
    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService#clear()
     */
    public void clear()
    {
        // TODO: Clear the RM audit trail
        
        if (logger.isInfoEnabled())
            logger.debug("Records Management audit log has been cleared");
    }
    
    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService#getDateLastStarted()
     */
    public Date getDateLastStarted()
    {
        // TODO: return proper date, for now it's today's date
        return new Date();
    }
    
    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService#getDateLastStopped()
     */
    public Date getDateLastStopped()
    {
        // TODO: return proper date, for now it's today's date
        return new Date();
    }
    
    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService#getAuditTrail()
     */
    public List<RecordsManagementAuditEntry> getAuditTrail()
    {
        return getAuditTrail(null);
    }
    
    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService#getAuditTrail(org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditQueryParameters)
     */
    public List<RecordsManagementAuditEntry> getAuditTrail(
                RecordsManagementAuditQueryParameters params)
    {
        if (logger.isDebugEnabled())
            logger.debug("Retrieving audit trail using parameters: " + params);
        
        List<RecordsManagementAuditEntry> entries = new ArrayList<RecordsManagementAuditEntry>();
        
        if (params != null)
        {
            // TODO: examine parameters and build up query or call relevant service methods,
            //       for now mimic user filter and max entries in here!
            
            if (params.getNodeRef() != null)
            {
                // get audit trail for provided node
                List<AuditInfo> auditLog = this.auditService.getAuditTrail(params.getNodeRef());
                
                if (logger.isDebugEnabled())
                    logger.debug("Found " + auditLog.size() + " audit log entries");
                
                for (AuditInfo entry: auditLog)
                {
                    RecordsManagementAuditEntry rmEntry = createRMAuditEntry(entry, params.getNodeRef());
                    if (rmEntry != null)
                    {
                        // NOTE: temporary user filtering
                        if (params.getUser() == null || params.getUser().equals(entry.getUserIdentifier()))
                        {
                            entries.add(rmEntry);
                        }
                        
                        // NOTE: temporary way to mimic maximum number of results
                        if (params.getMaxEntries() != -1 && (entries.size() == params.getMaxEntries()))
                        {
                            break;
                        }
                    }
                }
                
                if (logger.isDebugEnabled())
                    logger.debug("Returning " + entries.size() + " relevant audit log entries");
            }
        }
        else
        {
            // TODO: return whole RM audit trail, for now just let the empty list go back
        }
        
        return entries;
    }
    
    /**
     * Creates a RecordsManagementAuditEntry instance for the given AuditInfo.
     * 
     * @param entry The AuditInfo instance holding audit log entry to generate
     * @param nodeRef The node the audit log entry is for
     * @return RecordsManagementAuditEntry instance
     */
    protected RecordsManagementAuditEntry createRMAuditEntry(AuditInfo entry, NodeRef nodeRef)
    {
        RecordsManagementAuditEntry rmEntry = null;
        
        String service = entry.getAuditService();
        String method = entry.getAuditMethod();
        if (service != null && method != null)
        {
            // construct the event from the service and method
            String event = service + "." + method;
            String userName = entry.getUserIdentifier();
            String fullName = getFullName(userName);
            // TODO: Call the [yet to be implemented] RM security service to get user's role
            String userRole = "Records Manager";
            String nodeName = (String)nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
            
            // create the entry instance
            rmEntry = new RecordsManagementAuditEntry(entry.getDate(), userName, fullName, 
                        userRole, nodeRef, nodeName, event);
        }
        
        return rmEntry;
    }
    
    /**
     * Returns the full name of the given username.
     * 
     * @param userName User name to get full name for
     * @return User's full name
     */
    protected String getFullName(String userName)
    {
        // TODO: Add caching in here as the same username is going to be potentially
        //       looked up multiple times for the same request and the processing below
        //       won't be that cheap!
        
        String fullName = null;
        
        NodeRef person = personService.getPerson(userName);
        if (person != null)
        {
            String firstName = (String)nodeService.getProperty(person, ContentModel.PROP_FIRSTNAME);
            String lastName = (String)nodeService.getProperty(person, ContentModel.PROP_LASTNAME);
            
            fullName = ((firstName != null && firstName.length() > 0) ? firstName : "");
            if (lastName != null && lastName.length() > 0)
            {
                fullName += (fullName.length() > 0 ? " " : "");
                fullName += lastName;
            }
        }
        
        // make sure something is returned
        if (fullName == null || fullName.length() == 0)
        {
            fullName = userName;
        }
        
        return fullName;
    }
}