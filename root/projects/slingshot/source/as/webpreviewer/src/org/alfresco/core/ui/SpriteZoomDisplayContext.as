/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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

	/**
	 *  SpriteZoomDisplayContext is a helper class to SpriteXoomDisplay.
	 *  It describes the current state of the zoomable sprite inside the displays vieable area. 
	 */
	public class SpriteZoomDisplayContext
	{
		
		/**
		 * True if sprite is wider than the display area.
		 */
		public var overflowX:Boolean;
		
		/**
		 * True if sprite is higher than the display area.
		 */
		public var overflowY:Boolean;
		
		/**
		 * The width of the display area exluding the vertical scrollbar.
		 */
		public var screenWidth:Number;
		
		/**
		 * The hight of the display area excluding the horizontal srollbar.
		 */
		public var screenHeight:Number;

		/** 
		 * Constructor
		 */ 
		public function SpriteZoomDisplayContext()
		{				
		}

	}
}