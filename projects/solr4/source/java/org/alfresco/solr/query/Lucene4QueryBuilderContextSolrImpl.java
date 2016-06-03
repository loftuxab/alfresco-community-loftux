package org.alfresco.solr.query;

import java.util.Properties;

import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.parsers.FTSQueryParser;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.util.Version;
import org.apache.solr.core.CoreDescriptorDecorator;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.SyntaxError;

/**
 * @author andyh
 */
public class Lucene4QueryBuilderContextSolrImpl implements LuceneQueryBuilderContext<Query, Sort, ParseException>
{
    private Solr4QueryParser lqp;

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
     * @param req
     * @param model
     */
    public Lucene4QueryBuilderContextSolrImpl(DictionaryService dictionaryService, NamespacePrefixResolver namespacePrefixResolver, TenantService tenantService,
            SearchParameters searchParameters, MLAnalysisMode defaultSearchMLAnalysisMode, SolrQueryRequest req, AlfrescoSolrDataModel model, FTSQueryParser.RerankPhase rerankPhase)
    {
          lqp = new Solr4QueryParser(req.getSchema(), Version.LUCENE_48, searchParameters.getDefaultFieldName(), req.getSchema().getQueryAnalyzer(), rerankPhase);
//        lqp.setDefaultOperator(AbstractLuceneQueryParser.OR_OPERATOR);
        lqp.setDictionaryService(dictionaryService);
        lqp.setNamespacePrefixResolver(namespacePrefixResolver);
        lqp.setTenantService(tenantService);
          lqp.setSearchParameters(searchParameters);
//        lqp.setDefaultSearchMLAnalysisMode(defaultSearchMLAnalysisMode);
//        lqp.setIndexReader(indexReader);
//        lqp.setAllowLeadingWildcard(true);
//        this.namespacePrefixResolver = namespacePrefixResolver;
        
          Properties props = new CoreDescriptorDecorator(req.getCore().getCoreDescriptor()).getCoreProperties();
          int topTermSpanRewriteLimit = Integer.parseInt(props.getProperty("alfresco.topTermSpanRewriteLimit", "1000"));
          lqp.setTopTermSpanRewriteLimit(topTermSpanRewriteLimit);
          
          lqpa = new Lucene4QueryParserAdaptor(lqp);
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
