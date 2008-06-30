<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogpost.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/comments.lib.js">

function main()
{
    // gather all required data
    var site = page.url.args["site"];
    var container = "blog";
    // var path = "";
    var postId = page.url.args["postId"];
    
    // fetch the post data
    var postdata = fetchPost(site, container, postId);
    if (status.getCode() != status.STATUS_OK)
    {
        return;
    }
    model.post = postdata.item;
    
    // fetch the replies
    var commentsdata = fetchComments(postdata.item.nodeRef);
    if (status.getCode() != status.STATUS_OK)
    {
        return;
    }
    model.comments = commentsdata.items;
}

main();
