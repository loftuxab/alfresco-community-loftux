<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * System Settings GET method
 */
Admin.initModel(
   "Alfresco:Type=Configuration,Category=sysAdmin,id1=default",
   ["alfresco.context","alfresco.host","alfresco.protocol","alfresco.port","server.allowWrite","server.allowedusers","server.maxusers","share.context","share.host","site.public.group","share.protocol","share.port"],
   "admin-systemsettings"
);
