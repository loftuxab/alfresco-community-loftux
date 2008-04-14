<import resource="/org/alfresco/web/site/include/ads-support.js">

// set up form elements
wizard.addElement("endpointId", "");
wizard.addElement("connectorId", "");
wizard.addElement("endpointUrl", "");
wizard.addElement("defaultUri", "");

wizard.addElementFormat("endpointId", "Identifier", "textfield", 220);
wizard.addElementFormat("connectorId", "Connector", "combo", 220);
wizard.addElementFormat("endpointUrl", "URL", "textfield", 220);
wizard.addElementFormat("defaultUri", "Service Uri", "textfield", 220);

//
// Connector dropdown
//
wizard.addElementSelectionValue("connectorId", "http", "HTTP Connector");
wizard.addElementSelectionValue("connectorId", "web", "Web Connector");
wizard.addElementSelectionValue("connectorId", "alfresco", "Alfresco Connector");
wizard.updateElement("connectorId", connectorId);
