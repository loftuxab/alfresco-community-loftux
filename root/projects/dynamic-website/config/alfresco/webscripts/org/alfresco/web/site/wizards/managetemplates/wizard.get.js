<import resource="/org/alfresco/web/site/include/ads-support.js">





wizard.addGridColumn("templatename", "Template");
wizard.addGridColumn("id", "Template ID");
wizard.addGridColumn("templatetypename", "Type");

wizard.addGridColumnFormat("templatename", 200, true);
wizard.addGridColumnFormat("id", 120, true);
wizard.addGridColumnFormat("templatetypename", 200, true);

wizard.addGridToolbar("new_template", "New Template", "Add a New Template", "add");
wizard.addGridToolbarSpacer();
wizard.addGridToolbar("edit_template", "Edit Template", "Edit this Template", "edit");
wizard.addGridToolbarSpacer();
wizard.addGridToolbar("remove_template", "Remove Template", "Remove this Template", "remove");

wizard.addGridNoDataMessage("There are no templates currently defined for this website.");



// get all of the templates
var templates = sitedata.getTemplates();
for(var i = 0; i < templates.length; i++)
{
	var array = new Array();
	array[0] = templates[i].getTitle();
	array[1] = templates[i].getId();
	array[2] = "";
	
	var templateType = sitedata.getObject(templates[i].getProperty("template-type-id"));
	if(templateType != null)
		array[2] = templateType.getTitle();
		
	wizard.addGridData(array);
}

