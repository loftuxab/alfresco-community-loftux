<import resource="/include/support.js">

// incoming
var contentId = wizard.request("contentId");
var assocType = wizard.request("assocType");
var templateId = wizard.request("templateId");
var formatId = wizard.request("formatId");

var template = sitedata.getTemplate(templateId);
if(template != null)
{
	var templateName = template.getTitle();
	var templateDescription = template.getDescription();

	wizard.setResponseTitle("Are you sure?");
	wizard.setDialogHTML("Are you sure that you want to remove this association?");
}

