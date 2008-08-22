/**
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 
/**
 * Document path component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentPath
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * DocumentPath constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentPath} The new DocumentPath instance
    * @constructor
    */
   Alfresco.DocumentPath = function(htmlId)
   {
      this.name = "Alfresco.DocumentPath";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.onComponentsLoaded, this);
   
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("documentDetailsAvailable", this.onDocumentDetailsAvailable, this);
      
      return this;
   }
   
   Alfresco.DocumentPath.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: ""
      },
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setOptions: function SiteMembers_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setMessages: function DocumentPath_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DocumentPath_onComponentsLoaded()
      {
         // don't need to do anything we will be informed via an event when data is ready
      },
      
      /**
       * Event handler called when the "documentDetailsAvailable" event is received
       */
      onDocumentDetailsAvailable: function DocumentPath_onDocumentDetailsAvailable(layer, args)
      {
         var docData = args[1];
         var pathHtml = "";
         var baseLinkUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/documentlibrary#path=";
         var pathUrl = "/";
         
         // create an array of paths
         var path = docData.location.path;
         if (path.length > 1)
         {
            var folders = path.substring(1, path.length).split("/");
            
            if (folders.length > 0)
            {
               pathHtml += "&nbsp;>&nbsp;";
            }
            
            for (var x = 0; x < folders.length; x++)
            {
               pathUrl += folders[x];
               
               pathHtml += '<img src="' + Alfresco.constants.URL_CONTEXT + '/components/documentlibrary/images/folder-closed-16.png';
               pathHtml += '" /><span class="path-link"><a href="' + baseLinkUrl;
               pathHtml += this._encodePath(pathUrl);
               pathHtml += '">'
               pathHtml += $html(folders[x]);
               pathHtml += '</a></span>';
               
               if (x < (folders.length-1))
               {
                  pathHtml += "&nbsp;>&nbsp;";
                  pathUrl += "/";
               }
            }
         }
         
         Dom.get(this.id + "-path").innerHTML = pathHtml;
      },
      
      /**
       * Encodes the given path for use on a URL
       *
       * @method _encodePath
       * @param path The path to encode
       * @return The encoded path
       */
      _encodePath: function DocumentPath__encodePath(path)
      {
         var encodedPath = (YAHOO.env.ua.gecko) ? encodeURIComponent(path) : path;
         
         return encodedPath;
      }
   };
})();
