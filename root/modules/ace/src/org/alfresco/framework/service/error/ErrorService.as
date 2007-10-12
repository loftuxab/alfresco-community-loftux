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
 package org.alfresco.framework.service.error
{
	import flash.events.EventDispatcher;
	
	/**
	 * Error service.
	 * 
	 * @author Roy Wetherall
	 */
	public class ErrorService extends EventDispatcher
	{
		/** Error type constants */
		public static const APPLICATION_ERROR:String = "ApplicationError";
		
		/** Singleton instance */
		private static var _instance:ErrorService;
		
		/**		
		 * Getter for static instance property
		 */
		public static function get instance():ErrorService
		{
			if (ErrorService._instance == null)
			{
				ErrorService._instance = new ErrorService();
			}			
			return ErrorService._instance;
		}
		
		/**
		 * Raise an error with the error service
		 */
		public function raiseError(errorType:String, error:Error):void
		{
			// Raise the errorRaisedEvent
			this.dispatchEvent(new ErrorRaisedEvent(ErrorRaisedEvent.ERROR_RAISED, errorType, error));
		}
	}
}