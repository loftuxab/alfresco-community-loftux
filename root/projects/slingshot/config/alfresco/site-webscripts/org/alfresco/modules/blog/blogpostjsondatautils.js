
function convertPostsPerMonthJSONData(data)
{
   for (var x = 0; x < data.items.length; x++)
   {
      var item = data.items[x];
      item.beginOfMonth = new Date(item.beginOfMonth);
      // not necessary right now item.endOfMonth = new Date(item.endOfMonth)
   }

}

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

function convertCommentsJSONData(data)
{
   for (var x=0; x < data.items.length; x++)
   {
      convertComment(data.items[x]);
   }
}

/** Converts a comment and if available recursively its children. */
function convertComment(post)
{
   convertPostJSONData(post);
   if (post["children"] != undefined)
   {
      var children = post["children"];
      for(var x=0; x < children.length; x++)
      {
         convertComment(children[x]);
      }
   }
}
