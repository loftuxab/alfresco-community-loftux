
function convertTopicJSONData(topic)
{
    // created
    var created = new Date(topic["createdOn"])
    topic["createdOn"] = created;
    
    // last reply
    if(topic["lastReplyOn"] != undefined)
    {
        topic["lastReplyOn"] = new Date(topic["lastReplyOn"])
    }
}

/**
 * Converts the data object from strings to the proper types
 * (currently this only handles strings
 */
function convertTopicsJSONData(data)
{
    for(var x=0; x < data.items.length; x++)
    {
        convertTopicJSONData(data.items[x]);
    }
}

function convertRepliesJSONData(data)
{
    for (var x=0; x < data.items.length; x++)
    {
        convertReply(data.items[x]);
    }
}

/** Converts a reply and if available recursively its children. */
function convertReply(post)
{
    convertTopicJSONData(post);
    if (post["children"] != undefined)
    {
        var children = post["children"];
        for(var x=0; x < children.length; x++)
        {
            convertReply(children[x]);
        }
    }
}
