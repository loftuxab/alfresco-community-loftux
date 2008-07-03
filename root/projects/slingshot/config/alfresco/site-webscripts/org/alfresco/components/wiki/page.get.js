if (!page.url.args.title)
{
	status.redirect = true;
  	status.code = 301;
  	status.location = page.url.service + "?title=Main_Page";
}
else
{
   var context = url.context + "/page/site/" + page.url.templateArgs.site + "/wiki?title=" + page.url.args.title;
  	var result = remote.call("/slingshot/wiki/page/" + page.url.templateArgs.site + "/" + page.url.args.title + "?context=" + escape(context));

  	if (result)
  	{
		model.result = eval('(' + result + ')');
  	}
}