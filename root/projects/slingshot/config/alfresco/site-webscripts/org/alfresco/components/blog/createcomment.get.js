<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogpost.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/comments.lib.js">

function main()
{
   model.showComponent = false;
      
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
   
   // don't show the component if this is a draft post
   if (item.isDraft)
   {
      return;
   }
    
   // the nodeRef is all that the component actually needs.
   // Could therefore be generalized
   model.nodeRef = item.nodeRef;
   model.showComponent = true;
   model.site = site;
   model.container = container;
   model.itemTitle = item.title;
   model.itemName = item.name;
}

main();
