package util.searchservice{
	
	import mx.rpc.http.HTTPService;
	import util.authentication.*;
	import mx.controls.Alert;
	import util.webscript.WebScriptService;
	import util.webscript.SuccessEvent;
	import util.webscript.FailureEvent;
	import flash.events.EventDispatcher;
	import mx.controls.Alert;
	import mx.core.Repeater;
	import mx.rpc.events.ResultEvent;
	import util.error.ErrorService;
	import util.webscript.ConfigService;
	import mx.core.Application;
	
	public class SearchService extends EventDispatcher
	{
		private var _searchtext:String;
			
		/** Static instance of the authentication service */
		private static var _instance:SearchService;
		
		
		/**
		 * Singleton method to get the instance of the Search Service
		 */
		public static function get instance():SearchService
		{
			if (SearchService._instance == null)
			{
				SearchService._instance = new SearchService();
			}
			return SearchService._instance;
		}
		
		
		/**
		 * Default constructor
		 */
		public function SearchService()
		{
		}
		
		public function get searchtext():String
		{
			return this._searchtext;
		}
		
		public function search(searchtext:String):void
		{
			try
			{	
				
				var url:String = ConfigService.instance.url +  "/alfresco/service/sample/kb/search.atom";
				var webScript:WebScriptService = new WebScriptService(url, WebScriptService.GET, onSearchSuccess, onFailure);
				
				var params:Object = new Object();
				params.q = searchtext;
				
				webScript.execute(params);
			}
			catch (error:Error)
			{
				Alert.show(error.message);
			}
		}
		
		public function onSearchSuccess(event:SuccessEvent):void
		{
			dispatchEvent(new SearchCompleteEvent(SearchCompleteEvent.SEARCH_COMPLETE, event.result));
		}
		
		public function onFailure(event:FailureEvent):void
		{
			Alert.show("Error occured: " + event.fault.faultString);	
		}
	}
	
}