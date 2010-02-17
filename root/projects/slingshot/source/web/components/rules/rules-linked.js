/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * RulesLinked template.
 *
 * @namespace Alfresco
 * @class Alfresco.RulesLinked
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * RulesLinked constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RulesLinked} The new RulesLinked instance
    * @constructor
    */
   Alfresco.RulesLinked = function RulesLinked_constructor(htmlId)
   {
      Alfresco.RulesLinked.superclass.constructor.call(this, "Alfresco.RulesLinked", htmlId, ["button"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderRulesDetailsAvailable", this.onFolderRulesDetailsAvailable, this);

      return this;
   };

   YAHOO.extend(Alfresco.RulesLinked, Alfresco.component.Base,
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
          * nodeRef of folder being viewed
          *
          * @property nodeRef
          * @type Alfresco.util.NodeRef
          */
         nodeRef: null,

         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: ""
      },

      /**
       * Flag set after component is instantiated.
       *
       * @property isReady
       * @type {boolean}
       */
      isReady: false,

      /**
       * Current linkedFolder.
       *
       * @property linkedFolder
       * @type {object}
       */
      linkedFolder: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RulesLinked_onReady()
      {
         // Save references to dom objects
         this.widgets.pathEl = Dom.get(this.id + "-path");
         this.widgets.titleEl = Dom.get(this.id + "-title");

         // Create buttons
         this.widgets.viewLinkedFolderButton = Alfresco.util.createYUIButton(this, "view-button", this.onViewLinkedFolderButtonClick);
         this.widgets.changeLinkButton = Alfresco.util.createYUIButton(this, "change-button", this.onChangeLinkButtonClick);
         this.widgets.unlinkRulesButton = Alfresco.util.createYUIButton(this, "unlink-button", this.onUnlinkRulesButtonClick);

         // Display folder name & appropriate actions if info has been given
         this.isReady = true;
         if (this.linkedFolder !== null)
         {
            this._displayLinkedFolder();
         }
      },

      /**
       * Called when user clicks on the unlink rules button.
       * Will nulink the folder from the linked folder.
       *
       * @method onUnlinkRulesButtonClick
       * @param type
       * @param args
       */
      onUnlinkRulesButtonClick: function RulesLinked_onUnlinkRulesButtonClick(type, args)
      {
         // Check the state of the button
         this.widgets.unlinkRulesButton.set("disabled", true);

         // Start/stop inherit rules from parent folder
         Alfresco.util.Ajax.jsonRequest(
         {
            method: rulesAreInherited ? Alfresco.util.Ajax.GET : Alfresco.util.Ajax.GET,
            url: Alfresco.constants.PROXY_URI_RELATIVE + "api/sites",
            successCallback:
            {
               fn: function(response)
               {
                  if (response.json)
                  {
                     // Successfully unlinked folder, now reload page so other components can be brougt in
                     document.location.refresh();
                  }
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {
                  this.widgets.unlinkRulesButton.set("disabled", false);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: Alfresco.util.message("message.failure", this.name),
                     text: this.msg("message.unlinkRules-failure")
                  });
               },
               scope: this
            }
         });

      },

      /**
       * Called when user clicks on the view rules button.
       * Takes the user to the linked folders rule page.
       *
       * @method onViewLinkedFolderButtonClick
       * @param type
       * @param args
       */
      onViewLinkedFolderButtonClick: function RulesLinked_onViewLinkedFolderButtonClick(type, args)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{siteId}/folder-rules?nodeRef={nodeRef}",
         {
            siteId: this.options.siteId,
            nodeRef: this.linkedFolder.nodeRef.replace(":/", "")
         });
         window.location.href = url;
      },

      /**
       * Called when user clicks on the change link from button.
       * Displays a rule folder dialog.
       *
       * @method onChangeLinkButtonClick
       * @param type
       * @param args
       */
      onChangeLinkButtonClick: function RulesLinked_onChangeLinkButtonClick(type, args)
      {
         alert("popup rules selector with mode='folder'");
      },

      /**
       * Event handler called when the "folderRulesDetailsAvailable" event is received
       *
       * @method onFolderRulesDetailsAvailable
       * @param layer
       * @param args
       */
      onFolderRulesDetailsAvailable: function RulesLinked_onFolderRulesDetailsAvailable(layer, args)
      {
         var folderRulesData = args[1].folderRulesDetails;
         this.linkedFolder = folderRulesData.linkedFolder;

         if (this.isReady)
         {
            this._displayLinkedFolder();
         }
      },

      /**
       * Displays the folder name as the title
       *
       * @method _displayLinkedFolder
       * @param layer
       * @param args
       * @private
       */
      _displayLinkedFolder: function RulesLinked__displayLinkedFolder(layer, args)
      {
         // Display the title & path
         this.widgets.titleEl.innerHTML = this.linkedFolder.name;
         this.widgets.pathEl.innerHTML = this.linkedFolder.path;
      }

   });
})();
