<import resource="classpath:/alfresco/site-webscripts/org/alfresco/cloud/core/components/account/include/passwords.lib.js">

function main()
{
   // Look if its a saml enabled tenant
   var result = remote.connect("alfresco-noauth").get("/internal/saml/enabled/user/" + encodeURIComponent(user.id));
   if (result.status == 200)
   {
      var response = JSON.parse(result);
      if (response.isSamlEnabled)
      {
         model.passwordHelpLabel = "label.passwordhelp.saml";
      }
   }

   // Load password minimum length and other requirements from scoped config
   model.passwordPolicy = getPasswordPolicy(getUserNetworkId(user.id));
}

main();
