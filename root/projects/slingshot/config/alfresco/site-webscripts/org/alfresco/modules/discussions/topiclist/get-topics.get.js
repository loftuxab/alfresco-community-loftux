<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topics.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/paginationutils.js">

function main()
{
   // gather all required data
   var site = args["site"];
   var container = "discussions";
   var path = "";
   var filter = args["filter"];
   var tag = args["tag"];
   var paginationData = fetchPaginationDataFromRequest(0, 10);
   
   // fetch the data
   fetchAndAssignTopics(site, container, path, filter, tag, paginationData);
   
   // set additional template data
   model.site = site;
   model.htmlid = args["htmlid"];
   model.viewmode = (args["viewmode"] != undefined) ? args["viewmode"] : "details";
   model.filter = filter;
   model.tag = tag;
}

main();
