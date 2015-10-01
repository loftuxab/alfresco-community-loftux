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
package org.apache.solr.handler.component;

import static org.alfresco.repo.search.adaptor.lucene.QueryConstants.FIELD_SOLR4_ID;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.AlfrescoSolrDataModel.TenantAclIdDbId;
import org.alfresco.solr.content.SolrContentUrlBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.JavaBinCodec;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.handler.clustering.ClusteringComponent;
import org.apache.solr.handler.clustering.ClusteringEngine;
import org.apache.solr.handler.clustering.ClusteringParams;
import org.apache.solr.handler.clustering.DocumentClusteringEngine;
import org.apache.solr.handler.clustering.SearchClusteringEngine;
import org.apache.solr.handler.clustering.carrot2.CarrotClusteringEngine;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.DocListAndSet;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.update.DocumentBuilder;
import org.apache.solr.util.SolrPluginUtils;
import org.apache.solr.util.plugin.SolrCoreAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * @author Andy
 *
 */
public class AlfrescoSolrClusteringComponent extends SearchComponent implements SolrCoreAware {
        private transient static Logger log = LoggerFactory.getLogger(ClusteringComponent.class);

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
        
        /**
         * Base name for all component parameters. This name is also used to
         * register this component with SearchHandler.
         */
        public static final String COMPONENT_NAME = "clustering";

        /**
         * Declaration-order list of search clustering engines.
         */
        private final LinkedHashMap<String, SearchClusteringEngine> searchClusteringEngines = Maps.newLinkedHashMap();
        
        /**
         * Declaration order list of document clustering engines.
         */
        private final LinkedHashMap<String, DocumentClusteringEngine> documentClusteringEngines = Maps.newLinkedHashMap();

        /**
         * An unmodifiable view of {@link #searchClusteringEngines}.
         */
        private final Map<String, SearchClusteringEngine> searchClusteringEnginesView = Collections.unmodifiableMap(searchClusteringEngines);

        /**
         * Initialization parameters temporarily saved here, the component
         * is initialized in {@link #inform(SolrCore)} because we need to know
         * the core's {@link SolrResourceLoader}.
         * 
         * @see #init(NamedList)
         */
        private NamedList<Object> initParams;

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public void init(NamedList args) {
          this.initParams = args;
          super.init(args);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void inform(SolrCore core) {
          if (initParams != null) {
            log.info("Initializing Clustering Engines");

            // Our target list of engines, split into search-results and document clustering.
            SolrResourceLoader loader = core.getResourceLoader();
        
            for (Map.Entry<String,Object> entry : initParams) {
              if ("engine".equals(entry.getKey())) {
                NamedList<Object> engineInitParams = (NamedList<Object>) entry.getValue();
                
                String engineClassName = StringUtils.defaultIfBlank( 
                    (String) engineInitParams.get("classname"),
                    CarrotClusteringEngine.class.getName()); 
        
                // Instantiate the clustering engine and split to appropriate map. 
                final ClusteringEngine engine = loader.newInstance(engineClassName, ClusteringEngine.class);
                final String name = StringUtils.defaultIfBlank(engine.init(engineInitParams, core), "");
                final ClusteringEngine previousEntry;
                if (engine instanceof SearchClusteringEngine) {
                  previousEntry = searchClusteringEngines.put(name, (SearchClusteringEngine) engine);
                } else if (engine instanceof DocumentClusteringEngine) {
                  previousEntry = documentClusteringEngines.put(name, (DocumentClusteringEngine) engine);
                } else {
                  log.warn("Unknown type of a clustering engine for class: " + engineClassName);
                  continue;
                }
                if (previousEntry != null) {
                  log.warn("Duplicate clustering engine component named '" + name + "'.");
                }
              }
            }

            // Set up the default engine key for both types of engines.
            setupDefaultEngine("search results clustering", searchClusteringEngines);
            setupDefaultEngine("document clustering", documentClusteringEngines);

            log.info("Finished Initializing Clustering Engines");
          }
        }

        @Override
        public void prepare(ResponseBuilder rb) throws IOException {
          SolrParams params = rb.req.getParams();
          if (!params.getBool(COMPONENT_NAME, false)) {
            return;
          }
        }

        @Override
        public void process(ResponseBuilder rb) throws IOException {
          SolrParams params = rb.req.getParams();
          if (!params.getBool(COMPONENT_NAME, false)) {
            return;
          }
          String name = getClusteringEngineName(rb);
          boolean useResults = params.getBool(ClusteringParams.USE_SEARCH_RESULTS, false);
          if (useResults == true) {
            SearchClusteringEngine engine = getSearchClusteringEngine(rb);
            if (engine != null) {
              DocListAndSet results = rb.getResults();
              Map<SolrDocument,Integer> docIds = Maps.newHashMapWithExpectedSize(results.docList.size());
              SolrDocumentList solrDocList = docListToSolrDocumentList(
                  results.docList, rb.req, docIds);
              Object clusters = engine.cluster(rb.getQuery(), solrDocList, docIds, rb.req);
              rb.rsp.add("clusters", clusters);
            } else {
              log.warn("No engine for: " + name);
            }
          }
          boolean useCollection = params.getBool(ClusteringParams.USE_COLLECTION, false);
          if (useCollection == true) {
            DocumentClusteringEngine engine = documentClusteringEngines.get(name);
            if (engine != null) {
              boolean useDocSet = params.getBool(ClusteringParams.USE_DOC_SET, false);
              NamedList<?> nl = null;

              // TODO: This likely needs to be made into a background task that runs in an executor
              if (useDocSet == true) {
                nl = engine.cluster(rb.getResults().docSet, params);
              } else {
                nl = engine.cluster(params);
              }
              rb.rsp.add("clusters", nl);
            } else {
              log.warn("No engine for " + name);
            }
          }
        }
        
        public SolrDocumentList docListToSolrDocumentList(
                DocList docs,
                SolrQueryRequest req,
                Map<SolrDocument, Integer> ids ) throws IOException
            {
              IndexSchema schema = req.getSearcher().getSchema();

              SolrDocumentList list = new SolrDocumentList();
              list.setNumFound(docs.matches());
              list.setMaxScore(docs.maxScore());
              list.setStart(docs.offset());

              DocIterator dit = docs.iterator();

              while (dit.hasNext()) {
                int docid = dit.nextDoc();

                Document luceneDoc = req.getSearcher().doc(docid);
                SolrInputDocument input = getSolrInputDocument(luceneDoc, req);
                
                SolrDocument doc = new SolrDocument();
                
                for( String fieldName : input.getFieldNames()) {
                 
                    doc.addField( fieldName, input.getFieldValue(fieldName));
                }
                
                doc.addField("score", dit.score());
                
                list.add( doc );

                if( ids != null ) {
                  ids.put( doc, new Integer(docid) );
                }
              }
              return list;
            }
        
        private SolrInputDocument retrieveDocFromSolrContentStore(String tenant, long dbId) throws IOException
        {
            String contentUrl = SolrContentUrlBuilder
                        .start()
                        .add(SolrContentUrlBuilder.KEY_TENANT, tenant)
                        .add(SolrContentUrlBuilder.KEY_DB_ID, String.valueOf(dbId))
                        .get();
            ContentReader reader = AlfrescoSolrHighlighter.solrContentStore.getReader(contentUrl);
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
        
        private SolrInputDocument getSolrInputDocument(Document doc, SolrQueryRequest req) throws IOException
        {
            Document cachedDoc = null;
            try
            {
                String id = getFieldValueString(doc, FIELD_SOLR4_ID);
                TenantAclIdDbId tenantAndDbId = AlfrescoSolrDataModel.decodeNodeDocumentId(id);
                SolrInputDocument sid = retrieveDocFromSolrContentStore(tenantAndDbId.tenant, tenantAndDbId.dbId);
                return sid;
            }
            catch(StringIndexOutOfBoundsException e)
            {
                throw new IOException(e);
            }
        }
        
        private String getFieldValueString(Document doc, String fieldName)
        {
            IndexableField field = (IndexableField)doc.getField(fieldName);
            String value = null;
            if (field != null)
            {
                value = field.stringValue();
            }
            return value;
        }
        
        private SearchClusteringEngine getSearchClusteringEngine(ResponseBuilder rb){
          return searchClusteringEngines.get(getClusteringEngineName(rb));
        }
        
        private String getClusteringEngineName(ResponseBuilder rb){
          return rb.req.getParams().get(ClusteringParams.ENGINE_NAME, ClusteringEngine.DEFAULT_ENGINE_NAME);
        }

//        @Override
//        public void modifyRequest(ResponseBuilder rb, SearchComponent who, ShardRequest sreq) {
//          SolrParams params = rb.req.getParams();
//          if (!params.getBool(COMPONENT_NAME, false) || !params.getBool(ClusteringParams.USE_SEARCH_RESULTS, false)) {
//            return;
//          }
//          sreq.params.remove(COMPONENT_NAME);
//          if( ( sreq.purpose & ShardRequest.PURPOSE_GET_FIELDS ) != 0 ){
//            String fl = sreq.params.get(CommonParams.FL,"*");
//            // if fl=* then we don't need check
//            if( fl.indexOf( '*' ) >= 0 ) return;
//            Set<String> fields = getSearchClusteringEngine(rb).getFieldsToLoad(rb.req);
//            if( fields == null || fields.size() == 0 ) return;
//            StringBuilder sb = new StringBuilder();
//            String[] flparams = fl.split( "[,\\s]+" );
//            Set<String> flParamSet = new HashSet<>(flparams.length);
//            for( String flparam : flparams ){
//              // no need trim() because of split() by \s+
//              flParamSet.add(flparam);
//            }
//            for( String aFieldToLoad : fields ){
//              if( !flParamSet.contains( aFieldToLoad ) ){
//                sb.append( ',' ).append( aFieldToLoad );
//              }
//            }
//            if( sb.length() > 0 ){
//              sreq.params.set( CommonParams.FL, fl + sb.toString() );
//            }
//          }
//        }

        @Override
        public void finishStage(ResponseBuilder rb) {
          SolrParams params = rb.req.getParams();
          if (!params.getBool(COMPONENT_NAME, false) || !params.getBool(ClusteringParams.USE_SEARCH_RESULTS, false)) {
            return;
          }
          if (rb.stage == ResponseBuilder.STAGE_GET_FIELDS) {
            SearchClusteringEngine engine = getSearchClusteringEngine(rb);
            if (engine != null) {
              SolrDocumentList solrDocList = (SolrDocumentList)rb.rsp.getValues().get("response");
              // TODO: Currently, docIds is set to null in distributed environment.
              // This causes CarrotParams.PRODUCE_SUMMARY doesn't work.
              // To work CarrotParams.PRODUCE_SUMMARY under distributed mode, we can choose either one of:
              // (a) In each shard, ClusteringComponent produces summary and finishStage()
              //     merges these summaries.
              // (b) Adding doHighlighting(SolrDocumentList, ...) method to SolrHighlighter and
              //     making SolrHighlighter uses "external text" rather than stored values to produce snippets.
              Map<SolrDocument,Integer> docIds = null;
              Object clusters = engine.cluster(rb.getQuery(), solrDocList, docIds, rb.req);
              rb.rsp.add("clusters", clusters);
            } else {
              String name = getClusteringEngineName(rb);
              log.warn("No engine for: " + name);
            }
          }
        }

        /**
         * @return Expose for tests.
         */
        Map<String, SearchClusteringEngine> getSearchClusteringEngines() {
          return searchClusteringEnginesView;
        }

        @Override
        public String getDescription() {
          return "A Clustering component";
        }

        @Override
        public String getSource() {
          return "$URL: https://svn.apache.org/repos/asf/lucene/dev/branches/lucene_solr_4_9/solr/contrib/clustering/src/java/org/apache/solr/handler/clustering/ClusteringComponent.java $";
        }

        /**
         * Setup the default clustering engine.
         * @see "https://issues.apache.org/jira/browse/SOLR-5219"
         */
        private static <T extends ClusteringEngine> void setupDefaultEngine(String type, LinkedHashMap<String,T> map) {
          // If there's already a default algorithm, leave it as is.
          if (map.containsKey(ClusteringEngine.DEFAULT_ENGINE_NAME)) {
            return;
          }
        
          // If there's no default algorithm, and there are any algorithms available, 
          // the first definition becomes the default algorithm.
          if (!map.isEmpty()) {
            Entry<String,T> first = map.entrySet().iterator().next();
            map.put(ClusteringEngine.DEFAULT_ENGINE_NAME, first.getValue());
            log.info("Default engine for " + type + ": " + first.getKey());
          }
        }
      }
