<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Repository Server Clustering Test POST method
 */
function main()
{
   var clusterAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=Cluster,Tool=Admin",
         ["ClusteringEnabled", "NumClusterMembers"]
      );
   
   if(clusterAttributes["ClusteringEnabled"].value == true && clusterAttributes["NumClusterMembers"].value > 1)
   {
      model.status = "table";
      
      var clusterMemberPairs = Admin.getTabularDataAttributes(
            "Alfresco:Name=Cluster,Tool=QuickCheck",
            "ClusterMemberPairs",
            ["Node 1", "Node 2", "Node Pair Working?"]
         );
      
      var clusterMembers = Admin.getTabularDataAttributes(
            "Alfresco:Name=Cluster,Tool=Admin",
            "ClusterMembers",
            ["host.name","host.port","host.ip"]
         );
   
      var nodeIDs = [];
      var nodeNamePairs = {};
      
      for(var x = 0; x < clusterMembers.length; x++)
      {
         nodeIDs[x] = clusterMembers[x]["host.ip"].value + ":" + clusterMembers[x]["host.port"].value;
         nodeNamePairs[nodeIDs[x]] = clusterMembers[x]["host.name"].value + ":" + clusterMembers[x]["host.port"].value;
      }
      
      var results = [];
      for(var x = 0; x < clusterMemberPairs.length; x++)
      {
         var pairTest = clusterMemberPairs[x];
         var node1json = jsonUtils.toObject(pairTest["Node 1"].value);
         var node2json = jsonUtils.toObject(pairTest["Node 2"].value);
         var node1Id = node1json["ipAddress"] + ":" + node1json["port"];
         var node2Id = node2json["ipAddress"] + ":" + node2json["port"];
         var result = pairTest["Node Pair Working?"].value;
   
         results.push({
               "node1": node1Id,
               "node2": node2Id,
               "result": (result?"success":"failure")
            });
         results.push({
               "node1": node2Id,
               "node2": node1Id,
               "result": (result?"success":"failure")
            });
      }
      
      model.nodeNames = [];
      for(var x = 0; x < nodeIDs.length; x++)
      {
         model.nodeNames[x] = nodeNamePairs[nodeIDs[x]];
      }
   
      model.validation = [];
      for(var y = 0; y < clusterMembers.length; y++)
      {
         model.validation[y] = [];
         for(var x = 0; x < clusterMembers.length; x++)
         {
            model.validation[y][x] = (x == y ? "none" : "failure");
         }
      }
      
      for(var i = 0; i < results.length; i++)
      {
         var result = results[i];
         var posY = nodeIDs.indexOf(result["node1"]);
         var posX = nodeIDs.indexOf(result["node2"]);
         var working = result["result"];
         
         if(posY > -1 && posX > -1)
         {
            model.validation[posY][posX] = working;
         }
      }
   }
   else
   {
      model.status = "message"; 
      if(clusterAttributes["ClusteringEnabled"].value != true)
      {
         model.message = msg.get("clustering-test.message.notenabled");
      }   
      else if(clusterAttributes["NumClusterMembers"].value <= 1)
      {
         model.message = msg.get("clustering-test.message.onenode");
      }
      else
      {
         model.message = "error";
      }
   }
   
   model.tools = Admin.getConsoleTools("admin-clustering-test");
   model.metadata = Admin.getServerMetaData();
}

main();