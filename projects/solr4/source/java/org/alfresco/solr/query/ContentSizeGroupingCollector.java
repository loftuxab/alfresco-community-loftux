/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.AlfrescoSolrDataModel.FieldUse;
import org.alfresco.solr.tracker.TrackerStats.Bucket;
import org.alfresco.solr.tracker.TrackerStats.IncrementalStats;
import org.alfresco.solr.tracker.TrackerStats.SimpleStats;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.util.Counter;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DelegatingCollector;

/**
 * @author Andy
 *
 */
public class ContentSizeGroupingCollector extends DelegatingCollector
{
    ResponseBuilder rb;
    private int buckets;
    private int scale;
    IncrementalStats stats;
    String schemaFieldName;
    SchemaField schemaField;
    NumericDocValues numericDocValues;    
    /**
     * @param rb
     * @param buckets 
     */
    public ContentSizeGroupingCollector(ResponseBuilder rb, int scale, int buckets)
    {
        this.rb = rb;
        this.buckets = buckets;
        stats = new IncrementalStats(scale, buckets, null);
        schemaFieldName = AlfrescoSolrDataModel.getInstance().mapProperty("content.size", FieldUse.FACET, rb.req);
        schemaField = rb.req.getSchema().getFieldOrNull(schemaFieldName);
    }
    
    
    
    /* (non-Javadoc)
     * @see org.apache.solr.search.DelegatingCollector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
     */
    @Override
    public void setNextReader(AtomicReaderContext context) throws IOException
    {
        super.setNextReader(context);
        if(schemaField != null)
        {
            if(schemaField.getType().getNumericType() != null)
            {
                try
                {
                    numericDocValues = context.reader().getNumericDocValues(schemaFieldName);
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }



    public void collect(int doc) throws IOException 
    {

        if(numericDocValues != null)
        {
            long value = numericDocValues.get(doc);
            stats.add(value);
        }
        delegate.collect(doc);
    }

    public void finish() throws IOException 
    {
        NamedList<Object> analytics = new NamedList<>();
        rb.rsp.add("analytics", analytics);
        NamedList<Object> fieldCounts = new NamedList<>(); 
        analytics.add("contentSize()", fieldCounts);

        for(Bucket bucket :stats.getHistogram())
        {
            fieldCounts.add("["+(long)Math.ceil(bucket.leftBoundary)+ " TO "+(long)Math.ceil(bucket.rightBoundary)+">", (long)roundEven(bucket.countLeft + bucket.countRight));
        }


        if(this.delegate instanceof DelegatingCollector) {
            ((DelegatingCollector)this.delegate).finish();
        }
    }
    
  
    private long roundEven(double value)
    {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.round(MathContext.DECIMAL64);
        return bd.longValue();
    }
}
