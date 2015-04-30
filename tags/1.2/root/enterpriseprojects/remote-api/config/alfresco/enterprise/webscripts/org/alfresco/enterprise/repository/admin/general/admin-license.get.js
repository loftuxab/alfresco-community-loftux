<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * License GET method
 */
function main()
{
   //License
   model.licenseAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=License",
         ["Subject", "LicenseMode", "Issued", "Holder", "Days","Issuer", "ValidUntil", "MaxUsers", "RemainingDays", "MaxDocs", "CurrentUsers", "CurrentDocs", "HeartBeatDisabled", "CloudSyncKeyAvailable", "ClusterEnabled", "CryptodocEnabled"],
         "admin-license"
      );
   
   // MNT-10913
   // if flag "HeartBeatDisabled" is false, then heartbeat data will be sent to Alfresco.
   // Therefore we creating a new variable, that will no confused of users
   var heartBeatAttribute = model.licenseAttributes["HeartBeatDisabled"];
   if (heartBeatAttribute.value == true)
   {
      heartBeatAttribute.value = false;
   }
   else
   {
      heartBeatAttribute.value = true;
   }
   model.heartBeatAttribute = heartBeatAttribute;
   
   model.tools = Admin.getConsoleTools("admin-license");
   model.metadata = Admin.getServerMetaData();
}

main();