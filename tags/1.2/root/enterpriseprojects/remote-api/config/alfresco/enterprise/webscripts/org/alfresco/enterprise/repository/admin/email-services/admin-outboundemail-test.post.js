<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Outbound email POST method
 */
function main()
{
   var mbean = Admin.getMBean("Alfresco:Type=Configuration,Category=email,id1=outbound");
   model.success = false;

   try
   {
      var result = mbean.operations.sendTestMessage();

      if(result.indexOf(msg.get("email.outbound.test.send.success", [""])) == 0)
      {
         model.success = true;
         model.error = "";
      }
      else
      {
         model.success = false;
         model.error = result;
      }
   }
   catch(e)
   {
      model.success = false;

      if(e.javaException != null)
      {
         model.error = e.javaException.message + "\n";
         
         for(var i=0; i<e.javaException.stackTrace.length; i++)
         {
            model.error += "\n\tat " + e.javaException.stackTrace[i];
            
         }
      }
   }
}

main();