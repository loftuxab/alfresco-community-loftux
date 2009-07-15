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

import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEvent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Period;
import org.alfresco.service.namespace.QName;

/**
 * Disposition action interface
 * 
 * @author Roy Wetherall
 */
public interface DispositionActionDefinition
{
    /**
     * Get the NodeRef that represents the disposition action definition
     * 
     * @return NodeRef of disposition action definition
     */
    NodeRef getNodeRef();
    
    /**
     * Get disposition action id
     * 
     * @return  String  id
     */
    String getId();
    
    /**
     * Get the index of the action within the disposition instructions
     * 
     * @return  int     disposition action index
     */
    int getIndex();
    
    /**
     * Get the name of disposition action
     * 
     * @return  String  name
     */
    String getName();
    
    /**
     * Get the description of the disposition action
     * 
     * @return  String  description
     */
    String getDescription();
    
    /**
     * Get the period for the disposition action
     * 
     * @return  Period  disposition period
     */
    Period getPeriod();
    
    /**
     * Property to which the period is relative to
     * 
     * @return  QName   property name
     */
    QName getPeriodProperty();
    
    /**
     * List of events for the disposition
     * 
     * @return  List<RecordsManagementEvent>    list of events
     */
    List<RecordsManagementEvent> getEvents();
    
    /**
     * Indicates whether the disposition action is eligible when the earliest event is complete, otherwise
     * all events must be complete before eligibility.
     * 
     * @return  boolean     true if eligible on first action complete, false otherwise
     */
    boolean eligibleOnFirstCompleteEvent();
}
