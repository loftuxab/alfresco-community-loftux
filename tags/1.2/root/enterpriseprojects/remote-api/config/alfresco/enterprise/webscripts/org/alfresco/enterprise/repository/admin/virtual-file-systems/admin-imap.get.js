<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Fileservers GET method
 */
Admin.initModel(
   "Alfresco:Type=Configuration,Category=imap,id1=default",
   ["imap.server.enabled","imap.mail.to.default","imap.server.host","imap.mail.from.default","imap.server.imap.enabled","imap.server.port","imap.server.imaps.enabled","imap.server.imaps.port"],
   "admin-imap"
);
