<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/comments.lib.js">

function main()
{
    var nodeRef = "" + json.get("nodeRef");
    var content = "" + json.get("content");
     
    // update the post and assign returned data
    updateAndAssignComment(nodeRef, content);
    
    // set additional model data
    model.htmlid = json.get("htmlid");
}

main();
