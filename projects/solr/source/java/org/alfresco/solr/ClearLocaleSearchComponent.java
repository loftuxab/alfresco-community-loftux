package org.alfresco.solr;

import java.io.IOException;
import java.util.Locale;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryResponse;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.util.SolrPluginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author Andy
 *
 */
public class ClearLocaleSearchComponent extends SearchComponent
{
    protected final static Logger log = LoggerFactory.getLogger(ClearLocaleSearchComponent.class);

    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#getDescription()
     */
    @Override
    public String getDescription()
    {
        return "clearLocale";
    }

    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#getSource()
     */
    @Override
    public String getSource()
    {
        return "";
    }

    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#getSourceId()
     */
    @Override
    public String getSourceId()
    {
       return "";
    }

    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#getVersion()
     */
    @Override
    public String getVersion()
    {
        return "1";
    }

    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#prepare(org.apache.solr.handler.component.ResponseBuilder)
     */
    @Override
    public void prepare(ResponseBuilder rb) throws IOException
    {
       //nothing to do on prepare
    }

    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#process(org.apache.solr.handler.component.ResponseBuilder)
     */
    @Override
    public void process(ResponseBuilder rb) throws IOException
    {
        I18NUtil.setLocale(null);
    }

}
