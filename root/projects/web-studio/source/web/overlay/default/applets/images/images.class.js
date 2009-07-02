WebStudio.Applets.Images = WebStudio.Applets.Abstract.extend({
});

WebStudio.Applets.Images.prototype.getDependenciesConfig = function()
{
	return {
		"images" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : WebStudio.overlayPath + "/applets/images/images.class.css.jsp"
				}
			}
		}
	};
};

WebStudio.Applets.Images.prototype.bindSliderControl = function(container) 
{
	if(!this.imagesView)
	{
		var controlTemplate = this.instantiateControlTemplate(this.getId(), 'AlfrescoContentViewTemplate');
		
		this.imagesView = new WebStudio.ContentView('Control_' + this.getId());
		this.imagesView.setTemplate(controlTemplate);
		this.imagesView.setInjectObject(container);
		this.imagesView.application = this.getApplication();
		
		// set up content
		var items = [
			{
				url : "/studio/images/common/filetypes/bmp-128.png",
				thumbnailUrl : "/studio/images/common/filetypes/bmp-128.png",
				title : "Sample BMP file",
				description : "Sample BMP file description",
				mimetype: "png",
				endpoint: "http"
			}
			,
			{
				url : "/studio/images/common/filetypes/gif-128.png",
				thumbnailUrl : "/studio/images/common/filetypes/gif-128.png",
				title : "Sample GIF file",
				description : "Sample GIF file description",
				mimetype: "png",
				endpoint: "http"
			}
			,
			{
				url : "/studio/images/common/filetypes/jpg-128.png",
				thumbnailUrl : "/studio/images/common/filetypes/jpg-128.png",
				title : "Sample JPG file",
				description : "Sample JPG file description",
				mimetype: "png",
				endpoint: "http"
			}
			,
			{
				url : "/studio/images/common/filetypes/other_image-128.png",
				thumbnailUrl : "/studio/images/common/filetypes/other_image-128.png",
				title : "Sample Image file",
				description : "Sample Image file description",
				mimetype: "png",
				endpoint: "http"
			}
			,
			{
				url : "/studio/images/common/filetypes/png-128.png",
				thumbnailUrl : "/studio/images/common/filetypes/png-128.png",
				title : "Sample PNG file",
				description : "Sample PNG file description",
				mimetype: "png",
				endpoint: "http"
			}
		];
		this.imagesView.setItems(items);
		
		// set up the images to be draggable
		this.imagesView.draggable = true;
		this.imagesView.draggableScope = "region";
		this.imagesView.draggableType = "contentViewImage";
		
		// activate the control
		this.imagesView.activate();
		
		var _this = this;

		// TODO		
	}
	
	return this.imagesView;
};

WebStudio.Applets.Images.prototype.onShowApplet = function()
{
	if (this.imagesView)
	{
		this.imagesView.onResize();
	}

	// hide all designers
	this.getApplication().hideAllDesigners();
	   
	// show the page editor
	this.getApplication().showPageEditor();
};

WebStudio.Applets.Images.prototype.onHideApplet = function()
{
	// hide the page editor
	this.getApplication().hidePageEditor();
};
