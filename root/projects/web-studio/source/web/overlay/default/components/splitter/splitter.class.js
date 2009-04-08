if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.Splitter = function() 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.defaultTemplateSelector = "div[id=AlfSplitterTemplate]";

	this.dividerSize = 8;
	this.minLeftWidth = 0;
	this.minRightWidth = 0;

	this.defaultElementsConfig = {

		AlfSplitterPanel : {
			selector : "div[id=AlfSplitterPanel]",
			remove : true
		},

		AlfSplitterDivider : {
			selector : "div[id=AlfSplitterDivider]",
			remove : true
		},

		AlfSplitterCover : {
			selector : "div[id=AlfSplitterCover]",
			remove : true
		},

		AlfSplitterContainer : {
			selector : "div[id=AlfSplitterContainer]",
			remove : true
		}
	};
	return this;
};

WebStudio.Splitter.prototype = new WebStudio.AbstractTemplater('WebStudio.Splitter');

WebStudio.Splitter.prototype.activate = function (injectObject) 
{
	if (injectObject)
	{
		this.injectObject = injectObject;
	}

	this.buildGeneralLayer();
	this.attachPanels();
	this.attachEvents();
};

WebStudio.Splitter.prototype.attachPanels = function() 
{
	this.container = $(this.AlfSplitterContainer.el.clone());
	this.firstPanel = $(this.AlfSplitterPanel.el.clone());
	this.firstPanel.id = "AlfSplitterPanel1";
	this.divider = $(this.AlfSplitterDivider.el.clone());
	this.secondPanel = $(this.AlfSplitterPanel.el.clone());
	this.secondPanel.id = "AlfSplitterPanel2";

	this.container.injectInside(this.generalLayer);

	this.firstPanel.injectInside(this.container);
	this.divider.injectInside(this.container);
	this.secondPanel.injectInside(this.container);

	this.createCovers();

	var initialWidth = (this.container.offsetWidth - this.dividerSize) / 2;
	initialWidth = Math.max(1, initialWidth);

	this.firstPanel.setStyles({
		top : 0,
		left : 0,
		width : initialWidth
	});

	this.divider.setStyles({
		top : 0,
		left : initialWidth
	});

	this.divider.setStyle('width', this.dividerSize);

	// move the second panel down 3 for formatting purposes
	// TODO: do the other panels need to slide down as well?
	this.secondPanel.setStyles({
		top : 3,
		left : (initialWidth + this.dividerSize),
		width : initialWidth
	});
};

WebStudio.Splitter.prototype.attachEvents = function() 
{
	$(document.body).addEvents({

		mousemove : this.onMouseMove.bind(this),
		mouseup : this.onMouseUp.bind(this)

	});

	this.divider.addEvent("mousedown", this.onDividerMouseDown.bind(this));
};

WebStudio.Splitter.prototype.createCovers = function() 
{
	this.firstCover = $(this.AlfSplitterCover.el.clone());
	this.secondCover = $(this.AlfSplitterCover.el.clone());
};

WebStudio.Splitter.prototype.onDividerMouseDown = function(e) 
{
	var event = new Event(e);
	this.firstCover.injectInside(document.body);
	this.secondCover.injectInside(document.body);

	this.oldX = event.client.x;
	this.oldOffsetWidth = this.firstPanel.offsetWidth;

	this.placeCovers();
	this.blockSelection(document.body);
	this.active = true;
	event.preventDefault();
	return false;
};

WebStudio.Splitter.prototype.onMouseMove = function(e) 
{
	e = new Event(e);
	if (!this.active)
	{
		return;
	}
	var x = e.client.x;

	var dx = x - this.oldX;
	var newFirstWidth = this.oldOffsetWidth + dx;
	if (newFirstWidth < this.minLeftWidth) 
	{
		newFirstWidth = this.minLeftWidth;
	}
	var newSecondWidth = this.container.offsetWidth - newFirstWidth - this.dividerSize;

	if (newSecondWidth < this.minRightWidth) 
	{
		newSecondWidth = this.minRightWidth;
		newFirstWidth = this.container.offsetWidth - newSecondWidth - this.dividerSize;
	}

	this.firstPanel.setStyle('width', Math.max(1, newFirstWidth));
	this.divider.setStyle('left', newFirstWidth);
	this.secondPanel.setStyles({
		width : Math.max(1, newSecondWidth),
		left : (newFirstWidth + this.dividerSize)
	});

	this.placeCovers();
	this.onPanelsResize(newFirstWidth, newSecondWidth);
};

WebStudio.Splitter.prototype.onMouseUp = function() 
{
	if (this.firstCover && this.secondCover && this.active) 
	{
		this.firstCover.parentNode.removeChild(this.firstCover);
		this.secondCover.parentNode.removeChild(this.secondCover);
	}
	this.active = false;
	this.unblockSelection(document.body);
	this.oldX = -1;
};

WebStudio.Splitter.prototype.placeCovers = function() 
{
	this.firstCover.setStyles({
		left : this.firstPanel.offsetLeft,
		width : this.firstPanel.offsetWidth,
		height : this.firstPanel.offsetHeight
	});

	this.secondCover.setStyles({
		left : this.secondPanel.offsetLeft,
		width : this.secondPanel.offsetWidth,
		height : this.secondPanel.offsetHeight
	});
};

WebStudio.Splitter.prototype.setPanelsSize = function (firstPanelSize) 
{
	this.firstPanel.setStyle('width', firstPanelSize);
	this.divider.setStyle('left', firstPanelSize);
	this.secondPanel.setStyles({
		width : (this.container.offsetWidth - firstPanelSize - this.dividerSize),
		left : (firstPanelSize + this.dividerSize)
	});
};

WebStudio.Splitter.prototype.hidePanel = function (first) 
{
	var fPanel = first ? this.firstPanel : this.secondPanel;
	var sPanel = first ? this.secondPanel : this.firstPanel;
	this.firstPanelSize = this.firstPanel.offsetWidth;
	fPanel.setStyle('width', 0);
	this.divider.setStyles({
		left : 0,
		width : 0
	});
	sPanel.setStyles({
		left : 0,
		width : this.container.offsetWidth
	});
	
	this.hiddenDivider = true;
};

WebStudio.Splitter.prototype.showPanels = function () 
{
	this.setPanelsSize(this.firstPanelSize);
	this.divider.setStyle('width', this.dividerSize);
	
	this.hiddenDivider = false;
};

WebStudio.Splitter.prototype.setHeight = function (height) 
{
	height = Math.max(1, height);
	this.firstPanel.setStyle('height', height);
	this.divider.setStyle('height', height);
	this.secondPanel.setStyle('height', height);
	this.container.setStyle('height', height);
	this.generalLayer.setStyle('height', height);
};

WebStudio.Splitter.prototype.getHeight = function() 
{
	return this.container.getCoordinates().height;
};

WebStudio.Splitter.prototype.setWidth = function (width) 
{
	width = Math.max(1, width);
	
	var newSecondWidth = width - this.firstPanel.offsetWidth - this.dividerSize;
	
	// if the docking panel was hidden, utilize the full screen	
	if(this.hiddenDivider)
	{	
		newSecondWidth = width - this.firstPanel.offsetWidth;
	}

	this.container.setStyle('width', width);
	this.generalLayer.setStyle('width', width);
	this.injectObject.setStyle('width', width);

	this.secondPanel.setStyle('width', newSecondWidth);
};

WebStudio.Splitter.prototype.blockSelection = function(object) 
{
	object = $(object);
	if (typeof(object) == 'object') 
	{
		object.onselectstart = function(event) {
			event = new Event(event);
			event.preventDefault();
			return false;
		};
		object.setStyles({
			'-moz-user-select': 'none',
			'-khtml-user-select': 'none',
			'user-select': 'none'
		});
	}
};

WebStudio.Splitter.prototype.unblockSelection = function(object) {
	object = $(object);
	if (typeof(object) == 'object') 
	{
		object.onselectstart = null;
		object.setStyles({
			'-moz-user-select': '',
			'-khtml-user-select': '',
			'user-select': ''
		});
	}
};

WebStudio.Splitter.prototype.onPanelsResize = function (firstPanelSize, seconPanelSize) 
{
	//external method
};