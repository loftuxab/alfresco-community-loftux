<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Apply License POST method
 */
function main()
{
   var mbean = Admin.getMBean("Alfresco:Name=License");
   
   model.message = mbean.operations.loadLicense();
}

main();