package util.webscript
{
	/**
	 * Web script error class
	 * 
	 * @author Roy Wetherall
	 */	 
	public class WebScriptError extends Error
	{
		/**
		 * Constructor
		 */
		public function WebScriptError(message:String="", id:int=0)
		{
			super(message, id);
		}
		
	}
}