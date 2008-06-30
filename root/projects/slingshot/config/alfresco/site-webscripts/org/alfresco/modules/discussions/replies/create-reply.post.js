<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/replies.lib.js">


function main()
{
    // gather required data
    var postRef = "" + json.get("postRef");
    var content = "" + json.get("content");
    
    // create reply
    createAndAssignReply(postRef, content);
    
    // set additional template data
    model.htmlid = json.get("htmlid");
}

main();
