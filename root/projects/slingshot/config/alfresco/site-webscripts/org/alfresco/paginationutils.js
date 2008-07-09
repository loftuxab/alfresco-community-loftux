<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

/**
 * Functions to work with pagination.
 * 
 * Note: All these functions expect a json result data object containing
 * the root variables
 *  - index : start index of the returned page
 *  - count : number of elements in the returned page
 *  - total : total number of elements that could be queries
 */
 
/**
 * Returns whether data contains a valid interval.
 * Invalid interval means that index >= total.
 */
function isValidPageRange(data)
{
    return (data.startIndex < data.total)
}

/**
 * Returns a valid interval for the the passed data.
 * @return { startIndex, pageSize }
 */
function getValidPageRange(data)
{
    var startIndex = data.startIndex;
    var pageSize = data.pageSize;
    var itemCount = data.itemCount;
    var total = data.total;

    if (startIndex < total) {
        // the current pagination values are ok
        return data;
    }
    
    // if count < 1 there's not point of calculating anything
    // lets let the caller know that he's missing the function
    else if (pageSize < 1) {
        status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "getValidPageRange is not supported pageSize values smaller than 1");
        return data;
    }
    
    // if we are out of range, let's take the last page (for the given count)
    var newStartIndex = 0;
    if (total > 0)
    {
        var page = parseInt(total / pageSize);
        if ((total % pageSize) == 0)
        {
            page--;
        }
        newStartIndex = page * pageSize;
    }
    
    // put together a new page range that makes sense
    var validPageRange = {
        startIndex : newStartIndex,
        pageSize : pageSize,
    }
    return validPageRange;
}

/**
 * Fetches pagination params from the request
 * Note: Use this function for webscripts that are called in a page context
 */
function fetchPaginationDataFromPageRequest(defaultStartIndex, defaultPageSize)
{
    // create data object initialized with default values
    var paginationData = {
        startIndex: defaultStartIndex,
        pageSize: defaultPageSize
    };
    
    // check what variables we can find in the page request
    if (page.url.args["startIndex"] != undefined)
    {
        paginationData.startIndex = parseInt(page.url.args["startIndex"]);
    }
    if (page.url.args["pageSize"] != undefined)
    {
        paginationData.pageSize = parseInt(page.url.args["pageSize"]);
    }

    return sanityCheckData(paginationData);
}

/**
 * Fetches pagination params from the request.
 * 
 * Note: Use this function for directly called webscripts
 */
function fetchPaginationDataFromRequest(defaultStartIndex, defaultPageSize)
{
    // create data object initialized with default values
    var paginationData = {
        startIndex: defaultStartIndex,
        pageSize: defaultPageSize
    };

    // check what variables we can find in the request
    if (args["startIndex"] != undefined)
    {
        paginationData.startIndex = parseInt(args["startIndex"]);
    }
    if (args["pageSize"] != undefined)
    {
        paginationData.pageSize = parseInt(args["pageSize"]);
    }
    
    return sanityCheckData(paginationData);
}

/**
 * Ensures that startIndex and pageSize have reasonable values
 */
function sanityCheckData(paginationData)
{
    if (paginationData.pageSize == undefined || paginationData.pageSize == null || paginationData.pageSize < 1)
    {
        paginationData.pageSize = 10;
    }
    if (paginationData.startIndex == undefined || paginationData.startIndex == null || paginationData.startIndex < 0)
    {
        paginationData.startIndex = 0;
    }
    return paginationData;
}

/**
 * Adds the pagination parameters to a request url.
 * @return the new url
 */
function addPaginationParamsToUrl(url, paginationData)
{
    url = addParamToUrl(url, "startIndex", paginationData.startIndex);
    url = addParamToUrl(url, "pageSize", paginationData.pageSize);
    return url;
}

/**
 * Fetches a valid paginated result set.
 * The passed function is used to fetch the data. The result is
 * then checked for page validity. If the check fails the paging
 * data is adapted and the function called a second time.
 * 
 * @param func a function that returns a paginated result set.
 *        The function expects one parameter, the paginationData to use
 *        for the fetch.
 * @param the paginationData to use for the first request.
 * @return a valid paged result set or null in case an error occured.
 */
function fetchValidPaginatedData(func, paginationData)
{
    // fetch the data for a first time
    var data = func(paginationData);
    if (data == null)
    {
        return null;
    }
    else if (isValidPageRange(data))
    {
        return data;
    }
    
    // adapt the page range and try again
    paginationData = getValidPageRange(data);
    data = func(paginationData);
    
    // no checks here
    return data;
}