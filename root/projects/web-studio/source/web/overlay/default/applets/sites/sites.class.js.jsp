<%
	String overlayPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
	String iconsPath = overlayPath + "/images/icons";
%>

WebStudio.Applets.Sites = WebStudio.Applets.Abstract.extend({});

WebStudio.Applets.Sites.prototype.getDependenciesConfig = function()
{
	return {
		"sites" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : "<%=overlayPath%>/applets/sites/sites.class.css.jsp"
				}
			}
		}
	};
}

WebStudio.Applets.Sites.prototype.getTemplateDomId = function()
{
	return "ContentSitesSlider";
}

WebStudio.Applets.Sites.prototype.bindSliderControl = function(container) 
{
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
		}						
		treeView.activate();
		
		var rootNode = new YAHOO.widget.TextNode({id:'root', label: 'Sites', nodeID: 'root', innerNodesSlyle: 'icon-sites-folder', path: ''}, treeView.getRoot(), false);
		rootNode.labelStyle = 'icon-sites-root';
		treeView.setDynamicLoad(this.loadData.bind(treeView));
		treeView.draw();
		treeView.addNodeLink('root', rootNode);
			
		// add the application treeview drop handler
		treeView.dropFromTreeView = this.getApplication().dropFromTreeView.bind(this.getApplication());
				
		this.treeView = treeView;
	}
		
	return this.treeView;
}

WebStudio.Applets.Sites.prototype.loadData = function(node, fnLoadComplete)
{
	var time = new Date();
	var sUrl = WebStudio.ws.studio('/tree/sites') + '&path='+node.data.path+'&_dc='+(time.getSeconds()*1000 + time.getMilliseconds());

	var callback = {
		success: (function(oResponse) {
			var oResults = eval("(" + oResponse.responseText + ")");
			for (var i=0; oResults[i]; i++) 
			{
				var tempNode = new YAHOO.widget.TextNode({
					id: oResults[i].nodeId, 
					label:oResults[i].text, 
					nodeID: oResults[i].nodeId, 
					draggable:oResults[i].draggable, 
					path: node.data.path+'/'+oResults[i].text,
					alfType: oResults[i].alfType||null,
					mimetype: oResults[i].mimetype || null
				}, node, false);
				this.addNodeLink(oResults[i].nodeId, tempNode);
				
				var leaf = false;
				if (oResults[i].leaf)
				{
					leaf = true;
				}
				tempNode.isLeaf = leaf;
				if (oResults[i].alfType == 'componentType') 
				{
					tempNode.labelStyle = 'icon-sites-item';
				} 
				else
				{
					tempNode.labelStyle = 'icon-sites-folder';
				}
			}
			this.createDraggables.delay(200, this);
			oResponse.argument.fnLoadComplete();
		}).bind(this),
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
}

WebStudio.Applets.Sites.prototype.onShowSlider = function()
{
	// hide all designers
    this.getApplication().hideAllDesigners();
    
    // show the page editor
    this.getApplication().showPageEditor();
}

WebStudio.Applets.Sites.prototype.onHideSlider = function()
{
    // hide the page editor
    this.getApplication().hidePageEditor();
}
