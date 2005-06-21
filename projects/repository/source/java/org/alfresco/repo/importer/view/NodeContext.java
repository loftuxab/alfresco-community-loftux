package org.alfresco.repo.importer.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.importer.ImportNode;
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
    implements ImportNode
{
    private ParentContext parentContext;
    private NodeRef nodeRef;
    private TypeDefinition typeDef;
    private Map<QName, AspectDefinition> nodeAspects = new HashMap<QName, AspectDefinition>();
    private String childName;
    private Map<QName, Map<QName, String>> properties = new HashMap<QName, Map<QName, String>>();


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
        Map<QName, String> classProperties = properties.get(owningClass.getName());
        if (classProperties == null)
        {
            classProperties = new HashMap<QName, String>();
            properties.put(owningClass.getName(), classProperties);
        }
        classProperties.put(propDef.getName(), value);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImportNode#getProperties(org.alfresco.service.namespace.QName)
     */
    public Map<QName, String> getProperties(QName className)
    {
        Map<QName, String> classProperties = properties.get(className);
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
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImportNode#getNodeAspects()
     */
    public Set<QName> getNodeAspects()
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
