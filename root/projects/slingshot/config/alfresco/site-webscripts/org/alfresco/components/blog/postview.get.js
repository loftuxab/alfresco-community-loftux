<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogpost.lib.js">

function main()
{
   // gather all required data
   var site = page.url.templateArgs.site;
   var container = getTemplateParam("container", "blog");
   var path = getTemplateParam("path", "");
   var postId = getPageUrlParam("postId", null);
    
   // check whether we already loaded the item, load it otherwise
   var item = context.properties["blog-post-item"];
   if (item == undefined)
   {
      var data = fetchPost(site, container, postId);
      if (status.getCode() != status.STATUS_OK)
      {
         return;
      }
      context.properties["blog-post-item"] = data.item;
      item = data.item;
   }
   model.item = item;

   // assign additional model data
   model.site = page.url.templateArgs.site;
   model.editMode = getPageUrlParam("edit", "false") == "true";
}

main();
