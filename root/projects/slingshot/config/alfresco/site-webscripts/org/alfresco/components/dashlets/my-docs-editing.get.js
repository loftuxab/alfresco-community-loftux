function getUserContent(contentType)
{
   var uri = "";
   switch (contentType)
   {
      //docs in doclib
      case "documents":
         uri = "/slingshot/doclib/doclist/documents/node/alfresco/sites/home?filter=editingMe&max=3";
         
         break;
      //wiki pages , blog and forum posts
      case "content":
         uri = "/slingshot/dashlets/my-contents";
         break;
   }

   var json = remote.call(uri);   
   if (json.status == 200)
   {
      // Create the model
      var content = eval('(' + json + ')');
      model.src = json;
      return content;
   }
   else
   {
      model[contentType].error =
      {
         message: "label.error"
      };
   }

   return {
      items : []
   };
}

var contentTypes = ['documents','content'];
model.dataTypes = "";

for (var i = 0,len=contentTypes.length;i<len;i++)
{
   var contentType = contentTypes[i];
   model[contentType] = getUserContent(contentType);
}
