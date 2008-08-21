<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function main()
{
   // check that we got all required information
   var check = ["inviteId", "inviteeUserName", "siteShortName", "inviteTicket"];
   for (var x=0; x < check.length; x++)
   {
      if (page.url.args[check[x]] == undefined)
      {
         // redirect to error page
         status.code = 400;
         status.message = "One or more parameters are missing";
         return;
      }
   }
   
   // fetch the site name the user wants to cancel
   var json = remote.call("/api/sites/" + page.url.args.siteShortName);
   var profile =
   {
      title: "",
      shortName: ""
   };

   if (json.status == 200)
   {
      // Create javascript objects from the repo response
      var obj = eval('(' + json + ')');
      if (obj)
      {
         profile = obj;
      }
   }
   model.profile = profile;
   
   // also fetch the invite information, we need the name of the inviter
   var json = remote.call("/api/invites?inviteId=jbpm$" + page.url.args.inviteId);
   if (json.status == 200)
   {
      // Create javascript objects from the repo response
      model.inviteData = eval('(' + json + ')');
   }
   else
   {
      // Inform the user that there is no invite object available
      model.error = true;
   }
}

main();
