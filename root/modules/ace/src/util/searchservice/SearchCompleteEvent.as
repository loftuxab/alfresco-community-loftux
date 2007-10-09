package util.searchservice
{
	import flash.events.Event;
	import mx.controls.Alert;
	import mx.rpc.events.ResultEvent;

	/**
	 * Search complete event object
	 */
	public class SearchCompleteEvent extends Event
	{
		/** Event name */
		public static const SEARCH_COMPLETE:String = "searchComplete";
		
		/** Result object instance */
		private var _result:Object;
		
		/**
		 * Constructor
		 */
		public function SearchCompleteEvent(type:String, result:Object, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			
			this._result= result;
			
		}
		
		/**
		 * Getter for the result object instance
		 */
		public function get result():Object
		{
			return this._result;
		}
		
	}
}