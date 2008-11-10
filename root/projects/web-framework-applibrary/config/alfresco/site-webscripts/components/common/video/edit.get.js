<import resource="/include/support.js">

var properties = instance.object.properties;

// title field
model.title = form.bind("title", properties["title"], "");
model.description = form.bind("description", properties["description"], "");
model.renderer = form.bind("renderer", properties["renderer"], "");
model.backgroundColor = form.bind("backgroundColor", properties["backgroundColor"], "");

