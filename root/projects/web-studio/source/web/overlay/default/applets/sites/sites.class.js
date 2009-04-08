WebStudio.Applets.Sites = WebStudio.Applets.Abstract.extend({});

WebStudio.Applets.Sites.prototype.getDependenciesConfig = function()
{
	return {
		"sites" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : WebStudio.overlayPath + "/applets/sites/sites.class.css.jsp"
				}
			}
		}
	};
};

WebStudio.Applets.Sites.prototype.getTemplateDomId = function()
{
	return "SitesApplet_Slider";
};

WebStudio.Applets.Sites.prototype.bindSliderControl = function(container) 
{
	var _this = this;
	
	if(!this.treeView)
	{	
		var controlTemplate = this.instantiateControlTemplate(this.getId(), 'AlfrescoTreeViewSitesTemplate');
		
		var treeView = new WebStudio.TreeView('Control_'+this.getId());
		treeView.setTemplate(controlTemplate);
		treeView.setInjectObject(container);
		treeView.defaultElementsConfig = {
			TreeHolder: {
				selector: 'div[id=ATVTreeSites]'
			}
		};	
		treeView.draggable = true;
		treeView.draggableScope = 'region';
		treeView.draggableType = "sites";
		treeView.nodeToBindingDescriptor = this.nodeToBindingDescriptor;			
		treeView.activate();
		
		var rootNode = new YAHOO.widget.TextNode({id:'root', label: 'Sites', nodeID: 'root', innerNodesSlyle: 'icon-sites-folder', path: ''}, treeView.getRoot(), false);
		rootNode.labelStyle = 'icon-sites-root';
		treeView.setDynamicLoad(this.loadData.bind(treeView));
		treeView.draw();
		treeView.addNodeLink('root', rootNode);
				
		// set up 'expand' listener
		treeView.tree.subscribe("expandComplete", function(node) 
		{
			for(var i = 0; i < node.children.length; i++)
			{
				var childNode = node.children[i];
				
				var childDiv = childNode.getEl();
				if(childDiv)
				{
					childDiv.ondblclick = _this.nodeDoubleClickHandler.bind(childNode);
				}
			}
		});
				
		this.treeView = treeView;
	}
		
	return this.treeView;
};

// this = treeView
WebStudio.Applets.Sites.prototype.loadData = function(node, fnLoadComplete)
{
	var _this = this;
	
	var time = new Date();
	var sUrl = WebStudio.ws.studio('/tree/sites/'+node.data.path);

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
					alfType: oResults[i].alfType || null,
					shareType: oResults[i].shareType || null,
					mimetype: oResults[i].mimetype || null
				}, node, false);
				_this.addNodeLink(oResults[i].nodeId, tempNode);
				
				// set the url for Alfresco Share
				tempNode.shareUrl = oResults[i].shareUrl || null;
				
				if (oResults[i].alfType == 'dmFile') 
				{
					// dmFile
					tempNode.labelStyle = 'icon-sites-item';
				} 
				else
				{
					// assume dmSpace
					tempNode.labelStyle = 'icon-sites-folder';
				
					if(oResults[i].shareType == "wiki")
					{
						tempNode.labelStyle = 'icon-sites-wiki';
					}
					else if(oResults[i].shareType == "blog")
					{
						tempNode.labelStyle = 'icon-sites-blog';
					}
					else if(oResults[i].shareType == "discussions")
					{
						tempNode.labelStyle = 'icon-sites-discussions';
					}
					else if(oResults[i].shareType == "doclib")
					{
						tempNode.labelStyle = 'icon-sites-doclib';
					}
					else if(oResults[i].shareType == "calendar")
					{
						tempNode.labelStyle = 'icon-sites-calendar';
					}
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
		},
		argument: {
			"node": node,
			"fnLoadComplete": fnLoadComplete
		},
		timeout: 7000
	};
	YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
};

WebStudio.Applets.Sites.prototype.onShowApplet = function()
{
	// hide all designers
    this.getApplication().hideAllDesigners();
    
    // show the page editor
    this.getApplication().showPageEditor();
};

WebStudio.Applets.Sites.prototype.onHideApplet = function()
{
    // hide the page editor
    this.getApplication().hidePageEditor();
};

// this = node
WebStudio.Applets.Sites.prototype.nodeDoubleClickHandler = function(e)
{
	e = new Event(e);
	
	var url = null;
	
	if(this.isLeaf)
	{
		url = WebStudio.url.studio("/proxy/alfresco/api/node/content/workspace/SpacesStore/" + this.data.id + "/content");
		WebStudio.app.openBrowser('alfresco', url);
	}
	else
	{
		// open a browser to this fellow
		url = "http://localhost:8080/share/page" + this.shareUrl;
		
		WebStudio.app.openBrowser('share', url);
	}
	
	e.stop();					
};

WebStudio.Applets.Sites.prototype.bindPageEditor = function(pageEditor)
{
	/*
	if(this.treeView)
	{
		this.treeView.setDroppables(pageEditor.tabs);
	}
	*/
};

WebStudio.Applets.Sites.prototype.nodeToBindingDescriptor = function(node)
{
	var binding = {
		sourceType : "site",
		sourceEndpoint : "alfresco",
		sourcePath : node.data.path,
		sourceMimetype : node.data.mimetype,
		sourceAlfType : node.data.alfType,
		sourceCmType : node.data.cmType,
		sourceNodeRef : node.data.nodeRef,
		sourceNodeId : node.data.nodeId,
		sourceIsContainer : !node.data.leaf	 
	};

	return binding;
};