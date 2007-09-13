package util.authentication
{
	import flash.events.Event;

	/**
	 * Logout complete event class
	 *
	 * @author Roy Wetherall
	 */
	public class LogoutCompleteEvent extends Event
	{
		/** Event name */
		public static const LOGOUT_COMPLETE:String = "logoutComplete";
		
		/**
		 * Constructor
		 */
		public function LogoutCompleteEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
		
	}
}