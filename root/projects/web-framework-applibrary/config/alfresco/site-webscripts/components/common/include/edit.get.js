<import resource="/include/support.js">

var properties = instance.object.properties;
var source = instance.object.resources.get("source");

model.title = form.bind("title", properties["title"], "");
model.description = form.bind("description", properties["description"], "");
model.container = form.bind("container", properties["container"], "");

// source
model["sourceType"] = form.bind("sourceType", source.type, "");
model["sourceValue"] = form.bind("sourceValue", source.value, "");
model["sourceEndpoint"] = form.bind("sourceEndpoint", source.endpoint, "");


