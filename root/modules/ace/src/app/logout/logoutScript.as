// Logout Action Script 
import mx.controls.Alert;import util.authentication.AuthenticationService;

/**
 * Event handler for logout button link.  
 * 
 * Makes call to authenitcation service and redirects user to login dialog.
 */
private function onLogoutButtonLinkClick():void
{
	// Make call to authentication service to logout user	AuthenticationService.instance.logout();
}
