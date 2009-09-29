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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Edit freeze reason Action
 * 
 * @author Roy Wetherall
 */
public class EditHoldReasonAction extends RMActionExecuterAbstractBase
{
    /** Parameter names */
    public static final String PARAM_REASON = "reason";
    
    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        QName nodeType = this.nodeService.getType(actionedUponNodeRef);
        if (this.dictionaryService.isSubClass(nodeType, TYPE_HOLD) == true)
        {
            // Get the property values
            String reason = (String)action.getParameterValue(PARAM_REASON);
            if (reason == null || reason.length() == 0)
            {
                throw new AlfrescoRuntimeException("Can not edit freeze reason since no reason has been given.");
            }
            
            // Set the hold reason
            nodeService.setProperty(actionedUponNodeRef, PROP_HOLD_REASON, reason);

        }
        else
        {
            throw new AlfrescoRuntimeException("Can not edit a hold reason on a node that is not of type " + TYPE_HOLD.toString() + 
                                               "(" + actionedUponNodeRef.toString() + ")");
        }                
    }
    
    @Override
    public Set<QName> getProtectedAspects()
    {
        HashSet<QName> qnames = new HashSet<QName>();
        qnames.add(ASPECT_FROZEN);
        return qnames;
    }

    @Override
    public Set<QName> getProtectedProperties()
    {
        HashSet<QName> qnames = new HashSet<QName>();
        qnames.add(PROP_HOLD_REASON);
        return qnames;
    }

    @Override
    protected boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        QName nodeType = this.nodeService.getType(filePlanComponent);
        if (this.dictionaryService.isSubClass(nodeType, TYPE_HOLD) == true)
        {
            return true;
        }
        else
        {
            if(throwException)
            {
                throw new AlfrescoRuntimeException("Can not edit hold reason on a node that is not of type " + TYPE_HOLD.toString() + 
                    "(" + filePlanComponent.toString() + ")");
            }
            else
            {
                return false;
            }
        }        
    }

    
}