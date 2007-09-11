package util.authentication
{
	import flash.events.Event;

	/**
	 * Login complete event object
	 */
	public class LoginCompleteEvent extends Event
	{
		public static const LOGIN_COMPLETE = "loginComplete";
		
		private var _ticket:String;
		
		private var _userName:String;
		
		public function LoginCompleteEvent(type:String, ticket:String, userName:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			
			this._ticket = ticket;
			this._userName = userName;		
		}
		
		public function get ticket():String
		{
			return this._ticket;
		}
		
		public function get userName():String
		{
			return this._userName;
		}
	}
}