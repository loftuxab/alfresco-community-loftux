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
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.OwnableService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Declare record action
 * 
 * @author Roy Wetherall
 */
public class DeclareRecordAction extends RMActionExecuterAbstractBase
{
    /** Logger */
    private static Log logger = LogFactory.getLog(DeclareRecordAction.class);

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
     *      org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        if (recordsManagementService.isRecord(actionedUponNodeRef) == true)
        {
            if (recordsManagementService.isRecordDeclared(actionedUponNodeRef) == false)
            {
                // Aspect not already defined - check mandatory properties then add
                if (mandatoryPropertiesSet(actionedUponNodeRef) == true)
                {
                    // Add the declared aspect
                    this.nodeService.addAspect(actionedUponNodeRef, ASPECT_DECLARED_RECORD, null);
                    // remove all owner related rights 
                    this.ownableService.setOwner(actionedUponNodeRef, OwnableService.NO_OWNER);
                }
                else
                {
                    throw new AlfrescoRuntimeException("Can not declare record as not all mandatory properties have been set. (" + actionedUponNodeRef.toString() + ")");
                }
            }
        }
        else
        {
            throw new AlfrescoRuntimeException("Can only undeclare a record. (" + actionedUponNodeRef.toString() + ")");
        }
    }

    /**
     * Helper method to check whether all the mandatory properties of the node have been set
     * 
     * @param nodeRef
     *            node reference
     * @return boolean true if all mandatory properties are set, false otherwise
     */
    private boolean mandatoryPropertiesSet(NodeRef nodeRef)
    {
        boolean result = true;

        Map<QName, Serializable> nodeRefProps = this.nodeService.getProperties(nodeRef);

        QName nodeRefType = this.nodeService.getType(nodeRef);

        TypeDefinition typeDef = this.dictionaryService.getType(nodeRefType);
        for (PropertyDefinition propDef : typeDef.getProperties().values())
        {
            if (propDef.isMandatory() == true)
            {
                if (nodeRefProps.get(propDef.getName()) == null)
                {
                    logMissingProperty(propDef);

                    result = false;
                    break;
                }
            }
        }

        if (result != false)
        {
            Set<QName> aspects = this.nodeService.getAspects(nodeRef);
            for (QName aspect : aspects)
            {
                AspectDefinition aspectDef = this.dictionaryService.getAspect(aspect);
                for (PropertyDefinition propDef : aspectDef.getProperties().values())
                {
                    if (propDef.isMandatory() == true)
                    {
                        if (nodeRefProps.get(propDef.getName()) == null)
                        {
                            logMissingProperty(propDef);

                            result = false;
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    private void logMissingProperty(PropertyDefinition propDef)
    {
        if (logger.isWarnEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Mandatory property missing: ").append(propDef.getName());
            logger.warn(msg.toString());
        }
    }

    @Override
    public Set<QName> getProtectedAspects()
    {
        HashSet<QName> qnames = new HashSet<QName>();
        qnames.add(ASPECT_DECLARED_RECORD);
        return qnames;
    }

    @Override
    protected boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        if (recordsManagementService.isRecord(filePlanComponent) == true)
        {
            if (recordsManagementService.isRecordDeclared(filePlanComponent) == false)
            {
                // Aspect not already defined - check mandatory properties then add
                if (mandatoryPropertiesSet(filePlanComponent) == true)
                {
                    return true;
                }
                else
                {
                    if (throwException)
                    {
                        throw new AlfrescoRuntimeException("Can not declare record as not all mandatory properties have been set. (" + filePlanComponent.toString() + ")");
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            if (throwException)
            {
                throw new AlfrescoRuntimeException("Can only undeclare a record. (" + filePlanComponent.toString() + ")");
            }
            else
            {
                return false;
            }
        }
    }

}
