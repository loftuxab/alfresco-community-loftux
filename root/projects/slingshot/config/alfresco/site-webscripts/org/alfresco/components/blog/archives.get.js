<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogposts.lib.js">

function main()
{
   // gather all required data
   var site = page.url.templateArgs.site;
   var container = getTemplateParam("container", "blog");
   var path = getTemplateParam("path", "");
   
   // fetch the data
   var data = fetchPostsPerMonth(site, container, path);
   if (data == null)
   {
      model.items = [];
   }
   else
   {
      var items = data.items.reverse();
      model.items = items;
   }
}

main();