/**
 * List/Search groups
 */

function main ()
{
   // Get the args
   var shortNameFilter = args["shortNameFilter"];
   var zone = args["zone"];
   var sortBy = args["sortBy"];
   var sortAsc = args["dir"] != "desc";

   var paging = utils.createPaging(args);

   if (shortNameFilter == null)
   {
      shortNameFilter = "";
   }

   if (sortBy == null)
   {
      sortBy = "displayName";
   }
   //Return all users, even if APP.DEFAULT is used. Allows for use of Site groups
   if(zone == null||zone=="APP.DEFAULT")
   {
       // Do the search
       model.groups = groups.getGroupsInZone(shortNameFilter, null, paging, sortBy, sortAsc);
   }
   else
   {
       // Do the search
       model.groups = groups.getGroupsInZone(shortNameFilter, zone, paging, sortBy, sortAsc);
   }
   model.paging = paging;
}

main();