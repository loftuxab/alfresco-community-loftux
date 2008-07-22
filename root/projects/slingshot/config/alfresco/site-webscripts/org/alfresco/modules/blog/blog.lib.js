<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function getBlogRequestUrl(site, container)
{
    var url = "/api/blog/site/" + site + "/" + container;
    return url;
}

function fetchBlog(site, container)
{
    var url = getBlogRequestUrl(site, container);
    var data = doGetCall(url);
    if (data === null)
    {
        return null;
    }
    return data;
}

function fetchAndAssignBlog(site, container)
{
    var data = fetchBlog(site, container);
    if (data === null)
    {
        return;
    }
    applyDataToModel(data);
}
