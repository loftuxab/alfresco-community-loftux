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

import org.alfresco.module.org_alfresco_module_dod5015.CustomModelUtil;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminServiceImpl;
import org.alfresco.module.org_alfresco_module_dod5015.script.CustomReferenceType;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Association;
import org.alfresco.repo.dictionary.M2ChildAssociation;
import org.alfresco.repo.dictionary.M2Model;
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
	public static final String RMA_RECORD = "rma:record";

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
            this.createCustomChildAssoc(action, actionedUponNodeRef);
        }
        else
        {
            this.createCustomStandardAssoc(action, actionedUponNodeRef);
        }
        
        // A note on source and target role names:
        // The role names are required to have valid namespace prefixes e.g. "cm:" or "rmc:"
        // The subsequent part of the role name is just a string and is not validated.
	}
	

    @Override
    protected boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        return true;
    }

    private void createCustomStandardAssoc(Action action, NodeRef actionedUponNodeRef)
    {
        CustomModelUtil customModelUtil = new CustomModelUtil();
        customModelUtil.setContentService(contentService);

        Map<String, Serializable> params = action.getParameterValues();
        
        M2Model deserializedModel = customModelUtil.readCustomContentModel();
        M2Aspect customAssocsAspect = deserializedModel.getAspect(RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS);

        String name = (String)params.get(PARAM_NAME);
        String label = (String)params.get(PARAM_LABEL);
        
        CustomReferenceId crId = new CustomReferenceId(name, label, null, null);
        
        M2Association newAssoc = customAssocsAspect.createAssociation(crId.getReferenceId());

        //TODO Could be the customAssocs aspect
        newAssoc.setTargetClassName(DefineCustomAssociationAction.RMA_RECORD);
        
        customModelUtil.writeCustomContentModel(deserializedModel);
    }
    
    private void createCustomChildAssoc(Action action, NodeRef actionedUponNodeRef)
    {
        CustomModelUtil customModelUtil = new CustomModelUtil();
        customModelUtil.setContentService(contentService);

        Map<String, Serializable> params = action.getParameterValues();

        M2Model deserializedModel = customModelUtil.readCustomContentModel();
        M2Aspect customAssocsAspect = deserializedModel.getAspect(RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS);

        String name = (String)params.get(PARAM_NAME);
        String source = (String)params.get(PARAM_SOURCE);
        String target = (String)params.get(PARAM_TARGET);

        CustomReferenceId crId = new CustomReferenceId(name, null, source, target);

        M2ChildAssociation newAssoc = customAssocsAspect.createChildAssociation(crId.getReferenceId());

        //TODO Could be the cstom assocs aspect
        newAssoc.setTargetClassName(DefineCustomAssociationAction.RMA_RECORD);

        customModelUtil.writeCustomContentModel(deserializedModel);
    }
    
    /**
	 * 
	 * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
	 */
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList)
	{
        paramList.add(new ParameterDefinitionImpl(PARAM_REFERENCE_TYPE, DataTypeDefinition.TEXT, true, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_NAME, DataTypeDefinition.TEXT, true, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_LABEL, DataTypeDefinition.TEXT, false, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_SOURCE, DataTypeDefinition.TEXT, false, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_TARGET, DataTypeDefinition.TEXT, false, null));
	}
}