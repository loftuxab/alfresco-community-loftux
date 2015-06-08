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
import java.util.HashMap;

import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.AlfrescoSolrDataModel.FieldUse;
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
public class MimetypeGroupingCollector extends DelegatingCollector
{
    HashMap<String, Counter> counters = new HashMap<String, Counter>();
    ResponseBuilder rb;
    private HashMap<String, String> mappings;
    /**
     * @param rb
     * @param mappings 
     */
    public MimetypeGroupingCollector(ResponseBuilder rb, HashMap<String, String> mappings)
    {
        this.rb = rb;
        this.mappings = mappings;
    }
    
    public void collect(int doc) throws IOException 
    {
        String schemaFieldName = AlfrescoSolrDataModel.getInstance().mapProperty("content.mimetype", FieldUse.FACET, rb.req);
        SchemaField schemaField = rb.req.getSchema().getFieldOrNull(schemaFieldName);
        if(schemaField != null)
        {
            SortedDocValues sortedDocValues = context.reader().getSortedDocValues(schemaFieldName);
            
            if(sortedDocValues != null)
            {
                int ordinal = sortedDocValues.getOrd(doc);
                if(ordinal > -1)
                {
                   String value = (String)schemaField.getType().toObject(schemaField, sortedDocValues.lookupOrd(ordinal));
                   String group = mappings.get(value);
                   
                   Counter counter = counters.get(group);
                   if(counter == null)
                   {
                       counter = Counter.newCounter();
                       counters.put(group, counter);
                   }
                   counter.addAndGet(1);
                }
            }
        }
       
        delegate.collect(doc);
    }

    public void finish() throws IOException 
    {
        NamedList<Object> analytics = new NamedList<>();
        rb.rsp.add("analytics", analytics);
        NamedList<Object> fieldCounts = new NamedList<>(); 
        analytics.add("mimetype()", fieldCounts);
        for(String key : counters.keySet())
        {
            Counter counter = counters.get(key);
            fieldCounts.add(key, counter.get());
        }
        
        if(this.delegate instanceof DelegatingCollector) {
            ((DelegatingCollector)this.delegate).finish();
        }
    }
}
