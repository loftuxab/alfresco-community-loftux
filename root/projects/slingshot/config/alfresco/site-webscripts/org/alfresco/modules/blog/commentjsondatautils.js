
function convertCommentJSONData(item)
{
    // created
    var created = new Date(item["createdOn"])
    item["createdOn"] = created;
    
    // modified
    if(item["modifiedOn"] != undefined)
    {
        var modified = new Date(item["modifiedOn"]);
        item["modifiedOn"] = created;
        if ((modified.getTime() - created.getTime()) > 60000) // 60*1000 = 1 minute
        {
            item["isUpdated"] = true;
        }
        else
        {
            item["isUpdated"] = false;
        }
    }
    else
    {
        item["isUpdated"] = false;
    }
}

function convertCommentsJSONData(data)
{
    for (var x=0; x < data.items.length; x++)
    {
        convertComment(data.items[x]);
    }
}
