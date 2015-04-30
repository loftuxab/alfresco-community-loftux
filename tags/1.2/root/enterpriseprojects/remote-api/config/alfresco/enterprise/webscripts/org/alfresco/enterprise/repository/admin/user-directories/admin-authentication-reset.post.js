<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 *
 * Authentication Subsystem Reset Post
 */
function main()
{
   var content = jsonUtils.toObject(requestbody.content);
   var authenticatorName = content["authenticatorName"];

   var mbean = Admin.getMBean("Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=" + authenticatorName);
   if (jmx.queryMBeans(mbean).length === 0)
   {
      status.code = 400;
      status.message = "Unknown authentication ID provided: " + authenticatorName;
      status.redirect = true;
      return;
   }

   mbean.operations.revert();
}

main();