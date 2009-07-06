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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.action.impl;

import java.io.Serializable;
import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.CustomModelUtil;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminServiceImpl;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Property;
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
public class DefineCustomPropertyAction extends RMActionExecuterAbstractBase
{
    private static Log logger = LogFactory.getLog(DefineCustomPropertyAction.class);
    private static final String PARAM_NAME = "name";
    private static final String PARAM_TYPE = "type";

	/**
	 * 
	 * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
	 *      org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
	{
        Serializable serializableName = action.getParameterValue(PARAM_NAME);
        Serializable serializableType = action.getParameterValue(PARAM_TYPE);
        
        String name = (String)serializableName;
        QName type = (QName)serializableType;
        
        if (logger.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Creating custom property: ")
                .append(action.getParameterValue(PARAM_NAME))
                .append(", ")
                .append(PARAM_TYPE);
            logger.debug(msg.toString());
        }

        CustomModelUtil customModelUtil = new CustomModelUtil();
        customModelUtil.setContentService(contentService);

        M2Model deserializedModel = customModelUtil.readCustomContentModel();
        M2Aspect customPropsAspect = deserializedModel.getAspect(RecordsManagementAdminServiceImpl.RMC_CUSTOM_PROPS);

        // We're creating a full QName here in order to avoid the client code having to know
        // the correct prefix for the custom model.
        QName propQName = QName.createQName(RecordsManagementAdminServiceImpl.CUSTOM_MODEL_PREFIX, name, this.namespaceService);
        String propQNameAsString = propQName.toPrefixString(namespaceService);
        
        M2Property newProp = customPropsAspect.createProperty(propQNameAsString);
        newProp.setType(type.toPrefixString(namespaceService));
        //TODO There are many more setters on property than just name and type.

        customModelUtil.writeCustomContentModel(deserializedModel);
    }

	/**
	 * 
	 * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
	 */
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList)
	{
        paramList.add(new ParameterDefinitionImpl(PARAM_NAME, DataTypeDefinition.TEXT, true, "nameLabel"));
        paramList.add(new ParameterDefinitionImpl(PARAM_TYPE, DataTypeDefinition.QNAME, true, "typeLabel"));
 	}
}
