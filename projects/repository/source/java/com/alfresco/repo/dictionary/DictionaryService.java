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
    /**
     * Gets all Type definitions
     * 
     * @return collection of Type References
     */
    Collection<ClassRef> getTypes();
    
//    DDRef[] getTypes(String namespace);
    
//    DDRef[] getAspects();
    
//    DDRef[] getAspects(String namespace);

    /**
     * Gets a Class Definition
     *
     * @param classRef  reference of the class to retrieve
     * @return  the class definition (or null, if it does not exist)
     */
    ClassDefinition getClass(ClassRef classRef);

    /**
     * Gets a Type Definition
     * 
     * @param typeRef  reference of the type to retrieve
     * @return  the type definition (or null, if it does not exist)
     */
    TypeDefinition getType(ClassRef typeRef);

    /**
     * Gets an Aspect Definition
     * 
     * @param aspectRef  reference of the aspect to retrieve
     * @return  the aspect definition (or null, if it does not exist)
     */
    AspectDefinition getAspect(ClassRef aspectRef);
    
//    TypeDef getAnonymousType(DDRef type, DDRef[] aspects);

    /**
     * Gets a Property Definition
     * 
     * @param propertyRef  reference of the property to retrieve
     * @return  the property definition (or null, if it does not exist)
     */
    PropertyDefinition getProperty(PropertyRef propertyRef);
    
    PropertyDefinition getProperty(QName property);
    
    

//    AssociationDef getAssociation(AssociationRef association);
    
//    BehaviourDef[] getBehaviours();
    
      PropertyTypeDefinition getPropertyType(DictionaryRef propertyTypeRef);
    
}
