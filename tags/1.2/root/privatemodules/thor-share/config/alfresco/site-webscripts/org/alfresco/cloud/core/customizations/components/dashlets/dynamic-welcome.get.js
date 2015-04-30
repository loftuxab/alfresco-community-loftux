/**
 * Dynamic Welcome Component GET method
 */
for (var i = 0; i < model.columns.length; i++)
{
   if (model.columns[i])
   {
      if (model.columns[i].actionId == "-invite-button")
      {
         // Replace the "original invite page link" with a "cloud invite dialog link"
         model.columns[i].actionId = "-cloud-invite-button";
         model.columns[i].actionHref = "#";
         model.columns[i].actionMsg = "cloud.welcome.user.invite.link";
      }
      else if (model.columns[i].actionId == "-createSite-button")
      {
         // Only internal users are allowed to create sites
         if (user.properties["isExternal"])
         {
            model.columns[i] =
            {
               title: "cloud.welcome.user.sitesForExternalUser.title",
               description: "cloud.welcome.user.sitesForExternalUser.description",
               imageUrl: "/res/components/images/help-site-bw-64.png",
               actionMsg: "cloud.welcome.user.sitesForExternalUser.link",
               actionHref: page.url.context + "/page/site-finder",
               actionId: null,
               actionTarget: null
            };
         }
      }
      else if (model.columns[i].title == "welcome.cloud.sign-up.title")
      {
         model.columns[i] = {
            title: "welcome.user.dashboard.title",
            description: "welcome.user.dashboard.description",
            imageUrl: "/res/components/images/help-dashboard-bw-64.png",
            actionMsg: "welcome.user.dashboard.link",
            actionHref: page.url.context + "/page/user/customise-user-dashboard",
            actionId: null,
            actionTarget: null
         };
      }
   }
}
