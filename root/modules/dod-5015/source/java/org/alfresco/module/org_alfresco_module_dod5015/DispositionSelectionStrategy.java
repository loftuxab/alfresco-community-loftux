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
package org.alfresco.module.org_alfresco_module_dod5015;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class offers the default implementation of a strategy for selection of
 * disposition schedule for a record when there is more than one which is applicable.
 * An example of where this strategy might be used would be in the case of a record
 * which was multiply filed.
 * 
 * @author neilm
 */
public class DispositionSelectionStrategy implements RecordsManagementModel
{
    /** Logger */
    private static Log logger = LogFactory.getLog(DispositionSelectionStrategy.class);

    /** Node service */
    private NodeService nodeService;

    /** RecordsManagementService **/
    private RecordsManagementService recordsManagementService; 
    
    /**
     * Set node service
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setRecordsManagementService(RecordsManagementService recordsManagementService)
    {
        this.recordsManagementService = recordsManagementService;
    }
    
    public NodeRef selectDispositionScheduleFrom(List<NodeRef> recordFolders)
    {
        if (recordFolders == null || recordFolders.isEmpty())
        {
            return null;
        }
        else
        {
            //      46 CHAPTER 2 
            //      Records assigned more than 1 disposition must be retained and linked to the record folder (category) with the longest 
            //      retention period.

            // Assumption: an event-based disposition action has a longer retention
            // period than a time-based one - as we cannot know when an event will occur
            // TODO Automatic events?
            
            SortedSet<NodeRef> sortedFolders = new TreeSet<NodeRef>(new DispositionableNodeRefComparator());
            for (NodeRef f : recordFolders)
            {
                sortedFolders.add(f);
            }
            DispositionSchedule dispSchedule = recordsManagementService.getDispositionSchedule(sortedFolders.first());
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Selected disposition schedule: " + dispSchedule);
            }
            return dispSchedule.getNodeRef();
        }
    }

    /**
     * This class defines a natural comparison order between NodeRefs that have
     * the dispositionLifecycle aspect applied.
     * This order has the following meaning: NodeRefs with a 'lesser' value are considered
     * to have a shorter retention period, although the actual retention period may
     * not be straightforwardly determined in all cases.
     */
    class DispositionableNodeRefComparator implements Comparator<NodeRef>
    {
        public int compare(NodeRef f1, NodeRef f2)
        {
            //TODO Check the nodeRefs have the correct aspect
            
            DispositionAction da1 = recordsManagementService.getNextDispositionAction(f1);
            DispositionAction da2 = recordsManagementService.getNextDispositionAction(f2);
            
            if (da1 != null && da2 != null)
            {
                Date asOfDate1 = da1.getAsOfDate();
                Date asOfDate2 = da2.getAsOfDate();
                // If both record(Folder)s have asOfDates, then use these to compare
                if (asOfDate1 != null && asOfDate2 != null)
                {
                    return asOfDate1.compareTo(asOfDate2);
                }
                // If one has a date and the other doesn't, the one with the date is "less".
                // (Defined date is 'shorter' than undefined date as an undefined date means it may be retained forever - theoretically)
                else if (asOfDate1 != null || asOfDate2 != null)
                {
                    return asOfDate1 == null ? +1 : -1;
                }
                else
                {
                    // Neither has an asOfDate. (Somewhat arbitrarily) we'll use the number of events to compare now.
                    DispositionActionDefinition dad1 = da1.getDispositionActionDefinition();
                    DispositionActionDefinition dad2 = da2.getDispositionActionDefinition();
                    int eventsCount1 = 0;
                    int eventsCount2 = 0;
                    
                    if (dad1 != null)
                    {
                        eventsCount1 = dad1.getEvents().size();
                    }
                    if (dad2 != null)
                    {
                        eventsCount2 = dad2.getEvents().size();
                    }
                    return new Integer(eventsCount1).compareTo(eventsCount2);
                }
            }

            return 0;
        }
    }
}