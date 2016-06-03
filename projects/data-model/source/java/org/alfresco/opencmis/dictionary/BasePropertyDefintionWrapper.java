package org.alfresco.opencmis.dictionary;

import java.io.Serializable;

import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;

public class BasePropertyDefintionWrapper implements PropertyDefinitionWrapper, Serializable
{
    private static final long serialVersionUID = 1L;

    private PropertyDefinition<?> propDef;
    private QName alfrescoName;
    private TypeDefinitionWrapper owningType;
    private CMISPropertyAccessor accessor;
    private CMISPropertyLuceneBuilder luceneBuilder;

    public BasePropertyDefintionWrapper(PropertyDefinition<?> propDef, QName alfrescoName,
            TypeDefinitionWrapper owningType, CMISPropertyAccessor accessor, CMISPropertyLuceneBuilder luceneBuilder)
    {
        this.propDef = propDef;
        this.alfrescoName = alfrescoName;
        this.owningType = owningType;
        this.accessor = accessor;
        this.luceneBuilder = luceneBuilder;
    }

    @Override
    public PropertyDefinition<?> getPropertyDefinition()
    {
        return propDef;
    }

    @Override
    public String getPropertyId()
    {
        return propDef.getId();
    }

    @Override
    public QName getAlfrescoName()
    {
        return alfrescoName;
    }

    @Override
    public TypeDefinitionWrapper getOwningType()
    {
        return owningType;
    }

    @Override
    public CMISPropertyAccessor getPropertyAccessor()
    {
        return accessor;
    }

    @Override
    public CMISPropertyLuceneBuilder getPropertyLuceneBuilder()
    {
        return luceneBuilder;
    }

}
