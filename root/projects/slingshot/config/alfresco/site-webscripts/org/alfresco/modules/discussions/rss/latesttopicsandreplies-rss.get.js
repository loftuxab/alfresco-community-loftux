<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topics.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/paginationutils.js">

function getLatestPostsRequestUrl(site, container, path)
{
    var url = "/forum/site/" + site + "/" + container;
    if (path != null && path.length > 0)
    {
        url += "/" + path ;
    }
    url += "/latestposts";
    url += "?contentFormat=textDigest";
    url += "&startIndex=0&pageSize=20";
    return url;
}

function main()
{
    // gather all required data
    var site = args["site"];
    var container = (args["container"] != undefined) ? args["container"] : "discussions";
    var path = (args["path"] != undefined) ? args["path"] : "";

    // fetch the data.    
    var url = getLatestPostsRequestUrl(site, container, path);
    var data = doGetCall(url);
    if (data != null)
    {
        convertTopicsJSONData(data);
        applyDataToModel(data);
    }

    // set additional properties
    // PENDING: where to get this information?
    var lang = "en-us";
    model.lang = lang;
    model.site = site;
    model.container = container;
}

main();
