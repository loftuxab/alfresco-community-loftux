package util.webscript
{
	import mx.rpc.AsyncToken;
	import mx.rpc.Fault;
	import mx.messaging.messages.IMessage;
	import mx.rpc.events.FaultEvent;

	public class FailureEvent extends FaultEvent
	{
		public static const FAILURE:String = "failure";
		
		public function FailureEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=true, fault:Fault=null, token:AsyncToken=null, message:IMessage=null)
		{
			super(type, bubbles, cancelable, fault, token, message);
		}
		
	}
}