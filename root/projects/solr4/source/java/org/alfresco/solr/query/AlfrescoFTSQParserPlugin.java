package org.alfresco.solr.query;

import org.alfresco.repo.search.impl.parsers.FTSQueryParser;
import org.alfresco.repo.search.impl.parsers.FTSQueryParser.RerankPhase;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.util.Pair;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.SyntaxError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andy
 */
public class AlfrescoFTSQParserPlugin extends QParserPlugin
{
    protected final static Logger log = LoggerFactory.getLogger(AlfrescoFTSQParserPlugin.class);
   
	private NamedList args;
	
    /*
     * (non-Javadoc)
     * @see org.apache.solr.search.QParserPlugin#createParser(java.lang.String,
     * org.apache.solr.common.params.SolrParams, org.apache.solr.common.params.SolrParams,
     * org.apache.solr.request.SolrQueryRequest)
     */
    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
    {
        return new AlfrescoFTSQParser(qstr, localParams, params, req, args);

    }

    /*
     * (non-Javadoc)
     * @see org.apache.solr.util.plugin.NamedListInitializedPlugin#init(org.apache.solr.common.util.NamedList)
     */
    @Override
    public void init(NamedList args)
    {
    	this.args = args;
    }

    public static class AlfrescoFTSQParser extends AbstractQParser
    {
    	private RerankPhase rerankPhase = RerankPhase.SINGLE_PASS_WITH_AUTO_PHRASE;

		public AlfrescoFTSQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req, NamedList args)
        {
            super(qstr, localParams, params, req, args);
            Object arg = args.get("rerankPhase");
        	if(arg != null)
        	{
                rerankPhase = RerankPhase.valueOf(arg.toString());
        	}
        }

        /*
         * (non-Javadoc)
         * @see org.apache.solr.search.QParser#parse()
         */
        @Override
        public Query parse() throws SyntaxError
        {
            try
            {
                Pair<SearchParameters, Boolean> searchParametersAndFilter = getSearchParameters();

                Query query = AlfrescoSolrDataModel.getInstance().getFTSQuery(searchParametersAndFilter, req, rerankPhase);
                if(log.isDebugEnabled())
                {
                    log.debug("AFTS QP query as lucene:\t    "+query);
                }
                return query;
            }
            catch(ParseException e)
            {
                throw new SyntaxError(e);
            }
        }
    }

}
