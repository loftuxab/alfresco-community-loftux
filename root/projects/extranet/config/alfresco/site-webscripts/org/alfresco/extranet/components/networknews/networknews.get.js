//
// configuration: endpoint
//
var endpoint = instance.properties["endpoint"];
if(endpoint == null)
{
	endpoint = "alfresco";
}
model.endpoint = endpoint;

//
// configuration: itemType
//
var itemType = instance.properties["itemType"];
model.itemType = itemType;

//
// configuration: presentation
//
var presentation = instance.properties["presentation"];
if(presentation == null)
{
	presentation = "list";
}
model.presentation = presentation;

//
// configuration: maxcount
//
var maxcount = instance.properties["maxcount"];
if(maxcount != null)
{
	maxcount = parseInt(maxcount);
}
if(maxcount == null)
{
	maxcount = 1000;
}
model.maxcount = maxcount;



// build the url
var uri = "/network/news/retrieval";
if(itemType != null && itemType.length > 0)
{
	uri += "?itemType=" + itemType;
}

// call the endpoint
var connector = remote.connect("alfresco");
var json = connector.call(uri);

var obj = eval('(' + json + ')');
model.items = obj.results;
