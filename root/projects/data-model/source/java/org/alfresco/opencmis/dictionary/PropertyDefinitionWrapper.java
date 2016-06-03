package org.alfresco.opencmis.dictionary;

import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;

public interface PropertyDefinitionWrapper
{
    PropertyDefinition<?> getPropertyDefinition();

    String getPropertyId();

    QName getAlfrescoName();

    TypeDefinitionWrapper getOwningType();

    CMISPropertyAccessor getPropertyAccessor();
    
    CMISPropertyLuceneBuilder getPropertyLuceneBuilder();
}
