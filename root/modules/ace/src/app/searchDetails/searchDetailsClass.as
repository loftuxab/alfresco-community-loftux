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
 
package app.searchDetails
{
	import mx.containers.Canvas;
	import mx.controls.Alert;
	import mx.controls.LinkButton;
	import mx.controls.Label;
	import mx.controls.SWFLoader;
	import component.hyperlink.HyperLink;
	
	/**
	 * Search Details Class
	 * 
	 * This provides an encapsulation for the Repeater element details of SearchResults
	 * 
	 * @author Saravanan Sellathurai
	 */
	
	public class searchDetailsClass extends Canvas
	{
		public var summaryBtn:HyperLink = new HyperLink();
		private var _url:String;
		private var _title:String;
		private var _summary:String;		

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
		 * @set summary, doctitle & link property for the repeater
		 * 
		 */		
		public function set summary(summary:String):void
		{
			if (summary != null && summary.length != 0)
			{
				summaryBtn.linkText = summary;
			}
			else 
			{	
				summaryBtn.linkText = this._title;
			}
		}
			
		/**
		 * Link property setter
		 */
		public function set link(link:String):void
		{
			this._url = link;			
		}
		
		/**
		 * Document title setter
		 */
		public function set doctitle(title:String):void
		{
			this._title = title;
			if (summaryBtn.linkText == null || summaryBtn.linkText.length == 0)
			{
				summaryBtn.linkText = this._title;
			}
		}	
	}
}