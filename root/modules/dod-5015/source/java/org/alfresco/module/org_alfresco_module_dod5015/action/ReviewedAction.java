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
package org.alfresco.module.org_alfresco_module_dod5015.action;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
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
		//TODO Add type validation on the actionedUponNodeRef
		//     e.g.
		// QName recordType = this.nodeService.getType(actionedUponNodeRef);
		// if (((this.nodeService.hasAspect(actionedUponNodeRef, ASPECT_RECORD) == false) ||
		//     (this.nodeService.hasAspect(actionedUponNodeRef, ASPECT_UNDECLARED_RECORD) == true)))
		// {return; or throw Exception}

		List<NodeRef> containingFolders = this.getRecordFolders(actionedUponNodeRef);
		//TODO Note the (erroneous?) assumption here of a single containingFolder.
		NodeRef firstContainingFolder = containingFolders.get(0);
		Serializable period = this.nodeService.getProperty(firstContainingFolder, PROP_REVIEW_PERIOD);
		
		// Calculate the review schedule
		Date newAsOfDate = this.calculateAsOfDate((String)period, new Date());
		if (newAsOfDate != null)
		{
			if (logger.isDebugEnabled())
			{
				StringBuilder msg = new StringBuilder();
				msg.append("Setting new reviewAsOf property [")
				    .append(newAsOfDate)
				    .append("] on ")
				    .append(actionedUponNodeRef);
				logger.debug(msg.toString());
			}

			this.nodeService.setProperty(actionedUponNodeRef, PROP_REVIEW_AS_OF, newAsOfDate);
		}
		else
		{
			if (logger.isDebugEnabled())
			{
				StringBuilder msg = new StringBuilder();
				msg.append("New reviewAsOf property was null ")
				    .append(" for ")
				    .append(actionedUponNodeRef);
				logger.debug(msg.toString());
			}
		}

        // TODO .. should we mark all the children as reviewed?
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
}
