<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

// Grab the wiki pages for the (current) site
var url = "/slingshot/wiki/pages/" + page.url.templateArgs.site; 

var filter = page.url.args.filter;
if (filter)
{
   url += "?filter=" + filter;
}

model.pageList = doGetCall(url);