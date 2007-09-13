package util.webscript
{
	import mx.rpc.AsyncToken;
	import mx.rpc.events.ResultEvent;
	import mx.messaging.messages.IMessage;

    /**
     * Web script success event class
     * 
     * @author Roy Wetherall
     */
	public class SuccessEvent extends ResultEvent
	{
		/** Event name */
		public static const SUCCESS:String = "success";	
		
		/**
		 * Constructor
		 */
		public function SuccessEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=true, result:Object=null, token:AsyncToken=null, message:IMessage=null)
		{
			super(type, bubbles, cancelable, result, token, message);
		}
		
	}
}