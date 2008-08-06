<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

var title = page.url.args.title;
if (title)
{
   var context = page.url.context + "/page/site/" + page.url.templateArgs.site + "/wiki-page?title=" + page.url.args.title;
  	var uri = "/slingshot/wiki/page/" + page.url.templateArgs.site + "/" + page.url.args.title + "?context=" + escape(context);
  	
  	model.result = doGetCall(uri);
}
else
{
	status.redirect = true;
  	status.code = 301;
  	status.location = page.url.service + "?title=Main_Page";   
}