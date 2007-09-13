package util.authentication
{
	import app.logout.logout;
	
	import flash.events.Event;
	import flash.profiler.showRedrawRegions;
	
	import mx.controls.Alert;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.http.HTTPService;
	import mx.rpc.soap.WebService;
	
	import util.webscript.WebScriptService;
	import util.webscript.SuccessEvent;
	import util.webscript.FailureEvent;
	import flash.events.EventDispatcher;
	import util.error.ErrorService;

	/**
	 * Authentication service class.
	 * 
	 * @author Roy Wetherall
	 */
	public class AuthenticationService extends EventDispatcher
	{
		/** Static instance of the authentication service */
		private static var _instance:AuthenticationService;
		
		/** The current authentication ticket */
		private var _ticket:String = null;		
		
		/** The currently authenticated user name */
		private var _userName:String = null;
		
		/**
		 * Singleton method to get the instance of the Authentication Service
		 */
		public static function get instance():AuthenticationService
		{
			if (AuthenticationService._instance == null)
			{
				AuthenticationService._instance = new AuthenticationService();
			}
			return AuthenticationService._instance;
		}
			
		/**
		 * Getter for the ticket property
		 */		
		public function get ticket():String
		{
			return this._ticket;
		}		
		
		/**
		 * Getter for the userName property
		 */
		public function get userName():String
		{
			return this._userName;
		}
		
		/**
		 * Log in a user to the Alfresco repository and store the ticket.
		 */
		public function login(userName:String, password:String):void
		{
			// Create the web script obejct
			var url:String = "http://localhost:8080/alfresco/service/api/login";
			var webScript:WebScriptService = new WebScriptService(url, WebScriptService.GET, onLoginSuccess, onLoginFailure, false);
			
			// Build the parameter object
			var params:Object = new Object();
			params.u = userName;
			params.pw = password;
		
			// Execute the web script
			webScript.execute(params);
		}
		
		/**
		 * Log the current user out of the Alfresco repository
		 */
		public function logout():void
		{
			// Execute the logout web script
			var url:String = "http://localhost:8080/alfresco/service/api/login/ticket/" + this._ticket;				
			var webScript:WebScriptService = new WebScriptService(url, WebScriptService.DELETE, onLogoutSuccess);
			webScript.execute();										
		}
		
		/**
		 * On logout success event handler
		 */
		public function onLogoutSuccess(event:SuccessEvent):void
		{
			// Clear the current ticket information
			this._ticket = null;
			this._userName = null;
			
			// Raise on logout success event
			dispatchEvent(new LogoutCompleteEvent(LogoutCompleteEvent.LOGOUT_COMPLETE));			
		}
		
		/**
		 * On login success event handler
		 */
		public function onLoginSuccess(event:SuccessEvent):void
		{
			// Store the ticket in the authentication service
			this._ticket = event.result.ticket;
			// TODO stash the user in here too ...
			
			// Raise on login success event
			dispatchEvent(new LoginCompleteEvent(LoginCompleteEvent.LOGIN_COMPLETE, this._ticket, ""));
		}
		
		/**
		 * On login failure event handler
		 */
		public function onLoginFailure(event:FailureEvent):void
		{
			// Get the error details from the failure event
			var code:String = event.fault.faultCode;
			var message:String = event.fault.faultString;
			var details:String = event.fault.faultDetail;
			
			if (code == "403")
			{
				// Raise invalid credentials error	
				ErrorService.instance.raiseError(InvalidCredentialsError.INVALID_CREDENTIALS, new InvalidCredentialsError());
			}
			else
			{
				// TODO extend the parameters provided here ...
				
				// Raise general authentication error
				ErrorService.instance.raiseError(ErrorService.APPLICATION_ERROR, new AuthenticationError(message));
			}
		}
	}
}