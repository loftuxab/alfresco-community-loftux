package org.alfresco.repo.dictionary;

import java.util.Collection;

import org.alfresco.repo.ref.QName;


/**
 * Data Dictionary Service.
 * 
 * The Dictionary Service provides access to all Repository
 * meta-data.
 * 
 * @author David Caruana
 */
public interface DictionaryService
{

    public Collection<QName> getAllModels();
    
    public ModelDefinition getModel(QName model);


    Collection<QName> getAllPropertyTypes();
    
    Collection<QName> getPropertyTypes(QName model);
    
    PropertyTypeDefinition getPropertyType(QName name);
    
    
    /**
     * Gets all Type definitions
     * 
     * @return collection of Type References
     */
    Collection<QName> getAllTypes();
    
    Collection<QName> getTypes(QName model);

    /**
     * Gets a Type Definition
     * 
     * @param typeRef  reference of the type to retrieve
     * @return  the type definition (or null, if it does not exist)
     */
    TypeDefinition getType(QName name);

    TypeDefinition getAnonymousType(QName type, Collection<QName> aspects);

    
    Collection<QName> getAllAspects();
    
    Collection<QName> getAspects(QName model);

    /**
     * Gets an Aspect Definition
     * 
     * @param aspectRef  reference of the aspect to retrieve
     * @return  the aspect definition (or null, if it does not exist)
     */
    AspectDefinition getAspect(QName name);


    /**
     * Gets a Class Definition
     *
     * @param classRef  reference of the class to retrieve
     * @return  the class definition (or null, if it does not exist)
     */
    ClassDefinition getClass(QName name);
    
    
    /**
     * Determines whether the class is a sub-class of the specified class
     * 
     * @param className  the sub-class to test
     * @param ofClassName  the class to test against
     * @return  true => the class is a sub-class (or itself)
     */
    boolean isSubClass(QName className, QName ofClassName);

    
    PropertyDefinition getProperty(QName className, QName propertyName);
    
    PropertyDefinition getProperty(QName propertyName);

    AssociationDefinition getAssociation(QName className, QName associationName);
    
    AssociationDefinition getAssociation(QName associationName);
    
    
//    BehaviourDef[] getBehaviours();
    
    
}
