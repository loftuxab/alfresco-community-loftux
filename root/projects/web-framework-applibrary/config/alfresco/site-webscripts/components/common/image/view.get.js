var source = instance.object.resources.get("source");
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

	var text = instance.properties["imageText"];
	if(text != null)
	{
		model.text = text;
	}

	model.ready = true;
}
