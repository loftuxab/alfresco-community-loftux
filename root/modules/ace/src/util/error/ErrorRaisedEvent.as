package util.error
{
	import flash.events.Event;

	/**
	 * Error raised event class
	 *
	 * @author Roy Wetherall
	 */
	public class ErrorRaisedEvent extends Event
	{
		/** Event name */
		public static const ERROR_RAISED:String = "errorRaised";
		
		/** The error being raised */
		private var _error:Error;
		
		/** The error type used to filter when handling the error */
		private var _errorType:String;
		
		/**
		 * Constructor
		 */
		public function ErrorRaisedEvent(type:String, errorType:String, error:Error, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this._error = error;
			this._errorType = errorType;
		}
		
		/**
		 * Getter for the error property
		 */
		public function get error():Error
		{
			return this._error;
		}
		
		/**
		 * Getter for the errorType property
		 */
		public function get errorType():String
		{
			return this._errorType;	
		}
		
	}
}