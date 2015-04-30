<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Repository Server Clustering GET method
 */
function main()
{
   model.cluster = Admin.getMBeanAttributes(
         "Alfresco:Name=Cluster,Tool=Admin",
         ["ClusteringEnabled","ClusterName"]
      );
   
   if(model.cluster["ClusteringEnabled"].value == true)
   {
      model.clusterAdmin = Admin.getMBeanAttributes(
            "Alfresco:Name=Cluster,Tool=Admin",
            ["HostName","IPAddress","NumClusterMembers"]
         );
      
      model.clusterMembers = Admin.getTabularDataAttributes(
            "Alfresco:Name=Cluster,Tool=Admin",
            "ClusterMembers",
            ["host.ip","host.name","host.port","last.registered"]
         );
      
      model.offlineMembers = Admin.getTabularDataAttributes(
            "Alfresco:Name=Cluster,Tool=Admin",
            "OfflineMembers",
            ["host.ip","host.name","host.port","last.registered"]
         );
      
      model.nonClusteredServers = Admin.getTabularDataAttributes(
            "Alfresco:Name=Cluster,Tool=Admin",
            "NonClusteredServers",
            ["host.ip","host.name","host.port"]
         );
   }
   else
   {
      model.license = Admin.getMBeanAttributes(
            "Alfresco:Name=License",
            ["ClusterEnabled"]
         );
   }
   
   model.tools = Admin.getConsoleTools("admin-clustering");
   model.metadata = Admin.getServerMetaData();
}

main();