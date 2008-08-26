<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function main()
{
   // make sure we don't redirect by default
   model.doRedirect = false;
    
   // fetch the user information from the url
   var inviteId = page.url.args.inviteId;
   var inviteTicket = page.url.args.inviteTicket;
   if ((inviteId == undefined) || (inviteTicket == undefined))
   {
      model.error = "Parameters missing!";
      return;
   }
             
   // do invite request and redirect if it succeedes, show error otherwise
   var url = '/api/invite/' + inviteId + '/' + inviteTicket + '/accept';
   var connector = remote.connect("alfresco");
   var result = connector.put(url, "{}", "application/json");
   if (result.status != status.STATUS_OK)
   {
      model.doRedirect = false;
      var json = eval('(' + result.response + ')');
      model.error = json.message; // result.response;
   }
   else
   {
      // redirect to the site dashboard
      model.doRedirect = true;
      var data = eval('(' + result.response + ')');
      model.siteShortName = data.siteShortName;
   }
}

main();