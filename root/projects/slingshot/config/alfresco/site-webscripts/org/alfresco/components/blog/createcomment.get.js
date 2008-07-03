<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogpost.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/comments.lib.js">

function main()
{
    // gather all required data
    var site = page.url.templateArgs.site;
    var container = "blog";
    var postId = page.url.args["postId"];
    
    // fetch the post data
    var postdata = fetchPost(site, container, postId);
    if (status.getCode() != status.STATUS_OK)
    {
        return;
    }
    
    // the nodeRef is all that the component actually needs.
    // Could therefore be generalized
    model.nodeRef = postdata.item.nodeRef;
}

main();
