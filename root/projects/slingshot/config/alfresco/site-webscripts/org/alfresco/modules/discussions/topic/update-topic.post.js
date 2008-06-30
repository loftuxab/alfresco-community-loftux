<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topic.lib.js">

function main()
{
    var site = json.get("site");
    var container = "discussions";
    var path = "";
    var topicId = json.get("topicId");
    var title = "" + json.get("title");
    var content =  "" + json.get("content");

    // update topic and assign returned data
    updateAndAssignTopic(site, container, path, topicId, title, content);
    
    // set additional model data
    model.site = json.get("site");
}

main();
