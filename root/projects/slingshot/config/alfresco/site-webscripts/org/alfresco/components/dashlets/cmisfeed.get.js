<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils/feed.utils.js">

// Setup Repository CMIS page URL
// Note: this is not the recommended approach - the formal endpoint api should be used
model.remote = config.scoped["Remote"].remote.getEndpointDescriptor("alfresco").endpointUrl;

// Use the default
var conf = new XML(config.script);
uri = conf.feed[0].toString();

var connector = remote.connect("http");
var re = /^http:\/\//;
if (!re.test(uri))
{
   uri = "http://" + uri;
}
model.uri = uri;
model.limit = args.limit || 999;
model.target = args.target || "_self";

model.items = getRSSFeed(uri);
