<import resource="/include/support.js">

var properties = instance.object.properties;

model.title = form.bind("title", properties["title"], "");
model.description = form.bind("description", properties["description"], "");

// source bindings
model.fetch = form.bind("fetch", properties["fetch"], "");
model.source = form.bind("source", properties["source"], "");

// image attributes
model.imageText = form.bind("imageText", properties["imageText"], "");
