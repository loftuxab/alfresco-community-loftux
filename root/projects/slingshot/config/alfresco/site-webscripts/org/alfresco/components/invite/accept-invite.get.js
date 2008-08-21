<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function main()
{
   // fetch the user information from the url
   var inviteTicket = page.url.args.inviteTicket;
   var inviteId = page.url.args.inviteId;
   var inviteeUserName = page.url.args.inviteeUserName;
   var siteShortName = page.url.args.siteShortName;

   if ((inviteTicket == undefined) || (inviteId == undefined) ||
       (inviteeUserName == undefined) || (siteShortName == undefined))
   {
      model.error = "Parameters missing!";
      return;
   }
   
   var url = '/api/inviteresponse/accept' +
             '?inviteId=' + inviteId +
             '&inviteeUserName=' + inviteeUserName +
             '&siteShortName=' + siteShortName +
             '&inviteTicket=' + inviteTicket;
             
   // do invite request and redirect if it succeedes, show error otherwise
   var connector = remote.connect("alfresco");
   var result = connector.get(url);
   if (result.status != status.STATUS_OK)
   {
      var json = eval('(' + result.response + ')');
      model.error = json.message; // result.response;
   }
   else
   {
      // redirect to the site dashboard
      status.setLocation('/page/site/' + siteShortName + '/dashboard');
      status.setRedirect(true);
   }
}

main();