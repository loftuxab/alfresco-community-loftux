WebStudio.Applets.Videos = WebStudio.Applets.Abstract.extend({
});

WebStudio.Applets.Videos.prototype.getDependenciesConfig = function()
{
	return {
		"images" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : WebStudio.overlayPath + "/applets/videos/videos.class.css.jsp"
				}
			}
		}
	};
};

WebStudio.Applets.Videos.prototype.bindSliderControl = function(container) 
{
	if(!this.VideosView)
	{
		var controlTemplate = this.instantiateControlTemplate(this.getId(), 'AlfrescoContentViewTemplate');
		
		this.videosView = new WebStudio.ContentView('Control_' + this.getId());
		this.videosView.setTemplate(controlTemplate);
		this.videosView.setInjectObject(container);
		this.videosView.application = this.getApplication();
		
		// set up content
		var items = [
			{
				url : "/studio/images/common/filetypes/avi-128.png",
				thumbnailUrl : "/studio/images/common/filetypes/avi-128.png",
				title : "Sample AVI file",
				description : "Description of AVI file",
				mimetype: "png",
				endpoint: "http"				
			}
			,
			{
				url : "/studio/images/common/filetypes/mov-128.png",
				thumbnailUrl : "/studio/images/common/filetypes/mov-128.png",
				title : "Sample MOV file",
				description : "Description of MOV file",
				mimetype: "png",
				endpoint: "http"				
			}
			,
			{
				url : "/studio/images/common/filetypes/mpg-128.png",
				thumbnailUrl : "/studio/images/common/filetypes/mpg-128.png",
				title : "Sample MPG file",
				description : "Description of MPG file",
				mimetype: "png",
				endpoint: "http"				
			}
			,
			{
				url : "/studio/images/common/filetypes/other_movie-128.png",
				thumbnailUrl : "/studio/images/common/filetypes/other_movie-128.png",
				title : "Sample Movie file",
				description : "Description of Movie file",
				mimetype: "png",
				endpoint: "http"				
			}
			,
			{
				url : "/studio/images/common/filetypes/wmv-128.png",
				thumbnailUrl : "/studio/images/common/filetypes/wmv-128.png",
				title : "Sample WMV file",
				description : "Description of WMV file",
				mimetype: "png",
				endpoint: "http"								
			}
		];
		this.videosView.setItems(items);
		
		// set up the videos to be draggable
		this.videosView.draggable = true;
		this.videosView.draggableScope = "region";
		this.videosView.draggableType = "contentViewVideo";		
		
		// activate the control
		this.videosView.activate();
		
		var _this = this;

		// TODO		
	}
	
	return this.VideosView;
};

WebStudio.Applets.Videos.prototype.onShowApplet = function()
{
	if (this.VideosView)
	{
		this.VideosView.onResize();
	}

	// hide all designers
	this.getApplication().hideAllDesigners();
	   
	// show the page editor
	this.getApplication().showPageEditor();
};

WebStudio.Applets.Videos.prototype.onHideApplet = function()
{
	// hide the page editor
	this.getApplication().hidePageEditor();
};
