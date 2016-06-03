package org.alfresco.opencmis.dictionary;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

public interface TypeDefinitionWrapper
{
    TypeDefinition getTypeDefinition(boolean includePropertyDefinitions);

    String getTypeId();

    BaseTypeId getBaseTypeId();

    boolean isBaseType();

    QName getAlfrescoName();

    QName getAlfrescoClass();
    String getTenantId();
    TypeDefinitionWrapper getParent();

//    List<TypeDefinitionWrapper> getChildren();

    Collection<PropertyDefinitionWrapper> getProperties();
    
    Collection<PropertyDefinitionWrapper> getProperties(boolean update);

    PropertyDefinitionWrapper getPropertyById(String propertyId);

    PropertyDefinitionWrapper getPropertyByQueryName(String queryName);

    PropertyDefinitionWrapper getPropertyByQName(QName name);

    Map<Action, CMISActionEvaluator> getActionEvaluators();

    void updateDefinition(DictionaryService dictionaryService);
}
