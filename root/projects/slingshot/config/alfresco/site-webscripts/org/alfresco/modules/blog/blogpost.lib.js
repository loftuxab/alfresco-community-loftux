<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogpostjsondatautils.js">

function getPostRequestUrl(site, container, postId)
{
    var url = "/api/blog/post/site/" + site + "/" + container + "/" + postId;
    return url;
}

function fetchPost(site, container, postId)
{
    var url = getPostRequestUrl(site, container, postId);
    var data = doGetCall(url);
    if (data === null)
    {
        return null;
    }
    convertPostJSONData(data.item);
    return data;
}

function fetchAndAssignPost(site, container, postId)
{
    var data = fetchPost(site, container, postId);
    if (data === null)
    {
        return;
    }
    applyDataToModel(data);
}
