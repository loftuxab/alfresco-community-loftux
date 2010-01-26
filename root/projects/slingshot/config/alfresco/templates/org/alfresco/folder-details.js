<import resource="classpath:/alfresco/templates/org/alfresco/documentlibrary.js">

function toJSType(doclibType)
{
   var type = "Alfresco.FolderDetails";
   switch (String(doclibType))
   {
      case "dod5015":
         type = "Alfresco.RecordsFolderDetails";
         break;
   }
   return type;
}

model.jsType = toJSType(doclibType);

// Repository Library root node
var rootNode = "alfresco://company/home",
   repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
if (repoConfig !== null)
{
   rootNode = repoConfig.value;
}

model.rootNode = rootNode;