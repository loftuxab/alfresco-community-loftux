<%@ page import="org.alfresco.web.site.*"%>
<%@ page import="org.alfresco.web.site.model.*"%>
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
		
	String dsContext = URLUtil.browser(context, "/");
	
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
     <img src="<%=dsContext%>images/logo/alfresco_logo.gif" />
    </td>
  </tr>
  <tr>
    <td width="100%" class="floatingMenuOptions">
      <img id="incontextFloatingMenuImg" src="<%=dsContext%>themes/builder/images/default/icons/incontext/enable_edit_mode.gif">
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
	var fEl = Ext.get("divFloatingMenu");

	var padding = 5;

	var maxHeight = Ext.getBody().getHeight();
	var maxWidth = Ext.getBody().getWidth();

	maxHeight = maxHeight - 16;

	var x = 0 + padding + getInContextMenuXOffset();
	var y = maxHeight - fEl.getHeight() - padding;

	fEl.setLeft(x);
	fEl.setTop(y);

	setTimeout("floatingMenu()", 10);
}
floatingMenu();
</script>

<div id="renderingTemplateId" style="display:none"><%=renderingTemplateId%></div>
<div id="renderingPageId" style="display:none"><%=renderingPageId%></div>
<div id="rootPageId" style="display:none"><%=rootPageId%></div>
