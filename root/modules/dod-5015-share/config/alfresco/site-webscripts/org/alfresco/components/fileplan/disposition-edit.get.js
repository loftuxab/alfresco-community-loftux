/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Call the repo to create the site
   var scriptRemoteConnector = remote.connect("alfresco");
   var repoResponse = scriptRemoteConnector.get("/api/rma/admin/listofvalues");
   if (repoResponse.status == 401)
   {
      status.setCode(repoResponse.status, "error.loggedOut");
      return;
   }
   else
   {
      var repoJSON = eval('(' + repoResponse + ')');

      // Check if we got a positive result
      if (repoJSON.data)
      {
         var data = repoJSON.data;
         if(data && data.dispositionActions)
         {
            model.dispositionActions = data.dispositionActions.items;
         }
         if(data && data.events)
         {
            model.events = data.events.items;
         }
         if(data && data.periodTypes)
         {
            model.periodTypes = data.periodTypes.items;
         }
         if(data && data.periodProperties)
         {
            model.periodProperties = data.periodProperties.items;
         }
      }
      else if (repoJSON.status.code)
      {
         status.setCode(repoJSON.status.code, repoJSON.message);
         return;
      }
   }

}

main();