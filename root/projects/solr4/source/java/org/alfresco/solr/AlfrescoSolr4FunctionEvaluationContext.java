package org.alfresco.solr;

import org.alfresco.repo.search.impl.parsers.AlfrescoFunctionEvaluationContext;
import org.alfresco.repo.search.impl.parsers.FTSQueryException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.apache.solr.schema.IndexSchema;

/**
 * @author Andy
 *
 */
public class AlfrescoSolr4FunctionEvaluationContext extends AlfrescoFunctionEvaluationContext
{

    private IndexSchema indexSchema;

    /**
     * @param namespacePrefixResolver
     * @param dictionaryService
     * @param defaultNamespace
     * @param indexSchema 
     */
    public AlfrescoSolr4FunctionEvaluationContext(NamespacePrefixResolver namespacePrefixResolver, DictionaryService dictionaryService, String defaultNamespace, IndexSchema indexSchema)
    {
        super(namespacePrefixResolver, dictionaryService, defaultNamespace);
        this.indexSchema = indexSchema;
    }

    public String getLuceneFieldName(String propertyName)
    {
     
        if(indexSchema.getFieldOrNull(propertyName) != null)
        {
            return propertyName;
        }
        else
        {
            try
            {
                return super.getLuceneFieldName(propertyName);
            }
            catch(FTSQueryException e)
            {
                // unknown
                return "_dummy_";
            }
        }
    }
}
