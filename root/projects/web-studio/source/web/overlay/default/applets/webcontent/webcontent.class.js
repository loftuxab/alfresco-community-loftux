WebStudio.Applets.WebContent = WebStudio.Applets.Abstract.extend({});

WebStudio.Applets.WebContent.prototype.getDependenciesConfig = function()
{
	return {
		"webcontent" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : WebStudio.overlayPath + "/applets/webcontent/webcontent.class.css.jsp"
				}
			}
		}
	};
};

WebStudio.Applets.WebContent.prototype.getTemplateDomId = function()
{
	return "WebContentApplet_Slider";
};

WebStudio.Applets.WebContent.prototype.bindSliderControl = function(container) 
{
	var _this = this;
	
	if(!this.treeView)
	{
		var controlTemplate = this.instantiateControlTemplate(this.getId(), 'AlfrescoTreeViewWebContentTemplate');	
		
		var treeView = new WebStudio.TreeView('Control_'+this.getId());
		treeView.setTemplate(controlTemplate);
		treeView.setInjectObject(container);
		treeView.defaultElementsConfig = {
			MenuTemplate: {
				selector: 'div[id=ATVMenuTemplateWebContent]',
				remove: true
			},
			MenuHolder: {
				selector: 'div[id=ATVMenuWebContent]'
			},
			TreeHolder: {
				selector: 'div[id=ATVTreeWebContent]'
			}
		};	
		treeView.draggable = true;
		treeView.draggableScope = 'region';
		treeView.draggableType = "webcontent";
		treeView.nodeToBindingDescriptor = this.nodeToBindingDescriptor;			
		treeView.activate();
		
		var _treeView = treeView;
		
		var rootNode = new YAHOO.widget.TextNode({
			id:'root', 
			label: 'Web Site', 
			nodeID: 'root', 
			innerNodesSlyle: 'icon-web-content-folder', 
			path: ''
		}, treeView.getRoot(), false);
		
		rootNode.labelStyle = 'icon-web-content-root';
		treeView.setDynamicLoad(this.loadData.bind(treeView));
		treeView.draw();
		treeView.addNodeLink('root', rootNode);
		
		// bind double-clicks for root page
		var div = rootNode.getEl();
		if(div)
		{
			div.ondblclick = this.rootNodeDoubleClickHandler.bind(rootNode);
		}
		
		// set up a handler for changes to active node state
		treeView.onChangeActiveNode = function()
		{
			// no action
		};

		// disable the buttons (for now)
		treeView.getMenu().setDisabled("roots", 0);
		treeView.getMenu().setDisabled("roots", 1);
		treeView.getMenu().setDisabled("roots", 2);
		
		// set up 'expand' listener
		treeView.tree.subscribe("expandComplete", function(node) 
		{
			for(var i = 0; i < node.children.length; i++)
			{
				var childNode = node.children[i];
				
				var childDiv = childNode.getEl();
				if(childDiv)
				{
					childDiv.ondblclick = this.nodeDoubleClickHandler.bind(childNode);
				}
			}
		});		

		this.treeView = treeView;
	}
	
	return this.treeView;
};

WebStudio.Applets.WebContent.prototype.loadData = function(node, fnLoadComplete)
{
	var _this = this;
	
	var time = new Date();
	var sUrl = WebStudio.ws.studio('/tree/content') + '&nodeId='+node.data.nodeID+'&_dc='+(time.getSeconds()*1000 + time.getMilliseconds())+'&path='+node.data.path;
		
	var callback = {
		success: function(oResponse) {
			var oResults = eval("(" + oResponse.responseText + ")");
			for (var i = 0; oResults[i]; i++) 
			{
				var tempNode = new YAHOO.widget.TextNode({
					id: oResults[i].nodeId, 
					label:oResults[i].text, 
					nodeID: oResults[i].nodeId, 
					draggable:oResults[i].draggable, 
					path: node.data.path+'/'+oResults[i].text,
					alfType: oResults[i].alfType||null,
					expanded: true,
					mimetype: oResults[i].mimetype||null
				}, node, false);
				
				_this.addNodeLink(oResults[i].nodeId, tempNode);
				if (oResults[i].alfType == 'file') 
				{
					tempNode.labelStyle = 'icon-web-content-file';
					
					// see if we can guess a better icon based on the mimetype
					var fileType = oResults[i].alfFileType;
					var iconStyle = WebStudio.icons.getFileTypeIconClass16(fileType);
					if(iconStyle)
					{
						tempNode.labelStyle = iconStyle;
					}
				}
				else if (oResults[i].alfType == 'directory') 
				{
					tempNode.labelStyle = 'icon-web-content-folder';
				}
				else
				{
					tempNode.labelStyle = 'icon-web-content-file';
				}
				var leaf = false;
				if (oResults[i].leaf)
				{
					leaf = true;
				}
				tempNode.isLeaf = leaf;
			}
			_this.createDraggables.delay(200, _this);
			oResponse.argument.fnLoadComplete();
		}
		,
		failure: function(oResponse) {
			oResponse.argument.fnLoadComplete();
			Alfresco.App.onFailure(oResponse);
		}
		,
		argument: {
			"node": node,
			"fnLoadComplete": fnLoadComplete
		},
		timeout: 7000
	};
	YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
};

WebStudio.Applets.WebContent.prototype.onShowApplet = function()
{
	// hide all designers
	this.getApplication().hideAllDesigners();
		    
	// show the page editor
	this.getApplication().showPageEditor();
};

WebStudio.Applets.WebContent.prototype.onHideApplet = function()
{
	// hide the page editor
	this.getApplication().hidePageEditor();
};

// this = node
WebStudio.Applets.WebContent.prototype.rootNodeDoubleClickHandler = function(e)
{
	e = new Event(e);
				
	// link to the web project
	var url = "http://localhost:8080/alfresco/service/webframework/redirect/jsf-client/browse/webproject/" + WebStudio.context.getWebProjectId();
	WebStudio.app.openBrowser("alfresco", url);

	e.stop();
};

// this = node
WebStudio.Applets.WebContent.prototype.nodeDoubleClickHandler = function(e)
{
	e = new Event(e);
				
	if(this.isLeaf)
	{
		var url = WebStudio.url.studio("/proxy/alfresco-root/d/d/avm/" + WebStudio.context.getWebProjectId() + "/" + this.data.id + "/" + this.data.label);
		WebStudio.app.openBrowser('alfresco', url);
	}
	else
	{
		// we don't have a way to browse to a particular path in a sandbox yet
	}

	e.stop();
};

WebStudio.Applets.WebContent.prototype.bindPageEditor = function(pageEditor)
{
	/*
	if(this.treeView)
	{
		this.treeView.setDroppables(pageEditor.tabs);
	}
	*/
};

WebStudio.Applets.WebContent.prototype.nodeToBindingDescriptor = function(node)
{
	var sourceIsContainer = ("directory" == node.data.alfType);
	
	var binding = {
		sourceType : "webapp",
		sourceEndpoint : "alfresco",
		sourcePath : node.data.path,
		sourceMimetype : node.data.mimetype,
		sourceAlfType : node.data.alfType,
		sourceNodeId : node.data.nodeID,
		sourceIsContainer : sourceIsContainer 
	};

	return binding;
};
