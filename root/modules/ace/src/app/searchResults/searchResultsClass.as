// ActionScript file
package app.searchResults
{
	import mx.containers.Panel;
	import mx.controls.SWFLoader;
	import mx.controls.Alert;
	import mx.core.Repeater;
	import util.searchservice.*;
	import util.error.*;
	import component.swipe.*;
	import mx.containers.Canvas;
	import util.searchservice.*;
	import mx.collections.ArrayCollection;
	
	public class searchResultsClass extends Canvas
	{
	    [Bindable]
		public var results:Repeater;
		[Bindable]
		public var sortbyValues:Array = [ {label:"Relevance", data:1}, {label:"Size", data:2}, {label:"Alphabetical", data:3} ];
		public var swipe:Swipe;	
		public var myframe:SWFLoader;
		public var swfPanel:Canvas;
		public var resultsDispPanel:Canvas;
		public var url:String;
		
		public function resultClick(url:String):void
        {
          	resultsDispPanel.width=400;
          	swfPanel.visible=true;
          	swfPanel.x=400;
          	swfPanel.y=35;
          	myframe.visible=true;
           	myframe.source=url;        
            url = url;   	
        }	
        
        /** default Constructor */
       public function searchResultsClass()
       {
       	 	super();
       	 	
       	 	// Register interest in search service events
			SearchService.instance.addEventListener(SearchCompleteEvent.SEARCH_COMPLETE, doSearchComplete); 	       		
       }
       
       public function geturl():String
       {
       		return this.url;
       }		
     
       /**
		 * Event handler called when search is successfully completed
		 * 
		 * @param	event	search complete event
		 */
		private function doSearchComplete(event:SearchCompleteEvent):void
		{
			try
			{	
				this.results.dataProvider = event.result.feed.entry;
			}
			catch (error:Error)
			{
				ErrorService.instance.raiseError(ErrorService.APPLICATION_ERROR, error.message);	
			}
		}
        
        
	}

}