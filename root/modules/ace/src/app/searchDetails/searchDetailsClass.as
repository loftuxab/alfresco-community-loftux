// ActionScript file
package app.searchDetails
{
	import mx.containers.Canvas;
	import mx.controls.Alert;
	import mx.controls.LinkButton;
	import mx.controls.Label;
	import mx.controls.SWFLoader;
	
	public class searchDetailsClass extends Canvas
	{
		public var summaryBtn:LinkButton;
		private var url:String;
		private var title:String;
		
		
		public var myframe:SWFLoader;
		public var swfPanel:Canvas;
		public var resultsDispPanel:Canvas;
		
		/**
		 * Default Constructor
		 */		
		public function searchDetailsClass()
		{
			super();
			
		}
		
		/**
		 * 
		 * @set summary & link property for the repeater
		 * 
		 */		
		public function set summary(summary:String):void
		{
			if(summary!= null)
				summaryBtn.label = summary;
			else 
				summaryBtn.label = this.title;
				
			Alert.show(this.title);
		}
	
		public function set link(link:String):void
		{
			this.url = link;
			
		}
		
		public function set doctitle(title:String):void
		{
			this.title = title;
		}
	
	}
}