<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.tools.*"%>
<%@ page import="org.alfresco.web.site.model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="16kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);

	String aHost = request.getServerName();
	String aPort = "" + request.getServerPort();
	String aWebappUri = "/alfresco"; // TODO: Don't need this anymore
	String aWebscriptServiceUri = "/service";

	// determine jsessionid		
	String jSessionId = "";
	Cookie[] cookies = request.getCookies();
	if(cookies != null)
	{
		for(int ai = 0; ai < cookies.length; ai++)
		{
			if("JSESSIONID".equalsIgnoreCase(cookies[ai].getName()))
				jSessionId = cookies[ai].getValue();
		}
	}
	
	boolean isFileSystemCacheEnabled = CacheUtil.isFileSystemCacheEnabled(context);	
	
	String currentThemeId = ThemeUtil.getCurrentThemeId(context);	
	
	String extBlankImageUrl = URLUtil.browser(context, "/extjs/resources/images/default/s.gif");
%>

// INITIALIZATION
Ext.BLANK_IMAGE_URL = '<%=extBlankImageUrl%>';


String.prototype.startsWith = function(s) { return this.indexOf(s)==0; }

function getHost() {
<%
	if(aHost == null){ %>return null;<%}else{%>return "<%=aHost%>";<%}
%>
}
function getPort() {
<%
	if(aPort == null){ %>return null;<%}else{%>return "<%=aPort%>";<%}
%>
}
function getHttpHostPort() {
	return "http://" + getHost() + ":" + getPort();
}
function getServiceUri() {
<%
	if(aWebscriptServiceUri == null){ %>return null;<%}else{%>return "<%=aWebscriptServiceUri%>";<%}
%>
}
function getStoreId() {
<%
	String storeId = context.getStoreId();
	if(storeId == null) { 
		%>return null;<%
	}
	else {
		%>return "<%=storeId%>";<%
	}
%>
}
function getWebappPath() {
	return "/www/avm_webapps/ROOT";
}

function getCurrentTemplateId()
{
	var el = Ext.get("renderingTemplateId");
	if(el != null)
	{
		return el.dom.innerHTML;
	}
	return null;
}

function getCurrentPageId()
{
	var el = Ext.get("renderingPageId");
	if(el != null)
	{
		return el.dom.innerHTML;
	}
	return null;
}

function getRootPageId()
{
	var el = Ext.get("rootPageId");
	if(el != null)
	{
		return el.dom.innerHTML;
	}
	return null;
}


function determineRegionSourceId(regionScopeId)
{
	if("template" == regionScopeId)
		return getCurrentTemplateId();
	if("page" == regionScopeId)
		return getCurrentPageId();
	return "site";
}




function getAlfrescoTicket()
{
<%
	String ticket = (String) request.getParameter("ticket");
	if(ticket == null)
		ticket = (String) request.getHeader("ticket");
	if(ticket != null)
	{
%>
		return "<%=ticket%>";
<%
	}
	else
	{
%>	
		return null;
<%
	}
%>
}

function toBrowser(relativeUrl)
{
	if(relativeUrl == null)
		return "<%=URLUtil.browser(context, "/")%>";
	if(relativeUrl.startsWith("/"))
	{
		relativeUrl = relativeUrl.substring(1, relativeUrl.length);
	}
	return "<%=URLUtil.browser(context, "/")%>" + relativeUrl;
}
















//////////////////////////
//
// Forms stuff
// TODO: Make this entirely dynamic
//
//////////////////////////

function getFormNames()
{
	var formArray = new Array();
	formArray[formArray.length] = "article";
	formArray[formArray.length] = "press-release";
	formArray[formArray.length] = "event";
	formArray[formArray.length] = "product";
	return formArray;
}

function getFormTitle(formName)
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

function getFormDescription(formName)
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




// this just returns the query string

function getQueryString()
{
<%
	Map mapz = WebUtil.getQueryStringMap(request);
	String qsz = WebUtil.getQueryStringForMap(mapz);
	if(qsz == null) {
		%>return "?a=1";<%
	}
	else {
		%>return "<%=qsz%>";<%
	}
%>
}


var ifsce = <%=isFileSystemCacheEnabled%>;
function isCacheEnabled()
{
	return ifsce;
}


function getLocalProtocolHostPort()
{
	
	return "";
}

function buildProxiedUrl(url)
{
	//var proxiedURL = getLocalProtocolHostPort() + "/proxy?endpoint=" + url;
	var proxiedURL = url;
	return proxiedURL;
}

function getAdsWebScriptURL(webScript)
{
	var url = getServiceUri() + webScript + "?avmStoreId=" + getStoreId();
	return getHttpHostPort() + toBrowser(url);
}


var currentThemeId = "<%=currentThemeId%>";

function getCurrentThemeId()
{
	return currentThemeId;
}


//////////////////////////
//
// Themes stuff
// TODO: Make this entirely dynamic
//
//////////////////////////

function getThemeIds()
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

function getThemeName(themeId)
{
	return themeId;
}





////////////////////////////
//
// Utility Functions
//
////////////////////////////

function wait(msecs)
{
	var start = new Date().getTime();
	var cur = start
	while(cur - start < msecs)
	{
		cur = new Date().getTime();
	}
} 
