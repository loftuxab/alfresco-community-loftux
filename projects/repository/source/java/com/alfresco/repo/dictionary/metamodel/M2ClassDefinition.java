package org.alfresco.repo.dictionary.metamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.ChildAssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.ref.QName;


/**
 * Default Read-Only Class Definition Implementation
 * 
 * @author David Caruana
 */
public class M2ClassDefinition implements ClassDefinition
{
    /**
     * Class definition to wrap
     */
    protected M2Class m2Class;
    
    private ClassRef classRef;
    
    /**
     * Construct Read-Only Class Definition
     * 
     * @param m2Class  class definition
     * @return  read-only class definition
     */
    public static ClassDefinition create(M2Class m2Class)
    {
        if (m2Class instanceof M2Type)
        {
            return new M2TypeDefinition((M2Type)m2Class);
        }
        else if (m2Class instanceof M2Aspect)
        {
            return new M2AspectDefinition((M2Aspect)m2Class);
        }
        else
        {
            return new M2ClassDefinition(m2Class);
        }
    }
    
    
    /*package*/ M2ClassDefinition(M2Class m2Class)
    {
        this.m2Class = m2Class;

        // Force load-on-demand of related entities
        this.m2Class.getSuperClass();
        this.m2Class.getInheritedProperties();
        this.m2Class.getInheritedAssociations();
    }

    
    /*package*/ M2Class getM2Class()
    {
        return m2Class;
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ClassDefinition#getReference()
     */
    public ClassRef getReference()
    {
        if (classRef == null)
        {
            classRef = new ClassRef(getQName());
        }
        return classRef;
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ClassDefinition#getName()
     */
    public QName getQName()
    {
        return m2Class.getQName();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ClassDefinition#isAspect()
     */
    public boolean isAspect()
    {
        return (m2Class instanceof M2Aspect);
    }

    /**
     * @see M2Class#getSuperClass()
     */
    public ClassDefinition getSuperClass()
    {
        return m2Class.getSuperClass().getClassDefinition();
    }
    
    /**
     * @see #getAggregateProperties()()
     */
    public List<PropertyDefinition> getProperties()
    {
        List<M2Property> aggregatedProperties = getAggregateProperties();
        List<PropertyDefinition> propertyDefs = new ArrayList<PropertyDefinition>(aggregatedProperties.size());
        for (M2Property m2Property : aggregatedProperties)
        {
            propertyDefs.add(m2Property.getPropertyDefinition());
        }
        return propertyDefs;
    }

    /**
     * Finds the property name from the list of properties local to this class
     */
    public PropertyDefinition getProperty(String name)
    {
        List<M2Property> properties = m2Class.getProperties();
        for (M2Property property : properties)
        {
            if (property.getName().equals(name))
            {
                // found the property - get the cached defintion
                return property.getPropertyDefinition();
            }
        }
        // nothing found
        return null;
    }

    /**
     * Gets the full list of Properties to include in Class Definition
     * 
     * @return  properties
     */
    protected List<M2Property> getAggregateProperties()
    {
        return Collections.unmodifiableList(m2Class.getInheritedProperties());
    }
    
    /**
     * @see #getAggregateAssociations()
     */
    public List<AssociationDefinition> getAssociations()
    {
        List<M2Association> aggregatedAssociations = getAggregateAssociations();
        List<AssociationDefinition> assocDefs = new ArrayList<AssociationDefinition>(aggregatedAssociations.size());
        for (M2Association m2Assoc : aggregatedAssociations)
        {
            assocDefs.add(m2Assoc.getAssociationDefintion());
        }
        return Collections.unmodifiableList(assocDefs);
    }

    /**
     * Finds the association name from the list of associations local to this class
     */
    public AssociationDefinition getAssociation(String name)
    {
        List<M2Association> assocs = m2Class.getAssociations();
        for (M2Association assoc : assocs)
        {
            if (assoc.getName().equals(name))
            {
                // found the association - get the cached defintion
                return assoc.getAssociationDefintion();
            }
        }
        // nothing found
        return null;
    }

    /**
     * Get all associations, but return only those that are {@link ChildAssociationDefinition} types.
     */
    public List<ChildAssociationDefinition> getChildAssociations()
    {
        List<AssociationDefinition> assocs = getAssociations();
        List<ChildAssociationDefinition> childAssocs = new ArrayList<ChildAssociationDefinition>(assocs.size());
        for (AssociationDefinition assoc : assocs)
        {
            if (assoc instanceof ChildAssociationDefinition)
            {
                childAssocs.add((ChildAssociationDefinition) assoc);
            }
        }
        // done
        return Collections.unmodifiableList(childAssocs);
    }


    /**
     * Gets the full list of Associations to include in Class Definition
     * 
     * @return  properties
     */
    protected List<M2Association> getAggregateAssociations()
    {
        return Collections.unmodifiableList(m2Class.getInheritedAssociations());
    }
}
