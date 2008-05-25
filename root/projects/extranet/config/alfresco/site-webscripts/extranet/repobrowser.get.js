// create the connector
var connector = remote.connect("alfresco");

// the default uri
var uri = "/slingshot/doclib/doclist?path=/";

// if we have content, adjust the uri
if(context.content != null)
{
	uri = "/slingshot/doclib/doclist?nodeRef=" + context.content.id;
}

var json = connector.call(uri);
var docs = eval('(' + json + ')');
model.items = docs.doclist.items;
