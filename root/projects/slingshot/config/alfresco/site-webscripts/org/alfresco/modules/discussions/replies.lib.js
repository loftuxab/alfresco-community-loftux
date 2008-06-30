<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topicjsondatautils.js">

function nodeRefToUrl(nodeRef)
{
    // PENDING: use another method to decode the url
    return nodeRef.replace(/%3A/gi, ":").replace(/%2F/gi, "/").replace(/\:\/\//,"/");
}

function getRepliesRequestUrl(nodeRef)
{
    return "/forum/post/node/" + nodeRefToUrl(nodeRef) + "/replies";
}

function getRepliesRequestUrlIncludingLevels(nodeRef, levels)
{
    var url = getRepliesRequestUrl(nodeRef);
    url = addParamToUrl(url, "levels", levels);
    return url;
}

function fetchReplies(nodeRef, levels)
{
    var url = getRepliesRequestUrlIncludingLevels(nodeRef, levels);
    var data = doGetCall(url);
    if (data === null)
    {
        return null;
    }
    convertRepliesJSONData(data);
    return data;
}

function fetchAndAssignReplies(nodeRef, content)
{
    var data = fetchReplies(nodeRef, levels);
    if (data === null)
    {
        return;
    }
    applyDataToModel(data);
}

function createAndAssignReply(parentNodeRef, content)
{
    var params = {
        content : content
    };
    var paramsJSON = jsonUtils.toJSONString(params);
    var url = getRepliesRequestUrl(parentNodeRef);

    // fetch and assign data from the backend
    var data = doPostCall(url, paramsJSON);
    if (data === null)
    {
        return;
    }
    convertTopicJSONData(data.item);
    applyDataToModel(data);
}
