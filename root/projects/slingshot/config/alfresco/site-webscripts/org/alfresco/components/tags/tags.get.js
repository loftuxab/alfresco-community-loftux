<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function getTags(site, container)
{
    var url = "/api/site/" + site + "/" + container + "/tagscopetags";
    var data = doGetCall(url);
    if (data === null)
    {
        return null;
    }
    return data;
}

var site = page.url.templateArgs.site;
var container = args.container;
    
var tags = [];

var data = getTags(site, container);
if (data != null)
{
   tags = data.tags;
}

model.tags = tags;