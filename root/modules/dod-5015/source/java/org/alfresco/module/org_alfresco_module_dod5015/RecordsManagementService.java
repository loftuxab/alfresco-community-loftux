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

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Records management service interface
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementService
{
    /**
     * Indicates whether the given node is a record or not.
     * 
     * @param nodeRef   node reference
     * @return boolean  true if record, false otherwise
     */
    boolean isRecord(NodeRef nodeRef);
    
    /**
     * 
     * @param nodeRef
     * @return
     */
    boolean isRecordDeclared(NodeRef nodeRef);
    
    /**
     * 
     * @param nodeRef
     * @return
     */
    boolean isRecordFolderDeclared(NodeRef nodeRef);
    
    /**
     * Indicates whether the given node is a record folder or not.
     * 
     * @param nodeRef   node reference
     * @return boolean  true if record folder, false otherwise
     */
    boolean isRecordFolder(NodeRef nodeRef);
    
    /**
     * Indicates wehther the given node is a record management container of not.
     * 
     * @param nodeRef   node reference
     * @return boolean  true if records management container
     */
    boolean isRecordsManagementContainer(NodeRef nodeRef);
    
    /**
     * Get all the record folders that a record is filed into.
     * 
     * @param record            the record node reference
     * @return List<NodeRef>    list of folder record node references
     */
    List<NodeRef> getRecordFolders(NodeRef record);
    
    /**
     * 
     * @param recordFolder
     * @return
     */
    List<NodeRef> getRecords(NodeRef recordFolder);
    
    /**
     * Get the disposition instructions for a given record management node.
     * 
     * @param nodeRef                   node reference to rm container, record folder or record
     * @return DispositionInstructions  disposition instructions
     */
    DispositionInstructions getDispositionInstructions(NodeRef nodeRef);
    
    /**
     * 
     * @param dispositionInstructions
     * @param record
     */
    void updateNextDispositionAction(NodeRef nodeRef);
    
    /**
     * 
     * @param nodeRef
     * @return
     */
    boolean isNextDispositionActionEligible(NodeRef nodeRef);
    
    /**
     * Get the vital record definition for a given node reference within the file plan
     * 
     * @param nodeRef               node reference to a container, record folder or record
     * @return VitalRecordDetails   vital record details, null if none
     */
    VitalRecordDefinition getVitalRecordDefinition(NodeRef nodeRef);
}
