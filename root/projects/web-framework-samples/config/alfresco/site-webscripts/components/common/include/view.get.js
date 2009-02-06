<import resource="/components/common/js/component.js">

var source = Surf.Component.getSource();
if(source == null)
{
	model.ready = false;
	
}
else
{
	// set up the source
	var src = source.downloadURI;
	if(src.substring(0,1) == "/")
	{
		src = url.context + source.proxiedDownloadURI;
	}
	model.src = src;

	model.title = instance.properties["title"];
	if(model.title == null)
	{
		model.title = "";
	}

	model.description = instance.properties["description"];
	if(model.description == null)
	{
		model.description = "";
	}

	model.container = instance.properties["container"];
	if(model.container == null)
	{
		model.container = "div";
	}

	model.ready = true;
}

