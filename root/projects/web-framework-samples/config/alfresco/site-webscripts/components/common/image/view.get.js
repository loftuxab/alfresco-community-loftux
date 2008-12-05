<import resource="/components/common/js/component.js">

var source = WebStudio.Component.getSource();
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

	var text = instance.properties["imageText"];
	if(text != null)
	{
		model.text = text;
	}

	model.ready = true;
}
