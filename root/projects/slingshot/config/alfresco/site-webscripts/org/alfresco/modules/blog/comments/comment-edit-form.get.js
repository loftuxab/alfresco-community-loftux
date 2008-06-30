<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/comments.lib.js">

function main()
{
    // gather all required data
    var nodeRef = args["nodeRef"];
    
    // fetch the post
    fetchAndAssignComment(nodeRef);
    
    // assign additional model data
    model.htmlid = args["htmlid"];
}

main();
