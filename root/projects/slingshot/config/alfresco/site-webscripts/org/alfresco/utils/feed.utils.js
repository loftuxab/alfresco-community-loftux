/**
 * Takes a URL of an RSS feed and returns an array
 * of items in the feed.
 *
 * @param uri {String} the uri of the RSS feed
 */
function getRSSFeed(uri)
{
   var re = /^http:\/\//;
   if (!re.test(uri))
   {
      uri = "http://" + uri;
   }

   // We only handle "http" connections for the time being
   var connector = remote.connect("http");
   var result = connector.call(uri);

   var items = [];
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

    		/**
           * We do this (dynamically) as some feeds, e.g. the BBC, leave the trailing slash
           * off the end of the Yahoo Media namespace! Technically this is wrong but what to do. 
           */ 
         var mediaRe = /xmlns\:media="([^"]+)"/; 
         var hasMediaExtension = mediaRe.test(rssXml);
          
         if (hasMediaExtension)
         {
            var result = mediaRe.exec(rssXml);
            // The default (correct) namespace should be 'http://search.yahoo.com/mrss/'
            var media = new Namespace( result[1] );
            var fileext = /([^\/]+)$/;
         }

    		var item, obj;
    		for each (item in rss.channel..item)
    		{
    		   obj = {
    		      "title": item.title.toString(),
    		      "description": item.description.toString(),
    		      "link": item.link.toString()
    		   };
            
            if (hasMediaExtension)
            {
               var thumbnail = item.media::thumbnail;
             	if (thumbnail)
             	{
             		obj["image"] = item.media::thumbnail.@url.toString();
             	}

          		var attachment = item.media::content;
          		if (attachment)
          		{
          		   var contenturl = attachment.@url.toString();
          		   if (contenturl.length > 0)
          		   {
          		      var filename = fileext.exec(contenturl)[0];
                		// Use the file extension to figure out what type it is for now
                		var ext = filename.split(".");

             		   obj["attachment"] = {
             		      "url": contenturl,
             		      "name": filename,
             		      "type": (ext[1] ? ext[1] : "_default")
             		   }
          		   }
          		}
            }
    		  
    		   items.push(obj);
    		}
    	}	
    }
    
    return items;
 }
