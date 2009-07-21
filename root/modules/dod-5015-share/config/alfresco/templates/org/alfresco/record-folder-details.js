<import resource="classpath:/alfresco/templates/org/alfresco/folder-details.js">

function setNextDispositionAction()
{
   // Request the disposition actions
   var nodeRef = page.url.args.nodeRef.replace(":/", "");

   // Call the repo to create the site
   var scriptRemoteConnector = remote.connect("alfresco");
   var repoResponse = scriptRemoteConnector.get("/api/node/" + nodeRef + "/nextdispositionaction");
   if (repoResponse.status == 401)
   {
      status.setCode(repoResponse.status, "error.loggedOut");
      return;
   }
   else if(repoResponse.status == 404)
   {
      model.nextDispositionAction = "";
      return;
   }
   else
   {
      var repoJSON = eval('(' + repoResponse + ')');
      model.nextDispositionAction = repoJSON.data;
   }
}

setNextDispositionAction();