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
 * RulesHeader template.
 *
 * @namespace Alfresco
 * @class Alfresco.RulesHeader
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Selector = YAHOO.util.Selector,
      Event = YAHOO.util.Event;

   /**
    * RulesHeader constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RulesHeader} The new RulesHeader instance
    * @constructor
    */
   Alfresco.RulesHeader = function RulesHeader_constructor(htmlId)
   {
      Alfresco.RulesHeader.superclass.constructor.call(this, "Alfresco.RulesHeader", htmlId, ["button"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      YAHOO.Bubbling.on("folderRulesDetailsAvailable", this.onFolderRulesDetailsAvailable, this);

      return this;
   };

   YAHOO.extend(Alfresco.RulesHeader, Alfresco.component.Base,
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
          * The nodeRef of the folder being viewed
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
       * The folders name
       *
       * @property folderName
       * @type {string}
       */
      folderName: null,

      /**
       * The rules do list
       *
       * @property rules
       * @type {array}
       */
      rules: null,

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
      onReady: function RulesHeader_onReady()
      {
         // Save references to dom objects
         this.widgets.inheritRulesContainer = Dom.get(this.id + "-inheritRules-container");
         this.widgets.actionsEl = Dom.get(this.id + "-actions");
         this.widgets.titleEl = Dom.get(this.id + "-title");

         // Create buttons
         this.widgets.inheritRulesButton = Alfresco.util.createYUIButton(this, "inheritRules-button", this.onInheritRulesButtonClick);
         this.widgets.newRuleButton = Alfresco.util.createYUIButton(this, "newRule-button", this.onNewRuleButtonClick);
         this.widgets.copyRuleFromButton = Alfresco.util.createYUIButton(this, "copyRuleFrom-button", this.onCopyRuleFromButtonClick);
         this.widgets.runRulesMenu = Alfresco.util.createYUIButton(this, "runRules-menu", this.onRunRulesMenuSelect,
         {
            type: "menu",
            menu: "runRules-options"
         });

         // Display folder name & appropriate actions if info has been given
         this.isReady = true;
         if (this.rules !== null)
         {
            this._displayActions();
         }
         if (this.folderName !== null)
         {
            this._displayFolderName();
         }

      },

      /**
       * Called when user clicks on the inherite rule button.
       * Will start or stop inheritance and afterwards load the folders rule data once again
       * and dispatch it in an event.
       *
       * @method onInheritRulesButtonClick
       * @param type
       * @param args
       */
      onInheritRulesButtonClick: function RulesHeader_onInheritRulesButtonClick(type, args)
      {
         // Check the state of the button
         var rulesAreInherited = Dom.hasClass(this.widgets.inheritRulesContainer, "active");
         this.widgets.inheritRulesButton.set("disabled", true);

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
                     // Successfully started or stopped rules inheritance, now reaload info on the page
                     YAHOO.Bubbling.fire("folderRulesDetailsChanged",
                     {
                        nodeRef: this.options.nodeRef
                     });

                     // Change the icon/state of the inherit button
                     this.widgets.inheritRulesButton.set("disabled", false);
                     this._toggleInheritButton();
                  }
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {
                  this.widgets.inheritRulesButton.set("disabled", false);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: Alfresco.util.message("message.failure", this.name),
                     text: this.msg("message." + (rulesAreInherited ? "start" : "stop") + "InheritRules-failure")
                  });
               },
               scope: this
            }
         });

      },

      /**
       * Called when user clicks on the create rule button.
       * Takes the user to the new rule page.
       *
       * @method _toggleInheritButton
       * @private
       */
      _toggleInheritButton: function RulesHeader__toggleInheritButton()
      {
         var rulesAreInherited = Dom.hasClass(this.widgets.inheritRulesContainer, "active");
         if (rulesAreInherited)
         {
            Dom.removeClass(this.widgets.inheritRulesContainer, "active");
         }
         else
         {
            Dom.addClass(this.widgets.inheritRulesContainer, "active");
         }
      },

      /**
       * Called when user clicks on the create rule button.
       * Takes the user to the new rule page.
       *
       * @method onNewRuleButtonClick
       * @param type
       * @param args
       */
      onNewRuleButtonClick: function RulesHeader_onNewRuleButtonClick(type, args)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{siteId}/rule-edit?nodeRef={nodeRef}",
         {
            siteId: this.options.siteId,
            nodeRef: this.options.nodeRef.toString()
         });
         window.location.href = url;
      },

      /**
       * Called when user clicks on the copy rule from button.
       * Displays a rule folder dialog.
       *
       * @method onCopyRuleFromButtonClick
       * @param type
       * @param args
       */
      onCopyRuleFromButtonClick: function RulesHeader_onCopyRuleFromButtonClick(type, args)
      {
         if (!this.modules.rulesPicker)
         {
            this.modules.rulesPicker = new Alfresco.module.RulesPicker(this.id + "-rulesPicker");
         }

         this.modules.rulesPicker.setOptions(
         {
            mode: Alfresco.module.RulesPicker.MODE_COPY_FROM,
            siteId: this.options.siteId,            
            files: {
               displayName: this.folderName,
               nodeRef: this.options.nodeRef.toString()
            }
         }).showDialog();

      },

      /**
       * Called when an option in the Run Rules menu has been called.
       *
       * @method onRunRulesMenuSelect
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onRunRulesMenuSelect: function RulesHeader_onRunRulesMenuSelect(sType, aArgs, p_obj)
      {
         // Display a wait feedback message if the people hasn't been found yet
         this.widgets.runRulesMenu.set("disabled", true);
         YAHOO.lang.later(2000, this, function(){
            if (this.isSearching)
            {
               if (!this.widgets.feedbackMessage)
               {
                  this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.runningRules"),
                     spanClass: "wait",
                     displayTime: 0
                  });
               }
               else if (!this.widgets.feedbackMessage.cfg.getProperty("visible"))
               {
                  this.widgets.feedbackMessage.show();
               }
            }
         }, []);

         var runMode = aArgs[1].value;

         // TODO change to jsonPost and use runMode
         // Run rules for folder (and sub folders)
         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI_RELATIVE + "api/sites",
            successCallback:
            {
               fn: function(response)
               {
                  this._enableSearchUI();

                  var data = response.json;
                  if (data)
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.runRules-success")
                     });
                  }
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {
                  this._enableSearchUI();
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: this.msg("label.failure"), 
                     text: this.msg("message.runRules-failure")
                  });

               },
               scope: this
            }
         });

         Event.preventDefault(aArgs[0]);
      },


      /**
       * Enable search button, hide the pending wait message and set the panel as not searching.
       *
       * @method _enableSearchUI
       * @private
       */
      _enableSearchUI: function RulesHeader__enableSearchUI()
      {
         // Enable search button and close the wait feedback message if present
         if (this.widgets.feedbackMessage && this.widgets.feedbackMessage.cfg.getProperty("visible"))
         {
            this.widgets.feedbackMessage.hide();
         }
         this.widgets.runRulesMenu.set("disabled", false);
         this.isSearching = false;
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
         var folderData = args[1].folderDetails;
         this.folderName = folderData.fileName;
         if (this.isReady)
         {
            this._displayFolderName();
         }
      },

      /**
       * Displays the folder name as the title
       *
       * @method _displayFolderName
       * @param layer
       * @param args
       * @private
       */
      _displayFolderName: function RulesHeader__displayFolderName(layer, args)
      {
         // Display the title
         this.widgets.titleEl.innerHTML = this.folderName;
      },

      /**
       * Event handler called when the "folderRulesDetailsAvailable" event is received
       *
       * @method onFolderRulesDetailsAvailable
       * @param layer
       * @param args
       */
      onFolderRulesDetailsAvailable: function RulesHeader_onFolderRulesDetailsAvailable(layer, args)
      {
         var folderRulesData = args[1].folderRulesDetails;
         this.rules = folderRulesData.rules ? folderRulesData.rules : [],
         this.linkedFolder = folderRulesData.linkedFolder;

         if (this.isReady)
         {
            this._displayActions();
         }
      },

      /**
       * Decides how to display the actions depending on the folders rules info.
       *
       * @method _displayActions
       * @param layer
       * @param args
       */
      _displayActions: function RulesHeader__displayActions(layer, args)
      {
         if (this.rules.length > 0 && !this.linkedFolder)
         {
            // Give the inherit button the correct state/icon depending
            Dom.removeClass(this.widgets.inheritRulesContainer, "active");
            for (var i = 0, l = this.rules.length; i < l; i++)
            {
               if (this.rules[i].inheritedFolder)
               {
                  Dom.addClass(this.widgets.inheritRulesContainer, "active");
                  break;
               }
            }

            // Display actions container
            Dom.removeClass(this.widgets.actionsEl, "hidden");
         }
         else
         {
            Dom.addClass(this.widgets.actionsEl, "hidden");
         }
      }

   });
})();
