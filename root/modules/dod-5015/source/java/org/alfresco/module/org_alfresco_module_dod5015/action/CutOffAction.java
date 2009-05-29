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
package org.alfresco.module.org_alfresco_module_dod5015.action;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * @author Roy Wetherall
 */
public class CutOffAction extends RMActionExecuterAbstractBase
{

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        // TODO there are enough patterns in here to justify a "disposition" action that gave some
        //      structure to these actions
        
        // TODO check that this in actually the next disposition action for the record in question AND
        //            that it is eligiable for cut off 
        
        // Check whether the record already has the cutoff aspect or not
        if (this.nodeService.hasAspect(actionedUponNodeRef, ASPECT_CUT_OFF) == false)
        {
            QName recordType = this.nodeService.getType(actionedUponNodeRef);
            if ((this.dictionaryService.isSubClass(recordType, TYPE_RECORD_FOLDER) == true) ||
                ((this.nodeService.hasAspect(actionedUponNodeRef, ASPECT_RECORD) == true) &&
                 (this.nodeService.hasAspect(actionedUponNodeRef, ASPECT_UNDECLARED_RECORD) == false)))
            {        
                // TODO can not cut off if this is a record folder and it contains some undeclared records
                
                // Get the record categories
                // TODO for now assume only one record category
                NodeRef recordCategory = getRecordCategory(actionedUponNodeRef);
                
                // Apply the cut off aspect and set cut off date
                Map<QName, Serializable> cutOffProps = new HashMap<QName, Serializable>(1);
                cutOffProps.put(PROP_CUT_OFF_DATE, new Date());
                this.nodeService.addAspect(actionedUponNodeRef, ASPECT_CUT_OFF, cutOffProps);
                
                // Set the next disposition action                
                setNextDispositionAction(recordCategory, actionedUponNodeRef);
                
                // If it is a record folder cut off all the children
                if (this.dictionaryService.isSubClass(recordType, TYPE_RECORD_FOLDER) == true)
                {
                    // TODO we need to iterate over the child records and do the cut off accordingly 
                }
            }
            else
            {
                // TODO do we want to throw an exception here or just carry on regardless??
            }
        }
        // TODO do we throw an exception because the record is already cut off?
    }

    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {

    }

}
