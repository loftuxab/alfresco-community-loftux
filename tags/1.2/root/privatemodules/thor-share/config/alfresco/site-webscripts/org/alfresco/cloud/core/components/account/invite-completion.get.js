<import resource="classpath:/alfresco/site-webscripts/org/alfresco/cloud/core/components/account/include/passwords.lib.js">

function main()
{
   // Parse mandatory page parameters
   var id = args.pid;
   var key = args.key;
   if (!id || !key)
   {
      model.error = "invalid-invite-url";
      return;
   }

   // Get the invite details from the repo
   var inviteUrl = "/internal/cloud/site-invitations/" + encodeURIComponent(id) + "?key=" + encodeURIComponent(key),
      result = remote.connect("alfresco-noauth").get(inviteUrl);
   if (result.status != 200)
   {
      // The invite could not be loaded form the repo, display error
      model.error = "invalid-site-invite";
      return;
   }

   var invite = JSON.parse(result);
   model.inviterName = invite.inviterFirstName + " " + invite.inviterLastName;
   model.inviteSiteTitle = invite.siteTitle;
   model.username = invite.inviteeEmail;

   // Load password minimum length and other requirements from scoped config
   model.passwordPolicy = getPasswordPolicy(getUserNetworkId(model.username));

   // The URLContext includes the authenticated users home tenant, since we wish to construct
   // URLs that map to different tenants we need to remove the tenant from the URLContext to
   // create an application context. This will then be appended with each tenant as required.
   var currentTenantContext = url.context;
   var tenantIndex = currentTenantContext.lastIndexOf('/');
   var applicationContext = currentTenantContext.substring(0, tenantIndex);
   model.applicationContext = applicationContext;
   model.tenantContext = applicationContext + "/" + invite.siteTenantId;

   // Provide url to the current page to allow user to logout and revisit the current page
   model.postLogoutRedirectPage = currentTenantContext + "/page/invitation&redirectURLQueryKey=key&redirectURLQueryValue=" + encodeURIComponent(key) + "&redirectURLQueryKey=id&redirectURLQueryValue=" + encodeURIComponent(id);
   model.currentPage = currentTenantContext + "/page/invitation?key=" + encodeURIComponent(key) + "&id=" + encodeURIComponent(id);
   if (args.reject == "true")
   {
      model.postLogoutRedirectPage += "&redirectURLQueryKey=reject&redirectURLQueryValue=true";
      model.currentPage += "&reject=true";
   }

   if (user && !user.isGuest && user.id != invite.inviteeEmail)
   {
      // A logged in user has clicked an invite link for another user
      model.error = "wrong-site-invite-user";
      return;
   }
   
   // Is the user accepting or rejecting the invite?
   var userInviteResponse = (args.reject || "false") == "true" ? "reject" : "accept";

   if (invite.inviteeIsActivated || userInviteResponse == "reject")
   {
      // User already exist OR is rejecting the invite
      var inviteResponseUrl = "/internal/cloud/site-invitations/" + id + "/responses",
         inviteBody = jsonUtils.toJSONString({ response: userInviteResponse, key: key }),
         result = remote.connect("alfresco-noauth").post(inviteResponseUrl, inviteBody, "application/json");
      if (result.status == 200)
      {
         if (userInviteResponse == "reject")
         {
            // Reject worked, display success notification
            model.notification = "reject-site-invite-success";
            return;
         }
         else
         {
            // Accept worked...
            if (user.id == invite.inviteeEmail)
            {
               // Lets display the site's dashboard since the user is logged in
               model.redirect = "/page/site/" + invite.siteShortName + "/dashboard?refreshMetadata=true";
               model.notification = "accept-site-invite-success-redirect";
               return;
            }
            else
            {
               // Let's display login form
               model.startpage = "/page/site/" + encodeURIComponent(invite.siteShortName) + "/dashboard";
               model.welcome = "site-invite-login";
               model.loginForm = {};
               return;
            }
         }
      }
      else
      {
         // Reject/Accept failed, display error message
         model.error = userInviteResponse + "-site-invite-failure";
         return;
      }
   }
   else
   {
      if (args["alt-email"])
      {
         // User does not exist for the email used in the invite, but user wants to accept using another already existing user, let the ui handle the accept
         model.startpage = "/page/site/" + encodeURIComponent(invite.siteShortName) + "/dashboard";
         model.welcome = "accept-alt-email";
         model.loginWithActionForm =
         {
            action: "internal/cloud/site-invitations/" + encodeURIComponent(invite.id) + "/responses",
            hidden:
            {
               key: invite.key,
               response: "accept-alt-email"
            },
            usernameControl: "input",
            usernameName: "alt-email"
         };
         return;
      }
      else
      {
         // User doesn't exist but has accepted the invite, let the ui handle the accept
         var passwordHelpLabel = "label.passwordHelp";

         // First look if its a saml enabled tenant
         result = remote.connect("alfresco-noauth").get("/internal/saml/enabled/user/" + encodeURIComponent(invite.inviteeEmail));
         if (result.status == 200)
         {
            var response = JSON.parse(result);
            if (response.isSamlEnabled)
            {
               passwordHelpLabel += ".saml";

               // Redirect user to SAML log in page if they've not already come from the IDP (CLOUD-1371)
               var idpIndicator = page.url.args["idp"];

               if (idpIndicator != "true")
               {
                  // redirect user to log in page
                  model.instantRedirect = model.applicationContext + "/"
                        + invite.inviteeEmail.substring(invite.inviteeEmail.indexOf("@") + 1) + "?page="
                        + encodeURIComponent(model.currentPage.substring((currentTenantContext + "/page/").length));
               }
            }
         }

         // Define the form
         model.startpage = "/page/site/" + encodeURIComponent(invite.siteShortName) + "/dashboard";
         model.welcome = "site-invite-profile";
         model.profileForm =
         {
            action: "internal/cloud/site-invitations/" + encodeURIComponent(invite.id) + "/responses",
            hidden:
            {
               key: invite.key,
               response: "accept"
            },
            passwordHelp: msg.get(passwordHelpLabel)
         };
         return;
      }
   }
}

main();
