<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/commentjsondatautils.js">

function nodeRefToUrl(nodeRef)
{
    // PENDING: what is the best way to clean this up?
    return nodeRef.replace(/%3A/gi, ":").replace(/%2F/gi, "/").replace(/\:\/\//,"/");
}

// COMMENTS

function getCommentsRequestUrl(nodeRef)
{
    return "/api/node/" + nodeRefToUrl(nodeRef) + "/comments";
}

function fetchComments(nodeRef)
{
    var url = getCommentsRequestUrl(nodeRef);
    var data = doGetCall(url);
    if (data === null)
    {
        return null;
    }
    convertCommentsJSONData(data);
    return data;
}

function fetchAndAssignComments(nodeRef, content)
{
    var data = fetchComments(nodeRef, levels);
    if (data === null)
    {
        return;
    }
    applyDataToModel(data);
}


// COMMENT

function getCommentRequestUrl(nodeRef)
{
    return "/api/comment/node/" + nodeRefToUrl(nodeRef);
}


function fetchComment(nodeRef)
{
    var url = getCommentRequestUrl(nodeRef);
    var data = doGetCall(url);
    if (data === null)
    {
        return null;
    }
    convertCommentJSONData(data.item);
    return data;
}

function fetchAndAssignComment(nodeRef)
{
    var data = fetchComment(nodeRef);    
    if (data === null)
    {
        return null;
    }
    applyDataToModel(data);
}

function updateAndAssignComment(nodeRef, content)
{
    var params = {
        content : content
    };
    var paramsJSON = jsonUtils.toJSONString(params);
    var url = getCommentRequestUrl(nodeRef);

    // fetch and assign data from the backend
    var data = doPutCall(url, paramsJSON);
    if (data === null)
    {
        return;
    }
    convertCommentJSONData(data.item);
    applyDataToModel(data);
}
