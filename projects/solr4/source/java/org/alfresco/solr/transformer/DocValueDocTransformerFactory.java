package org.alfresco.solr.transformer;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.transform.DocTransformer;
import org.apache.solr.response.transform.TransformerFactory;

/**
 * @author Andy
 *
 */
public class DocValueDocTransformerFactory extends TransformerFactory
{

    /* (non-Javadoc)
     * @see org.apache.solr.response.transform.TransformerFactory#create(java.lang.String, org.apache.solr.common.params.SolrParams, org.apache.solr.request.SolrQueryRequest)
     */
    @Override
    public DocTransformer create(String field, SolrParams params, SolrQueryRequest req)
    {
        return new DocValueDocTransformer();
    }

}
