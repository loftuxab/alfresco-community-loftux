<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Edit Kerberos GET method
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
      bean, ["kerberos.authentication.user.configEntryName","kerberos.authentication.cifs.configEntryName",
             "kerberos.authentication.cifs.password","kerberos.authentication.stripUsernameSuffix",
             "kerberos.authentication.defaultAdministratorUserNames","kerberos.authentication.realm",
             "kerberos.authentication.http.configEntryName","kerberos.authentication.http.password",
             "kerberos.authentication.authenticateFTP"]
   );
}

main();