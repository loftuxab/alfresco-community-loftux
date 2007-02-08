/*
 * Copyright (C) 2005 Alfresco, Inc.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.alfresco.module.recordsManagement.action;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.VersionService;

/**
 * @author Roy Wetherall
 *
 */
public class DestroyDispositionAction extends ActionExecuterAbstractBase
{
    private NodeService nodeService;
    private VersionService versionService;
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setVersionService(VersionService versionService)
    {
        this.versionService = versionService;
    }
    
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        if (this.nodeService.exists(actionedUponNodeRef) == true)
        {
            // Ensure that any version history has been deleted for this node       
            this.versionService.deleteVersionHistory(actionedUponNodeRef);
            
            // Delete the node itself
            this.nodeService.addAspect(actionedUponNodeRef, ContentModel.ASPECT_TEMPORARY, null);
            this.nodeService.deleteNode(actionedUponNodeRef);
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        // This method has no parameters       
    }

}
