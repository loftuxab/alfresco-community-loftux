<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogpost.lib.js">

function main()
{
   // fetch required datadata
   var site = page.url.templateArgs.site;
   var container = getTemplateParam("container", "blog");
   var path = getTemplateParam("path", "");
   var postId = getPageUrlParam("postId", null);
   
   // fetch post if defined
   if (postId != null)
   {
      // fetch post
      fetchAndAssignPost(site, container, postId)
   }
    
   // assign additional model data
   model.site = site;
   model.container = container;
   model.path = path;
}

main();
