
var result = remote.call("/slingshot/wiki/page/" + page.url.args.site + "/" + page.url.args.title);

if (result)
{
	model.result = result;
}
