/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_dod5015.capability.impl;

import net.sf.acegisecurity.vote.AccessDecisionVoter;

import org.alfresco.module.org_alfresco_module_dod5015.capability.RMPermissionModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;

/**
 * @author andyh
 *
 */
public class TriggerAnEventCapability extends AbstractCapability
{

    /* (non-Javadoc)
     * @see org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AbstractCapability#hasPermissionImpl(org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected int hasPermissionImpl(NodeRef nodeRef)
    {
        return evaluate(nodeRef);
    }
    
    public int evaluate(NodeRef nodeRef)
    {
        if (isRm(nodeRef))
        {
            if (checkFilingUnfrozenUncutoff(nodeRef) == AccessDecisionVoter.ACCESS_GRANTED)
            {
                if (isRecordFolder(voter.getNodeService().getType(nodeRef)) || isRecord(nodeRef))
                {
                    if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.TRIGGER_AN_EVENT) == AccessStatus.ALLOWED)
                    {
                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                }
            }
            return AccessDecisionVoter.ACCESS_DENIED;
        }
        else
        {
            return AccessDecisionVoter.ACCESS_ABSTAIN;
        }
    }


    /* (non-Javadoc)
     * @see org.alfresco.module.org_alfresco_module_dod5015.capability.Capability#getName()
     */
    public String getName()
    {
        return RMPermissionModel.TRIGGER_AN_EVENT;
    }

    
}
