// ActionScript file
import util.authentication.AuthenticationService;
import util.authentication.LoginCompleteEvent;
import util.authentication.LogoutCompleteEvent;
import app.logout.logout;

/**
 * Application start event handler
 */
private function onCreateApplication():void
{
	myframe.visible=false;
	cb1.selected=true;
	
	// Register interest in authentication service events
	AuthenticationService.instance.addEventListener(LoginCompleteEvent.LOGIN_COMPLETE, doLoginComplete);
	AuthenticationService.instance.addEventListener(LogoutCompleteEvent.LOGOUT_COMPLETE, doLogoutComplete);
}

public function doLoginComplete(event:LoginCompleteEvent):void
{
	mainCanvas.visible = true;
    loginPanel.visible=false;    ;
}

private function doLogoutComplete(event:LogoutCompleteEvent):void
{
	mainCanvas.visible = false;
    loginPanel.visible=true;

    myframe.source='';
    resultsDispPanel.width=maxWidth;    
    searchResults.dataProvider="";  
    cb1.selected=true; 
}


