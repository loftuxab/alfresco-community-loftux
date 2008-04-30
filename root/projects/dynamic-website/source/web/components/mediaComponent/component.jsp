<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.web.site.model.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/alf.tld" prefix="alf" %>
<%@ page isELIgnored="false" %>
<alf:require script="/components/mediaComponent/uxmedia.js"/>
<alf:require script="/components/mediaComponent/uxflash.js"/>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);

	// config values
	String componentId = (String) context.getRenderData().get("component-id");

	String mediaType = (String) context.getRenderData().get("mediaType");
	String mediaUrl = (String) context.getRenderData().get("mediaUrl");

	String unsupportedText = (String) context.getRenderData().get("unsupportedText");
	if(unsupportedText == null || "".equals(unsupportedText))
		unsupportedText = "The viewer is not installed.";

	String width = (String) context.getRenderData().get("width");
	if(width == null || "".equals(width))
		width = "320";

	String height = (String) context.getRenderData().get("height");
	if(height == null || "".equals(height))
		height = "240";

	boolean configured = true;
	
	if(mediaType == null || "".equals(mediaType))
		configured = false;
	if(mediaUrl == null || "".equals(mediaUrl))
		configured = false;
		
	if(!configured)
	{
		String currentThemeId = ThemeUtil.getCurrentThemeId(context);
		String unconfiguredImageUrl = URLUtil.browser(context, "/themes/builder/images/" + currentThemeId + "/icons/unconfigured_component_large.gif");
		String renderString = "<img src='" + unconfiguredImageUrl + "' border='0' alt='Unconfigured Image Component' width='64px' height='64px' />";
		out.println(renderString);
		return;
	}
	
	String divId = "div-" + componentId;
%>
<div>
<br/>
<br/>
<div id="<%=divId%>"></div>

<%
	if(mediaUrl.startsWith("/"))
		mediaUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + mediaUrl;

	String extension = null;
	int x = mediaUrl.lastIndexOf(".");
	if(x > -1)
		extension = mediaUrl.substring(x+1, mediaUrl.length());
	if(extension != null)
		extension = extension.toLowerCase();
%>	
<script language="Javascript">

var width=320;
var height=240;

var extension = <%=(extension == null ? null : "\"" + extension + "\"")%>;
var isWindows = Ext.isWindows;
var isLinux = Ext.isLinux;
var isMac = Ext.isMac;

// base config
var config = {
       			url		: '<%=mediaUrl%>',
       			id		: '<%=componentId%>',
       			start		: true,
       			loop		: true,
       			controls	: true,
       			autoSize	: true
};



if("video" == "<%=mediaType%>")
{
	if("wmv" == extension || "avi" == extension)
	{
		if(isWindows)
		{
			config["mediaType"] = "WMV";
		}
	}
	
	if("mpg" == extension)
	{
		if(isMac || isLinux)
		{
			config["mediaType"] = "MOV";
		}
		if(isWindows)
		{
			config["mediaType"] = "WMV";
		}
	}
	if("swf" == extension)
	{
		config["mediaType"] = "SWF";
	}
	if("rv" == extension)
	{
		config["mediaType"] = "REAL";
	}
}

if("audio" == "<%=mediaType%>")
{
	if(isWindows)
	{
		config["mediaType"] = "WMV";
	}
	else
	{	
		if("mp3" == extension)
		{
			config["mediaType"] = "QTMP3";
		}
		if("mid" == extension)
		{
			config["mediaType"] = "QTMIDI";
		}
		if("wav" == extension)
		{
			config["mediaType"] = "QTWAV";
		}
	}
}

if("flash" == "<%=mediaType%>")
{
	config["mediaType"] = "SWF";
}

if("pdf" == "<%=mediaType%>")
{
	config["mediaType"] = "PDF";
}



	var p = new Ext.ux.MediaPanel({
		id		: 'win<%=componentId%>',
		renderTo	: '<%=divId%>',
		height		: <%=height%>,
		width		: <%=width%>,
		mediaCfg	: config
	});
	p.show();

</script>

<br/>
<br/>
<br/>
</div>