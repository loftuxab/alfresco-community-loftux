package util.searchservice
{
	import flash.events.Event;
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
		
		private var _totalresults:String;
		
		/**
		 * Constructor
		 */
		public function SearchCompleteEvent(type:String, result:Object, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			
			this._result= result;
			this._totalresults = result.feed.totalResults;
		}
		
		/**
		 * Getter for the result object instance
		 */
		public function get result():Object
		{
			return this._result;
		}
		
		/**
		 * 
		 * @Getter method for totalresults from search  
		 * 
		 */		
		public function get totalresults():String
		{
			return this._totalresults;
		}
		
		
	}
}