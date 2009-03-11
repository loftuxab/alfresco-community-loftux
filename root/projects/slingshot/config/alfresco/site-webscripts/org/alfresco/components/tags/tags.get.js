<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function getTags(site, container)
{
    var theUrl = "/api/tagscopes/site/" + site + "/" + container + "/tags";
    var data = doGetCall(theUrl);
    if (data === null)
    {
        return null;
    }
    return data;
}

var site = page.url.templateArgs.site,
   container = args.container,
   tags = [];

var data = getTags(site, container);
if (data != null)
{
   tags = data.tags;
}

model.tags = tags;