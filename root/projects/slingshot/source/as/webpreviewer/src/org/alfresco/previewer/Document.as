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
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	
	/**
	 * A sprite object that displays its children as pages in a document.
	 */
	public class Document extends Sprite
	{
		
		/**
		 * The outer padding of the whpole document (top, left, right, bottom)
		 */
		private var _padding:Number = 0;
		
		/** 
		 * The vertical gap/padding betwwen the children/pages.
		 */
		private var _gap:Number = 0;	
		
		/**
		 * The width of the widest page/child.
		 */
		private var _maximumPageWidth:Number;
		
		/**
		 * The height if the highest page/child.
		 */	
		private var _maximumPageHeight:Number;
		
		/**
		 * "start"-y-position for all the pages/children.
		 */ 
		private var _pageStarts:Array = new Array();
		
		/**
		 * "end"-y-position for all the pages/children.
		 */ 
		private var _pageEnds:Array = new Array();
		
		/**
		 * Constructor
		 */
		public function Document()
		{
			super();			
		}				
						
		/**
		 * Positions the children/pages based on the order they were added and the gap and padding.
		 * Also updates the _maximumPageWidth, _maximumPageHeight, _pageStarts and _pageEnds properties.
		 */
        private function redrawChildren():void 
        {               	    
        	graphics.clear();
        	_pageEnds = new Array();
        	_pageStarts = new Array();
            var obj:DisplayObject;
            var top:Number = 0, left:Number = 0;            
            top += _padding; // padding top
            left += _padding; // padding left
            var widest:Number = 0;
            var highest:Number = 0;
            for (var i:Number = 0; i < numChildren; i++)
            {
            	// Position each child/page.
            	obj = getChildAt(i);
            	obj.x = left;            	
            	obj.y = top;
            	_pageStarts.push(top);
            	top += obj.height;
            	_pageEnds.push(top);
            	
            	// Add gap as long as there are more pages after the current.
            	if (i < numChildren - 1)
            	{
            		top += _gap;
            	}
            	
            	// Save the width/height of the widest/highest page/child.
            	widest = Math.max(obj.width, widest);     
            	highest = Math.max(obj.height, highest);            	            	
            }
            top += _padding; // padding bottom
            left += widest; // widest page
            left += _padding; // padding right      
                        
            graphics.beginFill(0xFFCC00, 0); // alpha set to 0 so the yellow "padding" isn't "visible"
            graphics.drawRect(0, 0, left, top);
            graphics.endFill();
            
            // Set variables for use by getters later
            _maximumPageWidth = widest;
            _maximumPageHeight = highest;
        }
		
		/**
		 * Adds a Page as a child to the display list and treats it like a page in a document.
		 * 
		 * @param child A page in the document.
		 */
		override public function addChild(child:DisplayObject):DisplayObject {			
			if (child is Page)
			{			
				// Call super class addChild.
	            super.addChild(child);                  
	            
	            // Add event listener for page clicks
	            child.addEventListener(MouseEvent.CLICK, onPageClick);
	            
	            // Layout all the pages in the document.       
				redrawChildren();			           
				
				// Return the child/page.
	            return child;	            				
			}
			else
			{
				throw Error("A child to a Document must be of type Child.");
			}
        }
		
		/**
		 * Returns the padding used used for top, left, right and bottom.
		 * 
		 * @return the padding used used for top, left, right and bottom
		 */
		public function get padding():Number 
		{
			return _padding;
		}
		
		/**
		 * Sets the padding to be used for top, left, right and bottom.
		 * 
		 * @param the padding to be used for top, left, right and bottom.
		 */
		public function set padding(padding:Number):void
		{
			if (_padding != padding)
			{
				_padding = padding;
				
				// Make sure we layout the pages according to the new padding.
				redrawChildren();				
			}
		}
		
		/**
		 * Returns the gap between the children/pages in the document.
		 * 
		 * @return the gap between the children/pages in the document.
		 */
		public function get gap():Number
		{
			return _gap;
		}
		
		/**
		 * Sets the gap to be used between the children/pages in the document.
		 * 
		 * @param the gap to be used between the children/pages in the document.
		 */
		public function set gap(gap:Number):void
		{			
			if (_gap != gap)
			{
				_gap = gap;
				
				// Make sure we layout the pages according to the new gap.
				redrawChildren();				
			}
		}
			
		/**
		 * Returns the width of the document.
		 * 
		 * @return the width of the document.
		 */
		public function getDocWidth():Number
		{
			return width;
		}

		/**
		 * Returns the height of the document.
		 * 
		 * @return the height of the document.
		 */
		public function getDocHeight():Number
		{
			return height; 	
		}

		/**
		 * Returns the width of the widest page/child.
		 * 
		 * @return the width of the widest page/child.
		 */
		public function getMaximumPageWidth():Number
		{
			return _maximumPageWidth;			
		}
		
		/**
		 * Returns the height of the widest page/child.
		 * 
		 * @return the height of the widest page/child.
		 */
 		public function getMaximumPageHeight():Number
		{
			return _maximumPageHeight;
		}
		
		/**		 
		 * Get the "start"-y-position for the page defined by pageIndex.
		 * 
		 * @param pageIndex The index of the page (first page can be found on index 0)
		 * 
		 * @return the "start"-y-position for the page 
		 */
		public function getPageStart(pageIndex:int):Number
		{
			return _pageStarts[pageIndex];
		}

		/**		 
		 * Get the "end"-y-position for the page defined by pageIndex.
		 * 
		 * @param pageIndex The index of the page (first page can be found on index 0)
		 * 
		 * @return the "end"-y-position for the page 
		 */
		public function getPageEnd(pageIndex:int):Number
		{
			return _pageEnds[pageIndex];
		}
		
		/**
		 * Returns the number of pages/children in the document.
		 * 
		 * @return the number of pages/children in the document.
		 */
		public function getNoOfPages():Number
		{
			return numChildren;
		}
		
		/**
		 * Called when one of the pages is clicked.
		 * 
		 * @param event Describes the click event on the page.
		 */ 
		private function onPageClick(event:MouseEvent):void
		{
			var pageIndex:int = getChildIndex(event.currentTarget as DisplayObject);
			if (pageIndex != -1)
			{
				var de:DocumentEvent = new DocumentEvent(DocumentEvent.DOCUMENT_PAGE_CLICK);
				de.page = event.currentTarget as Page;
				de.pageIndex = pageIndex;
				dispatchEvent(de);
			}
		}
		
	}
}
