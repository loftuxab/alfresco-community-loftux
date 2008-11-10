<import resource="/components/common/js/mimetypes.js">

var source = instance.object.resources.get("source");
if(source == null)
{
	model.ready = false;
	
}
else
{
	model.metadata = source.metadata;

	model.container = eval('(' + model.metadata + ')');


	// property: view
	var view = instance.object.properties["view"];
	if(view == null)
	{
		view = "views/list";
	}
	model.view = view;

	// property: icon size
	var iconSize = instance.object.properties["iconSize"];
	if(iconSize == null)
	{
		iconSize = "72";
	}


	// set up mimetype icons
	var mimetypes = new WebStudio.Mimetypes();
	for(var i = 0; i < model.container.children.length; i++)
	{
		var child = model.container.children[i];

		var mimetype = child.mimetype;
		var filename = child.title;

		var iconUrl = mimetypes.getIcon(filename, mimetype, iconSize);
		child.iconUrl = iconUrl;
	}

	// set up link urls
	for(var i = 0; i < model.container.children.length; i++)
	{
		var child = model.container.children[i];

		var linkUrl = context.linkBuilder.object(child.id);
		child.linkUrl = linkUrl;
	}
	
	model.ready = true;
}