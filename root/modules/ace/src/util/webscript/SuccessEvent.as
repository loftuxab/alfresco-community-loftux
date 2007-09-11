package util.webscript
{
	import mx.rpc.AsyncToken;
	import mx.rpc.events.ResultEvent;
	import mx.messaging.messages.IMessage;

	public class SuccessEvent extends ResultEvent
	{
		public static const SUCCESS:String = "success";	
		
		public function SuccessEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=true, result:Object=null, token:AsyncToken=null, message:IMessage=null)
		{
			super(type, bubbles, cancelable, result, token, message);
		}
		
	}
}