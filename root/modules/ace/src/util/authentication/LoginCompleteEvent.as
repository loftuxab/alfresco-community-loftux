package util.authentication
{
	import flash.events.Event;

	/**
	 * Login complete event object
	 * 
	 * @author Roy Wetherall
	 */
	public class LoginCompleteEvent extends Event
	{
		/** Event name */
		public static const LOGIN_COMPLETE:String = "loginComplete";
		
		/** The ticket created during login */
		private var _ticket:String;
		
		/** The user name logged in */
		private var _userName:String;
		
		/**
		 * Constructor
		 */
		public function LoginCompleteEvent(type:String, ticket:String, userName:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			
			this._ticket = ticket;
			this._userName = userName;		
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
	}
}