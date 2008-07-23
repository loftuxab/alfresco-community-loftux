<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/comments.lib.js">

function main()
{
    var nodeRef = "" + json.get("nodeRef");
    var content = "" + json.get("content");
    var site = "" + json.get("site");
    var container = "" + json.get("container");
    var itemTitle = "" + json.get("itemTitle");
    var browseItemUrl = "" + json.get("browseItemUrl");
     
    // update the post and assign returned data
    updateAndAssignComment(nodeRef, content, site, container, itemTitle, browseItemUrl);
    
    // set additional model data
    model.htmlid = json.get("htmlid");
}

main();
