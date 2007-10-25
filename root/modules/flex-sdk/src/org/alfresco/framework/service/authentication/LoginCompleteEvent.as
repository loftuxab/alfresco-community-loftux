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
 package org.alfresco.framework.service.authentication
{
	import flash.events.Event;

	/**
	 * Login complete event object
	 * 
	 * @author Roy Wetherall
	 */
	public class LoginCompleteEvent extends Event
	{
		/** Event name */
		public static const LOGIN_COMPLETE:String = "loginComplete";
		
		/** The ticket created during login */
		private var _ticket:String;
		
		/** The user name logged in */
		private var _userName:String;
		
		/**
		 * Constructor
		 */
		public function LoginCompleteEvent(type:String, ticket:String, userName:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			
			this._ticket = ticket;
			this._userName = userName;		
		}
		
		/**
		 * Getter for the ticket property
		 */
		public function get ticket():String
		{
			return this._ticket;
		}
		
		/**
		 * Getter for the userName property
		 */
		public function get userName():String
		{
			return this._userName;
		}
	}
}