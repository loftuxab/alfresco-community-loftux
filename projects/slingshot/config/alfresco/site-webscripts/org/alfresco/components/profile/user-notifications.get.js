/**
 * User Profile Component - User Notifications GET method
 */

function main()
{
   // Call the repo to retrieve user properties
   var emailFeedDisabled = false;
   var result = remote.call("/api/people/" + encodeURIComponent(user.id));
   if (result.status == 200)
   {
      var person = JSON.parse(result);
      // we are interested in the "cm:emailFeedDisabled" property
      emailFeedDisabled = person.emailFeedDisabled;
   }
   
    // User may not have set preference, set default according to optin policy
   if(!emailFeedDisabled)
   {
      var settings = config.scoped["Notifications"]["settings"], optIn='true';
      if(settings)
      {
         optIn = (settings.getChild("optin").value + '').toLowerCase();
      }

      if (optIn === 'false')
      {
         emailFeedDisabled = false;
      }
      else
      {
         emailFeedDisabled = true;
      }
   }
   
   model.emailFeedDisabled = (emailFeedDisabled === true) ? true : false;
   
   // Widget instantiation metadata...
   var userNotification = {
      id : "UserNotifications", 
      name : "Alfresco.UserNotifications"
   };
   model.widgets = [userNotification];
}

main();

