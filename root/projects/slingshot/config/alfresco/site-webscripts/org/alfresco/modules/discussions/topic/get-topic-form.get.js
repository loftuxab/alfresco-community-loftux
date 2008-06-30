<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topic.lib.js">

function main()
{
    // fetch topic id - undefined in case a create topic form is requested
    var topicId = args["topicId"];
    
    if (topicId != undefined)
    {
        // get all required data
        var site = args["site"];
        var container = "discussions";
        var path = "";
        var topicId = args["topicId"];
        
        // fetch topic
        fetchAndAssignTopic(site, container, path, topicId)
    }
    
    // assign additional model data
    model.htmlId = args["htmlId"];
    model.site = args["site"];
}

main();
