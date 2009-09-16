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
package org.alfresco.module.org_alfresco_module_dod5015.identifier;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Record component identifier aspect behaviour
 * 
 * @author Roy Wetherall
 */
public class RecordComponentIdentifierAspect implements NodeServicePolicies.OnUpdatePropertiesPolicy,
                                                        RecordsManagementModel
{
    /** Policy component */    
    private PolicyComponent policyComponent;
    
    /** Node service */
    private NodeService nodeService;

    /**
     * @param policyComponent the policyComponent to set
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    /**
     * @param nodeService the nodeService to set
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }     

    /**
     * Initialise method
     */
    public void init()
    {
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"), 
                ASPECT_RECORD_COMPONENT_ID, 
                new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy#onUpdateProperties(org.alfresco.service.cmr.repository.NodeRef, java.util.Map, java.util.Map)
     */
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, final Map<QName, Serializable> after)
    {
        AuthenticationUtil.runAs(new RunAsWork<Object>()
        {
            public Object doWork()
            {
                // Check whether the identifier property has changed
                String beforeId = (String)before.get(PROP_IDENTIFIER);
                String afterId = (String)after.get(PROP_IDENTIFIER);
                if (before != null && after == null)
                {
                    // Get the db id
                    String dbId = (String)nodeService.getProperty(nodeRef, PROP_DB_UNIQUENESS_ID);
                    if (dbId != null)
                    {            
                        // TODO do we need to clear this out of the Db??
                        // uniquenessService.clear(dbId);
                        
                        // Clear the DbUniquenessId
                        nodeService.setProperty(nodeRef, PROP_DB_UNIQUENESS_ID, null);
                    }
                }
                else if (after != null && 
                        (before == null || after.equals(before) == false))
                {
                    // Get the context node
                    ChildAssociationRef childAssoc = nodeService.getPrimaryParent(nodeRef);
                    NodeRef context = childAssoc.getParentRef();
                    
                    // Get the db id (if null then carry on)
                    String dbId = (String)nodeService.getProperty(nodeRef, PROP_DB_UNIQUENESS_ID); 
                    
                    // TODO Check for uniqueness
                    //dbId = uniquenesService.isUnique(dbId, afterId, context);            
                    
                    // Set the DbUniquenessId
                    nodeService.setProperty(nodeRef, PROP_DB_UNIQUENESS_ID, dbId);
                }
                
                return null;
            }
        }, AuthenticationUtil.getSystemUserName()); 
    }
}
