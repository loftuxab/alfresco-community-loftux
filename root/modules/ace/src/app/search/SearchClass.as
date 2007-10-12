package app.search
{
	
	import mx.controls.Alert;
	import app.*;
	import mx.containers.Canvas;
	import org.alfresco.ace.service.articlesearchservice.ArticleSearchService;
	
	/**
	 * Search UI backing class
	 * 
	 * @author saravanan 
	 */	
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
			var pattern:RegExp = /"£"/;
			var numPattern:RegExp = /\d+/;
			if(searchText.length < 3) Alert.show("Input string needs minimum 3 characters");
			else if (searchText.length == 0) Alert.show("Null string not allowed");
			else if (searchText.search(pattern)!=-1) Alert.show("Special Characters not allowed");
			else if (searchText.search(numPattern)!=-1) Alert.show("Numbers not allowed");
			else ArticleSearchService.instance.search(searchText);	
		}
	}
}