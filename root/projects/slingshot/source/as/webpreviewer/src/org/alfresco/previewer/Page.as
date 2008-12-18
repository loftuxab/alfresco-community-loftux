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
	import flash.events.Event;
	import flash.events.MouseEvent;		
	
	/**
	 * Adds padding to the display object inside but the most important  
	 * feature is that it provides the possibility to use mouse events 
	 * such as mouse over, click, mouse out on the wrapped display object.
	 * 
	 * I.e. The content loaded though a Loader and found in loader.content 
	 * (typed as DisplayObject) does NOT provide mouse events even if the actual
	 * content inisde is say a MovieClip (which in the api says it 
	 * dispatches mouse events). So if an event listener that listen for 
	 * mouse events is attached to a loaded movie clip nothing happens; no errors 
	 * and no events.
	 * 
	 * By wrapping the loaded content inside a Prite we can listen for events on 
	 * the sprite instead. 
	 */
	public class Page extends Sprite
	{
		/**
		 * The backgound color to display if padding i used.
		 */
		public var backgroundColor:uint = 0x0054B9F8;  
		
		/**
		 * The wrapped display object.
		 */
		private var mc:DisplayObject;
		
		/**
		 * The border around mc; top, left, right and bottom.
		 */
		private var _padding:Number = 0;
		
		/**
		 * True if page should appear as interactive
		 */
		private var _interactive:Boolean = false;
		
		/**
		 * Constructor
		 */		
		public function Page()
		{
			super();				
			addEventListener(MouseEvent.MOUSE_OVER, onPageMouseOver);			
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
				redrawChild();				
			}
		}
		
		
		/**
		 * Returns true if the page appears as interactive and dispatches events
		 * 
		 * @return true if the page appears as interactive and dispatches events
		 */
		public function get interactive():Boolean 
		{
			return _interactive;
		}
		
		/**
		 * Set to true if the page shall appear as interactive and dispatches events
		 * 
		 * @param interactive true if the page shall appear as interactive and dispatches events
		 */
		public function set interactive(interactive:Boolean):void
		{
			if (_interactive != interactive)
			{
				_interactive = interactive;
				useHandCursor = interactive;
				buttonMode = interactive;				
			}
		}
			
		/**
		 * Positions the children with regards to padding.		 
		 */
        private function redrawChild():void 
        {
        	if (mc)
        	{
	        	// Position child with regards to padding               	    
	        	mc.x = _padding;
				mc.y = _padding;
				
				// Fill background with transparent graphics so the Pages dimensions include the padding			
	        	doFillBackground(0);
	        }
        }
			
		/**
		 * Called when the user moves the mouse over a page.
		 * 
		 * @param event A description of the mouse over event
		 */	
		public function onPageMouseOver(event:Event):void
		{				
			doFillBackground(1);
			addEventListener(MouseEvent.CLICK, onPageMouseClick);
			addEventListener(MouseEvent.MOUSE_OUT, onPageMouseOut);			
		}
	
		/**
		 * Called when the user moves the mouse out from a page.
		 * 
		 * @param event A description of the mouse out event
		 */	
		public function onPageMouseOut(event:Event):void
		{		
			doFillBackground(0);
			removeEventListener(MouseEvent.CLICK, onPageMouseClick);
		}
		
		/**
		 * Called when the user clicks a page.
		 * 
		 * @param event A description of the mouse click
		 */	
		public function onPageMouseClick(event:Event):void
		{
			// Create a fully colored border round the object			
			doFillBackground(1);			
				
			// Remove click event listener
			removeEventListener(MouseEvent.CLICK, onPageMouseClick);			
		}
		
		public override function addChild(child:DisplayObject):DisplayObject
		{
			// Remove old child if any
			var prevMcIndex:int = mc ? getChildIndex(mc) : -1;
			if (prevMcIndex > -1)
			{
				removeChildAt(prevMcIndex);
			}
			
			// Add new child
			mc = child;
			super.addChild(mc);		
			
			// Make sure child is positioned
			redrawChild();
			
			// Return child to caller 
			return mc;
		}
		
		private function doFillBackground(alpha:Number):void
		{			
			graphics.clear();
			graphics.beginFill(backgroundColor, _interactive ? alpha : 0);
	        graphics.drawRect(0, 0, mc.width + _padding * 2, mc.height + _padding * 2);	        
	        graphics.endFill();					
		}
				
	}
	
}