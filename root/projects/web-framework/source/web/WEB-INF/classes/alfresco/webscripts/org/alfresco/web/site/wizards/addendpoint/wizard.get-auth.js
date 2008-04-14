<import resource="/org/alfresco/web/site/include/ads-support.js">


// set up form elements
wizard.addElement("credentials", "");
wizard.addElement("authentication", "");
wizard.addElement("username", "");
wizard.addElement("password", "");

wizard.addElementFormat("credentials", "Credentials", "combo", 220);
wizard.addElementFormat("authentication", "Authentication", "combo", 220);
wizard.addElementFormat("username", "Username", "textfield", 120);
wizard.addElementFormat("password", "Password", "textfield", 120);
wizard.addElementFormatKeyPair("password", "inputType", "password");

//
// Credentials dropdown
//
wizard.addElementSelectionValue("credentials", "none", "None");
wizard.addElementSelectionValue("credentials", "currentuser", "Credentials from current user");
wizard.addElementSelectionValue("credentials", "specificuser", "Credentials from a specified user");

//
// Authentication dropdown
//
wizard.addElementSelectionValue("authentication", "none", "None");
wizard.addElementSelectionValue("authentication", "basic", "Basic Authentication");
wizard.addElementSelectionValue("authentication", "alf_ticket", "Alfresco Ticket Authentication");


