<import resource="classpath:/alfresco/site-webscripts/org/alfresco/cloud/core/components/account/include/passwords.lib.js">

function main()
{
   // Parse mandatory page parameters
   var id = args.pid;
   var key = args.key;
   if (!id || !key)
   {
      model.error = "invalid-signup-url";
      return;
   }
   
   var cancel = false;
   if(args.cancel) 
   {
      cancel = (args.cancel == 'true');
   }
   
   model.cancel = cancel;

   var currentTenantContext = url.context;
   var tenantIndex = currentTenantContext.lastIndexOf('/');
   var applicationContext = currentTenantContext.substring(0, tenantIndex);
   model.applicationContext = applicationContext;
   model.tenantContext = applicationContext;

   // Provide url to the current page to allow user to logout and revisit the current page
   model.postLogoutRedirectPage = currentTenantContext + "/page/activation&redirectURLQueryKey=key&redirectURLQueryValue=" + encodeURIComponent(key) + "&redirectURLQueryKey=id&redirectURLQueryValue=" + encodeURIComponent(id);
   if (cancel)
   {
      model.postLogoutRedirectPage += "&redirectURLQueryKey=cancel&redirectURLQueryValue=true";
   }

   // Get the signup details from the repo
   var signupUrl = "/internal/cloud/accounts/signupqueue/" + encodeURIComponent(id) + "?key=" + encodeURIComponent(key),
      result = remote.connect("alfresco-noauth").get(signupUrl);
   if (result.status != 200)
   {
      // The signup could not be loaded form the repo, display error
      model.error = "invalid-signup";
      return;
   }

   var signup = JSON.parse(result);
   model.username = signup.email;
   if (user && !user.isGuest && user.id != signup.email)
   {
      // A logged in user has clicked an signup link for another user
      model.error = "wrong-signup-user";
      return;
   }

   // Load password minimum length and other requirements from scoped config
   model.passwordPolicy = getPasswordPolicy(getUserNetworkId(model.username));

   if (signup.isActivated)
   {
      // User is already an active user in the system and doesn't need to fill in the profile form
      if (user.id == signup.email)
      {
         // User is logged in lets route him to the dashboard
         model.redirect = "/page/user/" + user.id + "/dashboard?refreshMetadata=true";
         return;
      }
      else
      {
         // Let user login
         model.startpage = ""; // User home
         model.welcome = "signup-login";
         model.loginForm = {};
         return;
      }
   }
   else
   {
     if (cancel)
      {
        var cancelUrl = "/internal/cloud/account-cancellation",
        inviteBody = jsonUtils.toJSONString({ id: signup.id, key: signup.key }),
        result = remote.connect("alfresco-noauth").post(cancelUrl, inviteBody, "application/json");
        if (result.status == 200)
        {
           model.instantRedirect = config.scoped["Cloud"]["cancelledSignup"].getChildValue("url");
           model.notification = "cancel.success";
           return;
        }
        else
        {
           model.error = "cancel.failure";
        }
      }
      else
      {
         // Registration is not activated yet and user doesn't want to canceling registration
         if (signup.isPreRegistered)
         {
            // User has already preregistered information when signing up.
            model.startpage = ""; // User home
            model.startpageIOS = "alfresco://activate-cloud-account/" + encodeURIComponent(signup.id);
            model.welcome = "signup-pregistered-profile";
            model.loginWithActionForm =
            {
               action: "internal/cloud/account-activations",
               hidden:
               {
                  id: signup.id,
                  key: signup.key
               },
               usernameControl: "label",
               username: signup.email
            };
            return;
         }
         else
         {
            // User is registered but hasn't completed the profile
            var passwordHelpLabel = "label.passwordHelp";

            // First look if its a saml enabled tenant
            result = remote.connect("alfresco-noauth").get("/internal/saml/enabled/user/" + encodeURIComponent(signup.email));
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
                     model.instantRedirect = currentTenantContext;
                  }

               }
           }

           // Define the form
           model.startpage = ""; // User home
           model.profileForm =
           {
              action: "internal/cloud/account-activations",
              hidden:
              {
                 id: signup.id,
                 key: signup.key
              },
              passwordHelp: msg.get(passwordHelpLabel)
           };
           if (signup.initiatorFirstName || signup.initiatorLastName)
           {
              model.inviterName = (signup.initiatorFirstName || "") + (signup.initiatorLastName ? " " + signup.initiatorLastName : "");
              model.welcome = "signup-initiated-profile";
              model.profileForm.button = "profile.button.initiated";
           }
           else
           {
              model.welcome = "signup-profile";
           }
           return;
         }
      }
   }
}

main();
