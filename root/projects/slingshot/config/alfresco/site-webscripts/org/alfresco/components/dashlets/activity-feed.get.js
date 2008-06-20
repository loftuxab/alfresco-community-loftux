
model.entries = [];

var result = remote.call("/api/activities/feed/site/" + page.url.args.site + "?format=atomfeed");
var re = /<\?xml[^\?]*\?>/;

if (result && re.test(result))
{
	result = new String(result);
	result = result.replace(re, '').replace(/^\s+/, ''); // rhino E4X bug 336551
	
	default xml namespace = new Namespace("http://www.w3.org/2005/Atom");
	
	var feed = new XML(result); 
	
	model.title = feed.title.toString();
	
	var entry;
	for each (entry in feed.entry)
	{
		model.entries.push({
			"title" : entry.title.toString(),
			"summary" : entry.summary.toString()
		});
	}
	
}