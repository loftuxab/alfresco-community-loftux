function toRepoType(appType)
{
   var type = "";
   switch (String(appType))
   {
      case "dod5015":
         type = "dod:filePlan";
         break;
      case "wcmqs":
    	 type = "ws:websiteContainer";
    	 break;
   }
   return type;
}

function fromRepoType(repoType)
{
   var type = "";
   switch (String(repoType))
   {
      case "dod:filePlan":
         type = "dod5015";
         break;
      case "ws:websiteContainer":
    	 type = "wcmqs";
    	 break;    		 
   }
   return type;
}

function getLocationType()
{
   // Need to know what type of node the container is
   var siteId = page.url.templateArgs.site,
      containerId = template.properties.container,
      containerType = "cm:folder",
      appType = "";

   if (siteId !== null)
   {
      var p = sitedata.getPage("site/" + siteId + "/dashboard");
      if (p != null)
      {
         pageMetadata = eval('(' + p.properties.pageMetadata + ')');
         pageMetadata = pageMetadata != null ? pageMetadata : {};
         doclibMeta = pageMetadata[page.id] || {};
         if (doclibMeta.titleId != null)
         {
            // Save the overridden page title into the request context
            context.setValue("page-titleId", doclibMeta.titleId);
         }
         appType = doclibMeta.type;
      }

      var connector = remote.connect("alfresco");
      result = connector.get("/slingshot/doclib/container/" + siteId + "/" + containerId + "?type=" + toRepoType(appType));
      if (result.status == 200)
      {
         var data = eval('(' + result + ')');
         containerType = data.container.type;
      }
   }
   
   return (
   {
      siteId: siteId,
      containerType: containerType
   });
}

var objLocation = getLocationType(),
   doclibType = fromRepoType(objLocation.containerType),
   scopeType = objLocation.siteId !== null ? "" : "repo-";

model.doclibType = doclibType == "" ? scopeType : doclibType + "-";

// Repository Library root node
var rootNode = "alfresco://company/home",
   repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
if (repoConfig !== null)
{
   rootNode = repoConfig.value;
}

model.rootNode = rootNode;