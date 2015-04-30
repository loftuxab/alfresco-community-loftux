<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Edit Passthru GET method
 */
function main()
{
   var id = args.id;
   if (id == null)
   {
      status.code = 400;
      status.message = "Sub-system name not been provided.";
      status.redirect = true;
      return;
   }
   
   var bean = "Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=" + id;
   if (jmx.queryMBeans(bean).length === 0)
   {
      status.code = 400;
      status.message = "Unknown authentication ID provided: " + id;
      status.redirect = true;
      return;
   }
   
   model.attributes = Admin.getMBeanAttributes(
      bean, ["passthru.authentication.useLocalServer","passthru.authentication.guestAccess",
             "passthru.authentication.authenticateFTP","passthru.authentication.domain",
             "passthru.authentication.protocolOrder","passthru.authentication.offlineCheckInterval",
             "ntlm.authentication.mapUnknownUserToGuest","passthru.authentication.defaultAdministratorUserNames",
             "passthru.authentication.servers","passthru.authentication.connectTimeout"]
   );
}

main();