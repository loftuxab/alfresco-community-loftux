/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.jcr.dictionary;

import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.OnParentVersionAction;

import org.alfresco.jcr.proxy.JCRProxyFactory;
import org.alfresco.jcr.session.SessionImpl;

/**
 * Alfresco implementation of a JCR Property Definition
 * 
 * @author David Caruana
 */
public class PropertyDefinitionImpl implements PropertyDefinition
{
    /** Session */
    private SessionImpl session;
    
    /** Proxy */
    private PropertyDefinition proxy;
    
    /** Alfresco Property Definition */
    private org.alfresco.service.cmr.dictionary.PropertyDefinition propDef;
    
    
    /**
     * Construct
     * 
     * @param propDef  Alfresco Property Definition
     */
    public PropertyDefinitionImpl(SessionImpl session, org.alfresco.service.cmr.dictionary.PropertyDefinition propDef)
    {
        this.session = session;
        this.propDef = propDef;
    }

    /**
     * Get proxied JCR PropertyDefinition
     */
    public PropertyDefinition getProxy()
    {
        if (proxy == null)
        {
            proxy = (PropertyDefinition)JCRProxyFactory.create(this, PropertyDefinition.class, session); 
        }
        return proxy;
    }
    
    /* (non-Javadoc)
     * @see javax.jcr.nodetype.PropertyDefinition#getRequiredType()
     */
    public int getRequiredType()
    {
        return DataTypeMap.convertDataTypeToPropertyType(propDef.getDataType());
    }
    
    /* (non-Javadoc)
     * @see javax.jcr.nodetype.PropertyDefinition#getValueConstraints()
     */
    public String[] getValueConstraints()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.PropertyDefinition#getDefaultValues()
     */
    public Value[] getDefaultValues()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.PropertyDefinition#isMultiple()
     */
    public boolean isMultiple()
    {
        return propDef.isMultiValued();
    }

    public NodeType getDeclaringNodeType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.ItemDefinition#getName()
     */
    public String getName()
    {
        return propDef.getName().toPrefixString(session.getNamespaceResolver());
    }
    
    /* (non-Javadoc)
     * @see javax.jcr.nodetype.ItemDefinition#isAutoCreated()
     */
    public boolean isAutoCreated()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.ItemDefinition#isMandatory()
     */
    public boolean isMandatory()
    {
        return propDef.isMandatory();
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.ItemDefinition#getOnParentVersion()
     */
    public int getOnParentVersion()
    {
        // TODO: Check this
        return OnParentVersionAction.IGNORE;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.ItemDefinition#isProtected()
     */
    public boolean isProtected()
    {
        return propDef.isProtected();
    }

}
