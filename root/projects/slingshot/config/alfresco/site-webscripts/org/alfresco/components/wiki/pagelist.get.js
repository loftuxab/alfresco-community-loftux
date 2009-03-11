<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

// Grab the wiki pages for the (current) site
var theUrl = "/slingshot/wiki/pages/" + page.url.templateArgs.site,
   filter = page.url.args.filter;

if (filter)
{
   theUrl += "?filter=" + filter;
}

var response = doGetCall(theUrl, true);
if (response.status.code != 200)
{
   model.error = response.message;
}
else
{
   model.pageList = response;   
}
