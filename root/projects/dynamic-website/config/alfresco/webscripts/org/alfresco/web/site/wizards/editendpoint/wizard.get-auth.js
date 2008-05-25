<import resource="/org/alfresco/web/site/include/ads-support.js">

// incoming endpoint id
if(endpointObjectId != null && endpointObjectId != "")
{
	// add the endpoint object id
	wizard.addHiddenElement("endpointObjectId", endpointObjectId);

	// data from the object
	var endpoint = sitedata.getObject(endpointObjectId);	
	
	// properties
	var authId = wizard.getSafeProperty(endpoint, "auth-id");
	var identity = wizard.getSafeProperty(endpoint, "identity");
	var username = wizard.getSafeProperty(endpoint, "username");
	var password = wizard.getSafeProperty(endpoint, "password");
	
	// set up form elements
	wizard.addElement("authId", authId);
	wizard.addElement("identity", identity);
	wizard.addElement("username", username);
	wizard.addElement("password", password);

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
}

