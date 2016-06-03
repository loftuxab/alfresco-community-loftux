package org.alfresco.solr.query;

import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LegacyLuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.SolrLuceneAnalyser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

/**
 * @author andyh
 */
public class LuceneQueryBuilderContextSolrImpl implements LuceneQueryBuilderContext<Query, Sort, ParseException>
{
    private SolrQueryParser lqp;

    private NamespacePrefixResolver namespacePrefixResolver;
    
    private LuceneQueryParserAdaptor<Query, Sort, ParseException> lqpa;

    /**
     * Context for building lucene queries
     *
     * @param dictionaryService
     * @param namespacePrefixResolver
     * @param tenantService
     * @param searchParameters
     * @param defaultSearchMLAnalysisMode
     * @param indexReader
     * @param defaultAnalyzer
     * @param model
     */
    public LuceneQueryBuilderContextSolrImpl(DictionaryService dictionaryService, NamespacePrefixResolver namespacePrefixResolver, TenantService tenantService,
            SearchParameters searchParameters, MLAnalysisMode defaultSearchMLAnalysisMode, IndexReader indexReader, Analyzer defaultAnalyzer, AlfrescoSolrDataModel model)
    {
        SolrLuceneAnalyser analyzer = new SolrLuceneAnalyser(dictionaryService, searchParameters.getMlAnalaysisMode() == null ? defaultSearchMLAnalysisMode : searchParameters
                .getMlAnalaysisMode(), defaultAnalyzer, model);
        lqp = new SolrQueryParser(searchParameters.getDefaultFieldName(), analyzer);
        lqp.setDefaultOperator(AbstractLuceneQueryParser.OR_OPERATOR);
        lqp.setDictionaryService(dictionaryService);
        lqp.setNamespacePrefixResolver(namespacePrefixResolver);
        lqp.setTenantService(tenantService);
        lqp.setSearchParameters(searchParameters);
        lqp.setDefaultSearchMLAnalysisMode(defaultSearchMLAnalysisMode);
        lqp.setIndexReader(indexReader);
        lqp.setAllowLeadingWildcard(true);
        this.namespacePrefixResolver = namespacePrefixResolver;
        
        lqpa = new LegacyLuceneQueryParserAdaptor(lqp);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext#getLuceneQueryParser()
     */
    public LuceneQueryParserAdaptor<Query, Sort, ParseException> getLuceneQueryParserAdaptor()
    {
        return lqpa;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext#getNamespacePrefixResolver()
     */
    public NamespacePrefixResolver getNamespacePrefixResolver()
    {
        return namespacePrefixResolver;
    }

}
