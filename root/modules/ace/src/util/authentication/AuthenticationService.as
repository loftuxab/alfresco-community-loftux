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

	/**
	 * Web script service object.  Encapsulates access to an Alfresco web script
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
		 * Default constructor
		 */
		public function AuthenticationService()
		{
		}
				
		public function get ticket():String
		{
			return this._ticket;
		}
		
		// TEMP
		//public function set ticket(ticket:String):void
		//{
	//		this._ticket = ticket;
//		}
		
		
		public function get userName():String
		{
			return this._userName;
		}
		
		public function login(userName:String, password:String):void
		{
			try
			{
				var url:String = "http://localhost:8080/alfresco/service/api/login";
				var webScript:WebScriptService = new WebScriptService(url, WebScriptService.GET, onLoginSuccess, onFailure);
				
				var params:Object = new Object();
				params.u = userName;
				params.pw = password;
			
				webScript.execute(params);
			}
			catch (error:Error)
			{
				Alert.show(error.message);
			}
		}
		
		public function logout():void
		{
			try
			{
				var url:String = "http://localhost:8080/alfresco/service/api/login/ticket/" + this._ticket;				
				var webScript:WebScriptService = new WebScriptService(url, WebScriptService.DELETE, onLogoutSuccess, onFailure);
				webScript.execute();										
			}
			catch (error:Error)
			{
				Alert.show(error.message);
			}
		}
		
		public function onLogoutSuccess(event:SuccessEvent):void
		{
			// Clear the current ticket information
			this._ticket = null;
			this._userName = null;
			
			// Raise on logout success event
			dispatchEvent(new LogoutCompleteEvent(LogoutCompleteEvent.LOGOUT_COMPLETE));			
		}
		
		public function onLoginSuccess(event:SuccessEvent):void
		{
			// Store the ticket in the authentication service
			this._ticket = event.result.ticket;
			// TODO stash the user in here too ...
			
			// Raise on login success event
			dispatchEvent(new LoginCompleteEvent(LoginCompleteEvent.LOGIN_COMPLETE, this._ticket, ""));
		}
		
		public function onFailure(event:FailureEvent):void
		{
			Alert.show("Error occured: " + event.fault.faultString);	
		}
	}
}