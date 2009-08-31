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
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.module.org_alfresco_module_dod5015.script.CustomReferenceType;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Neil McErlean
 */
public class DefineCustomAssociationAction extends DefineCustomElementAbstractAction
{
    private static final String PARAM_LABEL = "label";
    private static final String PARAM_TARGET = "target";
    private static final String PARAM_SOURCE = "source";
    private static final String PARAM_REFERENCE_TYPE = "referenceType";
    private static Log logger = LogFactory.getLog(DefineCustomAssociationAction.class);
    private RecordsManagementAdminService rmAdminService;
    
    public void setRecordsManagementAdminService(RecordsManagementAdminService rmAdminService)
    {
        this.rmAdminService = rmAdminService;
    }
    
	/**
	 * 
	 * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
	 *      org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
	{
	    Serializable refTypeSerializable = action.getParameterValue(PARAM_REFERENCE_TYPE);
	    CustomReferenceType refType = CustomReferenceType.getEnumFromString((String)refTypeSerializable);
	    
	    boolean isChildAssoc = refType.equals(CustomReferenceType.PARENT_CHILD);
	    
        if (logger.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Creating custom assoc: ")
                .append(action.getParameterValue(PARAM_NAME))
                .append("; isChild ")
                .append(isChildAssoc);
            logger.debug(msg.toString());
        }

        if (isChildAssoc)
        {
            Map<String, Serializable> params = action.getParameterValues();
            
            String source = (String)params.get(PARAM_SOURCE);
            String target = (String)params.get(PARAM_TARGET);
            
            rmAdminService.addCustomChildAssocDefinition(source, target);
        }
        else
        {
            Map<String, Serializable> params = action.getParameterValues();
            String label = (String)params.get(PARAM_LABEL);
            
            rmAdminService.addCustomAssocDefinition(label);
        }
	}
	

    @Override
    protected boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        return true;
    }
    
    /**
	 * 
	 * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
	 */
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList)
	{
        paramList.add(new ParameterDefinitionImpl(PARAM_REFERENCE_TYPE, DataTypeDefinition.TEXT, true, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_LABEL, DataTypeDefinition.TEXT, false, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_SOURCE, DataTypeDefinition.TEXT, false, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_TARGET, DataTypeDefinition.TEXT, false, null));
	}
}