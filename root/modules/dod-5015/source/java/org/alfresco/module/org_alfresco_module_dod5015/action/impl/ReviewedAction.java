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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.action.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.module.org_alfresco_module_dod5015.VitalRecordDefinition;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Neil McErlean
 */
public class ReviewedAction extends RMActionExecuterAbstractBase
{
    private static Log logger = LogFactory.getLog(ReviewedAction.class);

	/**
	 * 
	 * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
	 *      org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
	{
	    VitalRecordDefinition vrDef = this.recordsManagementService.getVitalRecordDefinition(actionedUponNodeRef);
        if (vrDef != null && vrDef.isVitalRecord() == true)
        {
    	    if (recordsManagementService.isRecord(actionedUponNodeRef) == true)
    	    {
    	        reviewRecord(actionedUponNodeRef, vrDef);
	        }
    	    else if (recordsManagementService.isRecordFolder(actionedUponNodeRef) == true)
    	    {
    	        for (NodeRef record : recordsManagementService.getRecords(actionedUponNodeRef))
                {
                    reviewRecord(record, vrDef);
                }
    	    }
	    }
	}
	
	private void reviewRecord(NodeRef nodeRef, VitalRecordDefinition vrDef)
	{
        // Calculate the next review date
        Date reviewAsOf = vrDef.getNextReviewDate();
        if (reviewAsOf != null)
        {
            // Log
            if (logger.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                    msg.append("Setting new reviewAsOf property [")
                       .append(reviewAsOf)
                       .append("] on ")
                       .append(nodeRef);
                 logger.debug(msg.toString());
            }
            
            this.nodeService.setProperty(nodeRef, PROP_REVIEW_AS_OF, reviewAsOf);
            //TODO And record previous review date, time, user
        }
	}

	/**
	 * 
	 * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
	 */
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList)
	{
		// Intentionally empty
	}
	
	@Override
    public Set<QName> getProtectedProperties()
    {
        HashSet<QName> qnames = new HashSet<QName>();
        qnames.add(PROP_REVIEW_AS_OF);
        return qnames;
    }

    @Override
    protected boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        return true;
    }
	
	
}
