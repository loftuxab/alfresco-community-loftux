if (typeof WebStudio == "undefined")
{
	var WebStudio = {};
}

WebStudio.SandboxDialog = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.ID = index;
	
	this.defaultTemplateSelector = 'div[id=AlfrescoWebStudioSandboxTemplatePanel]';
	
	this.defaultElementsConfig = {
		CreateWebSite: {
			selector: 'div[id=create-web-site]'
		},
		CreateWebSiteImg: {
			selector: 'img[id=create-web-site-img]'
		},
		CreateWebSiteBody: {
			selector: 'div[id=create-web-site-body]'
		},
		LoadWebSite: {
			selector: 'div[id=load-web-site]'
		},
		LoadWebSiteImg: {
			selector: 'img[id=load-web-site-img]'
		},
		LoadWebSiteBody: {
			selector: 'div[id=load-web-site-body]'
		},
		ToolCreateWebSiteName: {
			selector: 'input[id=AlfrescoWebStudioSandboxTemplate_WebSiteName]'
		},
		ToolCreateWebSiteBasedOn: {
			selector: 'select[id=AlfrescoWebStudioSandboxTemplate_BasedOn]'
		},
		ToolCreateWebSiteCreate: {
			selector: 'input[id=AlfrescoWebStudioSandboxTemplate_Create]'
		},
		ToolCreateWebSiteImage: {
			selector: 'img[id=AlfrescoWebStudioSandboxTemplate_Image]'
		},
		ToolLoadWebSiteSearchText: {
			selector: 'input[id=AlfrescoWebStudioSandboxTemplate_SearchText]'
		},
		ToolLoadWebSiteSearchButton: {
			selector: 'input[id=AlfrescoWebStudioSandboxTemplate_SearchButton]'
		},
		ToolLoadWebSiteSearchResults: {
			selector: 'div[id=load-web-site-search-results]'
		},
		ToolLoadWebSiteLoad: {
			selector: 'input[id=AlfrescoWebStudioSandboxTemplate_Load]'
		},
		ToolLoadWebSiteSelectedId: {
			selector: 'input[id=load-web-site-selected]'
		}
	}

	this.events = {};	
	this.nodes = {};
	this.droppables = [];
}

WebStudio.SandboxDialog.prototype = new WebStudio.AbstractTemplater('WebStudio.SandboxDialog');

WebStudio.SandboxDialog.prototype.activate = function() 
{	
	this.buildGeneralLayer();

	// set up the create web site event	
	this.CreateWebSite.el.addEvents({
		'mouseenter': (function(){ 
			this.CreateWebSite.el.setStyle('cursor', 'pointer'); 
		}).bind(this)
		,
		'click': (function(){ 
			this.CreateWebSiteImg.el.src = "/studio/overlay/default/images/arrows/arrow_open.gif"; 
			this.CreateWebSiteBody.el.setStyle('display', 'block');
			this.LoadWebSiteImg.el.src = "/studio/overlay/default/images/arrows/arrow_closed.gif";
			this.LoadWebSiteBody.el.setStyle('display', 'none');
			
			// update the "based on" drop down list
			this.updateCreateWebSiteBasedOn();	
			
		}).bind(this)
	});	

	// set up the load web site event	
	this.LoadWebSite.el.addEvents({
		'mouseenter': (function(){ 
			this.LoadWebSite.el.setStyle('cursor', 'pointer'); 
		}).bind(this)
		,
		'click': (function(){
			this.CreateWebSiteImg.el.src = "/studio/overlay/default/images/arrows/arrow_closed.gif"; 
			this.CreateWebSiteBody.el.setStyle('display', 'none');
			this.LoadWebSiteImg.el.src = "/studio/overlay/default/images/arrows/arrow_open.gif";
			this.LoadWebSiteBody.el.setStyle('display', 'block');
			
			// update the list of web projects
			this.updateLoadWebSiteSearchResults(null);
			
		}).bind(this)
	});

	// set up web site create click handler	s	
	this.ToolCreateWebSiteCreate.el.addEvent("click", this.webSiteCreateHandler);
	
	// set up web site load click handlers
	this.ToolLoadWebSiteLoad.el.addEvent("click", this.webSiteLoadHandler);	
}

WebStudio.SandboxDialog.prototype.updateCreateWebSiteBasedOn = function()
{
	// call over to login web script
	var url = WebStudio.ws.studio("/api/prebuilt/list", { } );
	this.call = YAHOO.util.Connect.asyncRequest('GET', url, {	
		success: (function(r) {
				
			// fault response into json
			var json = Json.evaluate(r.responseText);
			
			var html = "";
			var selectedId = null;
			for(var id in json.results)
			{
				html += "<option value='" + id + "'>";
				html += json.results[id].title;
				html += "</option>";
				
				if(selectedId == null)
				{
					selectedId = id;
				}
			}
			
			this.ToolCreateWebSiteBasedOn.el.setHTML(html);
			this.ToolCreateWebSiteImage.el.src = "/studio" + json.results[selectedId].previewImageUri;			
		
		}).bind(this)
		,
		failure: (function(r) {

			// TODO
					
		}).bind(this)
	});
}

WebStudio.SandboxDialog.prototype.updateLoadWebSiteSearchResults = function(text)
{
	// populate the web projects
	//var url = WebStudio.ws.repo("/api/wcm/webproject/list" );
	
	// NEW METHOD
	
	var url = WebStudio.ws.repo("/api/wcm/webprojects" );
	var callback = {
	
		success: (function(oResponse) {
			
			var data = Json.evaluate(oResponse.responseText);
			if(!data)
			{
				return false;
			}
			
			var html = "<table width='100%' id='loadWebProjectSearchResultsEl'>";
			html += "<tr><td></td><td nowrap><B>Web Site</B></td><td width='100%'><B>Title</B></td></tr>";

			for (var i = 0; i < data.length; i++) 
			{
				var id = data[i].webprojectref;
				var title = data[i].title;

				html += "<tr>";
				html += "<td><input type=\"radio\" name=\"sandboxSelectionId\" onClick=\"javascript:WebStudio.app.sandboxDialog.ToolLoadWebSiteSelectedId.el.value = '" + id + "';\" /></td>";
				html += "<td>" + id + "</td>";
				html += "<td>" + title + "</td>";
				html += "</tr>";

			}
			
			html + "</table>";
			
			this.ToolLoadWebSiteSearchResults.el.setHTML(html);
			$('loadWebProjectSearchResultsEl').addClass('sandbox-table');			

		}).bind(this)
		,
		failure: function(oResponse) {
		
			// TODO
		}
		,
		timeout: 7000
	};
	YAHOO.util.Connect.asyncRequest('GET', url, callback);
}

WebStudio.SandboxDialog.prototype.setWebSiteCreateHandler = function(f)
{
	this.webSiteCreateHandler = f;
}

WebStudio.SandboxDialog.prototype.setWebSiteLoadHandler = function(f)
{
	this.webSiteLoadHandler = f;
}

WebStudio.SandboxDialog.prototype.setActive = function() 
{
	//Set this window active, bring to front and apply Active styles
	this.generalLayer.setStyle('z-index', WebStudio.WindowsZIndex + this.zIndexUpper);
	WebStudio.WindowsZIndex++;
	WebStudio.WindowsActive = this;
	return this;
}


WebStudio.SandboxDialog.prototype.popup = function() 
{	
	this.block();
	this.show();
	this.centered();
	this.zIndexUpper = 2000;
	this.setActive();
}

WebStudio.SandboxDialog.prototype.popout = function()
{	
    this.hide();
    this.unblock();
}