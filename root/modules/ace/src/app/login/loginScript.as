// Login Action Script 

import mx.controls.Alert;
import util.authentication.AuthenticationService;

/**
 * Event handler for login button link.  
 * 
 * Makes call to authenitcation service and redirects user to search dialog.
 */
private function onLoginButtonLinkClick():void
{
	// Call authentication service to log user in
	AuthenticationService.instance.login(username.text, password.text);
}
