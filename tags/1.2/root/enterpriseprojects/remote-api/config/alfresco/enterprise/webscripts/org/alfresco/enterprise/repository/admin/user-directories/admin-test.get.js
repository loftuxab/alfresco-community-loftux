<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * TEST CONTROLS GET method
 */
Admin.initModel(
   "Alfresco:Name=License",
   ["Subject", "Issued", "Issuer", "MaxDocs", "HeartBeatDisabled"],
   "admin-test"
);
