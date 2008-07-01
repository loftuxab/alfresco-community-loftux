<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogposts.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/paginationutils.js">

function main()
{
    // gather all required data
    var site = args["site"];
    var container = (args["container"] != undefined) ? args["container"] : "blog";
    var path = (args["path"] != undefined) ? args["path"] : "";

    var paginationData = fetchPaginationDataFromRequest(0, 10);
    
    // fetch the data
    fetchAndAssignBlogPosts(site, container, path, "" /*filter*/, "" /*tag*/, null, null, paginationData);

    // set additional properties
    // PENDING: where to get this information?
    var lang = "en-us";
    model.lang = lang;
    model.site = site;
    model.container = container;
}

main();
