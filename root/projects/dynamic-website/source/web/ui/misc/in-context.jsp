<%@ page import="org.alfresco.web.site.*"%>
<%@ page import="org.alfresco.web.site.model.*"%>
<%@ page import="org.alfresco.web.site.config.*"%>
<%@ page import="org.alfresco.tools.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// get the query string map
	HashMap map = (HashMap) WebUtil.getQueryStringMap(request);
	
	// text
	String startEditingString = "Start Editing";
		
	String dsContext = RenderUtil.toBrowserUrl("/");
	
	// things to stamp onto page
	String rootPageId = null;
	String renderingPageId = context.getCurrentPage().getId();
	String renderingTemplateId = "";
	if(context.getCurrentTemplate() != null)
		renderingTemplateId = context.getCurrentTemplate().getId();
	if(context.getRootPage() != null)
		rootPageId = context.getRootPage().getId();
			
	String currentThemeId = ThemeUtil.getCurrentThemeId(context);
%>


<div id="divFloatingMenu" style="position:absolute;background-color:#ffffff;z-index:99999">
<table border="0" width="100" cellspacing="2" cellpadding="2" class="floatingMenu">
  <tr>
    <td width="100%" class="floatingMenuHeader" align="center">
     <img src="<%=dsContext%>ui/images/logos/alfresco_logo.gif" />
    </td>
  </tr>
  <tr>
    <td width="100%" class="floatingMenuOptions">
      <img id="incontextFloatingMenuImg" src="<%=dsContext%>ui/themes/builder/images/default/icons/incontext/enable_edit_mode.gif">
      <span id="incontextFloatingMenuText"><%=startEditingString%></span>
    </td>
  </tr>
</table>
</div>

<script type="text/javascript">
var floatingMenuText = Ext.get("incontextFloatingMenuText");
floatingMenuText.on("mouseover", function() { this.dom.style.cursor = 'hand'; });
floatingMenuText.on("click", function() { toggleInContextMode(); });

function floatingMenu()
{
	var startX = getInContextMenuXOffset(),
	startY = getInContextMenuYOffset();
	var ns = (navigator.appName.indexOf("Netscape") != -1);
	var d = document;
	function ml(id)
	{
		var el=d.getElementById?d.getElementById(id):d.all?d.all[id]:d.layers[id];
		if(d.layers)el.style=el;
		el.sP=function(x,y){this.style.left=x;this.style.top=y;};
		el.x = startX;
		el.y = ns ? pageYOffset + innerHeight : document.body.scrollTop + document.body.clientHeight;
		el.y -= startY;
		return el;
	}
	window.stayTopLeft=function()
	{
		startX = getInContextMenuXOffset(),
		startY = getInContextMenuYOffset();
	
		var pY = ns ? pageYOffset + innerHeight : document.body.scrollTop + document.body.clientHeight;
		ftlObj.x = startX;
		ftlObj.y += (pY - startY - ftlObj.y)/8;
		ftlObj.sP(ftlObj.x, ftlObj.y);
		setTimeout("stayTopLeft()", 10);
	}
	ftlObj = ml("divFloatingMenu");
	stayTopLeft();
}
floatingMenu();
</script>

<div id="renderingTemplateId" style="display:none"><%=renderingTemplateId%></div>
<div id="renderingPageId" style="display:none"><%=renderingPageId%></div>
<div id="rootPageId" style="display:none"><%=rootPageId%></div>
