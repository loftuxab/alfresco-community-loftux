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
 
package org.alfresco.ace.application.searchDetails
{
	import mx.containers.Canvas;
	import mx.controls.SWFLoader;
	import mx.controls.Text;
	import flash.events.Event;
	import mx.controls.Image;
	import org.alfresco.ace.application.searchDetails.searchDetailsClickEvent;
	import mx.events.DividerEvent;
	import mx.events.ResizeEvent;
	import org.alfresco.ace.control.hyperlink.HyperLink;
	import mx.containers.VBox;

	/**
	 * Search Details Class
	 * 
	 * This provides an encapsulation for the Repeater element details of SearchResults
	 * 
	 * @author Saravanan Sellathurai
	 */
	
	public class searchDetailsClass extends VBox
	{
		private var eventObj:Event;
		private var _url:String;
		private var _title:String;
		private var _summary:String;	
		private var _modified:String;	
		private static const DEFAULT_HIDE_WIDTH:int = 450;
		private var _hideWidth:int = DEFAULT_HIDE_WIDTH;
		private var _flag:Boolean;
		private var _dataUrl:String = "";
		private var _name:String = "";
		
	    public var imgTag:Image;
		public var myframe:SWFLoader;
		public var swfPanel:Canvas;
		public var resultsDispPanel:Canvas;
		public var titleBtn:HyperLink = new HyperLink();
		public var content:Text;
		public var updated:Text;
		
			
	
		/**
		 * Default Constructor
		 */		
		public function searchDetailsClass()
		{
			super(); 
			this.addEventListener(ResizeEvent.RESIZE, onResize);
		}
		
			
		/**
		 * @setter methods
		 * 
		 */
		 	
		public function set summary(summary:String):void
		{
				if(summary != null && summary.length != 0)
				{
					this._summary = summary;
					content.text = this._summary;
				}
				else
				{
					content.visible = false;
				}
		}
		
		public function set articleName(name:String):void
		{
			this._name = name;
			if(this._title == null) titleBtn.text = this._name;
		}
		
		public function set doctitle(title:String):void
		{
			this._title = title;
			if (title == null || title.length == 0)
			{
				titleBtn.text = this._name;
			}
			else
			{
				titleBtn.text = this._title;
			}
		}	
		
		public function set category(cat:String):void
		{
			if (cat == "FAQ") 
			{
				imgTag.source = "images/faq.png";	
			}
			else if (cat == "White Paper") 
			{
				imgTag.source = "images/white_paper.png";
			}
			else 
			{
				imgTag.source = "images/article.png";
			}			
		}
		
		public function set modified(modified:String):void
		{
			this._modified = modified;
			this.updated.text = modified;
		}
		
		public function set dataUrl(str:String):void
		{
			this._dataUrl = str;
		}
		
		/** Implementation for the search Details Click event*/
		public function onSearchDetailsClick():void
		{
		   dispatchEvent(new searchDetailsClickEvent(searchDetailsClickEvent.SEARCH_LINK_CLICK_EVENT,this._dataUrl,true,true));
		}
		
		
		/**  Implementation for resize event */
		private function onResize(event:ResizeEvent):void
		{
			if (this.width < this._hideWidth)
			{
				content.visible = false;
				content.includeInLayout = false;
			}
			else
			{
				content.visible = true;
				content.includeInLayout = true;
			}
		}
		
	}
}