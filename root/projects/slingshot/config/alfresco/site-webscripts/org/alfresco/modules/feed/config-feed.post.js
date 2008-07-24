var uri = String(json.get("url"));

var re = /^http:\/\//;
if (!re.test(uri))
{
   uri = "http://" + uri;
}

var c = sitedata.getComponent(url.templateArgs.componentId);
c.properties["feedurl"] = uri;
c.save();

// Expect to only handle "http" connections for the time being
var connector = remote.connect("http");
var result = connector.call(uri);

// TODO: refactor this out and put it into some RSS library
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
		
		var media = new Namespace("http://search.yahoo.com/mrss/");
      var mediaRe = /([^\/]+)$/;

		var item, obj;
		for each (item in rss.channel..item)
		{
		   obj = {
		      "title": item.title.toString(),
		      "description": item.description.toString(),
		      "link": item.link.toString()
		   };
		   
		   var attachment = item.media::content;
		   if (attachment)
		   {
		      var contenturl = attachment.@url.toString();
		      if (contenturl.length > 0)
		      {
		         var filename = mediaRe.exec(contenturl)[0];
      		   // Use the file extension to figure out what type it is for now
      		   var ext = filename.split(".");
      		   
   		      obj["attachment"] = {
   		         "url": contenturl,
   		         "name": filename,
   		         "type": (ext[1] ? ext[1] : "_default")
   		      }
		      }
		   }
		   
		   model.items.push(obj);
		}
	}	
}