function main()
{
   var accountUrl = "/internal/cloud/domains/" + context.attributes["org.alfresco.cloud.tenant.name"] + "/account",
       result = remote.connect("alfresco").get(accountUrl);
   if (result.status == 200)
   {
      model.account = JSON.parse(result).data;
   }
   else
   {
      model.message = "noAccount";
   }
}

main();