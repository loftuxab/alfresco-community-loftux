<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Repository Information GET method
 */
function main()
{
   // Current repository information
   model.currentAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=RepositoryDescriptor,Type=Current",
         ["Id","VersionNumber","VersionLabel","Schema","VersionBuild"]
      );

   // Initial repository information
   model.initialAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=RepositoryDescriptor,Type=Initially Installed",
         ["Id","VersionNumber","VersionLabel","Schema","VersionBuild"]
      );
   
   model.tools = Admin.getConsoleTools("admin-repositoryinfo");
   model.metadata = Admin.getServerMetaData();
}

main();