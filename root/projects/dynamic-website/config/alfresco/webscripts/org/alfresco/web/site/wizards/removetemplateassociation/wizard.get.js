<import resource="/org/alfresco/web/site/include/ads-support.js">


// incoming
var pageId = wizard.request("pageId");
var formatId = wizard.request("formatId");
if(formatId == null)
	formatId = "default"; // TODO CHANGE THIS

// load the page
var page = sitedata.getObject("page", pageId);

// load the template
var templatesMap = sitedata.findTemplatesMap(pageId);
var template = templatesMap[formatId];
if(template != null)
{
	var templateName = template.getTitle();
	var templateDescription = template.getDescription();

	wizard.setResponseTitle("Remove Association to " + templateName + " for '" + formatId + "'");
	wizard.setDialogHTML("Are you sure that you want to remove the following template association?<br/><br/><b>"+templateName+"</b><br/>"+templateDescription+"<br/>");
}

