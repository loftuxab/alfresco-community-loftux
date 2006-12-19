/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.rm.action;

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
