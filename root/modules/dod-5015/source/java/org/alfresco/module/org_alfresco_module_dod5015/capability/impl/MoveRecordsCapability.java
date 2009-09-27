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
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;

public class MoveRecordsCapability extends AbstractCapability
{

    public MoveRecordsCapability()
    {
        super();
    }

    @Override
    protected int hasPermissionImpl(NodeRef nodeRef)
    {
        // no way to know ...
        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }

    public int evaluate(NodeRef movee, NodeRef destination)
    {
        int state = AccessDecisionVoter.ACCESS_ABSTAIN;

        if (isRm(destination))
        {
            state = checkRead(movee, true);
            if (state != AccessDecisionVoter.ACCESS_GRANTED)
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }

            if (isRm(movee))
            {
                state = voter.getDeleteCapability().evaluate(movee);
            }
            else
            {
                if (voter.getPermissionService().hasPermission(getFilePlan(movee), PermissionService.DELETE) == AccessStatus.ALLOWED)
                {
                    state = AccessDecisionVoter.ACCESS_GRANTED;
                }
            }

            if (state == AccessDecisionVoter.ACCESS_GRANTED)
            {
                QName type = voter.getNodeService().getType(movee);
                // now we know the node - we can abstain for certain types and aspects (eg, rm)
                state = voter.createCapability.evaluate(destination, movee, type, null);

                if (state == AccessDecisionVoter.ACCESS_GRANTED)
                {
                    if (isRm(movee))
                    {
                        if (voter.getPermissionService().hasPermission(getFilePlan(movee), RMPermissionModel.MOVE_RECORDS) == AccessStatus.ALLOWED)
                        {
                            return AccessDecisionVoter.ACCESS_GRANTED;
                        }
                    }
                    else
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

    public String getName()
    {
        return RMPermissionModel.MOVE_RECORDS;
    }
}