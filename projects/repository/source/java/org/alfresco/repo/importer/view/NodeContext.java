package org.alfresco.repo.importer.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.importer.ImporterException;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
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
{
    private ParentContext parentContext;
    private NodeRef nodeRef;
    private TypeDefinition typeDef;
    private Map<QName, AspectDefinition> nodeAspects = new HashMap<QName, AspectDefinition>();
    private QName childName;
    private String nodeName;
    private Map<QName, Map<QName, Serializable>> properties = new HashMap<QName, Map<QName, Serializable>>();


    /**
     * Construct
     * 
     * @param elementName
     * @param parentContext
     * @param typeDef
     */
    /*package*/ NodeContext(QName elementName, ParentContext parentContext, TypeDefinition typeDef)
    {
        super(parentContext.getDictionaryService(), elementName, parentContext.getConfiguration(), parentContext.getImporterProgress());
        this.parentContext = parentContext;
        this.typeDef = typeDef;
    }
    
    /**
     * @return  the parent context
     */
    /*package*/ ParentContext getParentContext()
    {
        return parentContext;
    }

    /**
     * @return  the type definition
     */
    /*package*/ TypeDefinition getTypeDefinition()
    {
        return typeDef;
    }
    
    /**
     * @return  the node ref
     */
    /*package*/ NodeRef getNodeRef()
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
    
    /**
     * @return  the child name
     */
    /*package*/ QName getChildName()
    {
        if (childName == null)
        {
            // Default child name to node name, if there is one
            if (nodeName != null)
            {
                String localName = QName.createValidLocalName(nodeName);
                return QName.createQName(parentContext.getAssocType().getNamespaceURI(), localName);
            }
        }
        return childName;
    }
    
    /**
     * @param childName  the child name
     */
    /*package*/ void setChildName(QName childName)
    {
        this.childName = childName;
    }
    
    /**
     * Adds a property to the node
     * 
     * @param propDef  the property definition
     * @param value  the property value
     */
    /*package*/ void addProperty(PropertyDefinition propDef, Serializable value)
    {
        // Ensure property is valid for node
        ClassDefinition owningClass = null;
        if (typeDef.getProperties().containsKey(propDef.getName()))
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
                if (aspectDef.getProperties().containsKey(propDef.getName()))
                {
                    owningClass = aspectDef;
                    break;
                }
            }
        }
        if (owningClass == null)
        {
            throw new ImporterException("Property " + propDef.getName() + " is not valid for node " + typeDef.getName());
        }
        
        // Store property value
        Map<QName, Serializable> classProperties = properties.get(owningClass.getName());
        if (classProperties == null)
        {
            classProperties = new HashMap<QName, Serializable>();
            properties.put(owningClass.getName(), classProperties);
        }
        classProperties.put(propDef.getName(), value);
        
        // Extract name, if provided
        if (propDef.getName().equals(ContentModel.PROP_NAME))
        {
            nodeName = (String)value;
        }
    }
    
    /**
     * Gets the properties of the node for the specified class (type or aspect)
     * 
     * @param className  the type or aspect
     * @return  the properties
     */
    /*package*/ Map<QName, Serializable> getProperties(QName className)
    {
        Map<QName, Serializable> classProperties = properties.get(className);
        if (classProperties == null)
        {
            classProperties = Collections.emptyMap();
        }
        return classProperties; 
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
    
    /**
     * @return  the aspects of this node
     */
    /*package*/ Set<QName> getNodeAspects()
    {
        return nodeAspects.keySet();
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
