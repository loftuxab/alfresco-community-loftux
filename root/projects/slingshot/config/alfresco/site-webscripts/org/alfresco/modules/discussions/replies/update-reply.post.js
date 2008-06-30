<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/post.lib.js">

function main()
{
    var postRef = "" + json.get("postRef");
    var content = "" + json.get("content");
     
    // update the post and assign returned data
    updateAndAssignPost(postRef, content);
    
    // set additional model data
    model.htmlid = json.get("htmlid");
}

main();
