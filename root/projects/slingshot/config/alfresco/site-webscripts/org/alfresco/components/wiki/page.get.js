if (!page.url.args.title)
{
	status.redirect = true;
  	status.code = 301;
  	status.location = page.url.service + "?site=" + page.url.args.site + "&title=Main_Page";
}
else
{
  	var result = remote.call("/slingshot/wiki/page/" + page.url.args.site + "/" + page.url.args.title);

  	if (result)
  	{
		model.result = eval('(' + result + ')');
  	}
}
