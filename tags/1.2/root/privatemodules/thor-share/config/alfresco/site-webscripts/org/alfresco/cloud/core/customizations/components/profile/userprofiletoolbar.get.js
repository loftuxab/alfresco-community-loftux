/**
 * User Profile - Toolbar Component GET method - Cloud extension
 */
function extension()
{
   var profileId = page.url.templateArgs["userid"],
      profile;
   if (profileId && profileId != user.name)
   {
      // load user details for the profile from the repo
      profile = user.getUser(profileId);
   }

   // displaying a user profile that is not our own
   if (profile)
   {
      // check which network the user is in compared to ours
      // we also check for "null" homeTenant which is a public user
      var homeTenant = user.properties["homeTenant"],
          profileHomeTenant = profile.properties["homeTenant"];
      if (model.activeUserProfile && !homeTenant)
      {
         // find and remove the "following" links as they should not be visible for a public user
         for (var i=0; i<model.links.length; i++)
         {
            switch (model.links[i].id)
            {
               case "following-link":
               case "followers-link":
                  model.links.splice(i--, 1);
                  break;
            }
         }
      }
      if (!homeTenant || !profileHomeTenant || homeTenant != profileHomeTenant)
      {
         // find and remove the "others following" link as they should not be visible to this user
         for (var i=0; i<model.links.length; i++)
         {
            if (model.links[i].id == "otherfollowing-link")
            {
               model.links.splice(i, 1);
               break;
            }
         }
      }
   }

   // Add change language link if the user is allowed.
   if (model.activeUserProfile)
   {
      if (user.capabilities.isMutable)
      {
         // Add Change Locale link
         model.links.push(
            {
               id: "change-locale-link",
               href: "change-locale",
               cssClass: (model.activePage == "change-locale") ? "theme-color-4" : null,
               label: msg.get("link.changelocale", null)
            });
      }
   }

   // Remove "on premise" "user cloud auth" page
   for (var i=0; i<model.links.length; i++)
   {
      if (model.links[i].id == "user-cloud-auth-link")
      {
         model.links.splice(i, 1);
         break;
      }
   }
}

extension();