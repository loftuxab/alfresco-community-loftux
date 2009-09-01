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

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Event completion details
 * 
 * @author Roy Wetherall
 */
public class EventCompletionDetails
{
    private NodeRef nodeRef;
    private String eventName;
    private String eventLabel;
    private boolean eventExecutionAutomatic;
    private boolean eventComplete;
    private Date eventCompletedAt;
    private String eventCompletedBy;


    /**
     * @param nodeRef
     * @param eventName
     * @param eventLabel
     * @param eventExecutionAutomatic
     * @param eventComplete
     * @param eventCompletedAt
     * @param eventCompletedBy
     */
    public EventCompletionDetails(  NodeRef nodeRef,
                                    String eventName,
                                    String eventLabel,
                                    boolean eventExecutionAutomatic, 
                                    boolean eventComplete,
                                    Date eventCompletedAt, 
                                    String eventCompletedBy)
    {
        this.nodeRef = nodeRef;
        this.eventName = eventName;
        this.eventLabel = eventLabel;
        this.eventExecutionAutomatic = eventExecutionAutomatic;
        this.eventComplete = eventComplete;
        this.eventCompletedAt = eventCompletedAt;
        this.eventCompletedBy = eventCompletedBy;
    } 
    
    /**
     * @return the node reference
     */
    public NodeRef getNodeRef()
    {
        return nodeRef;
    }
    
    /**
     * @return the eventName
     */
    public String getEventName()
    {
        return eventName;
    }
    
    /**
     * @param eventName the eventName to set
     */
    public void setEventName(String eventName)
    {
        this.eventName = eventName;
    }
    
    /**
     * @return The display label of the event
     */
    public String getEventLabel()
    {
        return this.eventLabel;
    }
    
    /**
     * @return the eventExecutionAutomatic
     */
    public boolean isEventExecutionAutomatic()
    {
        return eventExecutionAutomatic;
    }
    
    /**
     * @param eventExecutionAutomatic the eventExecutionAutomatic to set
     */
    public void setEventExecutionAutomatic(boolean eventExecutionAutomatic)
    {
        this.eventExecutionAutomatic = eventExecutionAutomatic;
    }
    
    /**
     * @return the eventComplete
     */
    public boolean isEventComplete()
    {
        return eventComplete;
    }
    
    /**
     * @param eventComplete the eventComplete to set
     */
    public void setEventComplete(boolean eventComplete)
    {
        this.eventComplete = eventComplete;
    }
    
    /**
     * @return the eventCompletedAt
     */
    public Date getEventCompletedAt()
    {
        return eventCompletedAt;
    }
    
    /**
     * @param eventCompletedAt the eventCompletedAt to set
     */
    public void setEventCompletedAt(Date eventCompletedAt)
    {
        this.eventCompletedAt = eventCompletedAt;
    }
    
    /**
     * @return the eventCompletedBy
     */
    public String getEventCompletedBy()
    {
        return eventCompletedBy;
    }
    
    /**
     * @param eventCompletedBy the eventCompletedBy to set
     */
    public void setEventCompletedBy(String eventCompletedBy)
    {
        this.eventCompletedBy = eventCompletedBy;
    }  
}
