<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topic.lib.js">

function main()
{
    // fetch topic id - undefined in case a create topic form is requested
    var topicId = args["topicId"];
    var site = args["site"];
    var container = (args["container"] != undefined) ? args["container"] : "discussions";
    var path = (args["path"] != undefined) ? args["path"] : "";
    
    if (topicId != undefined)
    {   
        // fetch topic
        fetchAndAssignTopic(site, container, path, topicId)
    }
    
    // assign additional model data
    model.htmlId = args["htmlId"];
    model.site = site;
    model.container = container;
}

main();
