<import resource="/components/common/js/component.js">

var source = WebStudio.Component.getSource();
if(source == null)
{
	model.ready = false;
	
}
else
{
	// set up the source
	model.src = url.context + source.proxiedDownloadURI;

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

