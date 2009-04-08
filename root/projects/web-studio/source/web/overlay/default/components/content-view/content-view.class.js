if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.ContentView = function() 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.defaultTemplateSelector = 'div[id=AlfrescoContentViewTemplate]';
	
	this.defaultElementsConfig = {
		MenuTemplate: {
			selector: 'div[id=ACVMenuTemplate]',
			remove: true
		},
		MenuHolder: {
			selector: 'div[id=ACVMenu]'
		},
		Body: {
			selector: 'div[id=ACVBody]',
			objects: {
				ScrollContainer: {
					selector: '.scrollContainer'
				}
            }			
		}
	};

	this.events = {};
	
	this.INDEX_BUTTON_ADD = 0;
	this.INDEX_BUTTON_EDIT = 1;
	this.INDEX_BUTTON_COPY = 2;
	this.INDEX_BUTTON_DELETE = 3;	
};

WebStudio.ContentView.prototype = new WebStudio.AbstractTemplater('WebStudio.ContentView');

WebStudio.ContentView.prototype.activate = function() 
{	
	var _this = this;
	
	this.buildGeneralLayer();

	if (this.MenuHolder.el) 
	{
		this.menu = new WebStudio.Menu(this.ID+'Menu');
		this.menu.setConfig({roots: {blockSelection: true}});
		this.menu.setInjectObject(this.MenuHolder.el);
		this.menu.setTemplate(this.MenuTemplate.el);
		this.menu.activate();
		this.menu.show();

		this.menu.addEvent('click', 'ContentViewAddEditDelete', function(group, index) 
		{
			if (group == 'roots') 
			{
				if (index == _this.INDEX_BUTTON_ADD) {
					_this.fireEvent('AddContent');
				} else if (index == _this.INDEX_BUTTON_EDIT) {
					_this.fireEvent('EditContent');
				} else if (index == _this.INDEX_BUTTON_COPY) {
					_this.fireEvent('CopyContent');
				} else if (index == _this.INDEX_BUTTON_DELETE) {
					_this.fireEvent('DeleteContent');
				}
			}
		}, this);
	}
	
	// Set up initial state of buttons
	this.menu.setEnabled('roots', this.INDEX_BUTTON_ADD);
	this.menu.setDisabled('roots', this.INDEX_BUTTON_EDIT);
	this.menu.setDisabled('roots', this.INDEX_BUTTON_COPY);
	this.menu.setDisabled('roots', this.INDEX_BUTTON_DELETE);
		
	// Set up Events
	this.addEvent('AddContent', 'add_content', function() 
	{
		var w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
			refreshSession: 'true'
		});
		var url = WebStudio.ws.studio('/wizard/content/add');
		w.start(url, 'addnewcontent');
		w.onComplete = function() 
		{
		};
		
	}, this);

	this.addEvent('EditContent', 'edit_content', function() 
	{
		var w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
		});
		var url = WebStudio.ws.studio('/wizard/content/edit');
		w.start(url, 'editcontent');
		w.onComplete = function() 
		{
		};

	}, this);

	this.addEvent('CopyContent', 'copy_content', function() 
	{
		var w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
		});
		var url = WebStudio.ws.studio('/wizard/content/copy');
		w.start(url, 'editcontent');
		w.onComplete = function() 
		{
		};

	}, this);

	this.addEvent('DeleteContent', 'delete_content', function() 
	{
		var w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
		});
		var url = WebStudio.ws.studio('/wizard/content/remove');
		w.start(url, 'removecontent');
		w.onComplete = function() 
		{
		};

	}, this);

	var scrollContainer = this.Body[0].ScrollContainer.el;
	var slider = this.Body.el;

	var html = "";
	var items = _this.getItems();
	for (var i = 0; i < items.length; i++)
	{
		var url = items[i].url;
		var thumbnailImageUrl = items[i].thumbnailUrl;
		var title = items[i].title;
		var description = items[i].description;
		
		html += "<div class='panel' id='panel_" + i + "'>";
		html += "  <div class='inside' align='left' valign='top'>";
		html += "    <p style='float:left' width='100%' class='insideParagraph'>";
		html += "       <img id='panel_" + i + "_image' src='" + thumbnailImageUrl + "' alt='image' class='insideImage'>";
		html += "       <h2 class='insideHeader'>" + title + "</h2>";
		html += "       " + description;
		html += "    </p>";
		html += "    <div id='panel_" + i + "_topshadow' class='top-shadow'></div>";
		html += "    <div id='panel_" + i + "_bottomshadow' class='bottom-shadow'></div>";
		html += "  </div>";
		html += "</div>";		
	}

	Alf.setHTML(scrollContainer, html);
	
	// set up events on the panel divs
	var $container			= jQuery(scrollContainer);
	var $panels             = jQuery('div.panel', $container);
		
	$panels.css({'float' : 'left','position' : 'relative'});

	$panels.bind("mouseenter",function(){
		//_this.scaleElement(this, 0.2, 200);
		jQuery(this).removeClass('panel');
		jQuery(this).addClass('panel-over');		
	});
	$panels.bind("mouseleave", function(){
		//_this.scaleElement(this, 0, 50);
		jQuery(this).removeClass('panel-over');
		jQuery(this).addClass('panel');		
	});
	$panels.click(function(){
	
		$panels.each(function() {
			jQuery(this).removeClass('panel-selected');
		});
		
		jQuery(this).addClass('panel-selected');
	});
	$panels.bind("dblclick", function(e) {
		
		var count = parseInt(this.id.substring(6), 10);
		var src = _this.getItems()[count].url;

 		jQuery.facebox({ image: src });
 		
 		e.stopPropagation();
		
    });

	// should we make the labels draggable?    
    if (_this.draggable && _this.draggableType)
    {
	    $panels.each(function()
	    {
			var label = $(this);
			
			var scope = null;
			if (_this.draggableScope)
			{
				scope = _this.draggableScope;
			}
			
			// image properties
			var count = parseInt(this.id.substring(6), 10);
			var sourcePath = _this.getItems()[count].url;
			var sourceType = "url";
			var sourceEndpoint = _this.getItems()[count].endpoint;
			var sourceMimetype = _this.getItems()[count].mimetype;
						
			WebStudio.dd.makeDraggable(label, scope, {
				"source" : "content-view",
				"type" : _this.draggableType,
				"binding" : {
					"sourceType" : sourceType,
					"sourceEndpoint" : sourceEndpoint,
					"sourcePath" : sourcePath,
					"sourceMimetype" : sourceMimetype
				}	
			}, sourcePath);

	    });
	}
	
	return this;
};

WebStudio.ContentView.prototype.build = function() 
{
	this.generalLayer.set({
		id: this.ID
	});
};

WebStudio.ContentView.prototype.scaleElement = function(element, factor, speed)
{
	// recalculate the sizes of all images
	var controlWidth = this.getControlBodyWidth();
	
	var regWidth = controlWidth;
	var regImgWidth = 64;
	var regTitleSize = 12;
	var regParagraphSize = 10;
	
	var expandedWidth = controlWidth;
	var expandedImgWidth = 72;
	var expandedTitleSize = 16;
	var expandedParagraphSize = 14;

	var width = regWidth + ((expandedWidth - regWidth) * factor);
	var imgWidth = regImgWidth + ((expandedImgWidth - regImgWidth) * factor);
	var titleSize = regTitleSize + ((expandedTitleSize - regTitleSize) * factor);
	var paragraphSize = regParagraphSize + ((expandedParagraphSize - regParagraphSize) * factor);

	// default speed
	if (!speed)
	{
		speed = 300;
	}
	
	jQuery(element).animate({ width: width }, speed, "swing");
	jQuery(element).find("img").animate({ width: imgWidth }, speed, "swing");
	jQuery(element).find("h2").animate({ fontSize: titleSize }, speed, "swing");
	jQuery(element).find("p").animate({ fontSize: paragraphSize }, speed, "swing");
};

WebStudio.ContentView.prototype.onResize = function()
{
	var controlWidth = this.getControlBodyWidth();
	
	var scrollContainer = this.Body[0].ScrollContainer.el;
	var $container			= jQuery(scrollContainer);
	var $panels             = jQuery('div.panel', $container);
	
	// walk through the panels
	$panels.each(function() {
	
		// adjust the width of each panel
		jQuery(this).css({'width' : controlWidth});
		
	});

};

WebStudio.ContentView.prototype.getControlBodyWidth = function()
{
	return this.getControlWidth() - 20;
};

WebStudio.ContentView.prototype.getControlWidth = function()
{
	var controlWidth = $('AlfSplitterDivider').style.left;
	
	if (typeof controlWidth == 'string')
	{
		if (controlWidth.indexOf("px") > -1)
		{
			controlWidth = controlWidth.substring(0, controlWidth.length - 2);
			controlWidth = parseInt(controlWidth, 10);
		}	
	}
	
	return controlWidth;
};

WebStudio.ContentView.prototype.setItems = function(_items)
{
	this.items = _items;
};

WebStudio.ContentView.prototype.getItems = function()
{
	return this.items;
};

WebStudio.ContentView.prototype.onDrop = function(dropDivId, options)
{
	WebStudio.app.onDrop(dropDivId, options);
};
