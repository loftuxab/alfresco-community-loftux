// Get the Documents Modified data for this site
var json = remote.call("/slingshot/doclib/doclist/documents/node/alfresco/sites/home?filter=editingMe&max=50");

if (json.status == 200)
{
   // Create the model
   var docs = eval('(' + json + ')');
   model.docs = docs;
}
else
{
   model.docs =
   {
      message: "label.error"
   }
}
