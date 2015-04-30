<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Fileservers GET method
 */
Admin.initModel(
   "Alfresco:Type=Configuration,Category=fileServers,id1=default",
   ["filesystem.name", "cifs.enabled", "cifs.serverName", "cifs.domain", "cifs.hostannounce", "cifs.sessionTimeout", "ftp.enabled", "ftp.port", "ftp.dataPortTo", "ftp.dataPortFrom"],
   "admin-fileservers"
);
