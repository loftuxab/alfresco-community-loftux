function main()
{

   // See if user shall be redirected to other Share page after login at IDP by looking at the args.page property.
   // The page will be saved as a cookie using client side js code.
   //
   // Note! We don't want to use SAML RelayState parameter for this since:
   // - The page parameter could contain sensitive information, i.e. workflow ids &keys
   // - There are recommendations not tu use the RelayState
   model.redirectPage = args.page;
   if (!model.redirectPage)
   {
      model.redirectPage = "";
   }

   // Check if user already is logged in
   if (user && !user.isGuest && user.id)
   {
      // TODO: Shall we direct to Share (redirectPage or user dashboard) directly?
   }

   // Get the current tenant/network
   var currentTenantContext = url.context,
      tenantIndex = currentTenantContext.lastIndexOf('/');
   model.applicationContext = currentTenantContext.substring(0, tenantIndex);
   model.currentTenant = currentTenantContext.substring(tenantIndex +1);


   // Look if its a saml enabled tenant
   var result = remote.connect("alfresco-noauth").get("/internal/saml/enabled/tenant/" + encodeURIComponent(model.currentTenant));
   if (result.status == 200)
   {
      var response = JSON.parse(result);
      if (response.isSamlEnabled)
      {
         // Get the IDP details for the current network
         var idpurl = "/internal/saml/sso/" + encodeURIComponent(model.currentTenant),
            result = remote.connect("alfresco-noauth").get(idpurl);
         if (result.status == 200)
         {
            model.idp = JSON.parse(result);
         }
         else
         {
            model.error = result.status;
         }
      }
      else
      {
         model.error = true;
      }
   }
   else
   {
      model.error = result.status;
   }
}

main();