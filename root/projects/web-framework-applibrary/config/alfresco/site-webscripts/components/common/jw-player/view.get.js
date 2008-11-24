<import resource="/components/common/js/component.js">

var source = WebStudio.Component.getSource();
if(source == null || source.value == null || source.value == "")
{
	// just use JW's default video
	model.src = url.context + "/components/common/jw-player/video.flv";
}
else
{
	model.src = url.context + source.proxiedDownloadURI;
}

var previewImageUrl = instance.object.properties["previewImageUrl"];
if(previewImageUrl == null || previewImageUrl == "")
{
	previewImageUrl = url.context + "/components/common/jw-player/preview.jpg";
}
model.previewImageUrl = previewImageUrl;
	
model.ready = true;

