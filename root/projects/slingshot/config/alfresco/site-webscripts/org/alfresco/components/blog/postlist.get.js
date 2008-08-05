<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogposts.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/paginationutils.js">


function main()
{
   // template arguments
   var site = page.url.templateArgs.site;
   
   // template properties
   var container = template.properties["container"] != undefined ? template.properties["container"] : "blog";
   var path = template.properties["path"] != undefined ? template.properties["path"] : "";
   
   // url arguments
   var filter = page.url.args["filter"] != undefined ? page.url.args["filter"] : "";
   var fromDate = page.url.args["fromDate"] != undefined ? page.url.args["fromDate"] : "";
   var toDate = page.url.args["toDate"] != undefined ? page.url.args["toDate"] : "";
   var tag = page.url.args["tag"] != undefined ? page.url.args["tag"] : "";
   var paginationData = fetchPaginationDataFromPageRequest(0, 10);

   // new posts should be the default filter
   if (filter == "" && fromDate == "" && toDate == "" && tag == "")
   {
      filter = "new";
   }

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
      model.fromDateInt = fromDate;
   }
   else
   {
      model.fromDate = "";
   }
}

main();