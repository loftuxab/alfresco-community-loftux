package util.authentication
{
	import flash.events.Event;

	public class LogoutCompleteEvent extends Event
	{
		public static const LOGOUT_COMPLETE = "logoutComplete";
		
		public function LogoutCompleteEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
		
	}
}