<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Sync trigger method
 */
function main()
{
   var mbeanName = "Alfresco:Name=Schedule,Group=DEFAULT,Type=MonitoredCronTrigger,Trigger=syncTrigger";
   var mbean = Admin.getMBean(mbeanName);
   
   model.success = false;
   
   if (!mbean)
   {
      status.code = 500;
      status.message = "MBean not found: " + mbeanName;
      status.redirect = true;
      return;
   }
   
   try
   {
      mbean.operations.executeNow();
      model.success = true;
   }
   catch (e)
   {
      model.success = false;
      
      if (e.javaException != null)
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