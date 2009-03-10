var uri = args.webviewURI;

if (!uri)
{
   // Use the default
   var conf = new XML(config.script);
   uri = conf.uri[0].toString();
}

var webviewTitle = args.webviewTitle;
if (!webviewTitle)
{
   webviewTitle = "";
}

var connector = remote.connect("http");
var re = /^http:\/\//;
if (!re.test(uri))
{
   uri = "http://" + uri;
}

model.webviewTitle = webviewTitle;
model.uri = uri;
