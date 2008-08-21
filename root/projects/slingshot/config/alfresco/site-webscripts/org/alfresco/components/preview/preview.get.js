// Check mandatory parameters
var nodeRef = args.nodeRef; //page.url.args.nodeRef;
if(nodeRef == null || nodeRef.length == 0)
{
   status.code = 400;
   status.message = "Parameter 'nodeRef' is missing.";
   status.redirect = true;
}

// Call repo for node's metadata
var json = remote.call("/api/metadata?nodeRef=" + nodeRef)
var n = eval('(' + json + ')');
var mcns = "{http://www.alfresco.org/model/content/1.0}";

// Call repo for available previews
json = remote.call("/api/node/" + nodeRef.replace(":/", "") + "/content/thumbnaildefinitions");
var previews =  eval('(' + json + ')');

// Prepare the model
var node = {};
node.nodeRef = nodeRef;
node.name = n.properties[mcns + "name"];
node.icon = "/components/images/generic-file-32.png";
node.mimeType = n.mimetype;
node.previews = previews;

model.node = node;
