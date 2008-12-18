function main()
{
   // A default blog description
   var blog = 
   {
      permissions: {}
   };

   // Call the repo to get the permissions for the user for this blog
   var result = remote.call("/api/blog/site/" + page.url.templateArgs.site + "/" + (args.container ? args.container : "blog"));
   if (result.status == 200)
   {
      // Create javascript objects from the server response
      var obj = eval('(' + result + ')');
      blog = obj.item;
   }

   // Prepare the model for the template
   model.blog = blog;
}

main();