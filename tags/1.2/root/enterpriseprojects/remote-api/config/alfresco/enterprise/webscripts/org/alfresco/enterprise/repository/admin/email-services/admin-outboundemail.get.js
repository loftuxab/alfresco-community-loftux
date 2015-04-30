<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Outbound email GET method
 */
Admin.initModel(
   "Alfresco:Type=Configuration,Category=email,id1=outbound",
   ["mail.host","mail.encoding","mail.from.enabled","mail.port","mail.from.default","mail.protocol","mail.username","mail.password","mail.smtp.auth","mail.smtp.starttls.enable","mail.smtp.timeout","mail.smtp.debug","mail.smtps.auth","mail.smtps.starttls.enable","mail.testmessage.send","mail.testmessage.subject","mail.testmessage.to","mail.testmessage.text"],
   "admin-outboundemail"
);