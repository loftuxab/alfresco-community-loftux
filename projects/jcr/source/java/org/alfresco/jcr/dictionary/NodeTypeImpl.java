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

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Value;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;

import org.alfresco.jcr.repository.JCRNamespace;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.namespace.QName;

/**
 * Alfresco implementation of a Node Type Definition
 * 
 * @author David Caruana
 */
public class NodeTypeImpl implements NodeType
{
    // The required nt:base type specified by JCR
    public static QName NT_BASE = QName.createQName(JCRNamespace.NT_URI, "base");

    // The optional mix:referenceable specified by JCR
    public static QName MIX_REFERENCEABLE = QName.createQName(JCRNamespace.MIX_URI, "referenceable");

    
    private NodeTypeManagerImpl typeManager;
    private ClassDefinition classDefinition;

    
    /**
     * Construct
     * 
     * @param classDefinition  Alfresco class definition
     */    
    public NodeTypeImpl(NodeTypeManagerImpl typeManager, ClassDefinition classDefinition)
    {
        this.typeManager = typeManager;
        this.classDefinition = classDefinition;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.NodeType#getName()
     */
    public String getName()
    {
        return classDefinition.getName().toPrefixString(typeManager.getNamespaceService());
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.NodeType#isMixin()
     */
    public boolean isMixin()
    {
        return classDefinition.isAspect();
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.NodeType#hasOrderableChildNodes()
     */
    public boolean hasOrderableChildNodes()
    {
        // Note: For now, we don't expose this through JCR
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.NodeType#getPrimaryItemName()
     */
    public String getPrimaryItemName()
    {
        // NOTE: Alfresco does not support the notion of PrimaryItem (not yet anyway)
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.NodeType#getSupertypes()
     */
    public NodeType[] getSupertypes()
    {
        List<NodeType> nodeTypes = new ArrayList<NodeType>();
        NodeType[] declaredSupertypes = getDeclaredSupertypes();
        while (declaredSupertypes.length > 0)
        {
            // Alfresco supports single inheritence only
            NodeType supertype = declaredSupertypes[0];
            nodeTypes.add(supertype);
            declaredSupertypes = supertype.getDeclaredSupertypes();
        }
        return nodeTypes.toArray(new NodeType[nodeTypes.size()]);
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.NodeType#getDeclaredSupertypes()
     */
    public NodeType[] getDeclaredSupertypes()
    {
        // return no supertype when type is nt:base
        if (classDefinition.getName().equals(NT_BASE))
        {
            return new NodeType[] {};
        }
        
        // return root type when no parent (nt:base if a type hierarchy)
        QName parent = classDefinition.getParentName();
        if (parent == null)
        {
            if (classDefinition.isAspect())
            {
                return new NodeType[] {};
            }
            else
            {
                return new NodeType[] { typeManager.getNodeTypeImpl(NT_BASE) };
            }
        }
        
        // return the supertype
        return new NodeType[] { typeManager.getNodeTypeImpl(parent) };
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.NodeType#isNodeType(java.lang.String)
     */
    public boolean isNodeType(String nodeTypeName)
    {
        QName name = QName.createQName(nodeTypeName, typeManager.getNamespaceService());
        
        // is it one of standard types
        if (name.equals(NodeTypeImpl.NT_BASE))
        {
            return true;
        }

        // is it part of this class hierarchy
        return typeManager.getDictionaryService().isSubClass(name, classDefinition.getName());
    }

    public PropertyDefinition[] getPropertyDefinitions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PropertyDefinition[] getDeclaredPropertyDefinitions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public NodeDefinition[] getChildNodeDefinitions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public NodeDefinition[] getDeclaredChildNodeDefinitions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.NodeType#canSetProperty(java.lang.String, javax.jcr.Value)
     */
    public boolean canSetProperty(String propertyName, Value value)
    {
        // Note: Assumption is that for level 1 we can return false
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.NodeType#canSetProperty(java.lang.String, javax.jcr.Value[])
     */
    public boolean canSetProperty(String propertyName, Value[] values)
    {
        // Note: Assumption is that for level 1 we can return false
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.NodeType#canAddChildNode(java.lang.String)
     */
    public boolean canAddChildNode(String childNodeName)
    {
        // Note: Assumption is that for level 1 we can return false
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.NodeType#canAddChildNode(java.lang.String, java.lang.String)
     */
    public boolean canAddChildNode(String childNodeName, String nodeTypeName)
    {
        // Note: Assumption is that for level 1 we can return false
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.nodetype.NodeType#canRemoveItem(java.lang.String)
     */
    public boolean canRemoveItem(String itemName)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
}
