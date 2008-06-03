// these properties are pulled from the component instance xml

var endpoint = instance.properties["rss-endpoint"];
var uri = instance.properties["rss-uri"];


// do the connection and populate the model

var connector = remote.connect(endpoint);
var result = connector.call(uri);

if (result !== null)
{
	var rssXml = new String(result);	
	var re = /<[r|R][s|S]{2}/; // Is this really an RSS document?
	if (re.test(rssXml))
	{
		// Strip out any preceding xml processing instructions or E4X will choke
		var idx = rssXml.search(re);
		rssXml = rssXml.substring(idx);
		 
		var rss = new XML(rssXml); 
		model.title = rss.channel.title.toString();
		model.items = [];

		var item;
		for each (item in rss.channel..item)
		{
			model.items.push({
				"title" : item.title.toString(),
				"description" : item.description.toString(),
				"link" : item.link.toString()
			});
		}
	}	
}