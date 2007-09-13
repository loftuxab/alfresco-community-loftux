package util.authentication
{
	/**
	 * Authentication error class
	 * 
	 * @author Roy Wetherall
	 */
	public class AuthenticationError extends Error
	{
		/**
		 * Constructor
		 */
		public function AuthenticationError(message:String="", id:int=0)
		{
			super(message, id);
		}
		
	}
}