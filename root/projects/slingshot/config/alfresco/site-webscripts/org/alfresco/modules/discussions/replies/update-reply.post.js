<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/post.lib.js">

function main()
{
    var postRef = "" + json.get("postRef");
    var content = "" + json.get("content");
    var site = "" + json.get("site");
    var container = "" + json.get("container");
    var path = "" + json.get("path");
    var browseTopicUrl = "" + json.get("browseTopicUrl");
    
    // update the post and assign returned data
    updateAndAssignPost(site, container, path, postRef, content, browseTopicUrl);
    
    // set additional model data
    model.htmlid = json.get("htmlid");
}

main();
