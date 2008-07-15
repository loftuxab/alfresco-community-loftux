<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topic.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/replies.lib.js">

/**
 * Number of levels of replies that should be fetched
 * in the request. Currently we fetch all replies.
 */
var MAX_LEVELS_OF_REPLIES = 100000;

function main()
{
    // gather all required data
    var site = page.url.templateArgs.site;
    var container = "discussions";
    var path = "";
    var topicId = page.url.args["topicId"];
    
    // fetch the topic data
    var topicdata = fetchTopic(site, container, path, topicId);
    if (status.getCode() != status.STATUS_OK)
    {
        return;
    }
    model.topic = topicdata.item;
    
    // fetch the replies
    var repliesdata = fetchReplies(topicdata.item.nodeRef, MAX_LEVELS_OF_REPLIES);
    if (status.getCode() != status.STATUS_OK)
    {
        return;
    }
    model.replies = repliesdata.items;
    model.site = site;
    model.container = container;
    model.path = path;
}

main();
