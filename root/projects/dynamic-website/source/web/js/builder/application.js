function applicationButtonHandler(item, e) 
{
	var id = item.id;

	if('add_template_association' == id)
	{
		var currentPageId = getCurrentPageId();
		addTemplateAssociation(currentPageId);
	}
	
	if('remove_template_association' == id)
	{
		var gp = Ext.ComponentMgr.get(item.gridPanelId);		
		var selected = gp.getSelectionModel().getSelected();
		if(selected != null)
		{
			var pageid = selected.data["pageid"];
			var formatid = selected.data["formatid"];
			removeTemplateAssociation(pageid, formatid);
		}				
	}
	
	if('new_template' == id)
	{
		addNewTemplate();
	}
	
	if('edit_template' == id)
	{
		var gp = Ext.ComponentMgr.get(item.gridPanelId);		
		var selected = gp.getSelectionModel().getSelected();
		if(selected != null)
		{
			var templateId = selected.data["id"];
			editTemplate(templateId);
		}				
	}
	
	if('remove_template' == id)
	{
		var gp = Ext.ComponentMgr.get(item.gridPanelId);		
		var selected = gp.getSelectionModel().getSelected();
		if(selected != null)
		{
			var templateId = selected.data["id"];
			removeTemplate(templateId);
		}						
	}
	
	if('add_endpoint' == id)
	{
		addEndpoint();
	}

	if('edit_endpoint' == id)
	{
		var gp = Ext.ComponentMgr.get(item.gridPanelId);		
		var selected = gp.getSelectionModel().getSelected();
		if(selected != null)
		{
			var endpointId = selected.data["endpointId"];
			editEndpoint(endpointId);
		}				
	}
	
	if('remove_endpoint' == id)
	{
		var gp = Ext.ComponentMgr.get(item.gridPanelId);		
		var selected = gp.getSelectionModel().getSelected();
		if(selected != null)
		{
			var endpointId = selected.data["endpointId"];
			removeEndpoint(endpointId);
		}				
	}
	
	if('add_content_template_association' == id)
	{
		addContentTemplateAssociation();
	}
	
	if('remove_content_template_association' == id)
	{
		var gp = Ext.ComponentMgr.get(item.gridPanelId);		
		var selected = gp.getSelectionModel().getSelected();
		if(selected != null)
		{
			var associationId = selected.data["associationId"];
			removeContentTemplateAssociation(associationId);
		}				
	}









	var id = item.id;
	
	// configure_web_site
	if('configure_web_site' == id)
	{
		showSiteConfigurationWindow();
	}

	// configure_endpoints
	if('configure_endpoints' == id)
	{
		showEndpointManagerWindow();
	}

	// view_sandbox
	if('view_sandbox' == id)
	{
		window.open(getAdsWebScriptURL("/ads/redirect/viewsandbox"));
	}

	if('manage_templates' == id)
	{
		var currentPageId = getCurrentPageId();
		manageTemplates(currentPageId);	
	}

	if('manage_content_presentation' == id)
	{
		showContentPresentationManagerWindow();
	}
	
	if('associate_templates' == id)
	{
		var currentPageId = getCurrentPageId();
		templateAssociations(currentPageId);	
	}
	
	if('open_template' == id)
	{
		var currentTemplateId = getCurrentTemplateId();
		editTemplate(currentTemplateId);
	}
	
	
	
	if('refresh_cache' == id)
	{
		refreshCache();
	}
	
	
		
	
	
	
	//
	// REGION MENU clicks
	//
	
	if('add_new_component' == id)
	{
		addNewComponentToRegion(item.regionId, item.regionScopeId, item.regionSourceId);
	}
	
	if('configure_existing_component' == id)
	{
		editComponent(item.regionId, item.regionScopeId, item.regionSourceId, item.componentId, item.componentTypeId);
	}
	
	if('remove_existing_component' == id)
	{
		removeComponent(item.regionId, item.regionScopeId, item.regionSourceId, item.componentId, item.componentTypeId);
	}
	
	
	
	
	// docking panel on/off
	if('toggle_docking_panel' == id)
	{
		toggleDockingPanel();
	}
	


	
	
	//
	// Themes
	//
	if(id.length > 6)
	{
		if(id.substring(0,6) == "theme-")
		{
			var themeId = item.themeId;
			switchTheme(themeId);
		}
	}
	
	//
	// Alfresco Icon
	//
	if('learn_about_alfresco' == id)
	{
		window.open('http://www.alfresco.com');
	}



}



/*******************************************
 **  HELPER FUNCTIONS FOR LAUNCHING WINDOWS
 *******************************************/

function showContentPresentationManagerWindow()
{
	var initialUri = "/wizard/contenttypemanager";
	var key = "contenttypemanager";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.init();
}

function showEndpointManagerWindow()
{
	var initialUri = "/wizard/endpointmanager";
	var key = "endpointmanager";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.init();
}

function showSiteConfigurationWindow()
{
	var initialUri = "/wizard/siteconfig";
	var key = "site-configuration";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.init();
}

function showWelcomeWindow()
{
	var initialUri = "/wizard/welcome";
	var key = "welcome";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.init();
}

function editNavigationNode(node)
{
	var pageId = node.attributes.pageId;
	
	var initialUri = "/wizard/navproperties";
	var key = "navproperties";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { pageId: pageId } );	
	dialog.onFinish( function() {
		var treePanel = Ext.ComponentMgr.get("navigation_window_panel");
		treePanel.getLoader().load(node);
	});
	dialog.init();
}

function addNavigationNode(node)
{
	var pageId = node.attributes.pageId;
	
	var initialUri = "/wizard/addchildnav";
	var key = "addchildnav";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { pageId: pageId } );
	dialog.onFinish( function() {
		var treePanel = Ext.ComponentMgr.get("navigation_window_panel");
		treePanel.getLoader().load(node);
		node.expand();
	});
	dialog.init();
}

function removeNavigationNode(node)
{
	var pageId = node.attributes.pageId;
	var parentNode = node.parentNode;
	var parentPageId = parentNode.attributes.pageId;
	
	
	var initialUri = "/wizard/removechildnav";
	var key = "removenav";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { parentPageId: parentPageId, pageId: pageId } );
	dialog.onFinish( function() {
		var treePanel = Ext.ComponentMgr.get("navigation_window_panel");
		treePanel.getLoader().load(parentNode);
		//parentNode.expand();
	});
	dialog.init();
}

function manageTemplates(pageId)
{
	var initialUri = "/wizard/managetemplates";
	var key = "managetemplates";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { pageId: pageId } );
	dialog.init();
}


function addNewComponentToRegion(regionId, regionScopeId, regionSourceId)
{
	var initialUri = "/wizard/addnewcomponent";
	var key = "addnewcomponent";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { 
		regionId: regionId,
		regionScopeId: regionScopeId,
		regionSourceId: regionSourceId 
	} );
	dialog.init();
}

function editComponent(regionId, regionScopeId, regionSourceId, componentId, componentTypeId)
{
	var initialUri = "/component/" + componentTypeId + "/edit";
	var key = "editcomponent";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( {
		regionId: regionId,
		regionScopeId: regionScopeId,
		regionSourceId: regionSourceId,
		componentId: componentId,
		componentTypeId: componentTypeId
	} );
	dialog.init();
}

function removeComponent(regionId, regionScopeId, regionSourceId, componentId, componentTypeId)
{
	var initialUri = "/wizard/removecomponent";
	var key = "removecomponent";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( {
		regionId: regionId,
		regionScopeId: regionScopeId,
		regionSourceId: regionSourceId,
		componentId: componentId,
		componentTypeId: componentTypeId
	} );	
	dialog.init();
}

function addNewTemplate()
{
	var initialUri = "/wizard/addnewtemplate";
	var key = "addtemplate";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.init();
}

function editTemplate(templateId)
{
	var initialUri = "/wizard/edittemplate";
	var key = "edittemplate";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { templateId: templateId } );
	dialog.init();
}

function removeTemplate(templateId)
{
	var initialUri = "/wizard/removetemplate";
	var key = "removetemplate";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { templateId: templateId } );
	dialog.init();
}

function templateAssociations(pageId)
{
	var initialUri = "/wizard/templateassociations";
	var key = "templateassociations";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { pageId: pageId } );
	dialog.init();
}


function addTemplateAssociation(pageId)
{
	var initialUri = "/wizard/addtemplateassociation";
	var key = "addtemplateassociation";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { pageId: pageId } );
	dialog.onFinish( function() {
		// TODO: Tell the "templateassociations" dialog to refresh
	});
	dialog.init();
}

function removeTemplateAssociation(pageId, formatId)
{
	var initialUri = "/wizard/removetemplateassociation";
	var key = "removetemplateassociation";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { pageId: pageId, formatId: formatId } );
	dialog.onFinish( function() {
		// TODO: Tell the "templateassociations" dialog to refresh
	});
	dialog.init();
}

function addEndpoint()
{
	var initialUri = "/wizard/addendpoint";
	var key = "addendpoint";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.init();
}

function editEndpoint(endpointId)
{
	var initialUri = "/wizard/editendpoint";
	var key = "editendpoint";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { endpointId: endpointId } );
	dialog.init();
}

function removeEndpoint(endpointId)
{
	var initialUri = "/wizard/removeendpoint";
	var key = "removeendpoint";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { endpointId: endpointId } );
	dialog.init();
}

function addContentTemplateAssociation()
{
	var initialUri = "/wizard/add-ct-association";
	var key = "add-ct-association";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.init();
}

function removeContentTemplateAssociation(associationId)
{
	var initialUri = "/wizard/remove-ct-association";
	var key = "remove-ct-association";
	var dialog = dialogManager.getDialog(key);
	if(dialog != null) {
		dialog.processAction("cancel");	
		dialog = null;
	}
	if(dialog == null)
	{
		dialog = new DefaultDialog(key, initialUri);
		dialogManager.addDialog(key, dialog);	
	}
	dialog.setDefaultJson( { associationId: associationId } );
	dialog.init();
}
