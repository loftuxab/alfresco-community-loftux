var tenant = context.attributes["org.alfresco.cloud.tenant.name"];
if (tenant != null)
{
   var result = remote.call("/internal/cloud/domains/" + tenant + "/account");
   if (result.status == 200)
   {
      var data = JSON.parse(result);
      model.fileUploadSizeLimit = "" + data.data.usageQuota.fileUploadSizeUQ.q;
   }
}