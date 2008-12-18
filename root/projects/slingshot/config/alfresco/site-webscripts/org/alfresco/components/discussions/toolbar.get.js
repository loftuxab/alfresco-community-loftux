function main()
{
   // A default blog description
   var forum = { forumPermissions: {}};

   // Call the repo to get the permissions for the user for this blog
   var result = remote.call("/api/forum/site/" + page.url.templateArgs.site + "/" + (args.container ? args.container : "discussions") + "/posts?startIndex=0&pageSize=0");
   if (result.status == 200)
   {
      // Create javascript objects from the server response
      var obj = eval('(' + result + ')');
      forum = obj;
   }

   // Prepare the model for the template
   model.forum = forum;
}

main();