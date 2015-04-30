<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Repository Server Clustering Test GET method
 */
function main()
{
   var mbean = Admin.getMBean("Alfresco:Name=Cluster,Tool=QuickCheck");
   mbean.operations.checkCluster();
}

main();