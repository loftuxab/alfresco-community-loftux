<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.tools.*"%>
<%@ page import="org.alfresco.web.site.model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="16kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%	
	// is the editor on?
	boolean incontext_enabled = InContextUtil.isEnabled(session);

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
%>


// the in-context element registry
// this keeps in sync with the server side registry
var inContextElementRegistry = new Array();
inContextElementRegistry["incontext.enabled"] = <%=incontext_enabled%>;
var inContextElementIds = new Array();

<%
	// get all of the elements
	// write out to local registry
	String[] elementIds = InContextUtil.getInContextElementIds();
	for(int i = 0; i < elementIds.length; i++)
	{
		String elementId = elementIds[i];
		String elementName = InContextUtil.getInContextElementName(elementId);
		String elementType = InContextUtil.getInContextElementType(elementId);
		boolean enabled = InContextUtil.isElementEnabled(session, elementId);
%>	
inContextElementRegistry["<%=elementId%>.name"] = "<%=elementName%>";
inContextElementRegistry["<%=elementId%>.type"] = "<%=elementType%>";
inContextElementRegistry["<%=elementId%>.enabled"] = <%=enabled%>;
inContextElementIds[inContextElementIds.length] = "<%=elementId%>";
<%
		String state = InContextUtil.getElementState(session, elementId);		
		if(state != null)
		{
%>
inContextElementRegistry["<%=elementId%>.state"] = <%=state%>;
<%
		}
	}	
%>


function getInContextElementIds()
{
	return inContextElementIds;
}

function isInContextEnabled()
{
	return inContextElementRegistry["incontext.enabled"];
}

function setInContextElementEnabled(elementId, flag)
{
	inContextElementRegistry[elementId + ".enabled"] = flag;
}

function getInContextElementEnabled(elementId)
{
	if(isInContextEnabled())
	{
		var x = inContextElementRegistry[elementId+".enabled"];
		if(x == null)
			return false;
		return x;
	}
	return false;
}

function setInContextElementState(elementId, state)
{
	inContextElementRegistry[elementId+".state"] = state;
}

function getInContextElementState(elementId)
{
	var x = inContextElementRegistry[elementId+".state"];
	return x;
}

function getInContextElementName(elementId)
{
	return inContextElementRegistry[elementId+".name"];
}

function getInContextElementType(elementId)
{
	return inContextElementRegistry[elementId+".type"];
}





function flipInContextElementEnabled(elementId)
{
	var x = inContextElementRegistry[elementId+".enabled"];
	if(x)
		setInContextElementEnabled(elementId, false);
	else
		setInContextElementEnabled(elementId, true);
}





function getInContextURL(toggle, elementId, value)
{
<%
	HashMap map = WebUtil.getQueryStringMap(request);
	map.remove(InContextUtil.INCONTEXT_TOGGLE_REQUEST_PARAM);
	map.remove(InContextUtil.INCONTEXT_ELEMENT_REQUEST_PARAM);
	map.remove(InContextUtil.INCONTEXT_VALUE_REQUEST_PARAM);
	String qs = WebUtil.getQueryStringForMap(map);
	if(qs == null)
		qs = "";
%>
	var toAppend = "";
	toAppend += "<%=InContextUtil.INCONTEXT_TOGGLE_REQUEST_PARAM%>";
	toAppend += "=";
	toAppend += toggle;
	toAppend += "&";
	toAppend += "<%=InContextUtil.INCONTEXT_ELEMENT_REQUEST_PARAM%>";
	toAppend += "=";
	toAppend += elementId;
	
	if(value != null)
	{
		toAppend += "&";
		toAppend += "<%=InContextUtil.INCONTEXT_VALUE_REQUEST_PARAM%>";
		toAppend += "=";
		toAppend += value;
	}
	toAppend += "&a=1";

	var queryString = "<%=qs%>";
	if(queryString.length == 0)
		queryString = "?" + toAppend;
	else
		queryString = "?" + queryString + "&" + toAppend;

	
	var url = "/incontext/" + queryString;
<%
	if(jSessionId != null)
	{
%>
		url = url + ";JSESSIONID=<%=jSessionId%>";
<%
	}
%>
	return url;
}


// TODO: Super lame, how to calculate this?
function getInContextMenuYOffset()
{
	var offset = 85;
	if(Ext.isIE6 || Ext.isIE7)
		offset = 65;
	return offset;
}

// TODO: Super lame, how to calculate this?
function getInContextMenuXOffset()
{
	var offset = 5;
	if(isInContextEnabled())
	{
		// the dock container width + 10
		offset = 210;
		if(Ext.ComponentMgr.get("dockPanel") != null)
			offset = Ext.ComponentMgr.get("dockPanel").getSize()["width"] + 10;
	}
	return offset;
}


function getInContextWebScriptURL(webScript, dmMode)
{
	var url = getServiceUri() + webScript;
	url = getHttpHostPort() + toBrowser(url);

	if(dmMode == null)
	{
		url = url + "?avmStoreId=" + getStoreId();
	}
	var proxiedURL = buildProxiedUrl(url);
	return proxiedURL;
}

function doPersistElementSessionState(elementId)
{
	// fire to server to persist window state (to incontext session)
	var json = {};
	
	var el = Ext.get(elementId);
	if(el != null)
	{
		json["x"] = el.getBox()["x"];
		json["y"] = el.getBox()["y"];
		json["width"] = el.getBox()["width"];
		json["height"] = el.getBox()["height"];
		var persistUrl = getInContextURL("update", elementId, json.toJSONString());
		Ext.Ajax.request({
			url: persistUrl
		});
		setInContextElementState(elementId, json.toJSONString());
	}
}

function doToggleInContextEnabled(elementId)
{
	// toggle/flip the element/editor/window on or off
	flipInContextElementEnabled(elementId);	
	
	// check its current state locally
	var currentlyEnabled = getInContextElementEnabled(elementId);

	// fire back to update the server (via ajax)	
	var url = getInContextURL("on", elementId);
	if(!currentlyEnabled)
		url = getInContextURL("off", elementId);	
	Ext.Ajax.request({
		url: url
	});
}

function refreshFloatingMenu()
{
	// update text
	var floatingMenuTextSpan = Ext.get("incontextFloatingMenuText");
	var floatingMenuImg = Ext.get("incontextFloatingMenuImg");
	
	var currentlyEnabled = getInContextElementEnabled("incontext");
	if(currentlyEnabled)
	{
		if(floatingMenuTextSpan != null)
			floatingMenuTextSpan.dom.innerHTML = "Stop Editing";
		if(floatingMenuImg != null)
			floatingMenuImg.dom.src = toBrowser("/themes/builder/images/default/icons/incontext/disable_edit_mode.gif");
	}
	else
	{
		if(floatingMenuTextSpan != null)
			floatingMenuTextSpan.dom.innerHTML = "Start Editing";
		if(floatingMenuImg != null)
			floatingMenuImg.dom.src = toBrowser("/themes/builder/images/default/icons/incontext/enable_edit_mode.gif");
	}
	//floatingMenuTextSpan.repaint();
	//floatingMenuImg.repaint();	
}

function doSelectInContextEditor(newEditorId, originalEditorId)
{
	if("off" == newEditorId)
	{
		setInContextElementEnabled("incontext", false);
		var url = getInContextURL("off", "incontext");	
		Ext.Ajax.request({
			url: url
		});
		mainToolbarSelectedEditor = null;
	}
	else
	{
		// update locally that the original editor is off
		setInContextElementEnabled(originalEditorId, false);
		
		// update the server that the original editor is off
		var offUrl = getInContextURL("off", originalEditorId);
		Ext.Ajax.request({
			url: offUrl
		});	

		// update locally that the new editor is on
		setInContextElementEnabled(newEditorId, true);
		
		// update the server to turn on the new editor
		var onUrl = getInContextURL("on", newEditorId);
		Ext.Ajax.request({
			url: onUrl
		});	
		mainToolbarSelectedEditor = newEditorId;
	}	
}



var mainToolbarSelectedEditor = null;

// TODO: Find a way not to do this manually
if(getInContextElementEnabled("content_editor"))
	mainToolbarSelectedEditor = "content_editor";
if(getInContextElementEnabled("page_editor"))
	mainToolbarSelectedEditor = "page_editor";
if(getInContextElementEnabled("layout_editor"))
	mainToolbarSelectedEditor = "layout_editor";



function getInContextElementStateProperty(elementId, propertyName, defaultValue)
{
	var value = defaultValue;
	
	// see if the user has stored window state on the session already
	var state = getInContextElementState(elementId);
	if(state != null)
	{	
		try {
			var json = state;
			var propertyValue = json[propertyName];
			if(propertyValue != null)
				value = propertyValue;
		}
		catch(err) { }
	}

	return value;
}