function main()
{
   for (var i = 0; i < model.linkButtons.length; i++)
   {
      if (model.linkButtons[i].id == "login")
      {
         // Make sure we strip out the tenant id from the url
         model.linkButtons[i].href = url.context.match("/[^/]+")[0];
         model.linkButtons[i].cssClass = "quickshare-header-login-link";
      }
      else if(model.linkButtons[i].id == "document-details")
      {
         // Make sure we strip out the tenant id from the url
         model.linkButtons[i].href = url.context.match("/[^/]+")[0] + "/-default-/page/quickshare-redirect?id=" + encodeURIComponent(args.shareId);
      }

   }

}

main();