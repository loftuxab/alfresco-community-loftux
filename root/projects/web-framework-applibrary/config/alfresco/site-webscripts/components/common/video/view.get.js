var source = instance.object.resources.get("source");
if(source == null)
{
	model.ready = false;
}
else
{
	model.src = url.context + source.proxiedDownloadURI;


	// mimetype
	model.mimetype = instance.object.properties["mimetype"];
	if(model.mimetype != null)
	{
		model.isVideo = (model.mimetype.substring(0,5) == "video");
	}
	
	if(!model.isVideo)
	{
		model.msg = "The source file configured for this video component is not a video file.";
	}
	else
	{

		model.useQuicktime = false;
		model.useWindowsMedia = false;
		model.useReal = false;
		model.useShockwave = false;
		model.useJW = false;

		model.swfObjectPath = url.context + "/components/common/video/jw/player.swf";
		model.swfImagePreview = url.context + "/components/common/video/jw/preview.jpg";

		// which player to use
		model.player = instance.object.properties["player"];
		if(model.player == null || model.player == "quicktime")
		{
			model.useQuicktime = true;
		}
		if(model.player == "windowsmedia")
		{
			model.useWindowsMedia = true;
		}
		if(model.player == "shockwave")
		{
			model.useShockwave = true;
		}
		if(model.player == "real")
		{
			model.useReal = true;
		}
		if(model.player == "jw")
		{
			model.useJW = true;
		}
	
	}
}

