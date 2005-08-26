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
package org.alfresco.repo.importer.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.importer.ImportNode;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


/**
 * Maintains state about the currently imported node.
 * 
 * @author David Caruana
 *
 */
/*package*/ class NodeContext extends ElementContext
    implements ImportNode
{
    private ParentContext parentContext;
    private NodeRef nodeRef;
    private TypeDefinition typeDef;
    private String childName;
    private Map<QName, AspectDefinition> nodeAspects = new HashMap<QName, AspectDefinition>();
    private Map<QName, ChildAssociationDefinition> nodeChildAssocs = new HashMap<QName, ChildAssociationDefinition>();
    private Map<QName, Map<QName, String>> classProperties = new HashMap<QName, Map<QName, String>>();
    private Map<QName, String> nodeProperties = new HashMap<QName, String>();


    /**
     * Construct
     * 
     * @param elementName
     * @param parentContext
     * @param typeDef
     */
    /*package*/ NodeContext(QName elementName, ParentContext parentContext, TypeDefinition typeDef)
    {
        super(elementName, parentContext.getDictionaryService(), parentContext.getImporter());
        this.parentContext = parentContext;
        this.typeDef = typeDef;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImportNode#getParentContext()
     */
    public ParentContext getParentContext()
    {
        return parentContext;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImportNode#getTypeDefinition()
     */
    public TypeDefinition getTypeDefinition()
    {
        return typeDef;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImportNode#getNodeRef()
     */
    public NodeRef getNodeRef()
    {
        return nodeRef;
    }
    
    /**
     * @param nodeRef  the node ref
     */
    /*package*/ void setNodeRef(NodeRef nodeRef)
    {
        this.nodeRef = nodeRef;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImportNode#getChildName()
     */
    public String getChildName()
    {
        return childName;
    }
    
    /**
     * @param childName  the child name
     */
    /*package*/ void setChildName(String childName)
    {
        this.childName = childName;
    }
    
    /**
     * Adds a property to the node
     * 
     * @param propDef  the property definition
     * @param value  the property value
     */
    /*package*/ void addProperty(PropertyDefinition propDef, String value)
    {
        // Determine appropriate slot for placing property
        ClassDefinition owningClass = null;
        PropertyDefinition def = getDictionaryService().getProperty(typeDef.getName(), propDef.getName());
        if (def != null)
        {
            owningClass = typeDef;
        }
        else
        {
            Set<AspectDefinition> allAspects = new HashSet<AspectDefinition>();
            allAspects.addAll(typeDef.getDefaultAspects());
            allAspects.addAll(nodeAspects.values());
            for (AspectDefinition aspectDef : allAspects)
            {
                def = getDictionaryService().getProperty(aspectDef.getName(), propDef.getName());
                if (def != null)
                {
                    owningClass = aspectDef;
                    break;
                }
            }
        }
        
        // Store property value
        Map<QName, String> properties = classProperties.get(owningClass.getName());
        if (properties == null)
        {
            properties = new HashMap<QName, String>();
            classProperties.put(owningClass.getName(), properties);
        }
        properties.put(propDef.getName(), value);
        nodeProperties.put(propDef.getName(), value);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImportNode#getProperties(org.alfresco.service.namespace.QName)
     */
    public Map<QName, String> getProperties(QName className)
    {
        Map<QName, String> properties = classProperties.get(className);
        if (properties == null)
        {
            properties = Collections.emptyMap();
        }
        return properties; 
    }

    /**
     * Adds an aspect to the node
     * 
     * @param aspect  the aspect
     */
    /*package*/ void addAspect(AspectDefinition aspect)
    {
        nodeAspects.put(aspect.getName(), aspect);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImportNode#getNodeAspects()
     */
    public Set<QName> getNodeAspects()
    {
        return nodeAspects.keySet();
    }

    /**
     * Determine the type of definition (aspect, property, association) from the
     * specified name
     * 
     * @param defName
     * @return the dictionary definition
     */
    /*package*/ Object determineDefinition(QName defName)
    {
        Object def = determineAspect(defName);
        if (def == null)
        {
            def = determineProperty(defName);
            if (def == null)
            {
                def = determineAssociation(defName);
            }
        }
        return def;
    }
    
    /**
     * Determine if name referes to an aspect
     * 
     * @param defName
     * @return
     */
    private AspectDefinition determineAspect(QName defName)
    {
        AspectDefinition def = null;
        if (nodeAspects.containsKey(defName) == false)
        {
            def = getDictionaryService().getAspect(defName);
        }
        return def;
    }
    
    /**
     * Determine if name refers to a property
     * 
     * @param defName
     * @return
     */
    private PropertyDefinition determineProperty(QName defName)
    {
        PropertyDefinition def = null;
        if (nodeProperties.containsKey(defName) == false)
        {
            def = getDictionaryService().getProperty(typeDef.getName(), defName);
            if (def == null)
            {
                Set<AspectDefinition> allAspects = new HashSet<AspectDefinition>();
                allAspects.addAll(typeDef.getDefaultAspects());
                allAspects.addAll(nodeAspects.values());
                for (AspectDefinition aspectDef : allAspects)
                {
                    def = getDictionaryService().getProperty(aspectDef.getName(), defName);
                    if (def != null)
                    {
                        break;
                    }
                }
            }
        }
        return def;
    }
    
    /**
     * Determine if name referes to an association
     * 
     * @param defName
     * @return
     */
    private AssociationDefinition determineAssociation(QName defName)
    {
        AssociationDefinition def = null;
        if (nodeChildAssocs.containsKey(defName) == false)
        {
            def = getDictionaryService().getAssociation(typeDef.getName(), defName);
            if (def == null)
            {
                Set<AspectDefinition> allAspects = new HashSet<AspectDefinition>();
                allAspects.addAll(typeDef.getDefaultAspects());
                allAspects.addAll(nodeAspects.values());
                for (AspectDefinition aspectDef : allAspects)
                {
                    def = getDictionaryService().getAssociation(aspectDef.getName(), defName);
                    if (def != null)
                    {
                        break;
                    }
                }
            }
        }
        return def;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "NodeContext[childName=" + getChildName() + ",type=" + typeDef.getName() + ",nodeRef=" + nodeRef + 
            ",aspects=" + nodeAspects.values() + ",parentContext=" + parentContext.toString() + "]";
    }
    
}
