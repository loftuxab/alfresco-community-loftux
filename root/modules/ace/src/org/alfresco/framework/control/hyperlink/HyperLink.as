/*  
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing"
 */
 
package org.alfresco.framework.control.hyperlink
{
	import mx.controls.Text;
	import flash.events.MouseEvent;
	
	/**
	 * Hyper link control
	 * 
	 * @author Saravanan Sellathurai
	 * @author Roy Wetherall
	 */

	public class HyperLink extends Text
	{
		
		/** The rolled over state name */
		[Inspectable]
		private var _rolledOverStyleName:Object;
		
		/** Indicates whether the control is currently rolled over or not */
		private var _rolledOver:Boolean = false;
		
		/** The origional style name, used to recover after roll out */
		private var _origionalStyleName:Object;
			
		/**		
		 * Constructor
		 */
		public function HyperLink()
		{
			// Ensure the hand cursor is shown on roll over
			this.useHandCursor = true;
			this.buttonMode = true;
			this.mouseChildren = false;
			
			// Call the super class
			super();
			
			// Register interest in the event handlers
			addEventListener(MouseEvent.ROLL_OVER, onRollOver);
			addEventListener(MouseEvent.ROLL_OUT, onRollOut);
		}
	  	
		
		/**
		 * On roll over event handler
		 */
		private function onRollOver(event:MouseEvent):void
		{
			if (this._rolledOver == false)
			{
				this._origionalStyleName = this.styleName;	
				this.styleName = this._rolledOverStyleName;
				this._rolledOver = true;
			}		
		}

		/**
		 * On roll out event handler
		 */
		private function onRollOut(event:MouseEvent):void
		{
			if (this._rolledOver == true)
			{
				this.styleName = this._origionalStyleName;
				this._rolledOver = false;
			}	
		}
		
		/**
		 * Getter for the rolled over style name
		 */
		public function get rolledOverStyleName():Object
		{
			return this._rolledOverStyleName;
		}
		
		/** 
		 * Setter for the rolled over style name
		 */		
		public function set rolledOverStyleName(value:Object):void
		{
			this._rolledOverStyleName = value;
		}
	}

}