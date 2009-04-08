<%@ page import="org.alfresco.web.studio.*" %>
<%
	StringBuilder buffer = new StringBuilder(32768);

	// Include MooTools (minimized) 
	OverlayUtil.include(request, response, buffer, "/overlay/default/js/mootools/mootools.v1.11.js");

	// Include jQuery (minimized)
	//OverlayUtil.include(request, response, buffer, "/overlay/default/js/jquery/jquery-1.2.6.min.js");
	OverlayUtil.include(request, response, buffer, "/overlay/default/js/jquery/jquery-1.3.2.min.js");
	
	// Include html parser (not minimized, but very small)
	OverlayUtil.include(request, response, buffer, "/overlay/default/js/htmlparser/htmlparser.js");

	// Include jQuery UI (minimized)
	//OverlayUtil.include(request, response, buffer, "/overlay/default/js/jquery/ui/jquery-ui-personalized-1.6rc2.min.js");
	OverlayUtil.include(request, response, buffer, "/overlay/default/js/jquery/ui/jquery-ui-1.7.1.custom.min.js");
	OverlayUtil.include(request, response, buffer, "/overlay/default/js/jquery/plugins/jquery.hoverpulse.js");
	
	// Include jQuery Plugins (not minimized, but very small)
	OverlayUtil.include(request, response, buffer, "/overlay/default/js/jquery/plugins/jquery.flip.js");
	OverlayUtil.include(request, response, buffer, "/overlay/default/js/jquery/plugins/jquery.flyout.js");
	OverlayUtil.include(request, response, buffer, "/overlay/default/js/jquery/plugins/facebox.js");

	// Include YUI Core (all minimized)
	OverlayUtil.include(request, response, buffer, "/yui/utilities/utilities.js");
	OverlayUtil.include(request, response, buffer, "/yui/container/container-min.js");
	OverlayUtil.include(request, response, buffer, "/yui/menu/menu-min.js");
	OverlayUtil.include(request, response, buffer, "/yui/button/button-min.js");
	OverlayUtil.include(request, response, buffer, "/yui/datasource/datasource-min.js");
	OverlayUtil.include(request, response, buffer, "/yui/datatable/datatable-min.js");
	OverlayUtil.include(request, response, buffer, "/yui/editor/editor-min.js");
	OverlayUtil.include(request, response, buffer, "/yui/resize/resize-min.js");
	OverlayUtil.include(request, response, buffer, "/yui/treeview/treeview-min.js");
		
	// Now Include Web Studio Scripts
	if(WebStudio.getConfig().isDeveloperMode())
	{
		// if we are in developer mode, include the individual scripts
		// one at a time

		// Include Alfresco Tools
		OverlayUtil.include(request, response, buffer, "/overlay/default/alf.js");
	
		// Include the "web-studio" container
		OverlayUtil.include(request, response, buffer, "/overlay/default/web-studio.js");
		
		// Include Web Studio Core Utilities
		OverlayUtil.include(request, response, buffer, "/overlay/default/js/utils/alfresco.utils.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/js/utils/abstract-templater.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/js/utils/stretcher.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/js/utils/YAHOOExtends.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/js/utils/wizard.class.js");
		
		// Include Template Object Model
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/template-designer/template/model/abstract.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/template-designer/template/model/dynamic-template.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/template-designer/template/model/row.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/template-designer/template/model/column.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/template-designer/template/model/region.js");
		
		// Include Web Studio Core Components
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/drop-down-list/drop-down-list.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/forms/forms.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/menu/menu.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/menu-new/menu-new.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/panel/panel.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/region/region.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/sliders-sector/sliders-sector.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/splitter/splitter.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/tree-view/tree-view.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/window/window.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/page-editor/page-editor.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/templates-view/templates-view.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/template-designer/template-designer.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/template-designer/template/renderers/absolute.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/template-designer/template/renderers/table.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/pageblocker/pageblocker.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/components/content-view/content-view.class.js");
	
		// Include Web Studio Dialogs
		OverlayUtil.include(request, response, buffer, "/overlay/default/dialogs/login-dialog.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/dialogs/sandbox-dialog.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/dialogs/cta-dialog.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/dialogs/pta-dialog.class.js");
		
		// Include Web Studio Core Container
		OverlayUtil.include(request, response, buffer, "/overlay/default/container/container.js");
		
		// Include Abstracts
		OverlayUtil.include(request, response, buffer, "/overlay/default/applications/abstract-application.class.js");
		OverlayUtil.include(request, response, buffer, "/overlay/default/applets/abstract-applet.class.js");		
	}
	else
	{
		// Otherwise, we are in "production" or minimized mode
		// Include the build-time computed minimized javascript
		// This contains all of the files from above, compressed and plugged in during the build process
		OverlayUtil.include(request, response, buffer, "/overlay/default/minimized/alf-web-studio.min.js");
	}
			
	out.println(buffer.toString());
%>