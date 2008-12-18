/**
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * and Open Source Software ("FLOSS") applications as described in Alfresco's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing
 */

package org.alfresco.previewer
{
	import flash.display.AVM1Movie;
	import flash.display.Bitmap;
	import flash.display.Loader;
	import flash.display.MovieClip;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.KeyboardEvent;
	import flash.net.URLRequest;
	import flash.ui.Keyboard;
	import flash.utils.ByteArray;
	
	import mx.core.MovieClipAsset;
	import mx.events.ResizeEvent;
	
	import org.alfresco.core.ui.SpriteZoomDisplay;
	import org.alfresco.core.ui.SpriteZoomDisplayContext;
	
	/**
	 * A class that extends the SpriteZoomDisplay to get zoom, scroll and drag functionality.
	 * It also adds functionality that treats the zooomable/moveable sprite object as a document
	 * with pages so its possible move the sprite to display a certain page/part of the document/sprite
	 * and to know which page that currently is displayed.
	 */
	public class DocumentZoomDisplay extends SpriteZoomDisplay
	{
		
		/**
		 * The url to the document to display.
		 * Can point to a url with the following content:
		 * .png
		 * .gif
		 * .jpg
		 * .swf - Both Actionscript2 & 3 movies can be displayed 
		 *        but only Actionscript 3 movies can be navigated.
		 */
		private var _url:String;		

		/**
		 * If true loaded, the frames of Actionscript3 flash movies (.swf), will be treated 
		 * as pages in a document. 
		 * 
		 * Set to true if url points to a .swf that is a result from a conversion from
		 * a pdf or word document with several pages/frames.
		 * 
		 * Default is false, in other words if the content is and always have been 
		 * (not is a result form a conversion) a flash movie it will start playing.
		 */
		public var paging:Boolean = false; 
		
		/**
		 * The page that currently is displayed.
		 */
		private var _page:int = -1;		
		
		/**
		 * If true clicks on pages will dispatch events.  
		 */
		private var _interactive:Boolean = false;
		
		/**
		 * Flag to remember if we have added event listeners or not.
		 */
		private var addedEventListeners:Boolean = false;		
		
		/**
		 * URLRequest object that describes how the content can't be found.
		 */
		private var request:URLRequest;
		
		/** 
		 * The loader to load the conent into.
		 */
		private var loader:Loader;
		
		/**
		 * The Document to put inside the DocumentZoomDisplay so it can be zoomed and moved.
		 */		
		private var doc:Document;
		
		/**
		 * An array to store the Page(s) (containing a movie clip, image or swf movie) 
		 * before they are added as pages in the document.  
		 */
		private var pages:Array;
		
		/**
		 * Describes the contentType of the loaded content.
		 * Possible values are: ERROR, IMAGE, MOVIE_CLIP or AVM1_MOVIE 
		 */
		private var contentType:int = -1;
		private const ERROR:int = 0;		
		private const IMAGE:int = 1;
		private const MOVIE_CLIP:int = 2;
		private const AVM1_MOVIE:int = 3;
		
		/** 
		 * The scale for the document if all of it's width shall be visible.
		 */
		private var fitToWidth:Number;
		 
		/** 
		 * The scale for the document if all of it's height shall be visible.
		 */
		private var fitToHeight:Number;
		
		/** 
		 * The scale for the document ifboth it's width and height shall be visible.
		 */	
		private var fitToScreen:Number;
		
		/**
		 * The preloader to display while the content is loading.
		 */
		[Embed(source="assets/preloader.swf")]		
		public var Preloader:Class;
		public var preloader:MovieClipAsset;
		
		/**
		 * Constructor
		 */
		public function DocumentZoomDisplay()
		{
			super();			
		}

		/**
		 * Loads the content defined by url into the documentDisplay so it can be
		 * paged, zoomed and moved.
		 * 
		 * @param url THe url to the content to display.
		 */
		public function set url(url:String):void
		{
			// Remember the url
			this._url = url;
			
			// Reset values
			fitToWidth = 0; 
			fitToHeight = 0;
			fitToScreen = 0;
			
			// Load the content
			request = new URLRequest(_url);
			loader = new Loader();			 
			loader.load(request);
			
			// Listen to events from the content loading
			loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, onLoaderError); 
			loader.contentLoaderInfo.addEventListener(Event.COMPLETE, onLoaderComplete);
			
			// Display preloader
			if (preloader == null)
    	    {	    	        	        	   
    	    	preloader = MovieClipAsset(new Preloader());
    	    }
    	    sprite = preloader;										
		}
		
		/** 
		 * Returns the url to the content displayed.
		 * 
		 * @return the url to the content displayed.
		 */
		public function get url():String
		{
			return this._url;
		}
		
		/**
		 * Moves the sprite/document so the page defined by page is displayed if it exist.
		 * 
		 * @param page The actual page number, to display first page use 1
		 */
		public function set page(page:int):void
		{			
			// Make sure the page exist.
			if (doc && doc.getNoOfPages() > 0 && page > 0 && page <= doc.getNoOfPages())
			{
				// Find the y-position of the page in the document (first page in doc is indexed with 0).
				var yTo:Number = doc.getPageStart(page - 1) - doc.gap;
				
				// Scale and invert the y-position.
				yTo = yTo * _sprite.scaleY * -1;
				 
 				// Move document the the y-position so the page is displayed in the top of the display area.	
				moveSprite(doc.x, yTo);				
			}						
		}
		
		/**
		 * Returns the last page that was set.
		 * 
		 * @return the last page that was set.
		 */
		public function get page():int
		{
			return _page;
		}
		
		/**
		 * Returns true if the document's pages are clickable.
		 * 
		 * @return true if the document's pages are clickable.
		 */
		public function get interactiveDocument():Boolean
		{
			return _interactive;
		}

		/**
		 * Enables or disables the document displayed as interactive or not.
		 * 
		 * @param interactive If true the document's pages will be clickable.
		 */
		public function set interactiveDocument(interactive:Boolean):void
		{
			if(_interactive != interactive)
			{
				_interactive = interactive;
				
				// Make sure SpritZoomDisplay isn't draggable if we are in interactive mode
				draggingEnabled = !_interactive;			
				
				// Make sure each page has a background color if in interactive mode
				var p:Page;
				for(var i:int = 0; i < doc.numChildren; i++)
				{
					p = doc.getChildAt(i) as Page;
					p.interactive = _interactive;
				}
			}
		}
	
		/**
		 * Returns the number of pages in the document.
		 * 
		 * @return the number of pages in the document.
		 */
		public function getNoOfPages():Number
		{
			return doc.getNoOfPages();	
		}
		
		/** 
		 * Called when the loader failed to load the content.
		 * 
		 * @param event Describes the error that occured.
		 */
 		private function onLoaderError(event:Event):void
		{
			sprite = null;
			var e:DocumentZoomDisplayEvent = new DocumentZoomDisplayEvent(DocumentZoomDisplayEvent.DOCUMENT_LOAD_ERROR);
			e.errorCode = event.type;
			dispatchEvent(e);			 
		}

		/**
		 * Called when the content has been loaded.
		 * 
		 * @param event Event describing the succesful loading of the content.
		 */
		private function onLoaderComplete(event:Event):void 
		{	
			// Reset the pages	
			pages = new Array();
						
    	    if (loader.content is MovieClip && paging)
    	    {   
    	    	// We will create a multi paged document with a page for each fram in the loaded movie
    	    	contentType = MOVIE_CLIP;		    	
				var mc:MovieClip = MovieClip(loader.content);
				createMultiPageDocument(mc.totalFrames);	
    	    }
    	    else
    	    {
    	    	var p:Page = new Page();
    	    	// Find out what we have loaded and add it as page to the single paged document
		    	if (loader.content is MovieClip)
	    	    {    
	    	    	contentType = MOVIE_CLIP;	    	    	
	    	    	p.addChild(MovieClip(loader.content));		        	        	   											
	    	    }
	    	    else if (event.currentTarget.loader.content is flash.display.Bitmap)
	    	    {    
	        		contentType = IMAGE;
	    	    	p.addChild(Bitmap(loader.content));		        	        	   													    
	    	    }
	    	    else if (event.target.actionScriptVersion == 2)
		    	{
		    		contentType = AVM1_MOVIE;
	    	    	p.addChild(AVM1Movie(loader.content));	    	    	
	    	    }
	    	    else
	    	    {
	    	    	// Can't display url because loaded content is not a bitmap or movieclip.
    	    		sprite = null;
					var e:DocumentZoomDisplayEvent = new DocumentZoomDisplayEvent(DocumentZoomDisplayEvent.DOCUMENT_CONTENT_TYPE_ERROR);					
					dispatchEvent(e);	
					return;    	    	    	    	
	    	    }									
	    	    
	    	    // Create the document and add the page to it
				doc = new Document();
				pages.push(p);				
				doc.addChild(p);						
				
				// Add the document as the sprite/document of the document zoom display 										
				sprite = doc;
				
				// Make sure necessarry taks are taken now that the document is loaded and created
				documentComplete();	
    	    }
		}
				
		/**
		 * If created asynchronously they might never finsih for a browser with the debug player.
		 * 
		 * @param yetToCreate The number of frames that are left to create unique movie clip instances of.
		 */ 	 
		private function createMultiPageDocument(yetToCreate:Number):void
		{
			// Create a unique instance of the movie clip an set it to display the page/frame specified bt yetToCreate
    		createMovieClipInstance(
    			loader, 
    			function movieClipCreated(mc:MovieClip, obj:Object):void{
    				/**
    				 * Create a new Page containing the new unique move clip instance
    				 * and add it to the page array
    				 */
	    			var p:Page = new Page();	    			
	    			p.addChild(mc);
					pages.push(p);
												
					if (obj.yetToCreate > 0)
					{
						// Create more instances/frames/pages 
						createMultiPageDocument(obj.yetToCreate - 1);
					}	
					else
					{	
						// All insteancs have been created now create the document that will display them
						createMultiPageDocument2();											
					}
	    		}, 
	    		{
	    			yetToCreate: yetToCreate
    			}
			);	
		}
		
		/**
		 * Creates a unique instance of the movie clip inside the loader.
		 * 
		 * Note!
		 * 
		 * When using the Flash Debug player this might call an error to be thrown
		 * 
		 * @param loader The loader that contains the movie clip to duplicate and create a unique instanfe of.
		 * @param callback The function to call when the instance is created.
		 * @param obj The object to pass as the argument to the callback function.
		 */		
		private function createMovieClipInstance(loader:Loader, callback:Function, obj:Object=null):void
		{
			// Create a byte array rrepresentation of the movie clip
			var ba:ByteArray = loader.content.loaderInfo.bytes
			
			// Create the loader to load th representation into
			var l:Loader = new Loader();	
			
			// Listen for the complete event so we can invoke the callback		
			l.contentLoaderInfo.addEventListener(Event.COMPLETE, function bytesLoaded():void
			{
				// Invokde the callback since the instance is created 
				callback(MovieClip(l.content), obj);	
			});
			
			// Load the representation into the loader, but from the array instead of from the network
			l.loadBytes(ba);
		}

		/**
		 * Called when a unique instance has been created for each page/frame in the loaded move clip.
		 */  
		private function createMultiPageDocument2():void
		{	
			// Create the document with padding around and between the pages 		
			doc = new Document();
			doc.addEventListener(DocumentEvent.DOCUMENT_PAGE_CLICK, onDocumentPageClick);
			doc.padding = 5;
			doc.gap = 5;
			
			// Add clips/frames/pages from the pages array to the document
			var page:Page, 
				mc:MovieClip;
			for (var j:Number = 0; j < pages.length; j++)
			{
				page = pages[j] as Page;
				page.padding = 15;
    			mc = page.getChildAt(0) as MovieClip;
				mc.gotoAndStop(j + 1);
				doc.addChild(page);	
			}
						
			/**
			 *  Make sure the document's default position is top so the first pages is 
			 * visible and aligned to the top border of the display area
			 */
			verticalDefaultPosition = "top";
			
			// Add the document to the document zoom display
			sprite = doc;
			
			// Make sure necessarry taks are taken now that the document is loaded and created
			documentComplete();							
		}
		
		/**
		 * Called by other methods when the document is loaded and 
		 * set as the sprite to page, zoom and move.
		 */
		private function documentComplete():void
		{
			// Calculate the snapoints and dispatch them as an event
			dispatchSnapPoints();				
			if (!addedEventListeners)
			{
				// Add listeners for important events
				addedEventListeners = true;
				this.addEventListener(ResizeEvent.RESIZE, onDocumentDisplayResize);			
				
				stage.addEventListener(KeyboardEvent.KEY_DOWN, onKeyDown);
			}
		}
		
		/**
		 * Calculates the snappoints based on the laoded content and dispatches it as an event.
		 */
		private function dispatchSnapPoints():void
		{		
			if (doc.getNoOfPages() > 0)
			{
				// Fit to width  				
				var w:Number = doc.getMaximumPageWidth() + doc.padding * 2;
				var h:Number = doc.getDocHeight();
				
				// Get an approximate value that doesn't consider scrollbars
				fitToWidth = this.width / w;
						
				// Get info how these values will appear on the screen		
				var ctx:SpriteZoomDisplayContext = getZoomSpriteDisplatyContext(w * fitToWidth, h * fitToWidth);
				
				// Get an exact value that considers scrollbars
				fitToWidth = ctx.screenWidth / w;												
				
				// Fit to height  				
				w = doc.getDocWidth();
				h = doc.getMaximumPageHeight() + doc.padding * 2;
				
				// Get an approximate value that doesn't consider scrollbars
				fitToHeight = this.height / h;				
				
				// Get info how these values appear on the screen	
				ctx = getZoomSpriteDisplatyContext(w * fitToHeight, h * fitToHeight);
				
				// Get an exact value that considers scrollbars
				fitToHeight = ctx.screenHeight / h;																			
				
				// Use the value that displays most of the content				
				fitToScreen = Math.min(fitToWidth, fitToHeight);				
			}
			
			// Create the event
			var e:DocumentZoomDisplayEvent = new DocumentZoomDisplayEvent(DocumentZoomDisplayEvent.DOCUMENT_SNAP_POINTS_CHANGE);
			e.fitToWidth = fitToWidth;
			e.fitToHeight = fitToHeight;
			e.fitToScreen = fitToScreen;
			
			if (contentType == MOVIE_CLIP || contentType == AVM1_MOVIE)
			{
				/**
				 * Use the scale that makes all of the content fit,
				 * don't bother with original size since content is vector based.
				 */
				e.fitByContentType = e.fitToScreen;
			}
			else if (contentType == IMAGE)
			{
				if (fitToScreen > 1)
				{
					// The image's real size is smaller than the screen size, use the real size so we don't distort it
					e.fitByContentType = 1;
				}
				else
				{
					// The image's real size is larger than the display area, so make sure it fits.
					e.fitByContentType = e.fitToScreen;
				}
			}
			
			// Dispatch the event
			dispatchEvent(e);
		}		  					
		
		/**
		 * Called when a page in the document was clicked.
		 * 
		 * @param event Describes the page click event.
		 */
		 private function onDocumentPageClick(event:DocumentEvent):void
		 {
		 	if(_interactive)
		 	{		 	
		 		// Change zoom
			 	spriteNewZoom = fitToScreen;
			 	
				// Find the y-position of the page in the document.
				var yTo:Number = doc.getPageStart(event.pageIndex) - doc.gap;
				spriteNewY = yTo * fitToScreen * -1;			 	
				spriteNewYChanged = true;
				
				// Make sure sprite and the rest of the gui gets updated
			 	invalidateDisplayList();
			 	
			 	// Notify others about the zoom change
			 	var dsc:DocumentZoomDisplayEvent = new DocumentZoomDisplayEvent(DocumentZoomDisplayEvent.DOCUMENT_SCALE_CHANGE);
			 	dsc.documentScale = fitToScreen;
			 	dispatchEvent(dsc);			 				 				 			 			 		
		 	}
		 }
				
		/**
		 * Listen for key events that can be used to navigate in the document
		 */
        private function onKeyDown(event:KeyboardEvent):void 
        {			        				
			if (event.keyCode == Keyboard.HOME)
			{
				// Show first if HOME is clicked.
				page = 1;
			} 
			else if (event.keyCode == Keyboard.END)
			{
				// Show last page if END is clicked.
				page = doc.getNoOfPages();
			}
			else if (event.keyCode == Keyboard.LEFT || event.keyCode == Keyboard.PAGE_UP)
			{
				// Show previous page if LEFT or PAGE_UP is clicked.
				page = this._page - 1;
			}
			else if (event.keyCode == Keyboard.RIGHT || event.keyCode == Keyboard.PAGE_DOWN)
			{
				// Show next page if RIGHT or PAGE_DOWN is clicked.
				page = this._page + 1;
			}						 
			else if (event.keyCode == Keyboard.UP || event.keyCode == Keyboard.DOWN)
			{				
				// Show previous page if UP is cliked and next page if DOWN is clicked. 
				var newScrollbarPosition:Number = vsb.scrollPosition + (vsb.lineScrollSize * 3 * (event.keyCode == Keyboard.DOWN ? 1 : -1));
				adjustVerticalScrollbarPosition(newScrollbarPosition);	
			}			
		}
		
 		/**
 		 * Called by the FLEX framework when this componentneeds to redraw itself.
 		 * Other functions inside this class will tell the FLEX framework that this component 
 		 * needs to redraw itself by calling the invalidateDisplayList.
 		 * 
 		 * @param unscaledWidth The unscaled width of this component
         * @param unscaledHeight The unscaled height of this component 
 		 */
        override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void 
        {
        	// Call the updateDisplayList in SpriteZoomDisplay so scrollbars, dragging etc continues to work.
            super.updateDisplayList(unscaledWidth, unscaledHeight);
            
            // Update _page (if it has been changed) depending on what page that is displayed on the top of the screen
            if (_sprite && pages && pages.length > 1)
            {	
            	// Find out what page is displayed in the display areas top
            	var zcy:Number = Math.abs(_sprite.y);            
            	var cp:int = 1;
            	for (var pageIndex:Number = 0; pageIndex < doc.getNoOfPages(); pageIndex++)
            	{
            		/**
            		 * Need to floor them and add a pixel to sprite's position since the pixels 
            		 * can appear as 1200.32 and 1200.38 when scaled and all the numbers will 
            		 * not be "fraction"-exact as expected.
            		 */  
            		if ((Math.floor(zcy) + 1) < Math.floor(doc.getPageEnd(pageIndex) * spritePrevZoom))
            		{
            			cp = pageIndex + 1;
            			break;
            		}
            	}    
            	if (_page != cp)
            	{
            		dispatchPageScopeChange(cp);
            	}
            	
            }
            else if (_page != 0 && (!doc || doc.getNoOfPages() == 0))
            {
            	dispatchPageScopeChange(0);
            }
            else if (_page != 1 && _sprite && doc && doc.getNoOfPages() == 1)
            {            	
            	dispatchPageScopeChange(1);	
            }    
        }
        
        /**
    	 * Dispatches a documentPageScopeChange event with event-page set to page. 
         */
		private function dispatchPageScopeChange(page:int):void
		{
			// Set the page in the local model
			this._page = page;
			
			// Create and dispatch the event
			var e:DocumentZoomDisplayEvent = new DocumentZoomDisplayEvent(DocumentZoomDisplayEvent.DOCUMENT_PAGE_SCOPE_CHANGE);
			e.page = this._page;
			e.noOfPages = pages ? pages.length : 0;			
			dispatchEvent(e);
		}
		
		/**
		 * Called when ththis component is resized.
		 * Makes sure the new snapPoints for the display area is dispatched.
		 * 
		 * @param event Describes the resizing of this component.
		 */
		private function onDocumentDisplayResize(event:ResizeEvent):void
		{
			dispatchSnapPoints();
		} 
	}
}