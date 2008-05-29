var conf = new XML(config.script);
// We just grab the first one for now
var url = conf.feed[0];

var connectStr = url.split("://");
var type = connectStr[0];
var uri = connectStr[1];

if (type === "http")
{
	uri = "http://" + uri;
}

var connector = remote.connect(type);
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