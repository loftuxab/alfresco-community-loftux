<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topics.lib.js">

function main()
{
    // gather required data
    var site = json.get("site");
    var container = "discussions";
    var path = "";
    var title = "" + json.get("title");
    var content = "" + json.get("content");
    
    // Create topic and assign returned data
    createAndAssignTopic(site, container, path, title, content);
    
    // set additional template data
    model.site = json.get("site");
}

main();
