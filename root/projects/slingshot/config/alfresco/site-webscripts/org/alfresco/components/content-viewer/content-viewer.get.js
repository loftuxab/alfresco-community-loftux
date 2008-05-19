var contentviewer =  new XML(config.script);

var nodeRef = page.url.args.nodeRef;
if(nodeRef == null || nodeRef.length == 0)
{
   status.code = 400;
   status.message = "Parameter 'nodeRef' is missing.";
   status.redirect = true;
}

// var json = remote.call("api/metadata") Should be somthing like this when metadata service is ready
// now ges how the response could look and supply the mimeType in the nodeRef argument to make testing easy
var json = '{"mimeType": "' + nodeRef + '", "url":"/slingshot/HomeRun.swf"}';
var node = eval('(' + json + ')');

// If no viewer is configured for the nodes mime-type display a fallback template with an explanation text
var viewer = "viewers/no-viewer-exist.ftl";
var preload = false;
var content = null;

var contentViewer = contentviewer.content.(@mimetype==node.mimeType);
if(contentViewer.length() == 1)
{
   viewer = contentViewer.@viewer.toString();
   preload = contentViewer.@preload == "true";
}
else if(contentViewer.length() > 1){
   status.code = 500;
   status.message = "Multiple viewers (" + contentViewer.@viewer.toString() + ") defined for the mime-type ${node.mimeType}";
   status.redirect = true;
}
if(preload)
{
   // Should be somthing like this when the correct url has been passed from the metadata service
   // content = remote.call(node.url);
   // But for now use some testdata:
   // start-test-data
   if(node.mimeType == "text/plain")
   {
      content = "This is TEXT";
   }
   else if(node.mimeType == "text/html")
   {
      content = "<html><body>This is HTML</body></html>";
   }
   else if(node.mimeType == "text/xml")
   {
      content = "<test><description>This is XML</description></test>";
   }
   // end-test-data
}


logger.log("VIEWER:" + viewer);
logger.log("PRELOAD:" + preload);
logger.log("CONTENT:" + content);

model.viewer = viewer;
model.node = node;
model.content = content;














