var requestContext = site.getRequestContext();
var currentObjectId = requestContext.getCurrentObjectId();

var connector = remote.connect("alfresco");

var uri = "/slingshot/doclib/doclist?path=/";
if(currentObjectId != null)
{
	uri = "/slingshot/doclib/doclist?nodeRef=" + currentObjectId;
}

var json = connector.call(uri);
var docs = eval('(' + json + ')');
model.items = docs.doclist.items;
