<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogpost.lib.js">

function main()
{
    // fetch required datadata
    var site = page.url.args["site"];
    var container = template.properties["container"];
    if (container == undefined)
    {
        container = "blo";
    }
    
    // fetch post id - undefined in case a create topic form is requested
    var postId = page.url.args["postId"];
    if (postId != undefined)
    {
        // fetch post
        fetchAndAssignPost(site, container, postId)
    }
    
    // assign additional model data
    model.site = site;
    model.container = container;
}

main();
