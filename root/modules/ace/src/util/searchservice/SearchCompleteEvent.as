package util.searchservice
{
	import flash.events.Event;
	import mx.controls.Alert;
	import mx.rpc.events.ResultEvent;

	/**
	 * Login complete event object
	 */
	public class SearchCompleteEvent extends Event
	{
		public static const SEARCH_COMPLETE = "searchComplete";
		
		private var _result:Object;
		
		public function SearchCompleteEvent(type:String, result:Object, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			
			this._result= result;
			
		}
		
		public function get result():Object
		{
			return this._result;
		}
		
	}
}