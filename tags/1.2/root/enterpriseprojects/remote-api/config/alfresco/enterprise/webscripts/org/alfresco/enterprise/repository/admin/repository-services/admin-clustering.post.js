<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Repository Server Clustering POST method
 */
function main()
{

   var mbean = Admin.getMBean("Alfresco:Name=Cluster,Tool=Admin");

   try
   {
      var content = jsonUtils.toObject(requestbody.content);
      var hostIP = content["hostIP"];
      var port = content["port"];
      
      if(hostIP.length > 0 && port.length > 0)
      {
         mbean.operations.deregisterNonClusteredServer(hostIP, port);
         model.success = true;
      }
      else
      {
         model.success = false;
         model.error = "badrequest";
      }
   }
   catch(e)
   {
      model.success = false;

      if(e.javaException != null)
      {
         model.error = e.javaException.message + "\n";
      }
      else
      {
         model.error = "seelog";
      }
   }
}

main();