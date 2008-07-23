var title = page.url.args.title;
if (title)
{
   var context = page.url.context + "/page/site/" + page.url.templateArgs.site + "/wiki-page?title=" + page.url.args.title;
  	var result = remote.call("/slingshot/wiki/page/" + page.url.templateArgs.site + "/" + page.url.args.title + "?context=" + escape(context));

  	if (result)
  	{
		model.result = eval('(' + result + ')');
  	}
}
else
{
	status.redirect = true;
  	status.code = 301;
  	status.location = page.url.service + "?title=Main_Page";   
}