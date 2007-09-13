package util.error
{
	import flash.events.EventDispatcher;
	
	/**
	 * Error service.
	 * 
	 * @author Roy Wetherall
	 */
	public class ErrorService extends EventDispatcher
	{
		/** Error type constants */
		public static const APPLICATION_ERROR:String = "ApplicationError";
		
		/** Singleton instance */
		private static var _instance:ErrorService;
		
		/**		
		 * Getter for static instance property
		 */
		public static function get instance():ErrorService
		{
			if (ErrorService._instance == null)
			{
				ErrorService._instance = new ErrorService();
			}			
			return ErrorService._instance;
		}
		
		/**
		 * Raise an error with the error service
		 */
		public function raiseError(errorType:String, error:Error):void
		{
			// Raise the errorRaisedEvent
			this.dispatchEvent(new ErrorRaisedEvent(ErrorRaisedEvent.ERROR_RAISED, errorType, error));
		}
	}
}