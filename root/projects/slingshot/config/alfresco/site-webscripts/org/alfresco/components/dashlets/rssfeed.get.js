<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils/feed.utils.js">

var uri = args.feedurl;
if (!uri)
{
   // Use the default
   var conf = new XML(config.script);
   uri = conf.feed[0].toString();
}

var connector = remote.connect("http");
var re = /^http:\/\//;
if (!re.test(uri))
{
   uri = "http://" + uri;
}
model.uri = uri;
model.limit = args.limit || 999;
model.target = args.target || "_self";

var feed = getRSSFeed(uri);
model.title = feed.title;
model.items = feed.items;
