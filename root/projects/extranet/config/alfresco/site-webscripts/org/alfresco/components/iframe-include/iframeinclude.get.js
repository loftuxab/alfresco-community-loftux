var path = instance.properties["path"];
if(path == null)
{
	path = "/";
}

var endpoint = instance.properties["endpoint"];
if(endpoint == null)
{
	endpoint = "alfresco";
}

var url = url.context + "/proxy/" + endpoint + path;
model.url = url;

var width = instance.properties["width"];
if(width == null)
{
	width = "800px";
}
model.width = width;

var height = instance.properties["height"];
if(height == null)
{
	height = "600px";
}
model.height = height;
