/**
 * Adds a request parameter to a url
 * @return the new url
 */
function addParamToUrl(url, paramName, paramValue)
{
    if (url.indexOf('?') > -1)
    {
        return url += "&" + paramName + "=" + paramValue;
    }
    else
    {
        return url += "?" + paramName + "=" + paramValue;
    }
}

/** Adds a path element to a url.
 * E.g. if the url is /abc/def and elem is ghi the returned url would be
 * /abc/def/ghi. On the other hand, if elem is null, the input url would be
 * returned
 */
function addUrlPathElement(url, elem)
{
    elem = "" + elem; // make sure we use a javascript string
    if (elem != undefined && elem.length > 0)
    {
        url += "/" + elem;
    }
    return url;
}

/** 
 * Copies all properties from one javascript object to another.
 */
function copyDataToObject(data, target)
{
    for (n in data)
    {
        target[n] = data[n];
    }
}

/** 
 * Copies all properties from the passed javascript object to the model.
 */
function applyDataToModel(data)
{
    copyDataToObject(data, model);
}


/**
 * POST call
 */
function doPostCall(url, paramsJSON)
{
    var connector = remote.connect("alfresco");
    var result = connector.post(url, paramsJSON, "application/json");
    if (result.status != status.STATUS_OK)
    {
        status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to do backend call. " +
                       "status: " + result.status + ", response: " + result.response);
        return null;
    }
    return eval('(' + result.response + ')');
}


/**
 * PUT call
 */
function doPutCall(url, paramsJSON)
{
    var connector = remote.connect("alfresco");
    var result = connector.put(url, paramsJSON, "application/json");
    if (result.status != status.STATUS_OK)
    {
        status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to do backend call. " +
                       "status: " + result.status + ", response: " + result.response);
        return null;
    }
    return eval('(' + result.response + ')');
}


/**
 * GET call
 */
function doGetCall(url)
{
    var connector = remote.connect("alfresco");
    var result = connector.get(url);
    if (result.status != status.STATUS_OK)
    {
        status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to do backend call. " +
                       "status: " + result.status + ", response: " + result.response);
        return null;
    }
    return eval('(' + result.response + ')');
}


