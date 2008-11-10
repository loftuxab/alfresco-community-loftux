<%
	String overlayPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
	String iconsPath = overlayPath + "/images/icons";
%>

WebStudio.Applets.WebComponents = WebStudio.Applets.Abstract.extend({

	INDEX_BUTTON_FINDMORE: 0

});

WebStudio.Applets.WebComponents.prototype.getDependenciesConfig = function()
{
	return {
		"webcomponents" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : "<%=overlayPath%>/applets/webcomponents/webcomponents.class.css.jsp"
				}
			}
		}
	};
}

WebStudio.Applets.WebComponents.prototype.getTemplateDomId = function()
{
	return "SurfaceComponentsSlider";
}

WebStudio.Applets.WebComponents.prototype.bindSliderControl = function(container) 
{
	if(!this.treeView)
	{
		var controlTemplate = this.instantiateControlTemplate(this.getId(), 'AlfrescoTreeViewWebComponentsTemplate');
	
		this.treeView = new WebStudio.TreeView("Control_" + this.getId());
		this.treeView.setTemplate(controlTemplate);
		this.treeView.setInjectObject(container);
		this.treeView.defaultElementsConfig = {
			MenuTemplate: {
				selector: 'div[id=ATVMenuTemplateWebComponents]',
				remove: true
			},
			MenuHolder: {
				selector: 'div[id=ATVMenuWebComponents]'
			},
			TreeHolder: {
				selector: 'div[id=ATVTreeWebComponents]'
			}
		}				
		this.treeView.activate();
		
		var rootNode = new YAHOO.widget.TextNode({
			id:'root', 
			label: 'Component Library', 
			nodeID: 'root', 
			innerNodesSlyle: 'icon-web-components-folder'
		}, this.treeView.getRoot(), false);
		
		rootNode.labelStyle = 'icon-web-components-root';
		this.treeView.setDynamicLoad(this.loadData.bind(this.treeView));
		this.treeView.draw();
		this.treeView.addNodeLink('root', rootNode);
				
		// set up a handler for changes to active node state
		this.treeView.onChangeActiveNode = (function()
		{
			// no action
		}).bind(this);
		
		//
		// SET UP MENU
		//
		this.treeView.menu.addEvent('click', 'WebComponentsMenuClick', (function(group, index) 
		{
			if (group == 'roots') 
			{
				if (index == this.INDEX_BUTTON_FINDMORE) 
				{
					this.treeView.fireEvent('FindMore', this.treeView.getActiveNode());
				}
			}
		}).bind(this));
		
		this.treeView.addEvent('FindMore', 'WebComponentsFindMore', (function(node) 
		{
			WebStudio.app.openBrowser("network", "http://network.alfresco.com");
										
		}).bind(this.treeView));

		// add the application treeview drop handler
		this.treeView.dropFromTreeView = this.getApplication().dropFromTreeView.bind(this.getApplication());		
	}
	
	return this.treeView;
}

WebStudio.Applets.WebComponents.prototype.loadData = function(node, fnLoadComplete) 
{
	var time = new Date();
	var sUrl = WebStudio.ws.studio('/tree/components') + '&nodeId='+node.data.nodeID+'&_dc='+(time.getSeconds()*1000 + time.getMilliseconds());

	var callback = {
		success: (function(oResponse) {
			var data = Json.evaluate(oResponse.responseText);
			if(!data)
			{
				return false;
			}
			for (var i = 0; i < data.length; i++) 
			{
				var tn = new YAHOO.widget.TextNode( {
					id: data[i].nodeId, 
					label: data[i].text, 
					nodeID: data[i].nodeId, 
					draggable: data[i].draggable,
					expanded: true, 
					alfType: data[i].alfType||null
				}, node, false);
				
				this.addNodeLink(data[i].nodeId, tn);
				tn.isLeaf = data[i].leaf||false;
				
				if(data[i].alfType == 'componentType')
				{
					tn.labelStyle = 'icon-web-components-item';
				}
				else if(data[i].alfType == 'webscriptComponent')
				{
					tn.labelStyle = 'icon-web-components-item';
				}
				else
				{
					tn.labelStyle = 'icon-web-components-folder';
				}
			}
			this.createDraggables.delay(200, this);
			oResponse.argument.fnLoadComplete();
		}).bind(this)
		,
		failure: function(oResponse) {
			oResponse.argument.fnLoadComplete();
			Alfresco.App.onFailure(oResponse);
		}
		,
		argument: {
			"node": node,
			"fnLoadComplete": fnLoadComplete
		}
		,
		timeout: 7000
	};
	YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
}

WebStudio.Applets.WebComponents.prototype.onShowSlider = function()
{
	// hide all designers
	this.getApplication().hideAllDesigners();
   
	// show the page editor
	this.getApplication().showPageEditor();
}

WebStudio.Applets.WebComponents.prototype.onHideSlider = function()
{
	// hide the page editor
	this.getApplication().hidePageEditor();
}