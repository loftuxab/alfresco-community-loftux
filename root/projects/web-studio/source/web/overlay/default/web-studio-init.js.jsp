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

	// get the dom template
	StringBuilder overlayTemplate = null;
	if(!WebStudio.getConfig().isDeveloperMode())
	{
		// retrieve from cache if so configured
		overlayTemplate = (StringBuilder) OverlayUtil.getCachedResource(request, "overlayTemplate");
	}
	if(overlayTemplate == null)
	{
		overlayTemplate = new StringBuilder();

		// include the template
		OverlayUtil.include(request, overlayTemplate, "/overlay/default/template/dom.jsp");

		if(!WebStudio.getConfig().isDeveloperMode())
		{
			// cache the dom template
			OverlayUtil.setCachedResource(request, "overlayTemplate", overlayTemplate);
		}
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
	String originalContextPath = org.alfresco.web.studio.OverlayUtil.getOriginalContextPath(request);
	
	String overlayPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
	String imagesPath = overlayPath + "/images"; 
	String iconsPath = overlayPath + "/images/icons";
	
	String proxyStudioPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio");
	String proxyRepoPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/proxy/alfresco");
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

// Set up Paths
WebStudio.overlayPath = "<%=overlayPath%>";
WebStudio.overlayImagesPath = "<%=imagesPath%>";
WebStudio.overlayIconsPath = "<%=iconsPath%>";
WebStudio.originalContextPath = "<%=originalContextPath%>";
WebStudio.proxyStudioPath = "<%=proxyStudioPath%>";
WebStudio.proxyRepoPath = "<%=proxyRepoPath%>";

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
	
	// Put studio class into effect
	$(document.body).addClass("studio");

	// Start Web Studio
	WebStudio.app.init();
});


