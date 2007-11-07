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

package org.alfresco.framework.service.webscript
{
	import flash.xml.XMLDocument;
	import flash.xml.XMLNode;
	import flash.net.URLLoader;
    import flash.net.URLRequest;
    import org.alfresco.framework.service.error.ErrorService;
    import flash.events.Event;
    import mx.controls.Alert; 
    
    /**
	 * Config service.
	 * 
	 * This provides an encapsulated way for handling URLS
	 * 
	 * @author Saravanan Sellathurai
	 */
    	
	public class ConfigService
	{
		
		private var myXML:XML = new XML();
		private var XML_URL:String;
		private var myXMLURL:URLRequest;
		private var myLoader:URLLoader;
		private var myList:XMLList;
		private var myDoc:XMLDocument;
		private var node:XMLNode;
		
		private var _domain:String;
		private var _protocol:String;
		private var _port:String;
	
		/** Static instance of the authentication service */
		private static var _instance:ConfigService;
		
		/** url for config */
		private var _url:String;
		
		/**
		 * Singleton method to get the instance of the Search Service
		 */
		public static function get instance():ConfigService
		{
			if (ConfigService._instance == null)
			{
				ConfigService._instance = new ConfigService();
				
			}
			return ConfigService._instance;
		}
		
		/**
		 * Default constructor
		 */
		public function ConfigService()
		{
			
			/**
			* 
			* Place the ace-config.xml file into the bin directory, where all the compiled swf's reside
			* Sample contents of the ace-config.xml file
			* 
			* <?xml version="1.0"?>
			*	<ace-config>
			*		<url protocol="http" domain="localhost" port="8080"/>
			*	</ace-config>
			* 
			*/
			
			try
			{
				myXML = new XML();
				XML_URL = "ace-config.xml";
				myXMLURL = new URLRequest(XML_URL);
				myLoader = new URLLoader(myXMLURL); 
				myLoader.addEventListener("complete", xmlLoaded);
			}
			catch (error:Error)
			{
				ErrorService.instance.raiseError(ErrorService.APPLICATION_ERROR, error);
			}
		}
		
		public function get url():String
		{
			return this._url;
		}
		
		private function xmlLoaded(evtObj:Event):void
		{
			try
			{
				myXML = XML(myLoader.data);
				myDoc = new XMLDocument();
				
				myDoc.ignoreWhite=true;
				myDoc.parseXML(myXML.toXMLString());
				node = myDoc.firstChild;
				
				// get the url information from the ace-config.xml file
				this._domain = node.firstChild.attributes['domain'];
				this._port = node.firstChild.attributes['port'];
				this._protocol = node.firstChild.attributes['protocol'];
				
				if (this.port!= null)
				{
						this._url = this._protocol + "://" + this._domain + ":" + this._port;
				}
				else
				{
						this._url = this._protocol + "://" + this._domain;
				}
			}
			catch (error:Error)
			{
				ErrorService.instance.raiseError(ErrorService.APPLICATION_ERROR, error);
			}
			
		}
		
		/**
		 * getter methods
		 * to return the url info from the ace-config.xml file
		 */		
		 
		public function get domain():String
		{
			return this._domain;
		}
		
		public function get protocol():String
		{
			return this._protocol;
		}
		
		public function get port():String
		{
			return this._port;
		} 
		
	}
}