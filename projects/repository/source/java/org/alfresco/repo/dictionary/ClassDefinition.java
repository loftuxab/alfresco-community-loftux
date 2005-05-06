package org.alfresco.repo.dictionary;

import java.util.List;

import org.alfresco.repo.ref.QName;

/**
 * Read-only definition of a Class.
 * 
 * @author David Caruana
 */
public interface ClassDefinition
{
    /**
     * @return  the class reference
     */
    public ClassRef getReference();

    /**
     * @return the qualified name of the class
     */
    public QName getQName();
    
    /**
     * @return  the super class (or null, if this is the root)
     */
    public ClassDefinition getSuperClass();
    
    /**
     * @return Returns true => aspect, false => type
     */
    public boolean isAspect();

    /**
     * @return Returns the properties of the class, including inherited properties
     */
    public List<PropertyDefinition> getProperties();
    
    /**
     * @param name the simple name of the property, i.e. not the qualified name
     * @return Returns the property definition, or null if not found
     * 
     * @see PropertyRef#getPropertyName()
     */
    public PropertyDefinition getProperty(String name);

    /**
     * Fetch all associations applicable to this type, including child associations.
     * 
     * @return Returns the associations including inherited ones
     * 
     * @see ChildAssociationDefinition
     */
    public List<AssociationDefinition> getAssociations();
    
    /**
     * @param name the simple name of the association, i.e. not the qualified name
     * @return Returns the association definition, or null if not found.
     * 
     * @see AssociationRef#getAssociationName()
     * @see ChildAssociationDefinition
     */
    public AssociationDefinition getAssociation(String name);
    
    /**
     * @return Returns all child associations applicable to this type, including those
     *      inherited from super types
     */
    public List<ChildAssociationDefinition> getChildAssociations();
}
