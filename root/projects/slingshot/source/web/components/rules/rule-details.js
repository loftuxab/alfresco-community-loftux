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
 * RuleDetails component.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleDetails
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * RuleDetails constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RuleDetails} The new RuleDetails instance
    * @constructor
    */
   Alfresco.RuleDetails = function RuleDetails_constructor(htmlId)
   {
      Alfresco.RuleDetails.superclass.constructor.call(this, "Alfresco.RuleDetails", htmlId, []);

      // Instance variables
      this.folderDetails = null;
      this.ruleDetails = null;
      this.rule = null;
      this.ruleConfigsAreReady = false;

      // Decoupled event listeners
      YAHOO.Bubbling.on("ruleSelected", this.onRuleSelected, this);

      return this;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.RuleDetails, Alfresco.component.Base);

   /**
    * Augment prototype with Actions module
    */
   YAHOO.lang.augmentProto(Alfresco.RuleDetails, Alfresco.RuleConfigUtil);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.RuleDetails.prototype,
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
          * The nodeRef of folder who's rules are being viewed
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
       * Object describing the folder of the rule that is being viewed
       *
       * @property folderDetails
       * @type {object}
       */
      folderDetails: null,

      /**
       * Object describing basic information needed to load the rule
       *
       * @property ruleDetails
       * @type {object}
       */
      ruleDetails: null,

      /**
       * Object describing all information about a rule
       *
       * @property rule
       * @type {object}
       */
      rule: null,

      /**
       * Flag set after rule config compoents are ready.
       *
       * @property ruleConfigsAreReady
       * @type {boolean}
       * @default false
       */
      ruleConfigsAreReady: false,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RuleDetails_onReady()
      {
         /**
          * Load rule config components.
          * onRuleConfigsLoaded will be called when they have been inserted into the Dom and are visually ok.
          * onRuleConfigsReady will be called when they have loaded their dependencies and are ready to display rules.
          */
         this.loadRuleConfigs();

         // Save a refererence to the details display div so we can hide it during load and when nothing is selected
         this.widgets.displayEl = Dom.get(this.id + "-display");

         // Create buttons
         this.widgets.editButton = Alfresco.util.createYUIButton(this, "edit-button", this.onEditButtonClick);
         this.widgets.deleteButton = Alfresco.util.createYUIButton(this, "delete-button", this.onDeleteButtonClick);
      },

      /**
       * Called then the rule config components have been loaded and inserted to the Dom and are looking visually ok.
       * However they are not ready to use yet since they will need to load their own dependencies.
       *
       * @method onRuleConfigsLoaded
       * @override
       */
      onRuleConfigsLoaded: function RuleEdit_onRuleConfigsLoaded()
      {
         // Remove config loading message and display configs
         Dom.addClass(this.id + "-configsMessage", "hidden");
         Dom.removeClass(this.id + "-configsContainer", "hidden");
      },

      /**
       * Called then the rule config components have been loaded, inserted and are ready to dispaly rule configs
       *
       * @method onRuleConfigsReady
       * @override
       */
      onRuleConfigsReady: function RuleEditUtil_onRuleConfigsReady()
      {
         // Display rule info if rule has been loaded
         this.ruleConfigsAreReady = true;
         this._displayRule();
      },

      /**
       * Event handler called when the "ruleSelected" event is received
       *
       * @method onRuleSelected
       * @param layer
       * @param args
       */
      onRuleSelected: function RulesHeader_onRuleSelected(layer, args)
      {
         this.folderDetails = args[1].folderDetails;
         this.ruleDetails = args[1].ruleDetails;
         this._loadRule();
      },

      /**
       * Loads the rule from the server
       *
       * @method _loadRule
       * @private
       */
      _loadRule: function RuleDetails__loadRule()
      {
         // Hide component
         Dom.setStyle(this.widgets.displayEl, "display", "none");

         // Load rule information form server
         var nodeRefAsUrl = this.folderDetails.nodeRef.replace("://", "/"); // todo: USE rule.owningNode.nodeRef.replace("://", "/")
         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI_RELATIVE + "api/node/" + nodeRefAsUrl + "/ruleset/rules/" + this.ruleDetails.id,
            successCallback:
            {
               fn: function(response)
               {
                  if (response.json)
                  {
                     // Display rule info if config components has been loaded
                     this.rule = response.json;
                     this._displayRule();
                  }
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.getRuleFailure", this.name)
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Display the rule and its configuration
       *
       * @method _displayRule
       * @private
       */
      _displayRule: function RuleDetails__displayRule()
      {
         if (this.ruleConfigsAreReady && this.rule)
         {
            // Hide/show the edit & delete buttons
            if (this.rule.url.indexOf(this.options.nodeRef.uri) == -1 )
            {
               // This rule doesn't belong to the current folder
               Dom.addClass(this.id + "-actions", "hidden");
            }
            else
            {
               // This rule belongs to the current folder
               Dom.removeClass(this.id + "-actions", "hidden");
            }

            // Basic info
            Dom.get(this.id + "-title").innerHTML = $html(this.rule.title);
            Dom.get(this.id + "-description").innerHTML = $html(this.rule.description);
            Dom.removeClass(this.id + "-disabled", "enabled");
            Dom.removeClass(this.id + "-disabled", "disabled");
            Dom.addClass(this.id + "-disabled", this.rule.disabled == true ? "disabled" : "enabled");
            Dom.removeClass(this.id + "-executeAsynchronously", "enabled");
            Dom.removeClass(this.id + "-executeAsynchronously", "disabled");
            Dom.addClass(this.id + "-executeAsynchronously", this.rule.executeAsynchronously == true ? "enabled" : "disabled");
            Dom.removeClass(this.id + "-applyToChildren", "enabled");
            Dom.removeClass(this.id + "-applyToChildren", "disabled");
            Dom.addClass(this.id + "-applyToChildren", this.rule.applyToChildren == true ? "enabled" : "disabled");

            // Display all rule configs (when, if, unless & action)
            this.displayRuleConfigs(this.rule, Alfresco.RuleConfig.MODE_TEXT, null);

            // Display component again
            Alfresco.util.Anim.fadeIn(this.widgets.displayEl);
         }
      },

      /**
       * Fired when the user clicks the Edit button.
       * Takes the user back to the edit rule page.
       *
       * @method onEditButtonClick
       * @param event {object} a "click" event
       */
      onEditButtonClick: function RuleDetails_onEditButtonClick(event)
      {
         // Disable buttons to avoid double submits or cancel during post
         this.widgets.editButton.set("disabled", true);

         // Send the user to edit rule page
         var url = YAHOO.lang.substitute("rule-edit?nodeRef={nodeRef}&ruleId={ruleId}",
         {
            nodeRef: this.options.nodeRef.toString(),
            ruleId: this.ruleDetails.id.toString()
         });
         window.location.href = url;
      },

      /**
       * Fired when the user clicks the Delete button.
       *
       * @method onDeleteButtonClick
       * @param event {object} a "click" event
       */
      onDeleteButtonClick: function RuleDetails_onDeleteButtonClick(event)
      {
         this.widgets.deleteButton.set("disabled", true);
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.delete.title"),
            text: this.msg("message.confirm.delete"),
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function RuleDetails_onDeleteButtonClick_delete()
               {
                  this.destroy();
                  me._onDeleteRuleConfirmed.call(me);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function RuleDetails_onDeleteButtonClick_cancel()
               {
                  this.destroy();
                  me.widgets.deleteButton.set("disabled", false);
               },
               isDefault: true
            }]
         });
      },


      /**
       * Fired when the user clicks the Delete button.
       *
       * @method _onDeleteRuleConfirmed
       */
      _onDeleteRuleConfirmed: function RuleDetails__onDeleteRuleConfirmed()
      {
         if (!this.widgets.deleteFeedbackMessage)
         {
            this.widgets.deleteFeedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: Alfresco.util.message("message.deletingRule", this.name),
               spanClass: "wait",
               displayTime: 0
            });
         }
         else
         {
            this.widgets.deleteFeedbackMessage.show();
         }

         // Delete rule
         Alfresco.util.Ajax.jsonDelete(
         {
            url: Alfresco.constants.PROXY_URI_RELATIVE + "api/node/" + this.options.nodeRef.uri + "/ruleset/rules/" + this.ruleDetails.id,
            successCallback:
            {
               fn: function (response)
               {
                  this.widgets.deleteFeedbackMessage.hide();
                  this.widgets.deleteButton.set("disabled", false);
                  Dom.setStyle(this.widgets.displayEl, "display", "none");
                  YAHOO.Bubbling.fire("folderRulesDetailsChanged",
                  {
                     nodeRef: this.options.nodeRef
                  });
               },
               scope: this
            },
            failureCallback:
            {
               fn: function (response)
               {
                  this.widgets.feedbackMessage.destroy();
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: Alfresco.util.message("message.deletingRule-failure", this.name)
                  });
               },
               scope: this
            }
         });
      }

   }, true);
})();
