<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogposts.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/paginationutils.js">


function main()
{
   // gather all required data
   var site = page.url.templateArgs.site;
   var container = getTemplateParam("container", "blog");
   var path = getTemplateParam("path", "");
   var filter = getPageUrlParam("filter", "");
   var tag = getPageUrlParam("tag", "");
   var fromDate = getPageUrlParam("fromDate", "");
   var toDate = getPageUrlParam("toDate", "");
   var paginationData = fetchPaginationDataFromPageRequest(0, 10);

   // fetch the data
   fetchAndAssignBlogPosts(site, container, path, filter, tag, fromDate, toDate, paginationData);

   // assignadditional model data
   model.site = site;
   model.viewmode = (page.url.args["viewmode"] != undefined) ? page.url.args["viewmode"] : "details";
   model.filter = filter;
   model.tag = tag;
   if (fromDate.length > 0)
   {
      fromDate = parseInt(fromDate);
      model.fromDate = new Date(fromDate);
   }
   else
   {
      model.fromDate = "";
   }
}

main();