package org.alfresco.opencmis.mapping;

import java.io.Serializable;

import org.alfresco.opencmis.dictionary.CMISDictionaryService;
import org.alfresco.opencmis.dictionary.TypeDefinitionWrapper;
import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;


/**
 * Get the CMIS object type id property
 * 
 * @author andyh
 */
public class BaseTypeIdLuceneBuilder extends BaseLuceneBuilder
{
    private CMISDictionaryService dictionaryService;
    

    /**
     * Construct
     */
    public BaseTypeIdLuceneBuilder(CMISDictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneEquality(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        return lqpa.getFieldQuery("TYPE", getType(getValueAsString(value)), AnalysisMode.IDENTIFIER, luceneFunction);     
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneExists(LuceneQueryParserAdaptor<Q, S, E> lqpa, Boolean not) throws E
    {
        if (not)
        {
            return lqpa.getMatchNoneQuery();
        }
        else
        { 
            return lqpa.getMatchAllQuery();
        }
    }
    
    private String getType(String tableName)
    {
        TypeDefinitionWrapper typeDef = dictionaryService.findTypeByQueryName(tableName);
        if (typeDef == null)
        {
            throw new CmisInvalidArgumentException("Unknown type: " + tableName);
        }
        if(!typeDef.isBaseType())
        {
            throw new CmisInvalidArgumentException("Not a base type: " + tableName);
        }
        if(!typeDef.getTypeDefinition(false).isQueryable())
        {
            throw new CmisInvalidArgumentException("Type is not queryable: " + tableName);
        }
        return typeDef.getAlfrescoClass().toString();
    }
    
    private String getValueAsString(Serializable value)
    {
        String asString = DefaultTypeConverter.INSTANCE.convert(String.class, value);
        return asString;
    }
}
