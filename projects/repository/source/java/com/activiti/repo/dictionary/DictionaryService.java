package com.activiti.repo.dictionary;

import java.util.Collection;


public interface DictionaryService
{
    // TODO: Exceptions... 
    
    Collection/*<ClassRef>*/ getTypes();
    
//    DDRef[] getTypes(String namespace);
    
//    DDRef[] getAspects();
//    
//    DDRef[] getAspects(String namespace);
    
    ClassDefinition getClass(ClassRef classRef);
    
    TypeDefinition getType(ClassRef typeRef);
    
    AspectDefinition getAspect(ClassRef aspectRef);
//    
//    TypeDef getAnonymousType(DDRef type, DDRef[] aspects);
//    
    PropertyDefinition getProperty(PropertyRef propertyRef);
//    
//    AssociationDef getAssociation(AssociationRef association);
//    
//    BehaviourDef[] getBehaviours();
    
//    PropertyTypeDefinition getPropertyType(DictionaryRef propertyTypeRef);
    
}
