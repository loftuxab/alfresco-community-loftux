package org.alfresco.solr.query;

import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.ContextAwareQuery;
import org.alfresco.util.Pair;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andy
 */
public class AlfrescoLuceneQParserPlugin extends QParserPlugin
{
    protected final static Logger log = LoggerFactory.getLogger(AlfrescoLuceneQParserPlugin.class);
    
    /*
     * (non-Javadoc)
     * @see org.apache.solr.search.QParserPlugin#createParser(java.lang.String,
     * org.apache.solr.common.params.SolrParams, org.apache.solr.common.params.SolrParams,
     * org.apache.solr.request.SolrQueryRequest)
     */
    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
    {
        return new AlfrescoLuceneQParser(qstr, localParams, params, req);

    }

    /*
     * (non-Javadoc)
     * @see org.apache.solr.util.plugin.NamedListInitializedPlugin#init(org.apache.solr.common.util.NamedList)
     */
    public void init(NamedList arg0)
    {
    }

    public static class AlfrescoLuceneQParser extends AbstractQParser
    {
        public AlfrescoLuceneQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
        {
            super(qstr, localParams, params, req);
        }

        /*
         * (non-Javadoc)
         * @see org.apache.solr.search.QParser#parse()
         */
        @Override
        public Query parse() throws ParseException
        {
            Pair<SearchParameters, Boolean> searchParametersAndFilter = getSearchParameters();
            SearchParameters searchParameters = searchParametersAndFilter.getFirst();
            Boolean isFilter = searchParametersAndFilter.getSecond();

            String id = req.getSchema().getResourceLoader().getInstanceDir();
            IndexReader indexReader = req.getSearcher().getIndexReader();
            
            AbstractLuceneQueryParser lqp = AlfrescoSolrDataModel.getInstance(id).getLuceneQueryParser(searchParameters, indexReader);
            Query query = lqp.parse(searchParameters.getQuery());
            ContextAwareQuery contextAwareQuery = new ContextAwareQuery(query, Boolean.TRUE.equals(isFilter) ? null : searchParameters);
            if(log.isDebugEnabled())
            {
                log.debug("Lucene QP query as lucene:\t    "+contextAwareQuery);
            }
            return contextAwareQuery;
        }
    }

}
