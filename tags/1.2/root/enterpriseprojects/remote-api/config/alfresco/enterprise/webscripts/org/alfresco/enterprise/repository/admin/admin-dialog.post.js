<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Dialog page POST method. Saves JMX form data and perform client-side dialog close and framework callback.
 */
function main()
{
   try
   {
      Admin.persistJMXFormData();
   }
   catch (e)
   {
      // TODO: return error message arguments as part of the callback structure
   }
   
   var callback = args.cb;
   if (callback)
   {
      // sanistise callback args - to avoid XSS
      callback = callback.replace(/[^A-Za-z0-9 ,'"]/g, "");
   }
   model.callback = callback;
}

main();