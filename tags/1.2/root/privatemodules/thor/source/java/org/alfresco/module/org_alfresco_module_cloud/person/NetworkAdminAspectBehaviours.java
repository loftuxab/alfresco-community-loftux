/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.person;

import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.repo.copy.CopyBehaviourCallback;
import org.alfresco.repo.copy.CopyDetails;
import org.alfresco.repo.copy.CopyServicePolicies;
import org.alfresco.repo.copy.DefaultCopyBehaviourCallback;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * This bean provides the policies/behaviours associated with the {@link CloudModel#ASPECT_NETWORK_ADMIN} aspect.
 * 
 * @author Neil Mc Erlean
 * @since Thor
 */
public class NetworkAdminAspectBehaviours implements CopyServicePolicies.OnCopyNodePolicy
{
    private PolicyComponent policyComponent;

    /**
     * Set the policy component
     * 
     * @param policyComponent   policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * Initialise method
     */
    public void init()
    {
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "getCopyCallback"), 
                CloudModel.ASPECT_NETWORK_ADMIN,
                new JavaBehaviour(this, "getCopyCallback"));
    }
    
    @Override public CopyBehaviourCallback getCopyCallback(QName classRef, CopyDetails copyDetails)
    {
        return NetworkAdminAspectCopyBehaviourCallback.INSTANCE;
    }
    
    private static class NetworkAdminAspectCopyBehaviourCallback extends DefaultCopyBehaviourCallback
    {
        private static final CopyBehaviourCallback INSTANCE = new NetworkAdminAspectCopyBehaviourCallback();
        
        /**
         * We do not copy the {@link CloudModel#ASPECT_NETWORK_ADMIN} aspect.
         */
        @Override
        public boolean getMustCopy(QName classQName, CopyDetails copyDetails)
        {
            // Prevent the copying of the network admin aspect only.
            return (! CloudModel.ASPECT_NETWORK_ADMIN.equals(classQName));
        }
    }
}
