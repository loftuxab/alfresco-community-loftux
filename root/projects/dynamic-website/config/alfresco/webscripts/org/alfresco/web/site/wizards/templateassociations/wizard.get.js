<import resource="/org/alfresco/web/site/include/ads-support.js">


var pageId = wizard.request("pageId");
var page = sitedata.getObject(pageId);

wizard.addGridColumn("formatid", "Format");
wizard.addGridColumn("templatename", "Template Name");
wizard.addGridColumn("templateid", "TemplateID");
wizard.addGridColumn("pageid", "PageID");

wizard.addGridColumnFormat("formatid", 120, true);
wizard.addGridColumnFormat("templatename", 220, true);
wizard.addGridColumnFormat("templateid", 120, false);
wizard.addGridColumnFormat("pageid", 120, false);

wizard.addGridToolbar("add_template_association", "Associate Template", "Associate Template", "add");
wizard.addGridToolbarSpacer();
wizard.addGridToolbar("remove_template_association", "Unassociate Template", "Unassociate Template", "remove");

wizard.addGridNoDataMessage("There are no templates associated with this node.");


// get all of the templates associated to this node
var templates = sitedata.findTemplatesMap(pageId);
for(formatId in templates)
{
	var template = templates[formatId];
	if(template != null)
	{
		var templateId = template.getId();
	
		var array = new Array();
		array[0] = formatId;
		array[1] = template.getTitle();
		array[2] = templateId;
		array[3] = pageId;
		
		wizard.addGridData(array);
	}
}


