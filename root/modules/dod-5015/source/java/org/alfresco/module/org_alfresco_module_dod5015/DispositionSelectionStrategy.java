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

import java.util.List;

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

    /** Service registry */
    private RecordsManagementServiceRegistry serviceRegistry;

    /** Node service */
    private NodeService nodeService;

    /**
     * Set the service registry service
     * 
     * @param serviceRegistry   service registry
     */
    public void setRecordsManagementServiceRegistry(RecordsManagementServiceRegistry serviceRegistry)
    {
        // Internal ops use the unprotected services from the voter (e.g. nodeService)
        this.serviceRegistry = serviceRegistry;
    }
    
    /**
     * Set node service
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public NodeRef selectDispositionScheduleFrom(List<NodeRef> dispositionScheduleNodeRefs)
    {
        if (dispositionScheduleNodeRefs == null || dispositionScheduleNodeRefs.isEmpty())
        {
            return null;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Selecting disposition schedule from 1 of " + dispositionScheduleNodeRefs.size());
            }

            //      46 CHAPTER 2 
            //      Records assigned more than 1 disposition must be retained and linked to the record folder (category) with the longest 
            //      retention period.
            
            //TODO Implement a proper strategy here. For now, we're just returning the first.
            NodeRef firstDispSchedule = dispositionScheduleNodeRefs.get(0);
            if (logger.isDebugEnabled())
            {
                logger.debug("Selected disposition schedule: " + firstDispSchedule);
            }
            return firstDispSchedule;
        }
    }
}
