package
{
	import flash.external.ExternalInterface;
	import flash.system.System;
	
	import mx.core.Application;
	import mx.events.FlexEvent;
	
	import org.alfresco.previewer.DocumentZoomDisplayEvent;
	import org.alfresco.previewer.Previewer;
	import org.hasseg.externalMouseWheel.ExternalMouseWheelSupport;

	/**
	 * Wraps the Previewer component in a html/web environment, takes the supplied variables 
	 * and loads the content defined by the url and calls javascript callbacks if something goes wrong.
	 */ 
	public class WebPreviewerClass extends Application
	{
		/**
		 * UI CONTROLS IMPLEMENTED BY MXML 
		 * 
		 * Should really be protected or private but can't be since code behind is used.
		 */
				
		public var previewer:Previewer;
		
		/**
		 * A string representing the javascript callback method that should get called
		 * if an event happens that shall be communicated to environments outside the flash player
		 * such as an document zoom display load error.
		 */
		private var jsCallback:String;

		/**
		 * Constructor
		 */ 		
		public function WebPreviewerClass()
		{
			super();
			this.addEventListener(FlexEvent.APPLICATION_COMPLETE, onApplicationComplete);
		}
		
		/**
		 * Called by the FLEX framework when the whole application is complete and created.
		 * Will set the variables supplied throught the embed tags and set them on the components.
		 */
		public function onApplicationComplete(event:FlexEvent):void
		{
			// Add mouse wheel scroll support for browsers on mac.
			ExternalMouseWheelSupport.getInstance(stage);
			
			// If something goes wrong we want to get a chance of notifying the html/javascript environment.
			previewer.documentDisplay.addEventListener(DocumentZoomDisplayEvent.DOCUMENT_LOAD_ERROR, onDocumentDisplayError);
			previewer.documentDisplay.addEventListener(DocumentZoomDisplayEvent.DOCUMENT_CONTENT_TYPE_ERROR, onDocumentDisplayError);
			
			// Get variables from the embed/object tag
			var url:String = Application.application.parameters.url;							
			var paging:String = Application.application.parameters.paging;
			var fileName:String = Application.application.parameters.fileName;
			jsCallback = Application.application.parameters.jsCallback;
			
			// i18n labels
			var i18n:Object = new Object();
			i18n.actualSize = Application.application.parameters.i18n_actualSize;
			i18n.fitPage = Application.application.parameters.i18n_fitPage;
			i18n.fitWidth = Application.application.parameters.i18n_fitWidth;
			i18n.fitHeight = Application.application.parameters.i18n_fitHeight;
			i18n.fullscreen = Application.application.parameters.i18n_fullscreen;
			i18n.page = Application.application.parameters.i18n_page;
			i18n.pageOf = Application.application.parameters.i18n_pageOf;
						
			// Set variables on the preview component							
			previewer.paging = (paging.toLowerCase() == "true");
			previewer.fileName = fileName;
			previewer.i18nLabels = i18n;
			
			trace(System.totalMemory);
			// Start the loading the content in to the previewer				
			previewer.url = url; 			
		}
		
		/**
		 * Called if something goes wrong during the loading of the content specified by url.
		 * 
		 * @param event An event describing the error.
		 */
		private function onDocumentDisplayError(event:DocumentZoomDisplayEvent):void
		{
			if (ExternalInterface.available && jsCallback != null)
			{		
				var code:String = "error";
				code = event.type == DocumentZoomDisplayEvent.DOCUMENT_LOAD_ERROR ? "error.io" : code;
				code = event.type == DocumentZoomDisplayEvent.DOCUMENT_CONTENT_TYPE_ERROR ? "error.content" : code;
											
				ExternalInterface.call(jsCallback, {code: code});
			}				
		}
		
	}
}