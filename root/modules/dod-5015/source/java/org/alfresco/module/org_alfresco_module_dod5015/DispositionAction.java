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
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author Roy Wetherall
 */
public interface DispositionAction
{    
    /**
     * @return  the node reference
     */
    NodeRef getNodeRef();
    
    /**
     * @return  the disposition action definition
     */
    DispositionActionDefinition getDispositionActionDefinition();
    
    /**
     * @return the id of the action
     */
    String getId();
    
    /**
     * @return the name of the action
     */
    String getName();
    
    /**
     * @return the display label for the action 
     */
    String getLabel();
    
    /**
     * @return  the dispostion action as of eligibility date
     */
    Date getAsOfDate();
    
    /**
     * @return  true if the events are complete (ie: enough events have been completed to make the disposition
     *          action 
     */
    boolean isEventsEligible();
    
    /**
     * @return the user that started the action
     */
    String getStartedBy();
    
    /**
     * @return when the action was started
     */
    Date getStartedAt();
    
    /**
     * @return the user that completed the action
     */
    String getCompletedBy();
    
    /**
     * @return when the action was completed
     */
    Date getCompletedAt();
    
    /**
     * @return List of events that need to be completed for the action
     */
    List<EventCompletionDetails> getEventCompletionDetails();
}
