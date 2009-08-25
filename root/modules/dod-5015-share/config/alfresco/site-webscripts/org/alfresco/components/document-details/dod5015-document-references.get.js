function getDocReferences()
{
// http://localhost:8080/alfresco/service/api/node/workspace/SpacesStore/58bafca1-69d8-469a-8de7-b274b911d8a3/customreferences
   var nodeRef = page.url.args.nodeRef.replace(":/", "");
   var result = remote.call("/api/node/"+nodeRef+"/customreferences");
   if (result.status == 200)
   {
      var data = eval('(' + result.data.customReferences + ')');
      return data.data.customReferences
   }
   else {
      return []
   }
}

model.references = getDocReferences();