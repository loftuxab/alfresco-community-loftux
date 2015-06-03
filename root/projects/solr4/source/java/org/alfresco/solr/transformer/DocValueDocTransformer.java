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
package org.alfresco.solr.transformer;

import java.io.IOException;

import org.apache.lucene.index.SortedDocValues;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.response.transform.DocTransformer;
import org.apache.solr.response.transform.TransformContext;
import org.apache.solr.schema.SchemaField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andy
 *
 */
public class DocValueDocTransformer extends DocTransformer
{
    protected final static Logger log = LoggerFactory.getLogger(DocValueDocTransformer.class);
    
    TransformContext context;
    
    /* (non-Javadoc)
     * @see org.apache.solr.response.transform.DocTransformer#getName()
     */
    @Override
    public String getName()
    {
        return "Alfresco doc value document transformer";
    }

    
    public void setContext( TransformContext context ) 
    {
        this.context = context;
    }
    
    /* (non-Javadoc)
     * @see org.apache.solr.response.transform.DocTransformer#transform(org.apache.solr.common.SolrDocument, int)
     */
    @Override
    public void transform(SolrDocument doc, int docid) throws IOException
    {
        for(String fieldName :context.searcher.getFieldNames())
        {
            SchemaField schemaField = context.searcher.getSchema().getFieldOrNull(fieldName);
            if(schemaField != null)
            {
                if(schemaField.hasDocValues())
                {
                    SortedDocValues sortedDocValues = context.searcher.getAtomicReader().getSortedDocValues(fieldName);
                    if(sortedDocValues != null)
                    {
                        doc.removeFields(fieldName);
                        doc.addField(fieldName, schemaField.getType().toObject(schemaField, sortedDocValues.get(docid)));
                    }
                }
            }
        }
        
    }

    
}
