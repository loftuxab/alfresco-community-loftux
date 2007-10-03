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
 
package component.hyperlink
{
		import mx.controls.Text;
		import mx.controls.Text;
	  	import flash.events.Event;
	  	import flash.events.TextEvent;
	  	import flash.net.*;
	  	import mx.controls.Alert;
		
		/**
		 * Custom HyperLink Component.
		 * 
		 * This provides an encapsulated way for handling HTML type URLS
		 * 
		 * @author Saravanan Sellathurai
		 */
		
		 public class HyperLink extends Text
		 {
			  private var _colorNormal:String;   //Link text color.
			  private var _colorHover:String;    //Link hover text color.
			  
			  [Inspectable]
			  private var _linkText:String;      //Displayed Hyperlink text
			
			  /**
			   * Constructor
			   */    
			  public function HyperLink()
			  {
				   this.addEventListener("mouseOver",hover);
				   this.addEventListener("mouseOut",hover);
				   this.addEventListener("creationComplete",onCreationComplete);
				   super();
			  }//Constructor
			  
			  
			  /**
			   * Run by creation complete.  This is here to initialize this control
			   */   
			  private function onCreationComplete(oEvent:Event):void
			  {
				   
				   if (_linkText && _linkText.length > 0)  
				   {  	
				   		//only do this if we have link text
				    	setHtmlTextHoverOut();
				   }
				   else  
				   {
				     htmlText = "Property 'linkText' is REQUIRED";
				   }
			  }
			  			  
			  /**
			   * Concatenates the htmlText value and assigns it to the property
			   */
			  private function setHtmlTextHoverOut():void
			  {
				   var sHTML:String = '<font color="' + _colorNormal + '">';
				   sHTML += '<a href="event:myEvent" >';  
				   sHTML += _linkText;
				   sHTML += '</a>';
				   sHTML += '</font>';
				   this.htmlText = sHTML;
				   this.percentWidth = 100;
				   // option to set up tool tip this.toolTip = "Click to show " + linkText;
			  }
			  
			 /**
			   * Concatenates the htmlText value and assigns it to the property
			   */
			  private function setHtmlTextHover():void
			  {
				   var sHTML:String = '<font color="' + _colorNormal + '">';
				   sHTML += '<a href="event:myEvent" ><u>';  
				   sHTML += _linkText;
				   sHTML += '</u></a>';
				   sHTML += '</font>';
				   this.htmlText = sHTML;
				   this.percentWidth = 100;
				   
				   // option to set up tool tip this.toolTip = "Click to show " + linkText;
			  }
			  
			  
			  /** called by the mouseOver and mouseOut events of the control.
			   *  Toggles the color of the linkText
			   */
			  private function hover(oEvent:Event):void
			  {
				   if (oEvent.type == 'mouseOver')  
				   {
				   	 	 this.setHtmlTextHover();
				   	 	 this.htmlText = String(this.htmlText).replace(_colorNormal,_colorHover);
				   }
				   else  
				   {
				    	this.setHtmlTextHoverOut();
				    	this.htmlText = String(this.htmlText).replace(_colorHover,_colorNormal);
				   }
			  }
			  
			
			/**
			* @setter methods for mousehover and normal colors
			*/			  
	   		
	   		public function set colorNormal(normalColor:String):void
	   		{
	   			this._colorNormal = normalColor;
	   		}
	   		
	   		public function set colorHover(hoverColor:String):void
	   		{
	   			this._colorHover = hoverColor;
	   		}
	   		
	   		public function set linkText(linkText:String):void
	   		{
	   			this._linkText = linkText;
	   		}
	   		
	   		public function get linkText():String
	   		{
	   			return this._linkText;
	   		}
	   		
		} 
}