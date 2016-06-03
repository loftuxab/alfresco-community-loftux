package org.alfresco.opencmis.dictionary;

import java.util.List;

import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;

/**
 * Service to query the CMIS meta model
 * 
 * @author davidc
 */
public interface CMISDictionaryService
{
    /**
     * Find type for type id
     * 
     * @param typeId String
     * @return TypeDefinitionWrapper
     */
    TypeDefinitionWrapper findType(String typeId);

    List<TypeDefinitionWrapper> getChildren(String typeId);

    /**
     * Find type for Alfresco class name. Optionally, constrain match to one of
     * specified CMIS scopes
     * 
     * @param clazz QName
     * @param matchingScopes BaseTypeId...
     * @return TypeDefinitionWrapper
     */
    TypeDefinitionWrapper findTypeForClass(QName clazz, BaseTypeId... matchingScopes);

    TypeDefinitionWrapper findNodeType(QName clazz);

    TypeDefinitionWrapper findAssocType(QName clazz);

    PropertyDefinitionWrapper findProperty(String propId);

    PropertyDefinitionWrapper findPropertyByQueryName(String queryName);

    /**
     * Find a type by its query name
     * 
     * @param queryName String
     * @return TypeDefinitionWrapper
     */
    TypeDefinitionWrapper findTypeByQueryName(String queryName);

    /**
     * Get Base Types
     */
    List<TypeDefinitionWrapper> getBaseTypes();

    List<TypeDefinitionWrapper> getBaseTypes(boolean includeParent);

    /**
     * Get all Types
     * 
     * @return List<TypeDefinitionWrapper>
     */
    List<TypeDefinitionWrapper> getAllTypes();

    List<TypeDefinitionWrapper> getAllTypes(boolean includeParent);

    /**
     * Find data type
     * 
     * @param dataType QName
     * @return PropertyType
     */
    PropertyType findDataType(QName dataType);

    QName findAlfrescoDataType(PropertyType propertyType);
    
    boolean isExcluded(QName qname);
}
