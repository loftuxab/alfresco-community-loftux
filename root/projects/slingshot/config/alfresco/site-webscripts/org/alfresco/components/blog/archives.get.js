<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogposts.lib.js">

function main()
{
    // gather all required data
    var site = page.url.templateArgs.site;
    var container = "blog";
    var path = "";
    
    var data = fetchPostsPerMonth(site, container, path);
    var items = data.items.reverse();
    model.items = items;
}

main();