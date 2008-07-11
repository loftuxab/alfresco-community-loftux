<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function getContainerTagScopeRequestUrl(site, container)
{
    var url = "/api/site/" + site + "/" + container + "/tagscopetags";
    return url;
}

function getTags(site, container)
{
    var url = getContainerTagScopeRequestUrl(site, container);
    var data = doGetCall(url);
    if (data === null)
    {
        return null;
    }
    return data;
}

function main()
{
    // gather all required data
    var site = page.url.templateArgs.site;
    var container = "discussions";
    
    var data = getTags(site, container);
    if (data != null)
    {
       model.tags = data.tags;
    }
}

main();
