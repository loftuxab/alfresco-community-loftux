<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/paginationutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogpostjsondatautils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/paginator/paginator.js">

function getPostsRequestUrl(site, container, path)
{
    var url = "/api/blog/site/" + site + "/" + container;
    url = addUrlPathElement(url, path);
    url += "/posts";
    return url;
}

function getPostsPerMonthRequestUrl(site, container, path)
{
    return getPostsRequestUrl(site, container, path) + "permonth";
}

function getPostsRequestUrlWithParams(site, container, path, filter, tag, fromDate, toDate, paginationData)
{
    var url = getPostsRequestUrl(site, container, path);

    // check whether we got a filter or not
    if (filter != null && filter.length > 0)
    {
        // latest only
        if (filter == "new")
        {
            url += "/new";
        }
        else if (filter == "mydrafts")
        {
            url += "/mydrafts"
        }
        else if (filter == "mypublished")
        {
            url += "/mypublished"
        }
        else if (filter == "publishedext")
        {
            url += "/publishedext"
        }
    }
    
    if (fromDate != null && fromDate.length > 0)
    {
        url = addParamToUrl(url, "fromDate", fromDate);
    }
    if (toDate != null && toDate.length > 0)
    {
        url = addParamToUrl(url, "toDate", toDate);
    }
    
    if (tag != null && tag.length > 0)
    {
        url = addParamToUrl(url, "tag", tag);
    }
    
    // add the contentFormat
    url = addParamToUrl(url, "contentLength", "512");

    // pagination
    url = addPaginationParamsToUrl(url, paginationData);
    
    return url;
}

/**
 * Returns the months for which posts exist
 */
function fetchPostsPerMonth(site, container, path)
{
    var url = getPostsPerMonthRequestUrl(site, container, path);
    var data = doGetCall(url);
    if (data === null)
    {
        return null;
    }
    convertPostsPerMonthJSONData(data);
    return data;
}


/**
 * Returns the topics using the passed request data.
 */
function fetchBlogPosts(site, container, path, filter, tag, fromDate, toDate, paginationData)
{
    var callback = function fetchTopicsCallback(paginationData)
    {
        var url = getPostsRequestUrlWithParams(site, container, path, filter, tag, fromDate, toDate, paginationData);
        return doGetCall(url);
    }
    var data = fetchValidPaginatedData(callback, paginationData);
    if (data === null)
    {
        return null;
    }
    convertPostsJSONData(data);
    return data;
}

/** Fetches topics and assigns them to the model. */
function fetchAndAssignBlogPosts(site, container, path, filter, tag, fromDate, toDate, paginationData)
{
    var data = fetchBlogPosts(site, container, path, filter, tag, fromDate, toDate, paginationData);
    if (data == null) return;
    
    applyDataToModel(data);
    model.pdata = getPaginatorRenderingData(data);    
}



function createAndAssignPost(site, container, path, title, content)
{
    // fetch the information required to create the post
    var params = {
        title : title,
        content : content
    };
    var jsonParams = jsonUtils.toJSONString(params);
    var url  = getPostsRequestUrl(site, container, path);
    
    // Create post and assign returned data
    var data = doPostCall(url, jsonParams);
    if (data === null)
    {
        return;
    }
    convertPostJSONData(data.item);
    applyDataToModel(data);   
}
