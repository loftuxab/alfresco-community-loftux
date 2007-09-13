// ActionScript file
import util.authentication.AuthenticationService;
import util.authentication.LoginCompleteEvent;
import util.authentication.LogoutCompleteEvent;
import app.logout.logout;
import util.error.ErrorService;
import util.error.ErrorRaisedEvent;
import mx.controls.Alert;

/**
 * Application start event handler
 */
private function onCreateApplication():void
{
	myframe.visible=false;
	cb1.selected=true;
		// Register interest in the error service events	ErrorService.instance.addEventListener(ErrorRaisedEvent.ERROR_RAISED, onErrorRaised);	
	// Register interest in authentication service events
	AuthenticationService.instance.addEventListener(LoginCompleteEvent.LOGIN_COMPLETE, doLoginComplete);
	AuthenticationService.instance.addEventListener(LogoutCompleteEvent.LOGOUT_COMPLETE, doLogoutComplete);
}
/** * Event handler called when login is successfully completed *  * @param	event	login complete event */
public function doLoginComplete(event:LoginCompleteEvent):void
{
	mainCanvas.visible = true;
    loginPanel.visible=false;
}
/** * Event handler called when logout is successfully completed *  * @param	event	logout complete event */
private function doLogoutComplete(event:LogoutCompleteEvent):void
{
	mainCanvas.visible = false;
    loginPanel.visible=true;

    myframe.source='';
    resultsDispPanel.width=maxWidth;    
    searchResults.dataProvider="";  
    cb1.selected=true; 
}/** * Event handler called when error is raised *  */private function onErrorRaised(event:ErrorRaisedEvent):void{	// TODO figure out how we react to ApplicationError's raised	if (event.errorType == ErrorService.APPLICATION_ERROR)	{		Alert.show("Application Error: " + event.error.message);	}
}


