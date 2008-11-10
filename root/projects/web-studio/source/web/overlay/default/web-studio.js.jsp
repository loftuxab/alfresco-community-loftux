<%@ page import="org.alfresco.web.studio.*" %>
<%@ page import="org.alfresco.web.site.RequestContext" %>
<%@ page import="org.alfresco.web.site.RequestUtil" %>
<%@ page import="org.alfresco.web.config.WebStudioConfigElement" %>
<%@ page import="org.alfresco.web.config.WebStudioConfigElement.ApplicationDescriptor" %>
<%@ page import="org.alfresco.web.config.WebStudioConfigElement.AppletDescriptor" %>
<%@ page import="org.alfresco.tools.*" %>
<%@ page import="java.util.*" %>
<%@ page buffer="64kb" contentType="text/html;charset=UTF-8" %>
<%@ page autoFlush="true" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);

	// generate the dom.jsp template overlay just once
	StringBuilder overlayTemplate = (StringBuilder) OverlayUtil.getCachedResource(request, "overlayTemplate");
	overlayTemplate = null;
	if(overlayTemplate == null)
	{
		overlayTemplate = new StringBuilder();

		// include the template
		OverlayUtil.include(request, overlayTemplate, "/overlay/default/template/dom.jsp");
		
		// store the template
		OverlayUtil.setCachedResource(request, "overlayTemplate", overlayTemplate);
	}
		
	// determine jsessionid	(for Surf application)	
	String jSessionId = null;
	Cookie[] cookies = request.getCookies();
	if(cookies != null)
	{
		for(int ai = 0; ai < cookies.length; ai++)
		{
			if("JSESSIONID".equalsIgnoreCase(cookies[ai].getName()))
			{
				jSessionId = cookies[ai].getValue();
			}
		}
	}
	
	// determine the alfresco ticket
	String alfTicket = (String) request.getParameter("ticket");
	if(alfTicket == null)
	{
		alfTicket = (String) request.getHeader("ticket");
	}
	
	// query string
	Map queryStringMap = WebUtil.getQueryStringMap(request);
	String queryString = WebUtil.getQueryStringForMap(queryStringMap);
	
	// context path
	String contextPath = (String) OverlayUtil.getOriginalContextPath(request);
	
	// environment	
	String webProjectId = WebStudioUtil.getCurrentWebProject(request);
	String sandboxId = WebStudioUtil.getCurrentSandbox(request);
	String storeId = WebStudioUtil.getCurrentStore(request);
	String webappId = WebStudioUtil.getCurrentWebapp(request);
	String userId = WebStudioUtil.getCurrentUserId(request);
	
	// paths
	String overlayPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "proxy/alfresco-web-studio/overlay/default");
	String imagesPath = overlayPath + "/images"; 
%>
<%!
	public String cleanup(String s)
	{
	    StringBuffer out = new StringBuffer();
	    for(int i=0; i<s.length(); i++)
	    {
	        char c = s.charAt(i);
	        if(c=='"')
	        {
	        	out.append("'");
	        }
	        else if(c == '\n')
	        {
	        }
	        else if(c == '\r')
	        {
	        }
	        else
	        {
	            out.append(c);
	        }
	    }
	    return out.toString();
	}
%>

// Ensure WebStudio root object exists
if (typeof WebStudio == "undefined" || !WebStudio)
{
	var WebStudio = {};
}

WebStudio.overlayPath = "<%=overlayPath%>";
WebStudio.overlayImagesPath = "<%=imagesPath%>";

/**
 * Register namespace function
 */
WebStudio.registerNS = function(ns)
{
	var nsParts = ns.split(".");
	var root = window;

	for(var i = 0; i < nsParts.length; i++)
	{
		if(typeof root[nsParts[i]] == "undefined")
		{
   			root[nsParts[i]] = new Object();
   		}

		root = root[nsParts[i]];
	}
}

WebStudio.registerNS("WebStudio.Applications");
WebStudio.registerNS("WebStudio.Applets");
WebStudio.registerNS("WebStudio.Fx");
WebStudio.registerNS("WebStudio.Templates.Model");



/**
 * WebStudio top-level constants namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.constants
 */
WebStudio.constants = WebStudio.constants || {
	ABC: 1
};

/**
 * WebStudio top-level util namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.util
 */
WebStudio.util = WebStudio.util ||  {
	test: function() { }
};

/**
 * WebStudio top-level messages namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.messages
 */
WebStudio.messages = WebStudio.messages || {
};

/**
 * WebStudio top-level context namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.messages
 */
WebStudio.context = WebStudio.context || {

	webProjectId : <%=(webProjectId == null ? "null" : "\"" + webProjectId + "\"") %>
	,
	sandboxId : <%=(sandboxId == null ? "null" : "\"" + sandboxId + "\"") %>
	,
	storeId : <%=(storeId == null ? "null" : "\"" + storeId + "\"") %>
	,
	webappId: <%=(webappId == null ? "null" : "\"" + webappId + "\"") %>
	,
	username: <%=(userId == null ? "null" : "\"" + userId + "\"") %>
	,
	getWebProjectId : function() {
		return this.webProjectId;
	}
	,
	getSandboxId : function() {
		return this.sandboxId;
	}
	,
	getStoreId : function() {
		return this.storeId;
	}
	,
	getWebappId : function() {
		return this.webappId;
	}
	,
	getCurrentUserId : function() {
		return this.username;
	}
	,	
	isWebProjectMounted : function() {
		return (this.webProjectId != null);
	}
	,
	isSandboxMounted : function() {
		return (this.sandboxId != null);
	}
	,
	getWebProjectId : function() {
		return (this.webProjectId);
	}			
	,
	isAuthenticated : function() {
		return (this.getCurrentUserId() != null);
	}
};

/**
 * WebStudio top-level request namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.request
 */
WebStudio.request = WebStudio.request ||
{
	queryString : <%=(queryString == null ? "null" : "\"" + queryString + "\"") %>
	,
	alfrescoTicket : <%=(alfTicket == null ? "null" : "\"" + alfTicket + "\"") %>
	,
	jSessionID : <%=(jSessionId == null ? "null" : "\"" + jSessionId + "\"") %>
	,
	contextPath : <%=(contextPath == null ? "null" : "\"" + contextPath + "\"") %>
};

/**
 * WebStudio top-level forms namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.forms
 */
WebStudio.forms = WebStudio.forms ||
{
	getFormNames: function()
	{
		var formArray = new Array();
		formArray[formArray.length] = "article";
		formArray[formArray.length] = "press-release";
		formArray[formArray.length] = "event";
		formArray[formArray.length] = "product";
		return formArray;		
	}
	,
	getFormTitle: function(formName)
	{
		if("article" == formName)
			return "Article";
		if("press-release" == formName)
			return "Press Release";
		if("event" == formName)
			return "Event";
		if("product" == formName)
			return "Product";
		return null;		
	}
	,
	getFormDescription: function(formName)
	{
		if("article" == formName)
			return "Article Description";
		if("press-release" == formName)
			return "Press Release Description";
		if("event" == formName)
			return "Event Description";
		if("product" == formName)
			return "Product Description";
		return null;
	}
};

/**
 * WebStudio top-level themes namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.themes
 */
WebStudio.themes = WebStudio.themes ||
{
	getThemeIds : function()
	{
		var themeArray = new Array();
		themeArray[themeArray.length] = "default";
		themeArray[themeArray.length] = "black";
		themeArray[themeArray.length] = "darkgray";
		themeArray[themeArray.length] = "green";
		themeArray[themeArray.length] = "indigo";
		themeArray[themeArray.length] = "midnight";
		themeArray[themeArray.length] = "silverCherry";
		themeArray[themeArray.length] = "slate";
		return themeArray;
	}
	,
	getThemeName : function(themeId)
	{
		return themeId;
	}
};


/**
 * WebStudio top-level icons namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.icons
 */
WebStudio.icons = WebStudio.icons ||
{
	getFileTypeIconClass16 : function(fileType)
	{
		var iconClass = null;
		
		if("html" == fileType) { iconClass = "icon-filetype-html-16"; }
		if("image" == fileType) { iconClass = "icon-filetype-image-16"; }
		if("xml" == fileType) { iconClass = "icon-filetype-xml-16"; }
		if("pdf" == fileType) { iconClass = "icon-filetype-pdf-16"; }
		if("jsp" == fileType) { iconClass = "icon-filetype-txt-16"; }
		if("asp" == fileType) { iconClass = "icon-filetype-txt-16"; }
		if("php" == fileType) { iconClass = "icon-filetype-txt-16"; }
		if("text" == fileType) { iconClass = "icon-filetype-txt-16"; }
		
		return iconClass;
	}	
};



/**
 * WebStudio top-level url namespace.
 *
 * Consider that there are three machines:
 * Client - the Surf application (for example, port 8180)
 * Studio - the Studio application (for example, port 8280)
 * Repository - the Alfresco Repository (for example, port 8080) 
 * 
 * @namespace WebStudio
 * @class WebStudio.url
 */
WebStudio.url = WebStudio.url ||
{
	client : function(uri, param, bNoParams) {
		return this.uri("client", uri, params, bNoParams);
	}
	,
	studio : function(uri, params, bNoParams) {
		return this.uri("studio", uri, params, bNoParams);
	}
	,
	repo : function(uri, params, bNoParams) {
		return this.uri("repo", uri, params, bNoParams);
	}
	,
	uri: function(tier, uri, params, bNoParams)
	{
		var url = null;
		if("client" == tier)
		{
			url = "<%=OverlayUtil.getOriginalContextPath(request)%>" + uri;
		}
		if("studio" == tier)
		{
			url = "<%=OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio")%>" + uri;
		}
		if("repository" == tier || "repo" == tier)
		{
			url = "<%=OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/proxy/alfresco")%>" + uri;
		}
		if(url == null)
		{
			alert("Unable to resolve tier");
			return null;
		}
			
		var avmStoreId = WebStudio.context.getStoreId();
		var avmWebappId = WebStudio.context.getWebappId();
			
		if(!params)
		{
			params = { };
		}
			
		if(bNoParams != true)
		{
			// add in our special params
			if(avmStoreId)
			{
				params["alfStoreId"] = avmStoreId;
				if(avmWebappId)
				{
					params["alfWebappId"] = avmWebappId;
				}
			}
				
			// TODO: alfresco ticket, etc?
				
			// add in params
			var first = true;
			for(var key in params)
			{
				var value = params[key];
				
				if(first)
				{
					url = url + "?";
				}
				else
				{
					url = url + "&";
				}
				
				value = escape(value);
				
				url += key + "=" + value;
					
				first = false;
			}
		}
						
		return url;	
	}
}


/**
 * WebStudio top-level ws namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.ws
 */
WebStudio.ws = WebStudio.ws ||
{
	client : function(webscript, params) {
		return this.ws("client", webscript, params);
	}
	,
	studio : function(webscript, params) {
		return this.ws("studio", webscript, params);
	}
	,
	repo : function(webscript, params) {
		return this.ws("repo", webscript, params);
	}
	,
	ws : function(tier, webscript, params)
	{
		var url = null;
		if("client" == tier)
		{
			url = WebStudio.url.client("/service" + webscript, params);
		}
		if("studio" == tier)
		{
			url = WebStudio.url.studio("/service" + webscript, params);
		}
		if("repository" == tier || "repo" == tier)
		{
			url = WebStudio.url.repo(webscript, params);			
		}
		return url;
	}
};

//window.addEvent('domready', function() 
window.addEvent('load', function()
{
	var templateData = "<%=cleanup(overlayTemplate.toString())%>";
	
	//
	// Inject our template dom elements
	//
	var container = new Element('div');
	container.setHTML(templateData);

	var array = container.getChildren();
	for(var i = 0; i < array.length; i++)
	{
		$(array[i]).injectInside($(document.body));
	}
	
	//
	// Dom Manipulation to create bodyContainer
	//
	WebStudio.bodyContainer = new Element("div", {id:"bodyContainer"});
	WebStudio.bodyContainer.injectInside(document.body);
	WebStudio.bodyContainer.style.display = "none";

	var nodes = $(document.body).getChildren();
	var len = nodes.length;
	for (var i = 0; i < len; i++)
	{
		var id = nodes[i].id;
		if (id != "bodyContainer" && id != "templateContainer" && id != 'alfrescoPanelTbl' && nodes[i].tagName.toLowerCase() != "script")
		{
			nodes[i].injectInside(WebStudio.bodyContainer);
		}
	}
	
	// Create the Web Studio Instance
	// This is the bare framework with no "apps" loaded into it
	WebStudio.app = new WebStudio.Application();
	WebStudio.App = WebStudio.app; // legacy purposes
	WebStudio.app.setInjectObject($('alfrescoPanelHolder'));
	WebStudio.app.applications = { };
			
	// Set up the applications config
	var applicationsConfig = { };
	WebStudio.app.applicationsConfig = applicationsConfig;
<%
	String[] applicationIds = WebStudio.getConfig().getApplicationIds();
	for(int z = 0; z < applicationIds.length; z++)
	{
		ApplicationDescriptor appDescriptor = WebStudio.getConfig().getApplication(applicationIds[z]);
		String appClassName = appDescriptor.getBootstrapClassName();
		String appLocation = appDescriptor.getBootstrapLocation();
		String appObjectId = appDescriptor.getId();
		String appTitle = appDescriptor.getTitle();
		if(appTitle == null)
		{
			appTitle = appObjectId;
		}
		String appDescription = appDescriptor.getDescription();
		if(appDescription == null)
		{
			appDescription = appDescriptor.getTitle();
			if(appDescription == null)
			{
				appDescription = appObjectId;
			}
		}
%>
	applicationsConfig["<%=appObjectId%>"] = { };
	applicationsConfig["<%=appObjectId%>"]["title"] = "<%=appTitle%>";
	applicationsConfig["<%=appObjectId%>"]["description"] = "<%=appDescription%>";
	applicationsConfig["<%=appObjectId%>"]["classname"] = "<%=appClassName%>";
	applicationsConfig["<%=appObjectId%>"]["loader"] = { };
	applicationsConfig["<%=appObjectId%>"]["loader"]["bootstrap"] = { };
	applicationsConfig["<%=appObjectId%>"]["loader"]["bootstrap"]["name"] = "Loader_" + "<%=appObjectId%>";
	applicationsConfig["<%=appObjectId%>"]["loader"]["bootstrap"]["path"] = "<%=contextPath %>/proxy/alfresco-web-studio<%=appLocation%>";
	applicationsConfig["<%=appObjectId%>"]["applets"] = { };
	var appletsConfig = applicationsConfig["<%=appObjectId%>"]["applets"];
<%	
		// set up applets
		List appletIncludes = appDescriptor.getAppletIncludes();
		for(int y = 0; y < appletIncludes.size(); y++)
		{
			String appletId = (String) appletIncludes.get(y);
			AppletDescriptor appletDescriptor = WebStudio.getConfig().getApplet(appletId);			
			String appletClassName = appletDescriptor.getBootstrapClassName();
			String appletLocation = appletDescriptor.getBootstrapLocation();
			String appletObjectId = appletDescriptor.getId();
			String appletTitle = appletDescriptor.getTitle();
			if(appletTitle == null)
			{
				appletTitle = appletObjectId;
			}
			String appletDescription = appletDescriptor.getDescription();
			if(appletDescription == null)
			{
				appletDescription = appletDescriptor.getTitle();
				if(appletDescription == null)
				{
					appletDescription = appletObjectId;
				}
			}
%>
	appletsConfig["<%=appletObjectId%>"] = { };
	appletsConfig["<%=appletObjectId%>"]["title"] = "<%=appletTitle%>";
	appletsConfig["<%=appletObjectId%>"]["description"] = "<%=appletDescription%>";
	appletsConfig["<%=appletObjectId%>"]["classname"] = "<%=appletClassName%>";
	appletsConfig["<%=appletObjectId%>"]["loader"] = { };
	appletsConfig["<%=appletObjectId%>"]["loader"]["bootstrap"] = { };
	appletsConfig["<%=appletObjectId%>"]["loader"]["bootstrap"]["name"] = "Loader_" + "<%=appletObjectId%>";
	appletsConfig["<%=appletObjectId%>"]["loader"]["bootstrap"]["path"] = "<%=contextPath %>/proxy/alfresco-web-studio<%=appletLocation%>";
<%			
		}
	}
%>	

	// Put YUI class into effect
	$(document.body).addClass("yui-skin-sam");

	// Start Web Studio
	WebStudio.app.init();
});

WebStudio.configureRegion = function(elId, regionId, regionScopeId, regionSourceId)
{
	$(elId).setAttribute('regionId', regionId);
	$(elId).setAttribute('regionScopeId', regionScopeId);
	$(elId).setAttribute('regionSourceId', regionSourceId);
}

WebStudio.configureComponent = function(elId, componentId, componentTypeId, componentTitle, componentTypeTitle, componentEditorUrl)
{
	$(elId).setAttribute('componentId', componentId);
	$(elId).setAttribute('componentTypeId', componentTypeId);
	$(elId).setAttribute('componentTitle', componentTitle);
	$(elId).setAttribute('componentTypeTitle', componentTypeTitle);
	$(elId).setAttribute('componentEditorUrl', componentEditorUrl);
}

/**
 * WebStudio top-level parser namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.parser
 */
WebStudio.parser = WebStudio.parser ||
{
	parseHTML: function(html)
	{
		var range = document.createRange();
		range.selectNode(document.body);
		var parsedHtml = range.createContextualFragment(html);
		return parsedHtml;	
	}
};

/**
 * General purpose bootstrapper
 *
 * bootstraps = container object for loaded bootstraps (associative map)
 * config = instantiation config 
 *
 *  {
 *    "id" : { 
 *             "title" : ..., 
 *             "description" : ...,
 *             "classname" : ...
 *             "loader" : {
 *               "loader1" : {
 *                 "name" : ...,
 *                 "path" : ...
 *               }
 *             }
 *    }
 *  }
 *
 */
WebStudio.Bootstrap = function(config, bootstraps)
{
	this.config = config;
	this.bootstraps = bootstraps;
	if(!this.bootstraps)
	{
		this.bootstraps = { };
	}
}

WebStudio.Bootstrap.prototype.load = function()
{	
	// walk through the applications
	for(var id in this.config)
	{
		var classname = this.config[id].classname;
		var title = this.config[id].title;
		var description = this.config[id].description;
		var loaderConfig = this.config[id].loader;

		// create the loader
		if(!this.loaders)
		{
			this.loaders = { };
		}
		
		this.loaders[id] = new Alf.sourceLoader('Loader for ' + id, loaderConfig);
		var loader = this.loaders[id];
		loader.jsPath = "";
		loader.cssPath = "";
		loader.objectId = id;
		loader.objectTitle = title;
		loader.objectDescription = description;
		loader.objectClassname = classname;
		
		loader.bootstrap = this;
		
		// on load event
		loader.onLoad = (function(group, index) 
		{		
			var objectId = this.objectId;
			var objectTitle = this.objectTitle;
			var objectDescription = this.objectDescription;
			var objectClassname = this.objectClassname;
			
			if(objectClassname)
			{		
				// instantiate the bootstrap object
				var evalString = "new " + objectClassname + "('" + objectId + "', '" + objectTitle + "', '" + objectDescription + "')";		 
				var obj = eval(evalString);
				loader.bootstrap.bootstraps[objectId] = obj;
				
				// if the object has an initialize method, fire it
				if(obj.init)
				{
					obj.init();
					obj.isInitialized = true;			
				}
			}
			else
			{
				loader.bootstrap.bootstraps[objectId] = { isInitialized: true };
			}
			
		}).bind(loader);
		loader.onFailed = (function(group, index) 
		{
			var bootstrap = loader.bootstrap;
			
			if(bootstrap.onFailure)
			{
				bootstrap.onFailure.attempt();
			}				
			
		}).bind(loader);

		// fire the loader
		loader.load();		
	}
	
	// set up a timed check for completion
	this.totalWaitTime = 180000; // 180 seconds
	this.checkPeriod = 500; // check every half second
	this.maxCheckCount = this.totalWaitTime / this.checkPeriod;
	this.checkCount = 0;				
	this.checker = this.check.periodical(this.checkPeriod, this);	
}

WebStudio.Bootstrap.prototype.check = function()
{
	this.checkCount++;
	
	// check to see if all of the objects have finished bootstrapping
	var check = true;
	for(var id in this.config)
	{
		var obj = this.bootstraps[id];
		if(!obj || !obj.isInitialized)
		{
			check = false;
		}
	}			
	if(check)
	{
		$clear(this.checker);
		if(this.onSuccess)
		{
			this.onSuccess.bind(this).attempt();
		}
		return true;				
	}
	if (this.checkCount > this.maxCheckCount) 
	{
		$clear(this.checker);
		if(this.onFailure)
		{
			this.onFailure.bind(this).attempt();
		}				
		return false;
	};	
}

/*
WebStudio.Fx.fadeBg = function(element, config, amount)
{
	var mixColor = new Color('#FFFFFF');
	if(amount < 0)
	{
		mixColor = new Color('#000000');
		amount = amount * -1;
	}
	
	var currentColorValue = element.getStyle("background-color");
	var currentColor = new Color(currentColorValue);
	
	var newColor = currentColor.mix(mixColor, amount);
	
	this._fadeBg(element, config, newColor);
}

WebStudio.Fx._fadeBg = function(element, config, finalValue)
{
	if(!config)
	{
		config = { };
	}
	
	if(!config.duration)
	{
		config.duration = 500;
	}
	
	if(!config.transition)
	{
		config.transition = Fx.Transitions.linear;
	}
	
	if(!config.wait)
	{
		config.wait = false;
	}
		
	var myEffects = new Fx.Styles(element, config);
	myEffects.start({
    	'background-color': [element.getStyle("background-color"), finalValue]
	});
}
*/
