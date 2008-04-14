<import resource="/org/alfresco/web/site/include/ads-support.js">





wizard.addGridColumn("associationId", "Association ID");
wizard.addGridColumn("xformtype", "Type");
wizard.addGridColumn("formatId", "Format");
wizard.addGridColumn("templateName", "Template");
wizard.addGridColumn("templateId", "TemplateId");

wizard.addGridColumnFormat("associationId", 120, true);
wizard.addGridColumnFormat("xformtype", 120, true);
wizard.addGridColumnFormat("formatId", 60, false);
wizard.addGridColumnFormat("templateName", 120, true);
wizard.addGridColumnFormat("templateId", 120, true);

wizard.addGridToolbar("add_content_template_association", "New Association", "New Association", "add");
wizard.addGridToolbarSpacer();
wizard.addGridToolbar("remove_content_template_association", "Remove Association", "Remove Association", "delete");

wizard.addGridNoDataMessage("There are no associations currently defined.");


// get all of the content template associations
var associations = site.findContentAssociations(null, null, null, null);
for(var i = 0; i < associations.length; i++)
{
	var sourceId = associations[i].getProperty("sourceId");
	if(sourceId != null)
	{
		var associationId = associations[i].getProperty("id");
		var formatId = associations[i].getProperty("formatId");

		var destId = associations[i].getProperty("destId");
		var pageName = "NOT FOUND";
		var page = site.getObject(destId);
		if(page != null)
			pageName = page.getProperty("name");

		var array = new Array();
		array[0] = associationId;
		array[1] = sourceId;
		array[2] = formatId;
		array[3] = templateName;
		array[4] = templateId;

		wizard.addGridData(array);
	}	
}

