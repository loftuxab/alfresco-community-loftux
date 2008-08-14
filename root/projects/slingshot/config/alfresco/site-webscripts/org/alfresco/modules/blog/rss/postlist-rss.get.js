function convertPostJSONData(post)
{
   // created
   var created = new Date(post["createdOn"])
   post["createdOn"] = created;
   
   // modified
   if(post["modifiedOn"] != undefined)
   {
      var modified = new Date(post["modifiedOn"]);
      post["modifiedOn"] = created;
   }
   // released
   if (post["releasedOn"] != undefined)
   {
      post["releasedOn"] = new Date(post["releasedOn"]);
   }
   // updated
   if (post["updatedOn"] != undefined)
   {
       post["updatedOn"] = new Date(post["updatedOn"]);
   }
   // last comment
   if(post["lastCommentOn"] != undefined)
   {
      post["lastCommentOn"] = new Date(post["lastCommentOn"])
   }
}

/**
 * Converts the data object from strings to the proper types
 * (currently this only handles strings
 */
function convertPostsJSONData(data)
{
   for(var x=0; x < data.items.length; x++)
   {
      convertPostJSONData(data.items[x]);
   }
}

function main()
{
   // gather all required data
   var site = args["site"];
   var container = (args["container"] != undefined) ? args["container"] : "blog";
   
   var url = '/api/blog/site/' + site + '/' + container + "/posts?contentLength=512";
   
   var connector = remote.connect("alfresco");
   var result = connector.get(url);
   if (result.status != status.STATUS_OK)
   {
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to do backend call. " +
                     "status: " + result.status + ", response: " + result.response);
      return null;
   }
   var data = eval('(' + result.response + ')');
   convertPostsJSONData(data);
   model.items = data.items;

   // set additional properties
   var lang = "en-us";
   model.lang = lang;
   model.site = site;
   model.container = container;
}

main();
