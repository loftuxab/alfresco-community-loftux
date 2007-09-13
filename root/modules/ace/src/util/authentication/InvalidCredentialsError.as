package util.authentication
{
	/**
	 * Invalid credentials error class
	 * 
	 * @author Roy Wetherall
	 */
	public class InvalidCredentialsError extends Error
	{
		/** Error type */
		public static const INVALID_CREDENTIALS:String = "InvalidCredentials";
		
		/**
		 * Constructor
		 */	
		public function InvalidCredentialsError(message:String="Invalid connection details provided.", id:int=0)
		{
			super(message, id);
		}
		
	}
}