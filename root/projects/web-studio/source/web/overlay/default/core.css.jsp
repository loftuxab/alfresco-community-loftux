<%@ page import="org.alfresco.web.studio.*" %>
<%!
	public String retrieve(HttpServletRequest request, HttpServletResponse response, String path, String fileName)
	{
		StringBuilder buffer = new StringBuilder(32768);
		
		OverlayUtil.include(request, response, buffer, path + "/" + fileName);

		// do a replace
		String rootPath = OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/" + path + "/");
		String str = buffer.toString();
		str = str.replace("url(", "alftemp_(" + rootPath);
		str = str.replace("url (", "alftemp_(" + rootPath);
		str = str.replace("alftemp_(", "url(");

		return str;
	}

	public void yuiInclude(HttpServletRequest request, HttpServletResponse response, StringBuilder buffer, String path, String fileName)
	{
		String value = retrieve(request, response, path, fileName);
		buffer.append(value);
		buffer.append("\r\n");		
	}
%>
<%
	StringBuilder buffer = new StringBuilder(32768);
		
	// Include Web Studio Container
	OverlayUtil.include(request, response, buffer, "/overlay/default/web-studio.css.jsp");

	// Include YUI Fonts and YUI Reset
	yuiInclude(request, response, buffer, "/yui/fonts", "fonts.css");
	
	// Include YUI SAM Elements
	yuiInclude(request, response, buffer, "/yui/assets/skins/sam", "skin.css");
	
	// Include Web Studio Core Dialogs
	OverlayUtil.include(request, response, buffer, "/overlay/default/dialogs/login-dialog.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/dialogs/sandbox-dialog.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/dialogs/cta-dialog.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/dialogs/pta-dialog.class.css.jsp");
	
	// Include Web Studio Core Components
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/drop-down-list/drop-down-list.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/forms/forms.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/menu/menu.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/menu-new/menu-new.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/panel/panel.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/region/region.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/sliders-sector/sliders-sector.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/splitter/splitter.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/tree-view/tree-view.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/window/window.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/page-editor/page-editor.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/templates-view/templates-view.class.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/template-designer/template/renderers/absolute.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/template-designer/template/renderers/table.css.jsp");	
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/content-view/content-view.class.css.jsp");

	// Include Web Studio Additional Core Components
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/alf-window-progress.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/messagebox.css.jsp");
	OverlayUtil.include(request, response, buffer, "/overlay/default/components/site-config-form.css.jsp");
	
	// Include Web Studio Core Container	
	OverlayUtil.include(request, response, buffer, "/overlay/default/container/container.css.jsp");
	
	// Include jQuery Plugins
	OverlayUtil.include(request, response, buffer, "/overlay/default/css/jquery/plugins/facebox.css");
	
	out.println(buffer.toString());
%>
