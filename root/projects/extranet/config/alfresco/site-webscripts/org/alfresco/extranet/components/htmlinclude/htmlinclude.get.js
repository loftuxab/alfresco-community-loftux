// these properties are pulled from the component instance xml

var path = instance.properties["content-path"];
var endpoint = instance.properties["endpoint"];

var url = "/api/path/content/workspace/SpacesStore/Company%20Home" + path;

var connector = remote.connect(endpoint);
var content = connector.call(url);

model.content = content;
