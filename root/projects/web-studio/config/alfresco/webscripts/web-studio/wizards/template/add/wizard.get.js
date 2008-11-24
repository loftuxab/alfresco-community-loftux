<import resource="/include/support.js">

wizard.addElement("templateName", templateName);
wizard.addElementFormat("templateName", "Name", "textfield", 290);

wizard.addElement("templateDescription", templateDescription);
wizard.addElementFormat("templateDescription", "Description", "textarea", 290);

wizard.addElement("templateHeight", templateHeight);
wizard.addElementFormat("templateHeight", "Height", "textfield", 5);

wizard.addElement("templateWidth", templateWidth);
wizard.addElementFormat("templateWidth", "Width", "textfield", 5);

wizard.addHiddenElement("templateTypeId", "dynamic");

wizard.addElement("templateLayoutType", templateLayoutType);
wizard.addElementFormat("templateLayoutType", "Layout", "combo", 290);

wizard.addElementFormatKeyPair("templateLayoutType", "emptyText", "Layout Types");
wizard.addElementFormatKeyPair("templateLayoutType", "title", "Layout Types");

wizard.addElementSelectionValue("templateLayoutType", "Absolute Positioning", "Absolute Positioning");
wizard.addElementSelectionValue("templateLayoutType", "Table Layout", "Table Layout");