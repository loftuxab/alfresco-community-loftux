<import resource="/org/alfresco/web/site/include/ads-support.js">

// incoming endpoint id
if(endpointObjectId != null && endpointObjectId != "")
{
	// add the endpoint object id
	wizard.addHiddenElement("endpointObjectId", endpointObjectId);

	// data from the object
	var endpoint = site.getObject(endpointObjectId);	
	var credentials = wizard.getSafeSetting(endpoint, "credentials");
	var authentication = wizard.getSafeSetting(endpoint, "authentication");
	var username = wizard.getSafeSetting(endpoint, "username");
	var password = wizard.getSafeSetting(endpoint, "password");

	// set up form elements
	wizard.addElement("credentials", credentials);
	wizard.addElement("authentication", authentication);
	wizard.addElement("username", username);
	wizard.addElement("password", password);

	wizard.addElementFormat("credentials", "Run As", "combo", 220);
	wizard.addElementFormat("authentication", "Authentication", "combo", 220);
	wizard.addElementFormat("username", "Username", "textfield", 120);
	wizard.addElementFormat("password", "Password", "textfield", 120);
	wizard.addElementFormatKeyPair("password", "inputType", "password");

	//
	// Credentials dropdown
	//
	wizard.addElementSelectionValue("credentials", "none", "None");
	wizard.addElementSelectionValue("credentials", "currentuser", "Current user");
	wizard.addElementSelectionValue("credentials", "specificuser", "A specific user");
	wizard.updateElement("credentials", credentials);

	//
	// Authentication dropdown
	//
	wizard.addElementSelectionValue("authentication", "none", "None");
	wizard.addElementSelectionValue("authentication", "basic", "Basic Authentication");
	wizard.addElementSelectionValue("authentication", "alf_ticket", "Alfresco Ticket Authentication");
	wizard.updateElement("authentication", authentication);
}

