<import resource="/include/support.js">

var templateId = wizard.request("templateId");

wizard.addHiddenElement("templateId", templateId);

wizard.addElement("regionName", "");
wizard.addElementFormat("regionName", "Name", "textfield", 290);

wizard.addElement("regionDescription", "");
wizard.addElementFormat("regionDescription", "Description", "textarea", 290);

wizard.addElement("regionScope", regionScope);
wizard.addElementFormat("regionScope", "Scope", "combo", 290);

wizard.addElementFormatKeyPair("regionScope", "emptyText", "Scope");
wizard.addElementFormatKeyPair("regionScope", "title", "Scope");

wizard.addElementSelectionValue("regionScope", "Global", "Global");
wizard.addElementSelectionValue("regionScope", "Template", "Template");
wizard.addElementSelectionValue("regionScope", "Page", "Page");

wizard.addElement("regionX", "");
wizard.addElementFormat("regionX", "X", "textfield", 10);

wizard.addElement("regionY", "");
wizard.addElementFormat("regionY", "Y", "textfield", 10);

wizard.addElement("regionWidth", "200");
wizard.addElementFormat("regionWidth", "Width", "textfield", 10);

wizard.addElement("regionHeight", "200");
wizard.addElementFormat("regionHeight", "Height", "textfield", 10);