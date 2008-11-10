<%@ page import="org.alfresco.web.studio.*" %>
<%
	StringBuilder buffer = new StringBuilder(32768);

	// Include MooTools
	OverlayUtil.include(request, buffer, "/overlay/default/js/mootools/mootools.v1.11.js");

	// Include the "web-studio" container
	OverlayUtil.include(request, buffer, "/overlay/default/web-studio.js.jsp");
			
	// Include YUI Core
	OverlayUtil.include(request, buffer, "/yui/yahoo/yahoo-min.js");
	OverlayUtil.include(request, buffer, "/yui/yahoo-dom-event/yahoo-dom-event.js");
	OverlayUtil.include(request, buffer, "/yui/animation/animation-min.js");
	OverlayUtil.include(request, buffer, "/yui/dragdrop/dragdrop-min.js");
	OverlayUtil.include(request, buffer, "/yui/container/container-min.js");
	OverlayUtil.include(request, buffer, "/yui/element/element-beta-min.js");
	OverlayUtil.include(request, buffer, "/yui/connection/connection-min.js");		

	// Include YUI Elements for Web Studio Core
	OverlayUtil.include(request, buffer, "/yui/treeview/treeview-min.js");		
	OverlayUtil.include(request, buffer, "/yui/resize/resize-beta-min.js");				
	OverlayUtil.include(request, buffer, "/yui/datasource/datasource-beta-min.js");
	OverlayUtil.include(request, buffer, "/yui/datatable/datatable-beta-min.js");		
	OverlayUtil.include(request, buffer, "/yui/menu/menu-min.js");		
	OverlayUtil.include(request, buffer, "/yui/button/button-min.js");
	OverlayUtil.include(request, buffer, "/yui/utilities/utilities.js");
	
	// Include Web Studio Core Utilities
	OverlayUtil.include(request, buffer, "/overlay/default/js/utils/alfresco.utils.js");
	OverlayUtil.include(request, buffer, "/overlay/default/js/utils/abstract-templater.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/js/utils/stretcher.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/js/utils/YAHOOExtends.js");
	OverlayUtil.include(request, buffer, "/overlay/default/js/utils/wizard.class.js");
	
	// Include Template Object Model
	OverlayUtil.include(request, buffer, "/overlay/default/components/template-designer/template/model/abstract.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/template-designer/template/model/dynamic-template.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/template-designer/template/model/row.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/template-designer/template/model/column.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/template-designer/template/model/region.js");
	
	// Include Web Studio Core Components
	OverlayUtil.include(request, buffer, "/overlay/default/components/drop-down-list/drop-down-list.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/forms/forms.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/menu/menu.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/menu-new/menu-new.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/panel/panel.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/region/region.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/sliders-sector/sliders-sector.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/splitter/splitter.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/tree-view/tree-view.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/window/window.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/page-editor/page-editor.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/templates-view/templates-view.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/template-designer/template-designer.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/components/template-designer/template/renderers/absolute.js.jsp");
	OverlayUtil.include(request, buffer, "/overlay/default/components/template-designer/template/renderers/table.js.jsp");

	// Include Web Studio Dialogs
	OverlayUtil.include(request, buffer, "/overlay/default/dialogs/login-dialog.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/dialogs/sandbox-dialog.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/dialogs/cta-dialog.class.js");
	OverlayUtil.include(request, buffer, "/overlay/default/dialogs/pta-dialog.class.js");
	
	// Include Web Studio Core Container
	OverlayUtil.include(request, buffer, "/overlay/default/container/container.js.jsp");
	
	// Include Abstracts
	OverlayUtil.include(request, buffer, "/overlay/default/applications/abstract-application.class.js.jsp");
	OverlayUtil.include(request, buffer, "/overlay/default/applets/abstract-applet.class.js.jsp");	
	
	out.println(buffer.toString());
%>