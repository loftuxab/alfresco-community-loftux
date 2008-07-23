<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topicjsondatautils.js">

function getTopicRequestUrl(site, container, path, topicId)
{
    var url = "/api/forum/post/site/" + site + "/" + container;
    url = addUrlPathElement(url, path);
    url = addUrlPathElement(url, topicId);
    return url;
}

function fetchTopic(site, container, path, topicId)
{
    var url = getTopicRequestUrl(site, container, path, topicId);
    var data = doGetCall(url);
    if (data === null)
    {
        return null;
    }
    convertTopicJSONData(data.item);
    return data;
}

function fetchAndAssignTopic(site, container, path, topicId)
{
    var data = fetchTopic(site, container, path, topicId);
    if (data === null)
    {
        return;
    }
    applyDataToModel(data);
}

function updateAndAssignTopic(site, container, path, topicId, title, content, tags, browseTopicUrl)
{
    // fetch the information required to create the topic
    var params = {
        title : title,
        content : content,
        tags : tags,
        site : site,
        container : container,
        browseTopicUrl : browseTopicUrl
    };
    var jsonParams = jsonUtils.toJSONString(params);
    var url = getTopicRequestUrl(site, container, path, topicId);
    
    // Create topic and assign returned data
    var data = doPutCall(url, jsonParams);
    if (data === null)
    {
        return;
    }
    convertTopicJSONData(data.item);
    applyDataToModel(data);   
}
