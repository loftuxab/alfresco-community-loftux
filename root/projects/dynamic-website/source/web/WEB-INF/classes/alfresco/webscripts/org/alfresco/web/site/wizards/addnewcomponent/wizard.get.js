<import resource="/org/alfresco/web/site/include/ads-support.js">


// set up form elements
wizard.addElement("componentName", componentName);
wizard.addElement("componentDescription", componentDescription);
wizard.addElement("componentTypeId", componentTypeId);
wizard.addElementFormat("componentName", "Name", "textfield", 290);
wizard.addElementFormat("componentDescription", "Description", "textarea", 290);
wizard.addElementFormat("componentTypeId", "Type", "combo", 290);


//
// COMPONENT TYPE ID dropdown
//

wizard.addElementFormatKeyPair("componentTypeId", "emptyText", "Please select a Component Type");
wizard.addElementFormatKeyPair("componentTypeId", "title", "Component Types");

var componentTypes = site.getComponentTypes();
for(var i = 0; i < componentTypes.length; i++)
{
	var _componentTypeId = componentTypes[i].getProperty("id");
	var _componentTypeName = componentTypes[i].getProperty("name");
	wizard.addElementSelectionValue("componentTypeId", _componentTypeId, _componentTypeName);
}

wizard.updateElement("formatId", "default");


