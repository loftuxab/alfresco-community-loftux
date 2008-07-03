<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topic.lib.js">

function main()
{
    // gather all required data
    var site = page.url.templateArgs.site;
    var container = "discussions";
    var path = "";
    var topicId = page.url.args["topicId"];
    
    // fetch the topic
    fetchAndAssignTopic(site, container, path, topicId)
    
    // assign additional model data
    model.site = page.url.templateArgs.site;
    model.editMode = ((page.url.args["edit"] != undefined) && (page.url.args["edit"] == "true"));
}

main();