function sortByTitle(link1, link2)
{
   return (link1.title > link2.title) ? 1 : (link1.title < link2.title) ? -1 : 0;
}

function main()
{
   var site = page.url.templateArgs.site;
   var container = 'links';
	var url = '/api/links/site/' + site + '/' + container + '?page=1&pageSize=512';
   var connector = remote.connect("alfresco");
   var result = connector.get(url);
	if (result.status == 200)
	{
		var links = eval('(' + result.response + ')').items;
      links.sort(sortByTitle);
		model.links = links;
	}
}

main();