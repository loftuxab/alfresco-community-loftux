// Login Action Script 
package app.login
{
	import mx.controls.Alert;
	import util.authentication.AuthenticationService;
	import util.error.ErrorService;
	import util.error.ErrorRaisedEvent;
	import flash.events.Event;
	import flash.utils.describeType;
	import util.authentication.InvalidCredentialsError;
	import mx.core.ClassFactory;
	import mx.containers.Canvas;
	import mx.controls.Text;
	import mx.controls.TextInput;
	import mx.states.State;
	import mx.rpc.mxml.IMXMLSupport;

    public class LoginClass extends Canvas
	{
		/** UI controls */
		public var username:TextInput;
		public var password:TextInput;
		public var errorMessage:Text;
		
		/** 
		 * Constructor
		 */
		public function LoginClass()
		{
			// Call the super class
			super();
			
			// Register interest with the error service
			ErrorService.instance.addEventListener(ErrorRaisedEvent.ERROR_RAISED, onErrorRaised);	
		}
	
		/**
		 * Event handler for login button link.  
		 * 
		 * Makes call to authenitcation service and redirects user to search dialog.
		 */
		public function onLoginButtonLinkClick():void
		{	
			// Call authentication service to log user in
			AuthenticationService.instance.login(username.text, password.text);
		}
		
		/**
		 * On error raised event handler
		 */
		public function onErrorRaised(event:ErrorRaisedEvent):void
		{
			if (event.errorType == InvalidCredentialsError.INVALID_CREDENTIALS)
			{
				// Switch to the alternative state and set the error message
				currentState = "InvalidCredentials";
				errorMessage.text = event.error.message;
				
			}
		}
	}	
}