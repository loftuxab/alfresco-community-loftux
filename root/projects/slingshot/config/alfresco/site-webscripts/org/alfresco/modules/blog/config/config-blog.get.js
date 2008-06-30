<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blog.lib.js">

function main()
{
    // gather all required data
    var site = args["site"];
    var container = "blog";
    
    // fetch the current blog data
    fetchAndAssignBlog(site, container);
    
    model.site = site;
    model.container = container;
}

main();
