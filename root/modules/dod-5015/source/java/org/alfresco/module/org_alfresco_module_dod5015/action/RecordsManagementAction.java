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
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


/**
 * Record Management Action
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementAction
{
    /**
     * Get the name of the action
     * 
     * @return  String  action name
     */
    public String getName();
    
    /**
     * Get the label of the action
     * 
     * @return  String  action label
     */
    public String getLabel();
    
    /**
     * Get the description of the action
     * 
     * @return  String  action description 
     */
    public String getDescription();
    
    /**
     * Indicates whether this is a disposition action or not
     * 
     * @return  boolean     true if a disposition action, false otherwise
     */
    boolean isDispositionAction();
    
    /**
     * Execution of the action
     * 
     * @param filePlanComponent     file plan component the action is executed upon
     * @param parameters            action parameters
     */
    public RecordsManagementActionResult execute(NodeRef filePlanComponent, Map<String, Serializable> parameters);
    
    
    /**
     * Can this action be executed?
     * Does it meet all of its entry requirements - EXCEPT permission checks.
     * 
     * @param filePlanComponent     file plan component the action is executed upon
     * @param parameters            action parameters
     * @return
     */
    public boolean isExecutable(NodeRef filePlanComponent, Map<String, Serializable> parameters);
    
    
    /**
     * Get a set of properties that should only be updated via this or other action.
     * These properties will be rejected by updates via the generic public services, such as the NodeService.
     * 
     * @return the set of protected properties
     */
    public Set<QName> getProtectedProperties();
    
    /**
     * Get a set of aspects that should be updated via this or other actions.
     * The aspect can not be added via public services, such as the NodeService.
     * @return
     */
    public Set<QName> getProtectedAspects();

    /**
     * Some admin-related rmActions execute against a target nodeRef which is not provided
     * by the calling code, but is instead an implementation detail of the action.
     * 
     * @return the target nodeRef
     */
    public NodeRef getImplicitTargetNodeRef();
}
