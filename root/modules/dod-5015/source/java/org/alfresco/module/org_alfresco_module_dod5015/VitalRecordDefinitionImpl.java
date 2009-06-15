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
package org.alfresco.module.org_alfresco_module_dod5015;

import java.util.Date;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Period;

/**
 * Vital record definition implementation class
 * 
 * @author Roy Wetherall
 */
public class VitalRecordDefinitionImpl implements VitalRecordDefinition, RecordsManagementModel
{
    /** Service registry */
    private ServiceRegistry services;
    
    /** Node reference containing the vital record defintion aspect */
    private NodeRef nodeRef;
    
    /**
     * Constructor
     * 
     * @param services  service registry
     * @param nodeRef   node reference
     */
    public VitalRecordDefinitionImpl(ServiceRegistry services, NodeRef nodeRef)
    {
        // Set the services reference
        this.services = services;
        
        // Check that we have a node that has the vital record definition aspect attached
        if (this.services.getNodeService().hasAspect(nodeRef, ASPECT_VITAL_RECORD_DEFINITION) == false)
        {
            throw new AlfrescoRuntimeException("Vital record definition aspect is not present on node. (" + nodeRef + ")");
        }        
        this.nodeRef = nodeRef;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.VitalRecordDefinition#getNextReviewDate()
     */
    public Date getNextReviewDate()
    {
        return getReviewPeriod().getNextDate(new Date());
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.VitalRecordDefinition#getReviewPeriod()
     */
    public Period getReviewPeriod()
    {
        return (Period)this.services.getNodeService().getProperty(this.nodeRef, PROP_REVIEW_PERIOD);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.VitalRecordDefinition#isVitalRecord()
     */
    public boolean isVitalRecord()
    {
        // Default value set in model so this is safe
        return ((Boolean)this.services.getNodeService().getProperty(this.nodeRef, PROP_VITAL_RECORD_INDICATOR)).booleanValue();
    }

}
