// Login Action Script 

import mx.controls.Alert;
import util.authentication.AuthenticationService;
import util.error.ErrorService;
import util.error.ErrorRaisedEvent;
import flash.events.Event;
import flash.utils.describeType;
import util.authentication.InvalidCredentialsError;
import mx.core.ClassFactory;

/**
 * Event handler for login button link.  
 * 
 * Makes call to authenitcation service and redirects user to search dialog.
 */
private function onLoginButtonLinkClick():void
{
	// Register interest with the error service
	ErrorService.instance.addEventListener(ErrorRaisedEvent.ERROR_RAISED, onErrorRaised);
	
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
