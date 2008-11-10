<%
	String overlayPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
	String iconsPath = overlayPath + "/images/icons";
%>

WebStudio.Applets.WebContent = WebStudio.Applets.Abstract.extend({});

WebStudio.Applets.WebContent.prototype.getDependenciesConfig = function()
{
	return {
		"webcontent" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : "<%=overlayPath%>/applets/webcontent/webcontent.class.css.jsp"
				}
			}
		}
	};
}

WebStudio.Applets.WebContent.prototype.getTemplateDomId = function()
{
	return "ContentWebContentSlider";
}

WebStudio.Applets.WebContent.prototype.bindSliderControl = function(container) 
{
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
		}		
		treeView.activate();
		
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
		
		// set up a handler for changes to active node state
		treeView.onChangeActiveNode = (function()
		{
			// no action
		}).bind(this);		

		// add the application treeview drop handler
		treeView.dropFromTreeView = this.getApplication().dropFromTreeView.bind(this.getApplication());

		// disable the buttons (for now)
		treeView.getMenu().setDisabled("roots", 0);
		treeView.getMenu().setDisabled("roots", 1);
		treeView.getMenu().setDisabled("roots", 2);

		this.treeView = treeView;
	}
	
	return this.treeView;
}

WebStudio.Applets.WebContent.prototype.loadData = function(node, fnLoadComplete)
{
	var time = new Date();
	var sUrl = WebStudio.ws.studio('/tree/content') + '&nodeId='+node.data.nodeID+'&_dc='+(time.getSeconds()*1000 + time.getMilliseconds())+'&path='+node.data.path;
		
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
					expanded: true,
					mimetype: oResults[i].mimetype||null
				}, node, false);
				
				this.addNodeLink(oResults[i].nodeId, tempNode);
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

WebStudio.Applets.WebContent.prototype.onShowSlider = function()
{
	// hide all designers
	this.getApplication().hideAllDesigners();
		    
	// show the page editor
	this.getApplication().showPageEditor();
}

WebStudio.Applets.WebContent.prototype.onHideSlider = function()
{
	// hide the page editor
	this.getApplication().hidePageEditor();
}
