if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.TreeView = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;
	
	this.ID = index;
	
	if (typeof YAHOO == "undefined") 
	{
		this.addDebugMessages('error', 'YAHOO UI Library is needed for build Alfresco.TreeView');
		return null;
	} 
	else if (typeof YAHOO.widget == "undefined") 
	{
		this.addDebugMessages('error', 'YAHOO.widget.TreeView component of YAHOO UI Library is needed for build Alfresco.TreeView');
		return null;
	} 
	else if (typeof YAHOO.widget.TreeView == "undefined") 
	{
		this.addDebugMessages('error', 'YAHOO.widget.TreeView component of YAHOO UI Library is needed for build Alfresco.TreeView');
		return null;
	}
	
	this.defaultTemplateSelector = 'div[id=AlfrescoTreeViewTemplate]';
	
	this.defaultElementsConfig = {
		MenuTemplate: {
			selector: 'div[id=ATVMenuTemplate]',
			remove: true
		},
		MenuHolder: {
			selector: 'div[id=ATVMenu]'
		},
		TreeHolder: {
			selector: 'div[id=ATVTree]'
		}
	};

	this.events = {};
	
	this.nodes = {};
	this.droppables = [];
};

WebStudio.TreeView.prototype = new WebStudio.AbstractTemplater('WebStudio.TreeView');

WebStudio.TreeView.prototype.activate = function() 
{
	var _this = this;
	
	this.buildGeneralLayer();
	this.tree = new YAHOO.widget.TreeView(this.TreeHolder.el);

	this.tree.subscribe("labelClick", function(node) {
		_this.setActiveNode(node.data.id);
		return false;
	});

	if (this.MenuHolder && this.MenuHolder.el) 
	{
		this.menu = new WebStudio.Menu(this.ID+'Menu');
		this.menu.setConfig({roots: {blockSelection: true}});
		this.menu.setInjectObject(this.MenuHolder.el);
		this.menu.setTemplate(this.MenuTemplate.el);
		this.menu.activate();
		this.menu.show();
	}

	return this;
};

WebStudio.TreeView.prototype.setActiveNode = function(nodeID)
{
	var label = null;
	var table = null;
	
	if (this.nodes[nodeID]) 
	{
		if (this.activeNode) 
		{
			label = $(this.activeNode.getLabelEl());
			table = Alf.getParentByTag(label, 'table');
			table.removeClass('Active');
		}
		this.activeNode = this.nodes[nodeID];
		label = $(this.activeNode.getLabelEl());
		label.addEvent('focus', function(){
			this.blur();
		});
		label.blur();
		table = Alf.getParentByTag(label, 'table');
		table.addClass('Active');
				
		if(this.onChangeActiveNode)
		{
			this.onChangeActiveNode();
		}
	}
	return this;
};

WebStudio.TreeView.prototype.getActiveNode = function() 
{
	return this.activeNode;
};

WebStudio.TreeView.prototype.draw = function(params) 
{
	this.tree.draw(params);
	return this;
};

WebStudio.TreeView.prototype.addNodeLink = function(index, node) 
{
	this.nodes[index] = node;
	return this;
};

WebStudio.TreeView.prototype.getNodeByNodeID = function(nodeID) 
{
	if (this.nodes[nodeID])
	{
		return this.nodes[nodeID];
	}
	else
	{
		return false;
	}
};

WebStudio.TreeView.prototype.setDynamicLoad = function(params) 
{
	this.tree.setDynamicLoad(params);
	return this;
};

WebStudio.TreeView.prototype.createDraggables = function () 
{
	var _this = this;
	
	$each(this.nodes, function(item, index) 
	{
		if (item.data.draggable) 
		{
			_this.setDraggableNode(index);
		}
	});
	return this;
};

WebStudio.TreeView.prototype.setDroppables = function(map) 
{
	var _this = this;
	$each(map, function(it)
	{
		it.colorOverlay.addEvents({
			'over': function(el, obj){
				this.setOpacity(0.5);
			},
			'leave': function(el, obj){
				this.setOpacity(0.2);
			},
			'drop': function(el, obj){			
				
				el.remove();
				
				obj.options.label.fireEvent('mouseup');
				
				_this.dropFromTreeView(it.id, obj.options);
			}
		});
		
		_this.droppables.push(it.colorOverlay);
	});
	return this;
};

WebStudio.TreeView.prototype.checkExistsComponentInRegion = function(regionDivId) 
{
	var regionDiv = $(regionDivId);
	if (regionDiv.getAttribute("componentId"))
	{
		return true;
	}
	return false;
};

WebStudio.TreeView.prototype.setDraggableNode = function(nodeId) 
{
	var _this = this;
	
	var node = this.nodes[nodeId];
	if (node) 
	{
		var label = $(node.getLabelEl());
		if(label)
		{
			var table = Alf.getParentByTag(label, 'table');
			this.blockSelection(table);
			label.set({
				ATVID: this.ID,
				ATVNodeId: nodeId
			});
			label.addEvent('mousedown', function(e) {
				e = new Event(e).stop();
				this.setProperty('drag', 'true');
				this.setProperty('mdx', e.client.x);
				this.setProperty('mdy', e.client.y);
			});
			label.addEvent('mousemove', function(event) {
				if (this.getProperty('drag')) 
				{
					event = new Event(event);
					var mdx = this.getProperty('mdx').toInt();
					var mdy = this.getProperty('mdy').toInt();
					var distance = Math.sqrt((mdx - event.client.x)*(mdx - event.client.x) + (mdy - event.client.y)*(mdy - event.client.y)).toInt();
					if (distance > 5) 
					{
						var atv = WebStudio.TreeViews[this.getProperty('ATVID')];
						atv.dragNodeId = this.getProperty('ATVNodeId');
						var clone = this.clone();
						clone.set({id: ''});
						clone.setStyles(this.getCoordinates());
						clone.setStyles({
							'opacity': 0.7,
							'position': 'absolute',
							'z-index': 10,
							'width': 200
						});
						clone.addClass('ATVWebComponents');
						clone.addEvent('emptydrop', function() 
						{
							var atv = WebStudio.TreeViews[this.getProperty('ATVID')];
							var dragNode = atv.nodes[atv.dragNodeId];
							var label = $(dragNode.getLabelEl());
							var fx = new Fx.Styles(this, {duration: 300, transition: Fx.Transitions.linear});
							fx.addEvent('onComplete', function() {
								_this.remove();
							});
							var coor = label.getCoordinates();
							label.removeProperty('drag');
							fx.start({top: coor.top, left: coor.left, opacity: 0.2});						
							
						});
						clone.inject(document.body);
	
						var drag = clone.makeDraggable(
						{
						 droppables: _this.droppables,
						 nodeId: nodeId,
						 data: node.data,
						 label: this
						});
						drag.start(event);
					}
				}
			});
			
			label.addEvent('mouseup', function() 
			{
				this.removeProperty('drag');
			});
		}
	}
	return this;
};

WebStudio.TreeView.prototype.getRoot = function() 
{
	return this.tree.getRoot();
};

WebStudio.TreeView.prototype.build = function() 
{
	this.generalLayer.set({
		id: this.ID
	});
};

WebStudio.TreeView.prototype.setElementConfig = function(item, index, ob, config, oel) 
{ 
	//'item' = html dom element, 'index' = name of array of 'item's; ob = js container for 'index'; config = js object with configuration for item
};

WebStudio.TreeView.prototype.dropFromTreeView = function(dropDivId, options)
{
};

WebStudio.TreeView.prototype.getMenu = function()
{
	return this.menu;
};
