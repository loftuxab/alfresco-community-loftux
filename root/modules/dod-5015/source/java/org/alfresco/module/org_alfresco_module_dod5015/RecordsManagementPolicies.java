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

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Interface containing records management policies
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementPolicies
{
    /** Policy names */
    public static final QName BEFORE_RM_ACTION_EXECUTION = QName.createQName(NamespaceService.ALFRESCO_URI, "beforeRMActionExecution");
    public static final QName ON_RM_ACTION_EXECUTION = QName.createQName(NamespaceService.ALFRESCO_URI, "onRMActionExecution");
    public static final QName BEFORE_CREATE_REFERENCE = QName.createQName(NamespaceService.ALFRESCO_URI, "beforeCreateReference");
    public static final QName ON_CREATE_REFERENCE = QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateReference");
    public static final QName BEFORE_REMOVE_REFERENCE = QName.createQName(NamespaceService.ALFRESCO_URI, "beforeRemoveReference");
    public static final QName ON_REMOVE_REFERENCE = QName.createQName(NamespaceService.ALFRESCO_URI, "onRemoveReference");
    
    /** Before records management action execution */
    public interface BeforeRMActionExecution extends ClassPolicy
    {        
        public void beforeRMActionExecution(NodeRef nodeRef, String name, Map<String, Serializable> parameters);
    }
    
    /** On records management action execution */
    public interface OnRMActionExecution extends ClassPolicy
    {        
        public void onRMActionExecution(NodeRef nodeRef, String name, Map<String, Serializable> parameters);
    }
    
    /** Before creation of reference */
    public interface BeforeCreateReference extends ClassPolicy
    {
        public void beforeCreateReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference);
    }
    
    /** On creation of reference */
    public interface OnCreateReference extends ClassPolicy
    {
        public void onCreateReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference);
    }
    
    /** Before removal of reference */
    public interface BeforeRemoveReference extends ClassPolicy
    {
        public void beforeRemoveReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference);
    }
    
    /** On removal of reference */
    public interface OnRemoveReference extends ClassPolicy
    {
        public void onRemoveReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference);
    }
}
