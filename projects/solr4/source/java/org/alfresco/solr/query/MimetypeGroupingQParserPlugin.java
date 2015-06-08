/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.solr.query;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.SyntaxError;

/**
 * @author Andy
 *
 */
public class MimetypeGroupingQParserPlugin extends QParserPlugin
{

    /* (non-Javadoc)
     * @see org.apache.solr.util.plugin.NamedListInitializedPlugin#init(org.apache.solr.common.util.NamedList)
     */
    @Override
    public void init(NamedList args)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.solr.search.QParserPlugin#createParser(java.lang.String, org.apache.solr.common.params.SolrParams, org.apache.solr.common.params.SolrParams, org.apache.solr.request.SolrQueryRequest)
     */
    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
    {
        return new MimetypeGroupingQParser(qstr, localParams, params, req);
    }

    public static class MimetypeGroupingQParser extends AbstractQParser
    {
        public MimetypeGroupingQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
        {
            super(qstr, localParams, params, req);
        }

        /* (non-Javadoc)
         * @see org.apache.solr.search.QParser#parse()
         */
        @Override
        public Query parse() throws SyntaxError
        {
            return new MimetypeGroupingAnalyticsQuery();
        }
    }

}
