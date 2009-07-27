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
package org.alfresco.module.org_alfresco_module_dod5015.action.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMDispositionActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Cut off disposition action
 * 
 * @author Roy Wetherall
 */
public class CutOffAction extends RMDispositionActionExecuterAbstractBase
{
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.action.RMDispositionActionExecuterAbstractBase#executeRecordFolderLevelDisposition(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeRecordFolderLevelDisposition(Action action, NodeRef recordFolder)
    {
        // Close folder
        this.recordsManagementActionService.executeRecordsManagementAction(recordFolder, "closeRecordFolder");
        
        // Mark the folder as cut off
        doCutOff(recordFolder);
        
        // Mark all the declared children of the folder as cut off
        List<NodeRef> records = this.recordsManagementService.getRecords(recordFolder);
        for (NodeRef record : records)
        {
            doCutOff(record);
        }
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.action.RMDispositionActionExecuterAbstractBase#executeRecordLevelDisposition(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeRecordLevelDisposition(Action action, NodeRef record)
    {
        // Mark the record as cut off
        doCutOff(record);       
    }
    
    /**
     * Marks the record or record folder as cut off, calculating the cut off date.
     * 
     * @param nodeRef   node reference
     */
    private void doCutOff(NodeRef nodeRef)
    {
        if (this.nodeService.hasAspect(nodeRef, ASPECT_CUT_OFF) == false)
        {
            // Apply the cut off aspect and set cut off date
            Map<QName, Serializable> cutOffProps = new HashMap<QName, Serializable>(1);
            cutOffProps.put(PROP_CUT_OFF_DATE, new Date());
            this.nodeService.addAspect(nodeRef, ASPECT_CUT_OFF, cutOffProps);
        }
    }
    
    @Override
    public Set<QName> getProtectedProperties()
    {
        HashSet<QName> qnames = new HashSet<QName>();
        qnames.add(PROP_CUT_OFF_DATE);
        return qnames;
    }

    @Override
    public Set<QName> getProtectedAspects()
    {
        HashSet<QName> qnames = new HashSet<QName>();
        qnames.add(ASPECT_CUT_OFF);
        return qnames;
    }

    @Override
    protected boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        // dupicates code from close .. it should get the closed action somehow?
        if (this.recordsManagementService.isRecordFolder(filePlanComponent))
        {
            return true;
        }
        else
        {
            if (throwException)
            {
                throw new AlfrescoRuntimeException("Can not close a node unless it is a record folder. (" + filePlanComponent.toString() + ")");
            }
            else
            {
                return false;
            }
        }
    }
    
    
 }