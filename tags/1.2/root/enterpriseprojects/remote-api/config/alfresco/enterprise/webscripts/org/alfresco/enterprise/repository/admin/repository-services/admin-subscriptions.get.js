<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Subscription Services GET method
 */
Admin.initModel(
   "Alfresco:Type=Configuration,Category=Subscriptions,id1=default",
   ["subscriptions.enabled"],
   "admin-subscriptions"
);

