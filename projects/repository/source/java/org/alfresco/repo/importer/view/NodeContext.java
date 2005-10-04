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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.importer.ImportNode;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.view.ImporterException;
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
    private Map<QName, Map<QName, Serializable>> classProperties = new HashMap<QName, Map<QName, Serializable>>();
    private Map<QName, Serializable> nodeProperties = new HashMap<QName, Serializable>();
    private Map<QName, DataTypeDefinition> propertyDatatypes = new HashMap<QName, DataTypeDefinition>();
    

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
        
        // Do not import properties of sys:referenceable or cm:versionable
        // TODO: Make this configurable...
        if (owningClass.getName().equals(ContentModel.ASPECT_REFERENCEABLE) ||
            owningClass.getName().equals(ContentModel.ASPECT_VERSIONABLE))
        {
            return;
        }
        
        // Store property value
        Map<QName, Serializable> properties = classProperties.get(owningClass.getName());
        if (properties == null)
        {
            properties = new HashMap<QName, Serializable>();
            classProperties.put(owningClass.getName(), properties);
        }
        
        // Handle single / multi-valued cases
        Serializable newValue = value;
        Serializable existingValue = properties.get(propDef.getName());
        if (existingValue != null)
        {
            // convert from single to multi-valued
            if (!propDef.isMultiValued())
            {
                throw new ImporterException("Cannot import multiple values for single-valued property " + propDef.getName());
            }
            if (existingValue instanceof Collection)
            {
                // add to existing collection of values
                ((Collection<Serializable>)existingValue).add(value);
                newValue = existingValue;
            }
            else
            {
                // convert single to multi-valued
                List<Serializable>values = new ArrayList<Serializable>();
                values.add((String)existingValue);
                values.add(value);
                newValue = (Serializable)values;
            }
        }
        properties.put(propDef.getName(), newValue);
        nodeProperties.put(propDef.getName(), newValue);
    }
    
    /**
     * Adds a property datatype to the node
     * 
     * @param property  property name
     * @param datatype  property datatype
     */
    /*package*/ void addDatatype(QName property, DataTypeDefinition datatype)
    {
        propertyDatatypes.put(property, datatype);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImportNode#getProperties(org.alfresco.service.namespace.QName)
     */
    public Map<QName,Serializable> getProperties(QName className)
    {
        Map<QName, Serializable> properties = classProperties.get(className);
        if (properties == null)
        {
            properties = Collections.emptyMap();
        }
        return properties; 
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImportNode#getPropertyDatatypes()
     */
    public Map<QName, DataTypeDefinition> getPropertyDatatypes()
    {
        return propertyDatatypes;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImportNode#getProperties()
     */
    public Map<QName, Serializable> getProperties()
    {
        return nodeProperties;
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
    /*package*/ AspectDefinition determineAspect(QName defName)
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
    /*package*/ PropertyDefinition determineProperty(QName defName)
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
    /*package*/ AssociationDefinition determineAssociation(QName defName)
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
