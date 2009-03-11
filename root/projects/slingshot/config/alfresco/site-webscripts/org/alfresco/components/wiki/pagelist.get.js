<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

// Grab the wiki pages for the (current) site
var theUrl = "/slingshot/wiki/pages/" + page.url.templateArgs.site,
   filter = page.url.args.filter;

if (filter)
{
   theUrl += "?filter=" + filter;
}

model.pageList = doGetCall(theUrl);