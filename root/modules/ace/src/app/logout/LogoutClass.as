// Logout Action Script package app.logout{	import mx.controls.Alert;	import mx.containers.Canvas;
	import mx.controls.Label;		import mx.events.FlexEvent;	import mx.effects.Fade;	import mx.controls.Alert;	import mx.events.CloseEvent;
	import org.alfresco.framework.service.authentication.AuthenticationService;		public class LogoutClass extends Canvas	{
				/**
		 * Event handler for logout button link.  
		 * 
		 * Makes call to authenitcation service and redirects user to login dialog.
		 */
		public function onLogoutButtonLinkClick():void
		{
			// Make call to authentication service to logout user			AuthenticationService.instance.logout();
		}				// TODO embed image for the logout Alert		[Embed(source='../../images/user_icon.png')]		private var confirmIcon:Class;				public function confirm():void 		{			// instantiate the Alert box			var a:Alert = Alert.show("Are you sure to Logout ?", "Confirmation", Alert.YES|Alert.NO, this, confirmHandler, confirmIcon, Alert.NO);						// modify the look of the Alert box			a.setStyle("backgroundColor", 0xffffff);			a.setStyle("backgroundAlpha", 0.50);			a.setStyle("borderColor", 0xffffff);			a.setStyle("borderAlpha", 0.75);			a.setStyle("color", 0x000000); // text color		}				private function confirmHandler(event:CloseEvent):void 		{			if (event.detail == Alert.YES) 			{				// if user selected "yes"				onLogoutButtonLinkClick();			} else if (event.detail == Alert.NO) 			{				//  if user selected "no" TODO							}		}	}}
