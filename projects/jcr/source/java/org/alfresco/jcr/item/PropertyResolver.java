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
package org.alfresco.jcr.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.PathNotFoundException;

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;


/**
 * Responsible for resolving properties on Nodes
 * 
 * @author David Caruana
 */
public class PropertyResolver
{

    /**
     * Create Property List for all properties of this node
     * 
     * @return  list of properties (null properties are filtered)
     */
    public static List<PropertyImpl> createProperties(NodeImpl node, QNamePattern pattern)
    {
        // Create list of properties from node itself
        NodeService nodeService = node.session.getRepositoryImpl().getServiceRegistry().getNodeService();
        Map<QName, Serializable> properties = nodeService.getProperties(node.getNodeRef());
        List<PropertyImpl> propertyList = new ArrayList<PropertyImpl>(properties.size());        
        for (Map.Entry<QName, Serializable> entry : properties.entrySet())
        {
            QName propertyName = entry.getKey();
            if (pattern == null || pattern.isMatch(propertyName))
            {
                Serializable value = entry.getValue();
                if (value != null)
                {
                    PropertyImpl property = new PropertyImpl(node, propertyName);
                    propertyList.add(property);
                }
            }
        }
        
        // Add expected JCR properties
        if (pattern == null || pattern.isMatch(JCRUUIDProperty.PROPERTY_NAME))
        {
            propertyList.add(new JCRPrimaryTypeProperty(node));
        }
        if (pattern == null || pattern.isMatch(JCRPrimaryTypeProperty.PROPERTY_NAME))
        {
            propertyList.add(new JCRPrimaryTypeProperty(node));
        }
        if (pattern == null || pattern.isMatch(JCRMixinTypesProperty.PROPERTY_NAME))
        {
            propertyList.add(new JCRMixinTypesProperty(node));
        }
        
        return propertyList;
    }


    /**
     * Create property for the given named property
     * 
     * @param node
     * @param propertyName
     * @return
     * @throws PathNotFoundException
     */
    public static PropertyImpl createProperty(NodeImpl node, QName propertyName)
        throws PathNotFoundException
    {
        if (propertyName.equals(JCRUUIDProperty.PROPERTY_NAME))
        {
            return new JCRUUIDProperty(node);
        }
        if (propertyName.equals(JCRPrimaryTypeProperty.PROPERTY_NAME))
        {
            return new JCRPrimaryTypeProperty(node);
        }
        if (propertyName.equals(JCRMixinTypesProperty.PROPERTY_NAME))
        {
            return new JCRMixinTypesProperty(node);
        }

        NodeService nodeService = node.session.getRepositoryImpl().getServiceRegistry().getNodeService();
        Serializable value = nodeService.getProperty(node.getNodeRef(), propertyName);
        if (value == null)
        {
            throw new PathNotFoundException("Property path " + propertyName + " not found from node " + node.getNodeRef());
        }
        PropertyImpl propertyImpl = new PropertyImpl(node, propertyName);
        return propertyImpl;
    }
    
    
    /**
     * Check for existence of Property on specified Node
     * 
     * @param node
     * @param propertyName
     * @return
     */
    public static boolean hasProperty(NodeImpl node, QName propertyName)
    {
        if (propertyName.equals(JCRUUIDProperty.PROPERTY_NAME) ||
            propertyName.equals(JCRPrimaryTypeProperty.PROPERTY_NAME) ||
            propertyName.equals(JCRMixinTypesProperty.PROPERTY_NAME))
        {
            return true;
        }

        NodeService nodeService = node.session.getRepositoryImpl().getServiceRegistry().getNodeService();
        Serializable value = nodeService.getProperty(node.getNodeRef(), propertyName);
        return value != null;
    }
    
}
