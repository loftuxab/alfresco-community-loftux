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

// load the content object
var connector = remote.connect(endpoint);
var data = connector.get("/api/node/content/workspace/SpacesStore/" + rawId);

model.data = data;
model.content = content;