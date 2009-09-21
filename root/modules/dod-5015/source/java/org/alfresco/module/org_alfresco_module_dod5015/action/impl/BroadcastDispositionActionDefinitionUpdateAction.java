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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionAction;
import org.alfresco.module.org_alfresco_module_dod5015.EventCompletionDetails;
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
    private static final String CHANGED_PROPERTIES = "changedProperties";

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
     *      org.alfresco.service.cmr.repository.NodeRef)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        if (RecordsManagementModel.TYPE_DISPOSITION_ACTION_DEFINITION.equals(nodeService.getType(actionedUponNodeRef)) == false)
        {
            return;
        }
        
        Set<QName> changedProps = (Set<QName>)action.getParameterValue(CHANGED_PROPERTIES);

        // Navigate up the containment hierarchy to get the RecordCategory grandparent.
        NodeRef dispositionScheduleNode = nodeService.getPrimaryParent(actionedUponNodeRef).getParentRef();
        NodeRef recordCategoryNode = nodeService.getPrimaryParent(dispositionScheduleNode).getParentRef();
        boolean isRecordLevelDisposition = (Boolean)nodeService.getProperty(dispositionScheduleNode, PROP_RECORD_LEVEL_DISPOSITION);
        
        List<NodeRef> recordFolders = getRecordFoldersBeneath(recordCategoryNode);
        for (NodeRef recordFolder : recordFolders)
        {
            if (isRecordLevelDisposition == false)
            {
                if (changedProps.contains(PROP_DISPOSITION_PERIOD))
                {
                    Period dispositionPeriod = (Period)nodeService.getProperty(actionedUponNodeRef, PROP_DISPOSITION_PERIOD);
                    persistDispositionPeriod(recordFolder, dispositionPeriod);
                }
                if (changedProps.contains(PROP_DISPOSITION_EVENT) || changedProps.contains(PROP_DISPOSITION_EVENT_COMBINATION))
                {
                    List<String> dispositionEvents = (List<String>)nodeService.getProperty(actionedUponNodeRef, PROP_DISPOSITION_EVENT);
                    String dispositionEventCombination = (String)nodeService.getProperty(actionedUponNodeRef, PROP_DISPOSITION_EVENT_COMBINATION);
                    persistEvents(recordFolder, dispositionEvents, dispositionEventCombination);
                }
            }
            else
            {
                List<NodeRef> records = getRecordsBeneath(recordFolder);
                for (NodeRef nextRecord : records)
                {
                    if (changedProps.contains(PROP_DISPOSITION_PERIOD))
                    {
                        Period dispositionPeriod = (Period)nodeService.getProperty(actionedUponNodeRef, PROP_DISPOSITION_PERIOD);
                        persistDispositionPeriod(nextRecord, dispositionPeriod);
                    }
                    if (changedProps.contains(PROP_DISPOSITION_EVENT) || changedProps.contains(PROP_DISPOSITION_EVENT_COMBINATION))
                    {
                        List<String> dispositionEvents = (List<String>)nodeService.getProperty(actionedUponNodeRef, PROP_DISPOSITION_EVENT);
                        String dispositionEventCombination = (String)nodeService.getProperty(actionedUponNodeRef, PROP_DISPOSITION_EVENT_COMBINATION);
                        persistEvents(nextRecord, dispositionEvents, dispositionEventCombination);
                    }
                }
            }
        }
    }

    /**
     * This method finds all the children contained under the specified recordCategoryNode
     * which are record folders.
     * 
     * @param recordCategoryNode
     * @return
     */
    private List<NodeRef> getRecordFoldersBeneath(NodeRef recordCategoryNode)
    {
        List<NodeRef> result = new ArrayList<NodeRef>();
        // This recordCategory could contain 0..n RecordFolder children.
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(recordCategoryNode, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
        for (ChildAssociationRef nextAssoc : childAssocs)
        {
            NodeRef nextChild = nextAssoc.getChildRef();
            if (recordsManagementService.isRecordFolder(nextChild))
            {
                result.add(nextChild);
            }
        }
        return result;
    }

    /**
     * This method finds all the children contained under the specified recordFolderNode
     * which are record.
     * 
     * @param recordFolderNode
     * @return
     */
    private List<NodeRef> getRecordsBeneath(NodeRef recordFolderNode)
    {
        List<NodeRef> result = new ArrayList<NodeRef>();
        // This recordFolder could contain 0..n Record children.
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(recordFolderNode, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
        for (ChildAssociationRef nextAssoc : childAssocs)
        {
            NodeRef nextChild = nextAssoc.getChildRef();
            if (recordsManagementService.isRecord(nextChild))
            {
                result.add(nextChild);
            }
        }
        return result;
    }
    
    private void persistDispositionPeriod(NodeRef nextChild, Period dispositionPeriod)
    {
        if (nodeService.hasAspect(nextChild, ASPECT_DISPOSITION_LIFECYCLE))
        {
            List<ChildAssociationRef> nextActions = nodeService.getChildAssocs(nextChild, ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL);
            // There should be 0 or 1 elements
            if (nextActions.isEmpty() == false)
            {
                NodeRef nextActionNode = nextActions.get(0).getChildRef();
                // This is a dispositionAction, with dispositionAsOf property

                if (dispositionPeriod != null)
                {
                    Date now = new Date();
                    Date newAsOfDate = dispositionPeriod.getNextDate(now);
                    nodeService.setProperty(nextActionNode, PROP_DISPOSITION_AS_OF, newAsOfDate);
                }
                else
                {
                    //TODO Is this null if there are no timers?
//                    nodeService.setProperty(nextActionNode, PROP_DISPOSITION_AS_OF, null);
                }
            }
        }
    }

    private void persistEvents(NodeRef recordOrFolderNode, List<String> events, String combinator)
    {
        if (nodeService.hasAspect(recordOrFolderNode, ASPECT_DISPOSITION_LIFECYCLE))
        {
            List<ChildAssociationRef> nextActions = nodeService.getChildAssocs(recordOrFolderNode, ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL);
            // There should be 0 or 1 elements
            if (nextActions.isEmpty() == false)
            {
                NodeRef nextActionNode = nextActions.get(0).getChildRef();

                nodeService.setProperty(nextActionNode, PROP_DISPOSITION_EVENT, (Serializable)events);
                nodeService.setProperty(nextActionNode, PROP_DISPOSITION_EVENT_COMBINATION, combinator);
                
                // Now need to recalculate PROP_DISPOSITION_EVENTS_ELIGIBLE
                DispositionAction da = recordsManagementService.getNextDispositionAction(recordOrFolderNode);
                List<EventCompletionDetails> eventCompletionDetails = da.getEventCompletionDetails();
                
                boolean eligible = false;
                if (da.getDispositionActionDefinition().eligibleOnFirstCompleteEvent() == false)
                {
                    eligible = true;
                    for (EventCompletionDetails ecd : eventCompletionDetails)
                    {
                        if (ecd.isEventComplete() == false)
                        {
                            eligible = false;
                            break;
                        }
                    }
                }
                else
                {
                    for (EventCompletionDetails ecd : eventCompletionDetails)
                    {
                        if (ecd.isEventComplete() == true)
                        {
                            eligible = true;
                            break;
                        }
                    }
                }

                this.nodeService.setProperty(da.getNodeRef(), PROP_DISPOSITION_EVENTS_ELIGIBLE, eligible);
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
        qnames.add(PROP_DISPOSITION_EVENT);
        qnames.add(PROP_DISPOSITION_EVENT_COMBINATION);
        qnames.add(PROP_DISPOSITION_EVENTS_ELIGIBLE);
        return qnames;
    }

    @Override
    public Set<QName> getProtectedAspects()
    {
        return Collections.emptySet();
    }

}
