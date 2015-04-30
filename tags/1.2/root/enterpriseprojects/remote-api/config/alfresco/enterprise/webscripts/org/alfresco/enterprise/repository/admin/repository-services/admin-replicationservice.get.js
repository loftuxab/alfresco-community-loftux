<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Replication Service GET method
 */
Admin.initModel(
   "Alfresco:Type=Configuration,Category=Replication,id1=default",
   ["replication.enabled", "replication.transfer.readonly"],
   "admin-replicationservice"
);
