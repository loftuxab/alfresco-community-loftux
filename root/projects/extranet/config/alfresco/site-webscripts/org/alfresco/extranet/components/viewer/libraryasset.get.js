var id = content.id;
var endpoint = content.endpointId;
if(endpoint == null)
{
	endpoint = "alfresco";
}

// extract the id
var rawId = id;
var i = id.lastIndexOf("/");
if(i > -1)
{
	rawId = id.substring(i+1, rawId.length());
}

model.content = content;

// load the content object
var connector = remote.connect(endpoint);
var objectString = connector.get("/webframework/content/metadata?id="+id);
model.objectString = objectString;

var obj = eval('(' + objectString + ')');
model.obj = obj;
