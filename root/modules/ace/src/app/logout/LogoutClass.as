// Logout Action Script package app.logout{	import mx.controls.Alert;	import util.authentication.AuthenticationService;
	import mx.containers.Canvas;	public class LogoutClass extends Canvas	{
		/**
		 * Event handler for logout button link.  
		 * 
		 * Makes call to authenitcation service and redirects user to login dialog.
		 */
		public function onLogoutButtonLinkClick():void
		{
			// Make call to authentication service to logout user			AuthenticationService.instance.logout();
		}	}}
