<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/paginationutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topicjsondatautils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/paginator/paginator.js">

/**
 * Returns the request url to fetch the topics for a forum
 */
function getTopicsRequestUrl(site, container, path)
{
    var url = "/forum/site/" + site + "/" + container;
    url = addUrlPathElement(url, path);
    url += "/posts";
    return url;
}

/**
 * Returns the request url for the topics, taking into account the different
 * additional parameters.
 */
function getTopicsRequestUrlWithParams(site, container, path, filter, tag, paginationData)
{
    var url = getTopicsRequestUrl(site, container, path);

    // check whether we got a filter or not
    if (filter != undefined)
    {
        if (filter == "all")
        {
            // the default returns all
        }
        else if (filter == "new")
        {
            url += "/new";
        }
        else if (filter == "hot")
        {
            url += "/hot";
        }
        else if (filter == "mine")
        {
            url += "/myposts";
        }
    }
    
    // add the contentFormat
    url = addParamToUrl(url, "contentFormat", "textDigest");

    // pagination
    url = addPaginationParamsToUrl(url, paginationData);
    
    return url;
}

/**
 * Returns the topics using the passed request data.
 */
function fetchTopics(site, container, path, filter, tag, paginationData)
{
    var callback = function fetchTopicsCallback(paginationData)
    {
        var url = getTopicsRequestUrlWithParams(site, container, path, filter, tag, paginationData);
        return doGetCall(url);
    }
    var data = fetchValidPaginatedData(callback, paginationData);
    if (data === null)
    {
        return null;
    }
    convertTopicsJSONData(data);
    return data;
}

/** Fetches topics and assigns them to the model. */
function fetchAndAssignTopics(site, container, path, filter, tag, paginationData)
{
    var data = fetchTopics(site, container, path, filter, tag, paginationData);
    if (data == null) return;
    
    applyDataToModel(data);
    model.pdata = getPaginatorRenderingData(data);    
}


function createAndAssignTopic(site, container, path, title, content)
{
    // fetch the information required to create the topic
    var params = {
        title : title,
        content : content
    };
    var jsonParams = jsonUtils.toJSONString(params);
    var url  = getTopicsRequestUrl(site, container, path);
    
    // Create topic and assign returned data
    var data = doPostCall(url, jsonParams);
    if (data === null)
    {
        return;
    }
    convertTopicJSONData(data.item);
    applyDataToModel(data);   
}

function getListTitle(filter, tag)
{
    if (filter == null || filter == "all")
    {
        return "All topics";
    }
    else if (filter == "hot")
    {
        return "Hot topics";
    }
    else if (filter == "new")
    {
        return "New topics";
    }
    else if (tag != null && tag.length > 0)
    {
        return "Topics with tag " + tag;
    }
    else
    {
        return "Topics"
    }
}