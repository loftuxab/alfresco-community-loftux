<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogpost.lib.js">

function main()
{
    // gather all required data
    var site = page.url.args["site"];
    var container = "blog";
    // var path = "";
    var postId = page.url.args["postId"];
    
    // fetch the post
    fetchAndAssignPost(site, container, postId)
    
    // assign additional model data
    model.site = page.url.args["site"];
    model.editMode = ((page.url.args["edit"] != undefined) && (page.url.args["edit"] == "true"));
}

main();
