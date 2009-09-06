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
package org.alfresco.module.org_alfresco_module_dod5015.action.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Period;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * Action to implement the consequences of a change to the value of the DispositionActionDefinition
 * properties. When these properties are changed on a disposition schedule, then any associated
 * disposition actions may need to be updated as a consequence.
 * 
 * @author Neil McErlean
 */
public class BroadcastDispositionActionDefinitionUpdateAction extends RMActionExecuterAbstractBase
{
    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
     *      org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        if (RecordsManagementModel.TYPE_DISPOSITION_ACTION_DEFINITION.equals(nodeService.getType(actionedUponNodeRef)) == false)
        {
            return;
        }
        
        // Navigate up the containment hierarchy to get the RecordCategory grandparent.
        NodeRef dispositionScheduleNode = nodeService.getPrimaryParent(actionedUponNodeRef).getParentRef();
        NodeRef recordCategoryNode = nodeService.getPrimaryParent(dispositionScheduleNode).getParentRef();
        
        Period dispositionPeriod = (Period)nodeService.getProperty(actionedUponNodeRef, PROP_DISPOSITION_PERIOD);
        boolean isRecordLevelDisposition = (Boolean)nodeService.getProperty(dispositionScheduleNode, PROP_RECORD_LEVEL_DISPOSITION);

        // This recordCategory could contain 0..n RecordFolder children.
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(recordCategoryNode, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
        for (ChildAssociationRef nextAssoc : childAssocs)
        {
            NodeRef nextChild = nextAssoc.getChildRef();
            if (recordsManagementService.isRecordFolder(nextChild) && isRecordLevelDisposition == false)
            {
                persistLifecycleUpdates(nextChild, dispositionPeriod);
            }
            if (recordsManagementService.isRecordFolder(nextChild) && isRecordLevelDisposition == true)
            {
                // Each record Folder can contain 0..n Record children. TODO Consider multiple filing.
                List<ChildAssociationRef> childOfFolderAssocs = nodeService.getChildAssocs(nextChild, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
                for (ChildAssociationRef nextFolderAssoc : childOfFolderAssocs)
                {
                    NodeRef nextPotentialRecord = nextFolderAssoc.getChildRef();
                    if (recordsManagementService.isRecord(nextPotentialRecord))
                    {
                        persistLifecycleUpdates(nextPotentialRecord, dispositionPeriod);
                    }
                }
            }
        }
    }

    private void persistLifecycleUpdates(NodeRef nextChild, Period dispositionPeriod)
    {
        //TODO What about the other possible changes to the schedule?
        if (nodeService.hasAspect(nextChild, ASPECT_DISPOSITION_LIFECYCLE))
        {
            List<ChildAssociationRef> nextActions = nodeService.getChildAssocs(nextChild, ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL);
            // There should be 0 or 1 elements
            if (nextActions.isEmpty() == false)
            {
                NodeRef nextActionNode = nextActions.get(0).getChildRef();
                // This is a dispositionAction, with dispositionAsOf property

                Date now = new Date();
                Date newAsOfDate = dispositionPeriod.getNextDate(now);
                nodeService.setProperty(nextActionNode, PROP_DISPOSITION_AS_OF, newAsOfDate);
            }
        }
    }

    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        // Intentionally empty
    }

    @Override
    public boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        return true;
    }

    @Override
    public Set<QName> getProtectedProperties()
    {
        HashSet<QName> qnames = new HashSet<QName>();
        qnames.add(PROP_DISPOSITION_AS_OF);
        return qnames;
    }

    @Override
    public Set<QName> getProtectedAspects()
    {
        HashSet<QName> qnames = new HashSet<QName>();
        // Intentionally empty Set.
        return qnames;
    }

}
