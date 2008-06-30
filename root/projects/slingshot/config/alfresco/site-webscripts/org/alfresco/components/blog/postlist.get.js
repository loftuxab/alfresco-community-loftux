<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogposts.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/paginationutils.js">


function main()
{
    // gather all required data
    var site = page.url.args["site"];
    var container = "blog";
    var path = "";
    var filter = page.url.args["filter"];
    var tag = "";
    var fromDate = page.url.args["fromDate"];
    var toDate = page.url.args["toDate"];
    var paginationData = fetchPaginationDataFromPageRequest(0, 10);

    // fetch the data
    fetchAndAssignBlogPosts(site, container, path, filter, tag, fromDate, toDate, paginationData);

    // assignadditional model data
    model.site = site;
    model.viewmode = (page.url.args["viewmode"] != undefined) ? page.url.args["viewmode"] : "details";
    model.listTitle = getListTitle(filter, tag, fromDate, toDate);
}

main();
