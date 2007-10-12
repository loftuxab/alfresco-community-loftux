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
	import mx.controls.SWFLoader;
	import mx.controls.Alert;
	import mx.core.Repeater;
	import util.error.*;
	import component.swipe.*;
	import mx.containers.Canvas;
	import util.searchservice.*;
	import util.authentication.AuthenticationService;
	import mx.controls.Label;
	import mx.containers.VBox;
	import flash.events.Event;
	import app.searchDetails.searchDetailsClickEvent;
	
	
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
		public var swfTabbar:Canvas;
		public var myframe:SWFLoader;
		public var swfPanel:VBox;
		public var resultsDispPanel:Canvas;
		public var labelResultsFound:Label;
		
		private var _resultObj:Object;
		//private var _url:String;
	 
	    /** default Constructor */
	    public function searchResultsClass()
	    {
	       super();
	       
	     	// Register interest in events
			SearchService.instance.addEventListener(SearchCompleteEvent.SEARCH_COMPLETE, doSearchComplete); 	       		
       		this.addEventListener(searchDetailsClickEvent.SEARCH_LINK_CLICK_EVENT, onSearchDetailsClick);
       		
     	}
      
		/** Result Click event for the Repeater */
		private function onSearchDetailsClick(oEvent:searchDetailsClickEvent):void
        {
          	resultsDispPanel.percentWidth = 30;
          	swfPanel.visible = true;
          	//swfTabbar.visible = true;
           	swfPanel.percentWidth = 70;
           	myframe.visible = true;
			myframe.source = oEvent.data.toString() + "?ticket=" + AuthenticationService.instance.ticket;        
            //this._url = oEvent.data.toString() + "?ticket=" + AuthenticationService.instance.ticket;  
           // Alert.show(this._url);
		 }	
        
       
        /**Close Button Click event for the swf panel */
        public function CloseBtnClick():void
        {
 			resultsDispPanel.percentWidth = 100;
          	swfPanel.percentWidth = 0;
          	//swfTabbar.visible = false;
          	myframe.source = ''; 
         	this.results.dataProvider = this._resultObj;  
        }
       
       /** get method for url */
       //public function geturl():String 
      // {
       //		return this._url;
      // }	
       
       /** set method for url */
       //public function set url(str_url:String):void
      // {
      // 		this._url = str_url;	
      // }
       
      
        /**
		 * Event handler called when search is successfully completed
		 * 
		 * @event	search complete event
		 */
		private function doSearchComplete(event:SearchCompleteEvent):void
		{
			try
			{	
				this._resultObj = event.result.feed.entry;
				this.results.dataProvider = event.result.feed.entry;
				this.labelResultsFound.text = "Search Results :  "+ event.totalresults + " Items Found ";
				if(event.totalresults == "0") 
				{
					Alert.show("Result not found");
				}
			}
			catch (error:Error)
			{
				ErrorService.instance.raiseError(ErrorService.APPLICATION_ERROR, error.message);	
			}
		}
       
	}

}