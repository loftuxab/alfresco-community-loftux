<import resource="/org/alfresco/web/site/include/ads-support.js">


// things we receive
var componentId = wizard.request("componentId");
if(componentId != null)
{
	var component = site.getObject(componentId);
	if(component != null)
	{	
		// get the component name
		var componentName = component.getName();

		var html = "Are you sure that you would like to unassociate the following component:";
		html += "<br/><br/>";
		html += "<B>" + componentName + "</B>";
		html += "<br/><br/>";
		html += "This <u>will not delete</u> the component but will simply remove it from this page.";
		html += "<br/>";
		html += "If you would like to re-add it another time, just pick <b>Add Existing Component</b> from the menu.";

		// TODO: more information about this component
		// what it is bound to, what scope it is in, what configuration settings it has

		// set the html
		wizard.setDialogHTML(html);
	}
}
