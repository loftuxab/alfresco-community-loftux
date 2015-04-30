<import resource="classpath:/alfresco/site-webscripts/org/alfresco/cloud/core/components/account/include/passwords.lib.js">

function main()
{
   // Parse mandatory page parameters
   var id = args.pid;
   var key = args.key;
   if (!id || !key)
   {
      model.error = "invalid-reset-url";
      return;
   }

   // Provide url to the current page to allow user to logout and revisit the current page
   model.postLogoutRedirectPage = "/page/reset-password&redirectURLQueryKey=key&redirectURLQueryValue=" + encodeURIComponent(key) + "&redirectURLQueryKey=id&redirectURLQueryValue=" + encodeURIComponent(id);

   // Get the signup details from the repo
   var resetUrl = "/internal/cloud/users/passwords/resetrequests/" + encodeURIComponent(id) + "?key=" + encodeURIComponent(key),
      result = remote.connect("alfresco-noauth").get(resetUrl);
   if (result.status != 200)
   {
      // The signup could not be loaded form the repo, display error
      model.error = "invalid-reset";
      return;
   }

   var reset = JSON.parse(result);
   model.username = reset.username;
   if (user && !user.isGuest && user.id != reset.username)
   {
      // A logged in user has clicked a reset link for another user
      model.error = "wrong-reset-user";
      return;
   }

   // Load password minimum length and other requirements from scoped config
   model.passwordPolicy = getPasswordPolicy(getUserNetworkId(reset.username));
}

main();
