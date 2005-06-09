package org.alfresco.repo.dictionary.impl;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.ref.QName;


/**
 * Access to model items.
 * 
 * @author David Caruana
 *
 */
/*package*/ interface ModelQuery
{

    /**
     * Gets the specified property type
     * 
     * @param name  name of the property type
     * @return  property type definition
     */
    public PropertyTypeDefinition getPropertyType(QName name);

    /**
     * Gets the specified type
     * 
     * @param name  name of the type
     * @return  type definition
     */
    public TypeDefinition getType(QName name);
    
    /**
     * Gets the specified aspect
     * 
     * @param name  name of the aspect
     * @return  aspect definition
     */
    public AspectDefinition getAspect(QName name);
    
    /**
     * Gets the specified class
     * 
     * @param name  name of the class
     * @return  class definition
     */
    public ClassDefinition getClass(QName name);
    
    /**
     * Gets the specified property
     * 
     * @param name  name of the property
     * @return  property definition
     */
    public PropertyDefinition getProperty(QName name);
    
    /**
     * Gets the specified association
     * 
     * @param name  name of the association
     * @return  association definition
     */
    public AssociationDefinition getAssociation(QName name);
    
}
