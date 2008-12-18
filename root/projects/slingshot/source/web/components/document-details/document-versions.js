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
 * Document Details Version component.
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentVersions
 */
(function()
{

   /**
    * Dashboard DocumentVersions constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentVersions} The new component instance
    * @constructor
    */
   Alfresco.DocumentVersions = function DV_constructor(htmlId)
   {
      this.name = "Alfresco.DocumentVersions";
      this.id = htmlId;

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container"], this.onComponentsLoaded, this);

      return this;
   }

   Alfresco.DocumentVersions.prototype =
   {

      /**
       * Object container for initialization options
       *
       * @property options
       * @type {object} object literal
       */
      options:
      {
         /**
          * An array with labels in the same order as they are listed in the html template
          *
          * @property versions
          * @type Array an array of object literals of the following form:
          * {
          *    label: {string}, // the version ot revert the node to
          *    createDate: {string}, // the date the version was creted in freemarker?datetime format
          * }
          */
         versions: [],

         /**
          * The version ot revert the node to
          *
          * @property nodeRef
          * @type {string}
          */
         nodeRef: null,

         /**
          * The version ot revert the node to
          *
          * @property filename
          * @type {string}
          */
         filename: null         
      },


      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setOptions: function DV_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocumentVersions} returns 'this' for method chaining
       */
      setMessages: function DV_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DV_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function DV_onReady()
      {
         // Listen on clicks for revert version icons
         var versions = this.options.versions;
         var myThis = this;
         for (var i = 0; i < versions.length; i++)
         {
            var revertSpan = YAHOO.util.Dom.get(this.id + "-revert-span-" + i);
            if(revertSpan)
            {
               YAHOO.util.Event.addListener(revertSpan, "click",
                       function (event, obj)
                       {
                          // Find the index of the version link by looking at its id
                          var version = versions[obj.versionIndex];

                          // Find the version through the index and display the revert dialog for the version
                          Alfresco.module.getRevertVersionInstance().show({
                             filename: this.options.filename,
                             nodeRef: this.options.nodeRef,
                             version: version.label,
                             onRevertVersionComplete: {
                                fn: this.onRevertVersionComplete,
                                scope: this
                             }
                          });
                       }, {versionIndex: i}, this);
            }

            // Listen on clicks on the version - date row so we can expand and collapse it
            var expandDiv = YAHOO.util.Dom.get(this.id + "-expand-div-" + i);
            var moreVersionInfoDiv = YAHOO.util.Dom.get(this.id + "-moreVersionInfo-div-" + i);            
            if(expandDiv)
            {
               YAHOO.util.Event.addListener(expandDiv, "click",
                       function (event, obj)
                       {
                          //alert(obj.versionIndex);
                          var Dom = YAHOO.util.Dom;
                          //var moreVersionInfoDiv = Dom.get(this.id + "-moreVersionInfo-div-" + obj.versionIndex);
                          if(obj.moreVersionInfoDiv && Dom.hasClass(obj.expandDiv, "collapsed"))
                          {
                             Alfresco.util.Anim.fadeIn(obj.moreVersionInfoDiv);
                             Dom.removeClass(obj.expandDiv, "collapsed");
                             Dom.addClass(obj.expandDiv, "expanded");
                          }
                          else
                          {
                             Dom.setStyle(obj.moreVersionInfoDiv, "display", "none");
                             Dom.removeClass(obj.expandDiv, "expanded");
                             Dom.addClass(obj.expandDiv, "collapsed");
                          }
                       },
               {
                  expandDiv: expandDiv,
                  moreVersionInfoDiv: moreVersionInfoDiv
               }, this);
            }

            // Format and display the createdDate
            var createdDateSpan = document.getElementById(this.id + "-createdDate-span-" + i);
            createdDateSpan.innerHTML = Alfresco.util.formatDate(versions[i].createdDate);

         }

      },

      /**
       * Fired by the Revert Version component after a successfull revert.
       *
       * @method onRevertVersionComplete
       */
      onRevertVersionComplete: function DV_onRevertVersionComplete()
      {
         Alfresco.util.PopupManager.displayMessage({ text: Alfresco.util.message("message.revertComplete", this.name) });

         window.location.reload();
      }

   };
})();
