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
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Association;
import org.alfresco.repo.dictionary.M2ChildAssociation;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Neil McErlean
 */
public class DefineCustomAssociationAction extends DefineCustomElementAbstractAction
{
    private static final String PARAM_TARGET_MANDATORY_ENFORCED = "targetMandatoryEnforced";
    private static final String PARAM_TITLE = "title";
    private static final String PARAM_DESCRIPTION = "description";
    private static final String PARAM_SOURCE_MANY = "sourceMany";
    private static final String PARAM_SOURCE_MANDATORY = "sourceMandatory";
    private static final String PARAM_PROTECTED = "protected";
    private static final String PARAM_TARGET_MANDATORY = "targetMandatory";
    private static final String PARAM_TARGET_MANY = "targetMany";
    private static final String PARAM_TARGET_ROLE_NAME = "targetRoleName";
    private static final String PARAM_SOURCE_ROLE_NAME = "sourceRoleName";
    private static final String PARAM_IS_CHILD = "isChild";
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
	    super.executeImpl(action, actionedUponNodeRef);
	    
	    // isChild defaults to false
	    Serializable serializableParam = action.getParameterValue(PARAM_IS_CHILD);
	    boolean isChildValue = serializableParam == null ? false : (Boolean)serializableParam;
	    
        if (logger.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Creating custom assoc: ")
                .append(action.getParameterValue(PARAM_NAME))
                .append("; isChild ")
                .append(isChildValue);
            logger.debug(msg.toString());
        }

        if (isChildValue)
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

        QName assocQName = QName.createQName((String)params.get(PARAM_NAME), namespaceService);
        String assocQNameAsString = assocQName.toPrefixString(this.namespaceService);
        
        M2Association newAssoc = customAssocsAspect.createAssociation(assocQNameAsString);

        newAssoc.setDescription((String)params.get(PARAM_DESCRIPTION));
        newAssoc.setSourceRoleName(RecordsManagementAdminServiceImpl.CUSTOM_MODEL_PREFIX + ":" + params.get(PARAM_SOURCE_ROLE_NAME));
        newAssoc.setTargetClassName(DefineCustomAssociationAction.RMA_RECORD);
        newAssoc.setTargetRoleName(RecordsManagementAdminServiceImpl.CUSTOM_MODEL_PREFIX + ":" + params.get(PARAM_TARGET_ROLE_NAME));
        newAssoc.setTitle((String)params.get(PARAM_TITLE));
        
        // This boilerplate code is to ensure that a null Boolean is not set on the
        // association, as that would trigger the unboxing of a null and therefore a NPE.
        Serializable serializableParam = null;
        
        serializableParam = params.get(PARAM_TARGET_MANY);
        if (serializableParam != null)
        {
            Boolean bool = (Boolean)serializableParam;
            newAssoc.setTargetMany(bool);
        }
        serializableParam = params.get(PARAM_TARGET_MANDATORY);
        if (serializableParam != null)
        {
            Boolean bool = (Boolean)serializableParam;
            newAssoc.setTargetMandatory(bool);
        }
        serializableParam = params.get(PARAM_PROTECTED);
        if (serializableParam != null)
        {
            Boolean bool = (Boolean)serializableParam;
            newAssoc.setProtected(bool);
        }
        serializableParam = params.get(PARAM_SOURCE_MANDATORY);
        if (serializableParam != null)
        {
            Boolean bool = (Boolean)serializableParam;
            newAssoc.setSourceMandatory(bool);
        }
        serializableParam = params.get(PARAM_SOURCE_MANY);
        if (serializableParam != null)
        {
            Boolean bool = (Boolean)serializableParam;
            newAssoc.setSourceMany(bool);
        }

        customModelUtil.writeCustomContentModel(deserializedModel);
    }
    
    private void createCustomChildAssoc(Action action, NodeRef actionedUponNodeRef)
    {
        CustomModelUtil customModelUtil = new CustomModelUtil();
        customModelUtil.setContentService(contentService);

        Map<String, Serializable> params = action.getParameterValues();

        M2Model deserializedModel = customModelUtil.readCustomContentModel();
        M2Aspect customAssocsAspect = deserializedModel.getAspect(RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS);

        QName assocQName = QName.createQName((String)params.get(PARAM_NAME), namespaceService);
        String assocQNameAsString = assocQName.toPrefixString(this.namespaceService);

        M2ChildAssociation newAssoc = customAssocsAspect.createChildAssociation(assocQNameAsString);

        newAssoc.setDescription((String)params.get(PARAM_DESCRIPTION));
        newAssoc.setSourceRoleName(RecordsManagementAdminServiceImpl.CUSTOM_MODEL_PREFIX + ":" + params.get(PARAM_SOURCE_ROLE_NAME));
        newAssoc.setTargetClassName(DefineCustomAssociationAction.RMA_RECORD);
        newAssoc.setTargetRoleName(RecordsManagementAdminServiceImpl.CUSTOM_MODEL_PREFIX + ":" + params.get(PARAM_TARGET_ROLE_NAME));
        newAssoc.setTitle((String)params.get(PARAM_TITLE));

        // This boilerplate code is to ensure that a null Boolean is not set on the
        // association, as that would trigger the unboxing of a null and therefore a NPE.
        Serializable serializableParam = null;
        
        serializableParam = params.get(PARAM_TARGET_MANY);
        if (serializableParam != null)
        {
            Boolean bool = (Boolean)serializableParam;
            newAssoc.setTargetMany(bool);
        }
        serializableParam = params.get(PARAM_TARGET_MANDATORY);
        if (serializableParam != null)
        {
            Boolean bool = (Boolean)serializableParam;
            newAssoc.setTargetMandatory(bool);
        }
        serializableParam = params.get(PARAM_PROTECTED);
        if (serializableParam != null)
        {
            Boolean bool = (Boolean)serializableParam;
            newAssoc.setProtected(bool);
        }
        serializableParam = params.get(PARAM_SOURCE_MANDATORY);
        if (serializableParam != null)
        {
            Boolean bool = (Boolean)serializableParam;
            newAssoc.setSourceMandatory(bool);
        }
        serializableParam = params.get(PARAM_SOURCE_MANY);
        if (serializableParam != null)
        {
            Boolean bool = (Boolean)serializableParam;
            newAssoc.setSourceMany(bool);
        }
        
        customModelUtil.writeCustomContentModel(deserializedModel);
    }
    
    /**
	 * 
	 * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
	 */
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList)
	{
	    //TODO Could separate defineCustomChildAssociation out into a separate action.
        paramList.add(new ParameterDefinitionImpl(PARAM_IS_CHILD, DataTypeDefinition.BOOLEAN, true, null));

        paramList.add(new ParameterDefinitionImpl(PARAM_NAME, DataTypeDefinition.TEXT, true, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_SOURCE_ROLE_NAME, DataTypeDefinition.TEXT, true, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_TARGET_ROLE_NAME, DataTypeDefinition.TEXT, true, null));
	    
        paramList.add(new ParameterDefinitionImpl(PARAM_DESCRIPTION, DataTypeDefinition.TEXT, false, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_SOURCE_MANDATORY, DataTypeDefinition.BOOLEAN, false, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_SOURCE_MANY, DataTypeDefinition.BOOLEAN, false, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_TARGET_MANDATORY, DataTypeDefinition.BOOLEAN, false, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_TARGET_MANDATORY_ENFORCED, DataTypeDefinition.BOOLEAN, false, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_TARGET_MANY, DataTypeDefinition.BOOLEAN, false, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_TITLE, DataTypeDefinition.TEXT, false, null));
        paramList.add(new ParameterDefinitionImpl(PARAM_PROTECTED, DataTypeDefinition.BOOLEAN, false, null));
	}
}