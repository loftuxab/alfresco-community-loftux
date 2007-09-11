package util.webscript
{
	import mx.rpc.http.HTTPService;
	import flash.events.Event;
	import mx.rpc.AsyncToken;
	import mx.controls.Alert;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.events.FaultEvent;
	import util.authentication.AuthenticationService;
	
	public class WebScriptService extends HTTPService
	{
		public static const GET:String = "GET";
		public static const POST:String = "POST";
		public static const PUT:String = "PUT";
		public static const DELETE:String = "DELETE";
		
		private var _url:String;
		private var _method:String;
		
		public function WebScriptService(url:String, method:String, onSuccess:Function=null, onFailure:Function=null)
		{
			// Inherited constructor
			super();
			
			// Store the url and method in case we need them later
			this._url = url;
			this._method = method;
			
			// Set the method
			if (method == GET)
			{
				this.method = GET;
			}
			else
			{
				this.method = POST;
			}
			
			// Set the url
			this.url = url;		
			
			// Ensure that a success code is always returned			
			this.headers["alf-force-success-status"] = "TRUE";
            this.useProxy=false;	
            
            // Register the event listeners            
			addEventListener(ResultEvent.RESULT, onResultEvent);
			addEventListener(FaultEvent.FAULT, onFaultEvent);
			
			// Register the passed event handlers
			if (onSuccess != null)
			{
				addEventListener(SuccessEvent.SUCCESS, onSuccess);
			}
			if (onFailure != null)
			{
				addEventListener(FailureEvent.FAILURE, onFailure);
			}
			//else
			//{
				// TODO register default event handler here ...
			//}			
		}
		
		public function execute(parameters:Object=null):AsyncToken
		{
			if (parameters == null)
			{
				parameters = new Object();
			}
			
			parameters.alf_ticket = AuthenticationService.instance.ticket;
			
			// Tunnel delete and put methods
			if (this._method == PUT || this._method == DELETE)
			{
				parameters.alf_method = this._method;
			}			
			
			// Send the request to the HTTPService
			return send(parameters);
		}
		
		public function onResultEvent(event:ResultEvent):void
		{
			// TODO need to check for error codes in the return
//			event.result or event.status
			
			
			// Re-raise the onSuccess event
			var newEvent:SuccessEvent = new SuccessEvent(SuccessEvent.SUCCESS, event.bubbles, event.cancelable, event.result, event.token, event.message);			
			dispatchEvent(newEvent);
				
		}
		
		public function onFaultEvent(event:FaultEvent):void
		{
			// TODO what do we do if no one is listening to this?
			
			// Raise the onFailure event
			var newEvent:FailureEvent = new FailureEvent(FailureEvent.FAILURE, event.bubbles, event.cancelable, event.fault, event.token, event.message);
			dispatchEvent(newEvent);
		}
	}
}