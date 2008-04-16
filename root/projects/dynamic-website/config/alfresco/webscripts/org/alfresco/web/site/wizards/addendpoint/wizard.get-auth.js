<import resource="/org/alfresco/web/site/include/ads-support.js">


// set up form elements
wizard.addElement("authId", "");
wizard.addElement("identity", "");
wizard.addElement("username", "");
wizard.addElement("password", "");

wizard.addElementFormat("authId", "Authenticator", "combo", 220);
wizard.addElementFormat("identity", "Identity", "combo", 220);
wizard.addElementFormat("username", "Username", "textfield", 120);
wizard.addElementFormat("password", "Password", "textfield", 120);
wizard.addElementFormatKeyPair("password", "inputType", "password");

//
// Authenticator dropdown
//
wizard.addElementSelectionValue("authId", "none", "None");
wizard.addElementSelectionValue("authId", "basic", "Basic Authentication");
wizard.addElementSelectionValue("authId", "alf_ticket", "Alfresco Ticket Authentication");
wizard.updateElement("authId", authId);

//
// Identity dropdown
//
wizard.addElementSelectionValue("identity", "specific", "Specific User");
wizard.addElementSelectionValue("identity", "current", "Current User");
wizard.updateElement("identity", identity);
