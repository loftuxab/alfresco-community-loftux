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
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      YAHOO.Bubbling.on("folderRulesetDetailsAvailable", this.onFolderRulesetDetailsAvailable, this);
      YAHOO.Bubbling.on("linkedToFolderDetailsAvailable", this.onLinkedToFolderDetailsAvailable, this);

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
       * The inherit and folder rules for the folder
       *
       * @property ruleset
       * @type {object}
       */
      ruleset: null,

      /**
       * Current linkedToFolder.
       *
       * @property linkedToFolder
       * @type {object}
       */
      linkedToFolder: null,

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
         this.widgets.viewlinkedToFolderButton = Alfresco.util.createYUIButton(this, "view-button", this.onViewLinkedToFolderButtonClick);
         this.widgets.changeLinkButton = Alfresco.util.createYUIButton(this, "change-button", this.onChangeLinkButtonClick,
         {
            disabled: true
         });
         this.widgets.unlinkRulesButton = Alfresco.util.createYUIButton(this, "unlink-button", this.onUnlinkRulesButtonClick,
         {
            disabled: true
         });

         // Display folder name & appropriate actions if info has been given
         this.isReady = true;
         this._enableAndDisplay();
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
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI_RELATIVE + "api/actionQueue",
            dataObj:
            {
               actionedUponNode : this.options.nodeRef.toString(),
               actionDefinitionName: "unlink-rules"
            },
            successCallback:
            {
               fn: function(response)
               {
                  if (response.json)
                  {
                     // Successfully unlinked folder, now reload page so other components can be brougt in
                     document.location.reload();
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
       * @method onViewLinkedToFolderButtonClick
       * @param type
       * @param args
       */
      onViewLinkedToFolderButtonClick: function RulesLinked_onViewLinkedToFolderButtonClick(type, args)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{siteId}/folder-rules?nodeRef={nodeRef}",
         {
            siteId: this.linkedToFolder.site, 
            nodeRef: this.linkedToFolder.nodeRef
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
         if (!this.modules.rulesPicker)
         {
            this.modules.rulesPicker = new Alfresco.module.RulesPicker(this.id + "-rulesPicker");
         }

         this.modules.rulesPicker.setOptions(
         {
            mode: Alfresco.module.RulesPicker.MODE_LINK_TO,
            siteId: this.options.siteId,
            files: {
               displayName: this.folderDetails,
               nodeRef: this.options.nodeRef.toString()
            }
         }).showDialog();
      },

      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       *
       * @method onFolderDetailsAvailable
       * @param layer
       * @param args
       */
      onFolderDetailsAvailable: function RulesHeader_onFolderDetailsAvailable(layer, args)
      {
         this.folderDetails = args[1].folderDetails;
         this._enableAndDisplay();
      },

      /**
       * Event handler called when the "folderRulesetDetailsAvailable" event is received
       *
       * @method onFolderRulesetDetailsAvailable
       * @param layer
       * @param args
       */
      onFolderRulesetDetailsAvailable: function RulesLinked_onFolderRulesetDetailsAvailable(layer, args)
      {
         this.ruleset = args[1].folderRulesetDetails;
         this._enableAndDisplay();
      },

      /**
       * Event handler called when the "linkedToFolderDetailsAvailable" event is received
       *
       * @method onLinkedToFolderDetailsAvailable
       * @param layer
       * @param args
       */
      onLinkedToFolderDetailsAvailable: function RulesLinked_onLinkedToFolderDetailsAvailable(layer, args)
      {
         this.linkedToFolder = args[1].linkedToFolder;
         this._enableAndDisplay();
      },

      /**
       * Displays the folder name as the title
       *
       * @method _enableAndDisplay
       * @param layer
       * @param args
       * @private
       */
      _enableAndDisplay: function RulesLinked__enableAndDisplay(layer, args)
      {
         if (this.isReady && this.linkedToFolder && this.ruleset && this.folderDetails)
         {
            // Display the title & path
            this.widgets.changeLinkButton.set("disabled", false);
            this.widgets.unlinkRulesButton.set("disabled", false);
            this.widgets.titleEl.innerHTML = this.linkedToFolder.name;
            this.widgets.pathEl.innerHTML = this.linkedToFolder.path;
         }
      }

   });
})();
