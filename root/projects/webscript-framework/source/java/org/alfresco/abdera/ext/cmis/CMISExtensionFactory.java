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
package org.alfresco.abdera.ext.cmis;

import org.alfresco.abdera.ext.cmis.CMISProperty.CMISPropertyBoolean;
import org.alfresco.abdera.ext.cmis.CMISProperty.CMISPropertyDateTime;
import org.alfresco.abdera.ext.cmis.CMISProperty.CMISPropertyDecimal;
import org.alfresco.abdera.ext.cmis.CMISProperty.CMISPropertyHtml;
import org.alfresco.abdera.ext.cmis.CMISProperty.CMISPropertyId;
import org.alfresco.abdera.ext.cmis.CMISProperty.CMISPropertyInteger;
import org.alfresco.abdera.ext.cmis.CMISProperty.CMISPropertyString;
import org.alfresco.abdera.ext.cmis.CMISProperty.CMISPropertyUri;
import org.alfresco.abdera.ext.cmis.CMISProperty.CMISPropertyXml;
import org.apache.abdera.util.AbstractExtensionFactory;


/**
 * CMIS Version: 0.61
 *
 * CMIS Extension Factory for the Abdera ATOM Library.
 * 
 * @author davidc
 */
public class CMISExtensionFactory extends AbstractExtensionFactory
    implements CMISConstants
{
    
    public CMISExtensionFactory()
    {
        super(CMIS_NS, CMISRA_NS);
        addImpl(REPOSITORY_INFO, CMISRepositoryInfo.class);
        addImpl(CAPABILITIES, CMISCapabilities.class);
        addImpl(OBJECT, CMISObject.class);
        addImpl(PROPERTIES, CMISProperties.class);
        addImpl(STRING_PROPERTY, CMISPropertyString.class);
        addImpl(DECIMAL_PROPERTY, CMISPropertyDecimal.class);
        addImpl(INTEGER_PROPERTY, CMISPropertyInteger.class);
        addImpl(BOOLEAN_PROPERTY, CMISPropertyBoolean.class);
        addImpl(DATETIME_PROPERTY, CMISPropertyDateTime.class);
        addImpl(URI_PROPERTY, CMISPropertyUri.class);
        addImpl(ID_PROPERTY, CMISPropertyId.class);
        addImpl(XML_PROPERTY, CMISPropertyXml.class);
        addImpl(HTML_PROPERTY, CMISPropertyHtml.class);
        addImpl(PROPERTY_VALUE, CMISValue.class);
        addImpl(ALLOWABLE_ACTIONS, CMISAllowableActions.class);
        addImpl(CAN_DELETE, CMISAllowableAction.class);
        addImpl(CAN_UPDATE_PROPERTIES, CMISAllowableAction.class); 
        addImpl(CAN_GET_PROPERTIES, CMISAllowableAction.class); 
        addImpl(CAN_GET_RELATIONSHIPS, CMISAllowableAction.class); 
        addImpl(CAN_GET_PARENTS, CMISAllowableAction.class); 
        addImpl(CAN_GET_FOLDER_PARENT, CMISAllowableAction.class); 
        addImpl(CAN_GET_DESCENDANTS, CMISAllowableAction.class); 
        addImpl(CAN_MOVE, CMISAllowableAction.class); 
        addImpl(CAN_DELETE_VERSION, CMISAllowableAction.class); 
        addImpl(CAN_DELETE_CONTENT, CMISAllowableAction.class); 
        addImpl(CAN_CHECKOUT, CMISAllowableAction.class); 
        addImpl(CAN_CANCEL_CHECKOUT, CMISAllowableAction.class); 
        addImpl(CAN_CHECKIN, CMISAllowableAction.class); 
        addImpl(CAN_SET_CONTENT, CMISAllowableAction.class); 
        addImpl(CAN_GET_ALL_VERSIONS, CMISAllowableAction.class); 
        addImpl(CAN_ADD_TO_FOLDER, CMISAllowableAction.class); 
        addImpl(CAN_REMOVE_FROM_FOLDER, CMISAllowableAction.class); 
        addImpl(CAN_VIEW_CONTENT, CMISAllowableAction.class); 
        addImpl(CAN_ADD_POLICY, CMISAllowableAction.class); 
        addImpl(CAN_GET_APPLIED_POLICIES, CMISAllowableAction.class); 
        addImpl(CAN_REMOVE_POLICY, CMISAllowableAction.class); 
        addImpl(CAN_GET_CHILDREN, CMISAllowableAction.class); 
        addImpl(CAN_CREATE_DOCUMENT, CMISAllowableAction.class); 
        addImpl(CAN_CREATE_FOLDER, CMISAllowableAction.class); 
        addImpl(CAN_CREATE_RELATIONSHIP, CMISAllowableAction.class); 
        addImpl(CAN_CREATE_POLICY, CMISAllowableAction.class); 
        addImpl(CAN_DELETE_TREE, CMISAllowableAction.class);
        addImpl(CHILDREN, CMISChildren.class);
    }

}
