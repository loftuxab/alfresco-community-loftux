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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.module.org_alfresco_module_dod5015.jscript;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementServiceRegistry;
import org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.scripts.ScriptException;

/**
 * Records management service
 * 
 * @author Roy Wetherall
 */
public class ScriptRecordsManagmentService extends BaseScopableProcessorExtension
                                           implements RecordsManagementModel 
{
    private RecordsManagementServiceRegistry rmServices;
    
    public void setRecordsManagementServiceRegistry(RecordsManagementServiceRegistry rmServices)
    {
        this.rmServices = rmServices;
    }
    
    public ScriptRecordsManagmentNode getRecordsManagementNode(ScriptNode node)
    {
        ScriptRecordsManagmentNode result = null;
        
        if (rmServices.getNodeService().hasAspect(node.getNodeRef(), ASPECT_FILE_PLAN_COMPONENT) == true)
        {
            // TODO .. at this point determine what type of records management node is it and 
            //         create the appropariate sub-type
            result = new ScriptRecordsManagmentNode(node.getNodeRef(), rmServices);
        }
        else
        {
            throw new ScriptException("Node is not a records management node type.");
        }
        
        return result;
    }
    
    /**
     * Set the RM permission
     * 
     * @param node
     * @param permission
     * @param authority
     */
    public void setPermission(ScriptNode node, String permission, String authority)
    {
        RecordsManagementSecurityService securityService = rmServices.getRecordsManagementSecurityService();
        securityService.setPermission(node.getNodeRef(), authority, permission);
    }
}
