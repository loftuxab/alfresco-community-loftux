<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Search Service GET method
 */
function main()
{
   try
   {
      //Search manager
      model.searchAttributes = Admin.getMBeanAttributes(
            "Alfresco:Type=Configuration,Category=Search,id1=manager",
            ["sourceBeanName"]
         );
   }
   catch(e)
   {
      model.searchAttributes = null;
   }

   try
   {   
      //NoIndex
      model.noindexAttributes = Admin.getMBeanAttributes(
            "Alfresco:Type=Configuration,Category=Search,id1=managed,id2=noindex",
            ["search.solrTrackingSupport.enabled","solr.query.cmis.queryConsistency","solr.query.fts.queryConsistency"]
            );
   }
   catch(e)
   {
      model.noindexAttributes = null;
   }

   try
   {
      //Solr
      model.solrAttributes = Admin.getMBeanAttributes(
            "Alfresco:Type=Configuration,Category=Search,id1=managed,id2=solr",
            ["search.solrTrackingSupport.enabled","solr.port","solr.host","solr.port.ssl","solr.baseUrl","tracker.alfresco.active","tracker.alfresco.last.indexed.txn","tracker.alfresco.approx.indexing.time.remaining","tracker.alfresco.disk","tracker.alfresco.lag","tracker.alfresco.approx.txns.remaining","tracker.alfresco.memory","tracker.archive.active","tracker.archive.last.indexed.txn","tracker.archive.approx.indexing.time.remaining","tracker.archive.disk","tracker.archive.lag","tracker.archive.approx.txns.remaining","tracker.archive.memory","solr.backup.alfresco.remoteBackupLocation","solr.backup.alfresco.cronExpression","solr.backup.alfresco.numberToKeep","solr.backup.archive.remoteBackupLocation","solr.backup.archive.cronExpression","solr.backup.archive.numberToKeep","solr.query.cmis.queryConsistency","solr.query.fts.queryConsistency"]
            );
   }
   catch(e)
   {
      model.solrAttributes = null;
   }  

   try
   {
      //Solr 4
      model.solr4Attributes = Admin.getMBeanAttributes(
            "Alfresco:Type=Configuration,Category=Search,id1=managed,id2=solr4",
            ["search.solrTrackingSupport.enabled","solr.port","solr.host","solr.port.ssl","solr.baseUrl","solr.suggester.enabled","tracker.alfresco.active","tracker.alfresco.last.indexed.txn","tracker.alfresco.approx.indexing.time.remaining","tracker.alfresco.disk","tracker.alfresco.lag","tracker.alfresco.approx.txns.remaining","tracker.alfresco.memory","tracker.archive.active","tracker.archive.last.indexed.txn","tracker.archive.approx.indexing.time.remaining","tracker.archive.disk","tracker.archive.lag","tracker.archive.approx.txns.remaining","tracker.archive.memory","solr.backup.alfresco.remoteBackupLocation","solr.backup.alfresco.cronExpression","solr.backup.alfresco.numberToKeep","solr.backup.archive.remoteBackupLocation","solr.backup.archive.cronExpression","solr.backup.archive.numberToKeep","solr.query.cmis.queryConsistency","solr.query.fts.queryConsistency"]
            );
   }
   catch(e)
   {
      model.solr4Attributes = null;
   }  
   
   //Database Patch
   model.patchAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=MetadataQueryIndexesCheck",
         ["Applied"]
         );

   model.tools = Admin.getConsoleTools("admin-searchservice");
   model.metadata = Admin.getServerMetaData();
}

main();