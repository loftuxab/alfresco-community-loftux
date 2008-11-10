<%
	String overlayPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
	String iconsPath = overlayPath + "/images/icons";
%>

WebStudio.Applets.Navigation = WebStudio.Applets.Abstract.extend({ 
	
	INDEX_BUTTON_ADD: 0,
	INDEX_BUTTON_EDIT: 1,
	INDEX_BUTTON_COPY: 2,
	INDEX_BUTTON_DELETE: 3

});

WebStudio.Applets.Navigation.prototype.getDependenciesConfig = function()
{
	return {
		"navigation" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : "<%=overlayPath%>/applets/navigation/navigation.class.css.jsp"
				}
			}
		}
	};
}

WebStudio.Applets.Navigation.prototype.getTemplateDomId = function()
{
	return "SurfaceNavigationSlider";
}

WebStudio.Applets.Navigation.prototype.bindSliderControl = function(container) 
{
	if(!this.treeView)
	{
		var controlTemplate = this.instantiateControlTemplate(this.getId(), 'AlfrescoTreeViewNavigationTemplate');
			
		//	
		// Set up the Tree Control
		//
		this.treeView = new WebStudio.TreeView('Control_'+this.getId());
		this.treeView.setTemplate(controlTemplate);
		this.treeView.setInjectObject(this.container);
		this.treeView.defaultElementsConfig = {
			MenuTemplate: {
				selector: 'div[id=ATVMenuTemplateNavigation]',
				remove: true
			},
			MenuHolder: {
				selector: 'div[id=ATVMenuNavigation]'
			},
			TreeHolder: {
				selector: 'div[id=ATVTreeNavigation]'
			}
		}		
		this.treeView.activate();
		
		var pid = context.getRootPageId();
		var pname = context.getRootPageTitle();
		if(!pname)
		{
			pname = "Root Page";
		}
		
		var rootNode = new YAHOO.widget.TextNode({
			id:pid, 
			label: pname, 
			nodeID: pid, 
			innerNodesSlyle: 'icon-navigation-root',
			isRoot: true
		}, this.treeView.getRoot(), false);
		
		rootNode.labelStyle = 'icon-navigation-root';
		this.treeView.setDynamicLoad(this.loadData.bind(this.treeView));
		this.treeView.draw();
		this.treeView.addNodeLink(pid, rootNode);
	
		// bind double-clicks for root page
		var div = rootNode.getEl();
		if(div != null)
		{
			div.ondblclick = (function(e){
				e = new Event(e);
				WebStudio.app.loadPage(this.data.id);
				e.stop();
			}).bind(rootNode);
		}
				
		// set up 'expand' listener
		this.treeView.tree.subscribe("expandComplete", function(node) 
		{
			for(var i = 0; i < node.children.length; i++)
			{
				var childNode = node.children[i];
				
				var childDiv = childNode.getEl();
				if(childDiv != null)
				{
					childDiv.ondblclick = (function(e) {
						e = new Event(e);
						WebStudio.app.loadPage(this.data.id);
						e.stop();					
					}).bind(childNode);
				}
			}
		});
		
		// set up a handler for changes to active node state
		this.treeView.onChangeActiveNode = (function()
		{
			if (this.treeView.activeNode.isRoot)
			{
				this.treeView.menu.setEnabled('roots', this.INDEX_BUTTON_ADD);
				this.treeView.menu.setDisabled('roots', this.INDEX_BUTTON_EDIT);
				this.treeView.menu.setDisabled('roots', this.INDEX_BUTTON_COPY);
				this.treeView.menu.setDisabled('roots', this.INDEX_BUTTON_DELETE);
			}
			else
			{
				this.treeView.menu.setEnabled('roots', this.INDEX_BUTTON_ADD);
				this.treeView.menu.setEnabled('roots', this.INDEX_BUTTON_EDIT);
				this.treeView.menu.setEnabled('roots', this.INDEX_BUTTON_COPY);
				this.treeView.menu.setEnabled('roots', this.INDEX_BUTTON_DELETE);
			}
		}).bind(this);
		
		
		
		
		//
		// SET UP MENU
		//
		this.treeView.menu.addEvent('click', 'NavigationAddEditDelete', (function(group, index) 
		{
			if (group == 'roots') 
			{
				if (index == this.INDEX_BUTTON_ADD) {
					this.treeView.fireEvent('Add', this.treeView.getActiveNode());
				} else if (index == this.INDEX_BUTTON_EDIT) {
					this.treeView.fireEvent('Edit', this.treeView.getActiveNode());
				} else if (index == this.INDEX_BUTTON_COPY) {
					this.treeView.fireEvent('Copy', this.treeView.getActiveNode());
				} else if (index == this.INDEX_BUTTON_DELETE) {
					this.treeView.fireEvent('Delete', this.treeView.getActiveNode());
				}
			}
		}).bind(this));		
		
		this.treeView.addEvent('Add', 'buildNavigationTree', (function(node) 
		{
			if(!node)
			{
				return;
			}
			
			var w = new WebStudio.Wizard();
			var pageId = node.data.id;
			if(!pageId)
			{
				// TODO: error out
				return;
			}
			w.setDefaultJson(
			{
				pageId: pageId,
				refreshSession: 'true'
			});
			var url = WebStudio.ws.studio('/wizard/navigation/add');
			w.start(url, 'addpage');
			w.onComplete = (function() 
			{
				var node = this.getActiveNode();
				
				node.collapse();
				node.childrenRendered = false;
				node.dynamicLoadComplete = false;
				for(var i = 0; i < node.children.length; i++)
				{
					this.tree.removeNode(node.children[i]);
				}
				node.children = [];
				
				//this.activeNode = null;
				node.expand();
				
			}).bind(this);
			
		}).bind(this.treeView));
		
		this.treeView.addEvent('Edit', 'buildNavigationTree', (function(node) 
		{
		    if(!node)
		    {
		    	return;
		    }
			var w = new WebStudio.Wizard();
			w.setDefaultJson(
			{
				pageId: node.data.id,
				refreshSession: 'true'
			});
			var url = WebStudio.ws.studio('/wizard/navigation/edit');
			w.start(url, 'editpage');
	
			w.onComplete = (function()
			{
				var parent = this.getActiveNode().parent;
				
				parent.collapse();
				parent.childrenRendered = false;
				parent.dynamicLoadComplete = false;
				for(var i = 0; i < parent.children.length; i++)
				{
					this.tree.removeNode(parent.children[i]);
				}
				parent.children = [];
						
				this.activeNode = parent;			
				this.activeNode.expand();
				
			}).bind(this);
				
		}).bind(this.treeView));
	
		this.treeView.addEvent('Copy', 'buildNavigationTree', (function(node) 
		{
		    if(!node)
		    {
		    	return;
		    }
			var w = new WebStudio.Wizard();
			w.setDefaultJson(
			{
				pageId: node.data.id,
				parentId: node.data.parentId,
				refreshSession: 'true'
			});
			var url = WebStudio.ws.studio('/wizard/navigation/copy');
			w.start(url, 'copypage');
	
			w.onComplete = (function()
			{
				var parent = this.getActiveNode().parent;
				
				parent.collapse();
				parent.childrenRendered = false;
				parent.dynamicLoadComplete = false;
				for(var i = 0; i < parent.children.length; i++)
				{
					this.tree.removeNode(parent.children[i]);
				}
				parent.children = [];
						
				this.activeNode = parent;			
				this.activeNode.expand();
				
			}).bind(this);
				
		}).bind(this.treeView));

		this.treeView.addEvent('Delete', 'buildNavigationTree', (function(node) 
		{
		    if(!node)
		    {
		    	return;
		    }
			var w = new WebStudio.Wizard();
			w.setDefaultJson(
			{
				pageId: node.data.id,
				parentId: node.data.parentId,
				refreshSession: 'true'
			});
			var url = WebStudio.ws.studio('/wizard/navigation/delete');
			w.start(url, 'deletepage');
	
			w.onComplete = (function()
			{
				var parent = this.getActiveNode().parent;
				
				parent.collapse();
				parent.childrenRendered = false;
				parent.dynamicLoadComplete = false;
				for(var i = 0; i < parent.children.length; i++)
				{
					this.tree.removeNode(parent.children[i]);
				}
				parent.children = [];
				
				this.activeNode = parent;
				this.activeNode.expand();
				
			}).bind(this);
	
		}).bind(this.treeView));
		
		// add the application treeview drop handler
		this.treeView.dropFromTreeView = this.getApplication().dropFromTreeView.bind(this.getApplication());
		
		// initial button state
		this.treeView.menu.setDisabled('roots', this.INDEX_BUTTON_ADD);
		this.treeView.menu.setDisabled('roots', this.INDEX_BUTTON_EDIT);
		this.treeView.menu.setDisabled('roots', this.INDEX_BUTTON_COPY);
		this.treeView.menu.setDisabled('roots', this.INDEX_BUTTON_DELETE);
		
	}
	
	return this.treeView;
}

WebStudio.Applets.Navigation.prototype.loadData = function(node, fnLoadComplete) 
{
	var time = new Date();
	var sUrl = WebStudio.ws.studio('/tree/navigation', { "pageId" : node.data.id, "_dc" : time.getMilliseconds() } );
	var callback = {
		success: (function(oResponse) {
			var oResults = eval("(" + oResponse.responseText + ")");
			for (var i=0; oResults[i]; i++) 
			{
				var tempNode = new YAHOO.widget.TextNode({
					id: oResults[i].pageId, 
					label:oResults[i].text, 
					nodeID: oResults[i].pageId, 
					parentId: oResults[i].parentId, 
					draggable: oResults[i].draggable
				}, node, false);
				this.addNodeLink(oResults[i].pageId, tempNode);
				
				var leaf = false;
				if (oResults[i].leaf)
				{
					leaf = true;
				}

				tempNode.isLeaf = leaf;
				tempNode.labelStyle = 'icon-navigation-node';
				tempNode.isRoot = false;								
			}
			this.createDraggables.delay(200, this);
			oResponse.argument.fnLoadComplete();
		}).bind(this),
		failure: function(oResponse) {
			oResponse.argument.fnLoadComplete();
			WebStudio.app.onFailure(oResponse);
		},
		argument: {
			"node": node,
			"fnLoadComplete": fnLoadComplete
		},
		timeout: 7000
	};
	YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
}

WebStudio.Applets.Navigation.prototype.onShowSlider = function()
{
	this.getApplication().hideAllDesigners();
}

WebStudio.Applets.Navigation.prototype.onHideSlider = function()
{
}
