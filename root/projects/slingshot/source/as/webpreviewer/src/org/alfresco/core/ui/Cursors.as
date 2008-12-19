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

package org.alfresco.core.ui
{
	import flash.events.Event;
	
	import mx.managers.CursorManager;
	import mx.managers.CursorManagerPriority;
	
	/**
	 * Helper class for managing cursors.
	 */
	public class Cursors
	{
		/** 
		 * Array of ids to all the current hand cursors that are in use.
		 */ 
		private static var handCursorIds:Array = new Array;

		/** 
		 * Array of ids to all the current grab cursors that are in use.
		 */ 
		private static var grabCursorIds:Array = new Array;
		
		/**
		 * Constructor
		 */
		public function Cursors()
		{
		}		
		
		/**
		 * The hand cursor image
		 */
	    [Embed(source="assets/cursor-hand.png")]
		private static var handCursor:Class;

		/**
		 * The grab cursor image
		 */
	    [Embed(source="assets/cursor-grab.png")]
		private static var grabCursor:Class;
	
		/**
		 * Show cursor as a hand.
		 * 
		 * @param event Any event that this method was set to listen for.
		 */
		public static function showHandCursor(event:Event):void 
		{
			handCursorIds.push(CursorManager.setCursor(handCursor, CursorManagerPriority.MEDIUM));
		    CursorManager.showCursor();
	   	}

		/**
		 * Hide the last hand cursor.
		 * 
		 * @param event Any event that this method was set to listen for.
		 */
	   	public static function hideHandCursor(event:Event):void 
	   	{
        	CursorManager.removeCursor(handCursorIds.pop());
	   	}
		
		/**
		 * Display cursor as a grabbing hand.
		 * 
		 * @param event Any event that this method was set to listen for.
		 */
		public static function showGrabCursor(event:Event):void 
		{
			grabCursorIds.push(CursorManager.setCursor(grabCursor, CursorManagerPriority.HIGH));
		    CursorManager.showCursor();
	   	}
	   	
		/**
		 * Hide the last grab cursor.
		 * 
		 * @param event Any event that this method was set to listen for.
		 */	   	
	   	public static function hideGrabCursor(event:Event):void 
	   	{
        	CursorManager.removeCursor(grabCursorIds.pop());
	   	}
	}
}