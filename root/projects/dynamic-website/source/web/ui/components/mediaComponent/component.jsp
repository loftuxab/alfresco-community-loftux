<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.web.site.model.*" %>
<%@ page import="org.alfresco.web.site.config.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/adw.tld" prefix="adw" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// get the configuration
	RuntimeConfig configuration = (RuntimeConfig) request.getAttribute("component-configuration");

	String componentPathUri = (String) config.get("component-path-uri");
	
	// config values
	String componentId = (String) configuration.get("component-id");

	String mediaType = (String) configuration.get("mediaType");
	String url = (String) configuration.get("url");

	String unsupportedText = (String) configuration.get("unsupportedText");
	if(unsupportedText == null || "".equals(unsupportedText))
		unsupportedText = "The viewer is not installed.";

	String width = (String) configuration.get("width");
	if(width == null || "".equals(width))
		width = "320";

	String height = (String) configuration.get("height");
	if(height == null || "".equals(height))
		height = "240";

	boolean configured = true;
	
	if(mediaType == null || "".equals(mediaType))
		configured = false;
	if(url == null || "".equals(url))
		configured = false;
		
	if(!configured)
	{
		String currentThemeId = ThemeUtil.getCurrentThemeId(context);
		String unconfiguredImageUrl = URLUtil.toBrowserUrl("/ui/themes/builder/images/" + currentThemeId + "/icons/unconfigured_component_large.gif");
		String renderString = "<img src='" + unconfiguredImageUrl + "' border='0' alt='Unconfigured Image Component'/>";
		out.println(renderString);
		return;
	}
	
	String divId = "div-" + componentId;
%>
<adw:require>
	<script type="text/javascript" src="<%=componentPathUri%>/mediapanel.js"></script>
</adw:require>
<div>
<br/>
<br/>
<div id="<%=divId%>"></div>
</div>
<%
	if(url.startsWith("/"))
		url = "http://" + request.getServerName() + ":" + request.getServerPort() + url;

	String extension = null;
	int x = url.lastIndexOf(".");
	if(x > -1)
		extension = url.substring(x+1, url.length());
	if(extension != null)
		extension = extension.toLowerCase();
%>	
<script language="Javascript">

//var width = Ext.get("<%=divId%>").parent().getWidth();
//var height = Ext.get("<%=divId%>").parent().getHeight();
var width=320;
var height=240;

var extension = <%=(extension == null ? null : "\"" + extension + "\"")%>;
var isWindows = Ext.isWindows;
var isLinux = Ext.isLinux;
var isMac = Ext.isMac;

var config = {
       url         :'<%=url%>'
       ,unsupportedText : '<%=unsupportedText%>'
       ,start	: true
}

if("video" == "<%=mediaType%>")
{
	if("wmv" == extension || "avi" == extension)
	{
		if(isWindows)
		{
			config["mediaType"] = "WMV";
			config["params"] = { };
		}
	}
	
	if("mpg" == extension)
	{
		if(isMac || isLinux)
		{
			config["mediaType"] = "MOV";
			config["params"] = { };
		}
		if(isWindows)
		{
			config["mediaType"] = "WMV";
			config["params"] = { };
		}
	}
	if("swf" == extension)
	{
		config["mediaType"] = "SWF";
		config["params"] = { };
	}
	if("rv" == extension)
	{
		config["mediaType"] = "REAL";
		config["params"] = { };
	}
}

if("audio" == "<%=mediaType%>")
{
	if(isWindows)
	{
		config["mediaType"] = "WMV";
		config["params"] = { };			
	}
	else
	{	
		if("mp3" == extension)
		{
			config["mediaType"] = "QTMP3";
			config["params"] = { };
		}
		if("mid" == extension)
		{
			config["mediaType"] = "QTMIDI";
			config["params"] = { };
		}
		if("wav" == extension)
		{
			config["mediaType"] = "QTWAV";
			config["params"] = { };
		}
	}
}

if("flash" == "<%=mediaType%>")
{
	config["mediaType"] = "flashpanel";
	config["params"] = { };
}

if("pdf" == "<%=mediaType%>")
{
	config["mediaType"] = "PDF";
	config["params"] = { };
}

var p = new Ext.ux.MediaPanel({
        id:'<%=componentId%>',
        renderTo: '<%=divId%>',
        height: height,
        width : width,
        mediaCfg: config
});

//p.show();

</script>
<br/>
<br/>
<br/>