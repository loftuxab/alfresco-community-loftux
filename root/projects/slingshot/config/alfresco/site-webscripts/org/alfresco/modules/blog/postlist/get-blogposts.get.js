<import resource="classpath:alfresco/site-webscripts/org/alfresco/paginationutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogposts.lib.js">

function main()
{
    // gather all required data
    var site = args["site"];
    var container = "blog";
    var path = "";
    var filter = args["filter"];
    var tag = "";
    var fromDate = args["fromDate"];
    var toDate = args["toDate"];
    var paginationData = fetchPaginationDataFromRequest(0, 10);

    // fetch the data
    fetchAndAssignBlogPosts(site, container, path, filter, tag, fromDate, toDate, paginationData);

    // assignadditional model data
    model.site = site;
    model.viewmode = (args["viewmode"] != undefined) ? args["viewmode"] : "details";
    model.listTitle = getListTitle(filter, tag, fromDate, toDate);
}

main();
