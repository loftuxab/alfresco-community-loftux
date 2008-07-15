<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/post.lib.js">

function main()
{
    // gather all required data
    var postRef = args["postRef"];
    
    // fetch the post
    fetchAndAssignPost(postRef);
    
    // assign additional model data
    model.htmlid = args["htmlid"];
    model.isEdit = (args["isEdit"] != undefined) ? (args["isEdit"] == "true") : false;
    model.site = args["site"];
    model.container = args["container"];
    model.path = args["path"];
}

main();
