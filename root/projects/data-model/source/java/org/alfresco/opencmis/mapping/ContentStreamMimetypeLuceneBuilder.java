package org.alfresco.opencmis.mapping;

import java.io.Serializable;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;

/**
 * Lucene Builder for CMIS content stream mimetype property
 * 
 * @author andyh
 */
public class ContentStreamMimetypeLuceneBuilder extends AbstractSimpleLuceneBuilder
{
    private DictionaryService dictionaryService;
    
    /**
     * Construct
     * 
     * @param dictionaryService DictionaryService
     */
    public ContentStreamMimetypeLuceneBuilder(DictionaryService dictionaryService)
    {
        super();
        this.dictionaryService = dictionaryService;
    }

    @Override
    public String getLuceneFieldName()
    {
        StringBuilder field = new StringBuilder(128);
        field.append("@");
        field.append(ContentModel.PROP_CONTENT);
        field.append(".mimetype");
        return field.toString();
    }

    @Override
    protected String getValueAsString(Serializable value)
    {
        Object converted = DefaultTypeConverter.INSTANCE.convert(dictionaryService.getDataType(DataTypeDefinition.TEXT), value);
        String asString = DefaultTypeConverter.INSTANCE.convert(String.class, converted);
        return asString;
    }

    @Override
    protected QName getQNameForExists()
    {
        return ContentModel.PROP_CONTENT;
    }

    @Override
    protected DataTypeDefinition getInDataType()
    {
        return dictionaryService.getDataType(DataTypeDefinition.TEXT);
    }
}
