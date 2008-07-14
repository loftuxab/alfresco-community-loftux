<import resource="classpath:alfresco/site-webscripts/org/alfresco/paginationutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topics.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/paginator/paginator.js">



function main()
{
    // gather all required data
    var site = page.url.templateArgs.site;
    var container = "discussions";
    var path = "";
    var filter = page.url.args["filter"];
    var tag = page.url.args["tag"];
    var paginationData = fetchPaginationDataFromPageRequest(0, 10);

    // fetch the data
    fetchAndAssignTopics(site, container, path, filter, tag, paginationData);

    // assignadditional model data
    model.site = site;
    model.viewmode = (page.url.args["viewmode"] != undefined) ? page.url.args["viewmode"] : "details";
    model.filter = filter;
    model.tag = tag;
}

main();