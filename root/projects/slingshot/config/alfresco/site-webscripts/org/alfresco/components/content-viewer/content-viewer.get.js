var contentviewer =  new XML(config.script);

var nodeRef = page.url.args.nodeRef;
if(nodeRef == null || nodeRef.length == 0)
{
   status.code = 400;
   status.message = "Parameter 'nodeRef' is missing.";
   status.redirect = true;
}
logger.log("CALL:" + "/api/metadata?nodeRef=" + nodeRef);
var json = remote.call("/api/metadata?nodeRef=" + nodeRef)
logger.log("JSOPN:" + json);
var node = eval('(' + json + ')');

// If no viewer is configured for the nodes mime-type display a fallback template with an explanation text
var viewer = "viewers/no-viewer-exist.ftl";
var preload = false;
var content = null;

var contentViewer = contentviewer.content.(@mimetype==node.mimetype);
if(contentViewer.length() == 1)
{
   viewer = contentViewer.@viewer.toString();
   preload = contentViewer.@preload == "true";
}
else if(contentViewer.length() > 1){
   status.code = 500;
   status.message = "Multiple viewers (" + contentViewer.@viewer.toString() + ") defined for the mime-type ${node.mimetype}";
   status.redirect = true;
}
if(preload)
{
   logger.log("PRELOAD!");
   var mcns = "{http://www.alfresco.org/model/content/1.0}";
   var contentUrl = node.properties[mcns + "content"];
   logger.log("PRELOAD:" + contentUrl);
   content = remote.call(contentUrl);
   logger.log("PRELOADED:" + content);

   // start-test-data
   /*
   if(node.mimetype == "text/plain")
   {
      content = "This is TEXT";
   }
   else if(node.mimetype == "text/html")
   {
      content = "<html><body>This is HTML</body></html>";
   }
   else if(node.mimetype == "text/xml")
   {
      content = "<test><description>This is XML</description></test>";
   }
   */
   // end-test-data
}


logger.log("VIEWER:" + viewer);
logger.log("PRELOAD:" + preload);
logger.log("CONTENT:" + content);

model.viewer = viewer;
model.node = node;
model.content = content;














