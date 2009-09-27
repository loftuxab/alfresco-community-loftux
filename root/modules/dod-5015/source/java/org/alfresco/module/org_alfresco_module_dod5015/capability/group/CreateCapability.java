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
package org.alfresco.module.org_alfresco_module_dod5015.capability.group;

import net.sf.acegisecurity.vote.AccessDecisionVoter;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMEntryVoter;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMPermissionModel;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AbstractCapability;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.namespace.QName;

public class CreateCapability extends AbstractGroupCapability
{

    public CreateCapability()
    {
        super();
    }

    @Override
    protected int hasPermissionImpl(NodeRef nodeRef)
    {
        return evaluate(nodeRef, null, null, null);
    }

    public int evaluate(NodeRef destination, NodeRef linkee, QName type, QName assocType)
    {
        if (linkee != null)
        {
            if (checkRead(linkee, true) != AccessDecisionVoter.ACCESS_GRANTED)
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
        }
        if (isRm(destination))
        {
            if ( ((assocType == null) || !assocType.equals(ContentModel.ASSOC_CONTAINS)))
            {
                if(linkee == null)
                {
                    if(isRecord(destination) && !isDeclared(destination))
                    {
                        if (voter.getPermissionService().hasPermission(destination, RMPermissionModel.FILE_RECORDS) == AccessStatus.ALLOWED)
                        {
                            return AccessDecisionVoter.ACCESS_GRANTED;
                        }
                    }   
                }
                else
                {
                    if(isRecord(linkee) && isRecord(destination) && !isDeclared(destination))
                    {
                        if (voter.getPermissionService().hasPermission(destination, RMPermissionModel.FILE_RECORDS) == AccessStatus.ALLOWED)
                        {
                            return AccessDecisionVoter.ACCESS_GRANTED;
                        }
                    }
                }
              
            }
            if (checkFilingUnfrozenUncutoffOpen(destination) == AccessDecisionVoter.ACCESS_GRANTED)
            {
                if (isRecordFolder(voter.getNodeService().getType(destination)))
                {
                    if (voter.getPermissionService().hasPermission(destination, RMPermissionModel.FILE_RECORDS) == AccessStatus.ALLOWED)
                    {
                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                }
            }
            if ((checkFilingUnfrozenUncutoff(destination) == AccessDecisionVoter.ACCESS_GRANTED) && isClosed(destination))
            {
                if (isRecordFolder(voter.getNodeService().getType(destination)))
                {
                    if (voter.getPermissionService().hasPermission(getFilePlan(destination), RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS) == AccessStatus.ALLOWED)
                    {
                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                }
            }
            if ((checkFilingUnfrozen(destination) == AccessDecisionVoter.ACCESS_GRANTED) && isCutoff(destination))
            {
                if (isRecordFolder(voter.getNodeService().getType(destination)))
                {
                    if (voter.getPermissionService().hasPermission(getFilePlan(destination), RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS) == AccessStatus.ALLOWED)
                    {
                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                }
            }
        }
        if (voter.getCreateModifyDestroyFoldersCapability().evaluate(destination, type) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getDeclareRecordsInClosedFoldersCapability().evaluate(destination) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.createModifyRecordsInCuttoffFoldersCapability.evaluate(destination) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.createModifyDestroyFileplanMetadataCapability.evaluate(destination) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getChangeOrDeleteReferencesCapability().evaluate(destination, linkee) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        return AccessDecisionVoter.ACCESS_DENIED;
    }

    public String getName()
    {
        return "Create";
    }

}