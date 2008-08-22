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
   
   //fetch the invite information, which gives us all information required to display the page
   var url = "/api/invite/" + page.url.args.inviteId + "/" + page.url.args.inviteTicket;
   var json = remote.call(url);
   if (json.status == 200)
   {
      // Create javascript objects from the repo response
      var data = eval('(' + json + ')');
      model.invite = data.invite;
   }
   else
   {
      // Inform the user that there is no invite object available
      model.error = true;
   }
}

main();
