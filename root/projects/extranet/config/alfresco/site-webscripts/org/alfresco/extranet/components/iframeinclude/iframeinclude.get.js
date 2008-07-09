// these properties are pulled from the component instance xml

var path = instance.properties["content-path"];
var endpoint = instance.properties["endpoint"];

var url = url.context + "/proxy/" + endpoint + "/api/path/content/workspace/SpacesStore/Company%20Home" + path;

model.url = url;

var height = instance.properties["height"];
if(height == null)
{
	height = "600px";
}
model.height = height;
