function main()
{
   var idpSessionIndex = args.idpSessionIndex;

   // Get the IDP details for the current network
   // Note! Assumes the user is primary to current network, meaning that user should be directed to this page
   // with the users home tenant in the url context.

   var idpurl = "/internal/saml/slo/" + encodeURIComponent(idpSessionIndex),
      result = remote.connect("alfresco").get(idpurl);
   if (result.status == 200)
   {
      model.idp = JSON.parse(result);
   }
   else
   {
      model.error = result.status;
   }
}

main();
