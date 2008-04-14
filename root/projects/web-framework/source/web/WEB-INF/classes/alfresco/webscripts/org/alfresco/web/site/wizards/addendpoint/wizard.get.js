<import resource="/org/alfresco/web/site/include/ads-support.js">




// set up form elements
wizard.addElement("endpointId", "");
wizard.addElement("protocol", "http");
wizard.addElement("host", "localhost");
wizard.addElement("port", "80");
wizard.addElement("uri", "/service");

wizard.addElementFormat("endpointId", "Identifier", "textfield", 220);
wizard.addElementFormat("protocol", "Protocol", "combo", 120);
wizard.addElementFormat("host", "Host", "textfield", 220);
wizard.addElementFormat("port", "Port", "textfield", 120);
wizard.addElementFormat("uri", "URI", "textfield", 220);



//
// Protocol dropdown
//
wizard.addElementSelectionValue("protocol", "http", "HTTP");
wizard.addElementSelectionValue("protocol", "https", "HTTPS");




