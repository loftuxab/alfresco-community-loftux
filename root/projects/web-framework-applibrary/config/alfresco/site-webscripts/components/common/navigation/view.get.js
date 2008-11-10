// renderer attribute
var renderer = instance.properties["renderer"];
if(renderer == null)
{
	renderer = "horizontal";
}
model.renderer = renderer;

// background color attribute
var backgroundColor = instance.properties["backgroundColor"];
if(backgroundColor == null)
{
	backgroundColor = "lightblue";
}
model.backgroundColor = backgroundColor;

// set up rendering attributes
model.rootpage = sitedata.getRootPage();
model.linkbuilder = context.getLinkBuilder();
