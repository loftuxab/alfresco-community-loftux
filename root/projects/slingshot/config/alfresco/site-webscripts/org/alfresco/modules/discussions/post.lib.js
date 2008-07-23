<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topicjsondatautils.js">

/** Converts a traditional nodeRef to a nodeRef URL of the type
 * storetype/storeid/id.
 * 
 * We have to do this as the passed nodeRef has probably been encoded.
 */
function nodeRefToUrl(nodeRef)
{
    return nodeRef.replace(/%3A/gi, ":").replace(/%2F/gi, "/").replace(/\:\/\//,"/");
}

function getPostRequestUrl(nodeRef)
{
    return "/api/forum/post/node/" + nodeRefToUrl(nodeRef);    
}

function fetchPost(nodeRef)
{
    var url = getPostRequestUrl(nodeRef);
    var data = doGetCall(url);
    if (data === null)
    {
        return null;
    }
    convertTopicJSONData(data.item);
    return data;
}

function fetchAndAssignPost(nodeRef)
{
    var data = fetchPost(nodeRef);    
    if (data === null)
    {
        return null;
    }
    applyDataToModel(data);
}

function updateAndAssignPost(site, container, path, parentNodeRef, content, browseTopicUrl)
{
    var params = {
        site : site,
        container : container,
        path : path,
        content : content,
        browseTopicUrl : browseTopicUrl
    };
    var paramsJSON = jsonUtils.toJSONString(params);
    var url = getPostRequestUrl(parentNodeRef);

    // fetch and assign data from the backend
    var data = doPutCall(url, paramsJSON);
    if (data === null)
    {
        return;
    }
    convertTopicJSONData(data.item);
    applyDataToModel(data);
}
