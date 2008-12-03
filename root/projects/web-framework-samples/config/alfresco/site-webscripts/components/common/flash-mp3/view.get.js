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
	
	model.appearance = instance.properties["appearance"];
	if(model.appearance == null || model.appearance == "")
	{
		model.appearance = "full";
	}

	model.songTitle = instance.properties.songTitle;
	if(model.songTitle == null || model.songTitle == "")
	{
		model.songTitle = source.id;
	}

	model.ready = true;
}
