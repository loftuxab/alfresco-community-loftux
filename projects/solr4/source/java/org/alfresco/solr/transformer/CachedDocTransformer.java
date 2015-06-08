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

import static org.alfresco.repo.search.adaptor.lucene.QueryConstants.FIELD_SOLR4_ID;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.AlfrescoSolrDataModel.TenantAclIdDbId;
import org.alfresco.solr.content.SolrContentStore;
import org.alfresco.solr.content.SolrContentUrlBuilder;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.JavaBinCodec;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.response.transform.DocTransformer;
import org.apache.solr.response.transform.TransformContext;
import org.apache.solr.schema.SchemaField;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andy
 *
 */
public class CachedDocTransformer extends DocTransformer
{
    protected final static Logger log = LoggerFactory.getLogger(CachedDocTransformer.class);

    // write a BytesRef as a byte array
    JavaBinCodec.ObjectResolver resolver = new JavaBinCodec.ObjectResolver()
    {
        @Override
        public Object resolve(Object o, JavaBinCodec codec) throws IOException
        {
            if (o instanceof BytesRef)
            {
                BytesRef br = (BytesRef) o;
                codec.writeByteArray(br.bytes, br.offset, br.length);
                return null;
            }
            return o;
        }
    };

    private TransformContext context;

    static SolrContentStore solrContentStore;
    
    /* (non-Javadoc)
     * @see org.apache.solr.response.transform.DocTransformer#getName()
     */
    @Override
    public String getName()
    {
        return "Alfresco cached document transformer";
    }

    private static synchronized void setStaticContext( TransformContext context )
    {
        if(solrContentStore == null)
        {
            try
            {
                context.req.getCore().getResourceLoader();
                solrContentStore = getSolrContentStore(SolrResourceLoader.locateSolrHome());
            }
            catch (JobExecutionException e)
            {
            }
        }
    }
    
    public void setContext( TransformContext context ) 
    {
        setStaticContext(context);
        this.context = context;
    }
    
    /* (non-Javadoc)
     * @see org.apache.solr.response.transform.DocTransformer#transform(org.apache.solr.common.SolrDocument, int)
     */
    @Override
    public void transform(SolrDocument doc, int docid) throws IOException
    {
        if(solrContentStore == null)
        {
            throw new IOException("No content store");
        }
        
   
        SolrInputDocument cachedDoc = null;
        try
        {
            String id = getFieldValueString(doc, FIELD_SOLR4_ID);
            TenantAclIdDbId tenantAndDbId = AlfrescoSolrDataModel.decodeNodeDocumentId(id);
            cachedDoc = retrieveDocFromSolrContentStore(tenantAndDbId.tenant, tenantAndDbId.dbId);
        }
        catch(StringIndexOutOfBoundsException e)
        {
            // ignore invalid forms ....
        }
        
        if(cachedDoc != null)
        {
            Collection<String> fieldNames = cachedDoc.getFieldNames();
            for (String fieldName : fieldNames)
            {
               SchemaField schemaField = context.searcher.getSchema().getFieldOrNull(fieldName);
               if(schemaField != null)
               {
                   doc.removeFields(fieldName);
                   if(schemaField.multiValued())
                   {
                       int index = fieldName.lastIndexOf("@{");
                       if(index == -1)
                       { 
                           doc.addField(fieldName, cachedDoc.getFieldValues(fieldName));
                       }
                       else
                       {
                           String alfrescoFieldName = AlfrescoSolrDataModel.getInstance().getAlfrescoPropertyFromSchemaField(fieldName);
                           Collection<Object> values = cachedDoc.getFieldValues(fieldName);
                           ArrayList<Object> newValues = new ArrayList<Object>(values.size());
                           for(Object value : values)
                           {
                               if(value instanceof String)
                               {
                                   String stringValue = (String) value;
                                   int start = stringValue.lastIndexOf('\u0000');
                                   if(start == -1)
                                   {
                                        newValues.add(stringValue);
                                   }
                                   else
                                   {
                                       newValues.add(stringValue.substring(start+1));
                                   }
                               }
                               else
                               {
                                   newValues.add(value);
                               }
                               
                           }
                           doc.removeFields(alfrescoFieldName);
                           doc.addField(alfrescoFieldName, newValues);
                       }
                   }
                   else
                   {
                       int index = fieldName.lastIndexOf("@{");
                       if(index == -1)
                       { 
                            doc.addField(fieldName, cachedDoc.getFieldValue(fieldName));
                       }
                       else
                       {
                           String alfrescoFieldName = AlfrescoSolrDataModel.getInstance().getAlfrescoPropertyFromSchemaField(fieldName);
                           
                           Object value = cachedDoc.getFieldValue(fieldName);
                           if(value instanceof String)
                           {
                               String stringValue = (String) value;
                               int start = stringValue.lastIndexOf('\u0000');
                               if(start == -1)
                               {
                                   doc.removeFields(alfrescoFieldName);
                                   doc.addField(alfrescoFieldName, stringValue);
                               }
                               else
                               {
                                   doc.removeFields(alfrescoFieldName);
                                   doc.addField(alfrescoFieldName, stringValue.substring(start+1));
                               }
                           }
                           else
                           {
                               doc.removeFields(alfrescoFieldName); 
                               doc.addField(alfrescoFieldName, value);
                           }
                       }
                   }
               }
            }
        }

    }

    private SolrInputDocument retrieveDocFromSolrContentStore(String tenant, long dbId) throws IOException
    {
        String contentUrl = SolrContentUrlBuilder
                    .start()
                    .add(SolrContentUrlBuilder.KEY_TENANT, tenant)
                    .add(SolrContentUrlBuilder.KEY_DB_ID, String.valueOf(dbId))
                    .get();
        ContentReader reader = CachedDocTransformer.solrContentStore.getReader(contentUrl);
        SolrInputDocument cachedDoc = null;
        if (reader.exists())
        {
            // try-with-resources statement closes all these InputStreams
            try (
                    InputStream contentInputStream = reader.getContentInputStream();
                    // Uncompresses the document
                    GZIPInputStream gzip = new GZIPInputStream(contentInputStream);
                )
            {
                cachedDoc = (SolrInputDocument) new JavaBinCodec(resolver).unmarshal(gzip);
            }
            catch (Exception e)
            {
                // Don't fail for this
                log.warn("Failed to get doc from store using URL: " + contentUrl, e);
                return null;
            }
        }
        return cachedDoc;
    }
    
    private static SolrContentStore getSolrContentStore(String solrHome) throws JobExecutionException
    {
        // TODO: Could specify the rootStr from a properties file.
        return new SolrContentStore(locateContentHome(solrHome));
    }
    
    public static String locateContentHome(String solrHome)
    {
        String contentDir = null;
        // Try JNDI
        try
        {
            Context c = new InitialContext();
            contentDir = (String) c.lookup("java:comp/env/solr/content/dir");
            log.info("Using JNDI solr.content.dir: " + contentDir);
        }
        catch (NoInitialContextException e)
        {
            log.info("JNDI not configured for solr (NoInitialContextEx)");
        }
        catch (NamingException e)
        {
            log.info("No solr/content/dir in JNDI");
        }
        catch (RuntimeException ex)
        {
            log.warn("Odd RuntimeException while testing for JNDI: " + ex.getMessage());
        }

        // Now try system property
        if (contentDir == null)
        {
            String prop = "solr.solr.content.dir";
            contentDir = System.getProperty(prop);
            if (contentDir != null)
            {
                log.info("using system property " + prop + ": " + contentDir);
            }
        }

        // if all else fails, try
        if (contentDir == null)
        {
            return solrHome + "ContentStore";

        }
        else
        {
            return contentDir;
        }
    }
    
    private String getFieldValueString(SolrDocument doc, String fieldName)
    {
        IndexableField field = (IndexableField)doc.getFieldValue(fieldName);
        String value = null;
        if (field != null)
        {
            value = field.stringValue();
        }
        return value;
    }
}
