package util.webscript
{
	import mx.rpc.AsyncToken;
	import mx.rpc.Fault;
	import mx.messaging.messages.IMessage;
	import mx.rpc.events.FaultEvent;

    /**
    * Web script failure event clasee
    * 
    * @author Roy Wetherall
    */
	public class FailureEvent extends FaultEvent
	{
		/** Event name */
		public static const FAILURE:String = "failure";
		
		/**
		 * Constructor
		 */
		public function FailureEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=true, fault:Fault=null, token:AsyncToken=null, message:IMessage=null)
		{
			super(type, bubbles, cancelable, fault, token, message);
		}
		
	}
}