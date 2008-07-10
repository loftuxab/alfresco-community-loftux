<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topics.lib.js">

function main()
{
    // gather required data
    var site = "" + json.get("site");
    var container = json.has("container") ? "" + json.get("container") : "discussions";
    var path = json.has("path") ? "" + json.get("path") : "";
    var title = "" + json.get("title");
    var content = "" + json.get("content");
    var htmlid = "" + json.get("htmlid");
    var tagsJSONArray = json.get("tags");
    var tags = [];
    for (var x=0; x<tagsJSONArray.length; x++)
    {
        tags.push(tagsJSONArray[x]);
    }
    
    // Create topic and assign returned data
    createAndAssignTopic(site, container, path, title, content, tags);
    
    // set additional template data
    model.site = site;
    model.htmlid = htmlid;
}

main();
