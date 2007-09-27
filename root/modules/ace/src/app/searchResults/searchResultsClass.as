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
 
package app.searchResults
{
	import mx.containers.Panel;
	import mx.controls.SWFLoader;
	import mx.controls.Alert;
	import mx.core.Repeater;
	import util.searchservice.*;
	import util.error.*;
	import component.swipe.*;
	import mx.containers.Canvas;
	import util.searchservice.*;
	import mx.collections.ArrayCollection;
	import mx.controls.Text;
	import util.authentication.AuthenticationService;
	
	/**
	 * SearchResults Class
	 * 
	 * This provides an encapsulation for the SearchResults
	 * 
	 * @author Saravanan Sellathurai
	 */
	 
	public class searchResultsClass extends Canvas
	{
	    [Bindable]
		public var results:Repeater;
		[Bindable]
		public var sortbyValues:Array = [ {label:"Relevance", data:1}, {label:"Size", data:2}, {label:"Alphabetical", data:3} ];
		public var swipe:Swipe;	
		public var myframe:SWFLoader;
		public var swfPanel:Canvas;
		public var resultsDispPanel:Canvas;
		private var _url:String;
		public var content:Text;
		
		/** Result Click event for the Repeater */
		public function resultClick(str_url:String):void
        {
          	resultsDispPanel.percentWidth = 30;
          	swfPanel.visible = true;
           	swfPanel.percentWidth = 70;
          	myframe.visible = true;
           	myframe.source = str_url;        
            this._url = str_url + "?alf_ticket=" + AuthenticationService.instance.ticket;  
           
         }	
        
        /**Close Button Click event for the swf panel */
        public function CloseBtnClick():void
        {
 			resultsDispPanel.percentWidth = 100;
          	swfPanel.percentWidth = 0;
         	myframe.source = '';        
         }
        
        /** default Constructor */
       public function searchResultsClass()
       {
       	 	super();
       	 	
       	 	// Register interest in search service events
			SearchService.instance.addEventListener(SearchCompleteEvent.SEARCH_COMPLETE, doSearchComplete); 	       		
       }
      
       /** get method for url */
       public function geturl():String 
       {
       		return this._url;
       }	
       
       /** set method for url */
       public function set url(str_url:String):void
       {
       		this._url = str_url;	
       }
       
      
        /**
		 * Event handler called when search is successfully completed
		 * 
		 * @event	search complete event
		 */
		private function doSearchComplete(event:SearchCompleteEvent):void
		{
			try
			{	
				this.results.dataProvider = event.result.feed.entry;
			}
			catch (error:Error)
			{
				ErrorService.instance.raiseError(ErrorService.APPLICATION_ERROR, error.message);	
			}
		}
        
        
	}

}