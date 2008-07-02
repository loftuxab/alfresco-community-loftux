if (!page.url.args.title)
{
	status.redirect = true;
  	status.code = 301;
  	status.location = page.url.service + "?site=" + page.url.args.site + "&title=Main_Page";
}
else
{
   var context = url.context + "/page/wiki?site=" + page.url.args.site + "&title=" + page.url.args.title;
  	var result = remote.call("/slingshot/wiki/page/" + page.url.args.site + "/" + page.url.args.title + "?context=" + escape(context));

  	if (result)
  	{
		model.result = eval('(' + result + ')');
  	}
}
