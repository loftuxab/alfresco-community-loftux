<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/replies.lib.js">


function main()
{
    // gather required data
    var postRef = "" + json.get("postRef");
    var content = "" + json.get("content");
    var site = "" + json.get("site");
    var container = "" + json.get("container");
    var path = "" + json.get("path");
    
    // create reply
    createAndAssignReply(site, container, path, postRef, content);
    
    // set additional template data
    model.htmlid = json.get("htmlid");
}

main();
