package app.search
{

	import mx.controls.Alert;
	import util.searchservice.SearchService;
	import app.*;
	import mx.containers.Canvas;

	public class SearchClass extends Canvas
	{
		
		public function SearchClass()
		{
			super();
		}
		
		/**
	 	* 
	 	* Makes call to search service and redirects user to search results display
	 	* 
	 	*/
		public function onSearchButtonLinkClick(searchText:String):void
		{
			SearchService.instance.search(searchText);	
		}
	}
}