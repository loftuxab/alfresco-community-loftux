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
    * RuleDetails constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RuleDetails} The new RuleDetails instance
    * @constructor
    */
   Alfresco.RuleDetails = function RuleDetails_constructor(htmlId)
   {
      Alfresco.RuleDetails.superclass.constructor.call(this, "Alfresco.RuleDetails", htmlId, []);

      // Decoupled event listeners
      YAHOO.Bubbling.on("ruleSelected", this.onRuleSelected, this);

      return this;
   };

   YAHOO.extend(Alfresco.RuleDetails, Alfresco.component.Base,
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
       * nodeRef of the rule that is being viewed
       *
       * @property ruleNodeRef
       * @type Alfresco.util.NodeRef
       */
      ruleNodeRef: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RuleDetails_onReady()
      {
         // Save a refererence to the details display div so we can hide it during load and when nothing is selected
         this.widgets.displayEl = Dom.get(this.id + "-display");

         // Create buttons
         this.widgets.editButton = Alfresco.util.createYUIButton(this, "edit-button", this.onEditButtonClick);
         this.widgets.deleteButton = Alfresco.util.createYUIButton(this, "delete-button", this.onDeleteButtonClick);
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
         this.ruleNodeRef = new Alfresco.util.NodeRef(args[1].ruleDetails.nodeRef);
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
         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI_RELATIVE + "api/sites",
            successCallback:
            {
               fn: function(response)
               {
                  if (response.json)
                  {
                     //var rule = response.json.data;
                     var rule = {
                        name: "A rule",
                        description: "This is a fake test rule",
                        modified: "2009-12-12",
                        active: true,
                        runInBackground: false,
                        ruleAppliesToSubFolders: true,
                        "when":
                        {
                           relation: "or",
                           configurations: [
                              { text: "Items are created" }
                           ]
                        },
                        "if":
                        {
                           relation: "and",
                           configurations: [
                              { text: "Name contains *.doc" },
                              { text: "Type is PNG" }
                           ]
                        },
                        "unless":
                        {
                           relation: "and",
                           configurations: [
                           ]
                        },
                        "action":
                        {
                           relation: "or",
                           configurations: [
                              { text: "Move document to '/other/doclib'" }
                           ]
                        }
                     };
                     this._displayRule(rule);
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
       * @param rule {object} rule info
       * @private
       */
      _displayRule: function RuleDetails__displayRule(rule)
      {
         // Basic info
         Dom.get(this.id + "-name").innerHTML = rule.name;
         Dom.get(this.id + "-description").innerHTML = rule.description;
         Dom.get(this.id + "-name").innerHTML = rule.name;
         Dom.removeClass(this.id + "-active", "active");
         Dom.removeClass(this.id + "-active", "inactive");
         Dom.addClass(this.id + "-active", rule.active == true ? "active" : "inactive");
         Dom.removeClass(this.id + "-runInBackground", "active");
         Dom.removeClass(this.id + "-runInBackground", "inactive");
         Dom.addClass(this.id + "-runInBackground", rule.runInBackground == true ? "active" : "inactive");
         Dom.removeClass(this.id + "-ruleAppliesToSubFolders", "active");
         Dom.removeClass(this.id + "-ruleAppliesToSubFolders", "inactive");
         Dom.addClass(this.id + "-ruleAppliesToSubFolders", rule.ruleAppliesToSubFolders == true ? "active" : "inactive");

         // Configurations
         this._displayConfigurations(rule["when"].configurations, rule["when"].relation, "when");
         this._displayConfigurations(rule["if"].configurations, rule["if"].relation, "if");
         this._displayConfigurations(rule["unless"].configurations, rule["unless"].relation, "unless");
         this._displayConfigurations(rule["action"].configurations, rule["action"].relation, "action");

         // Display component again
         Alfresco.util.Anim.fadeIn(this.widgets.displayEl);
      },

      /**
       * Display a set of configurations in the container
       *
       * @method _displayConfigurations
       * @param configurations {array} One of the configuration sets from the rule object
       * @param relation {string} String to represent the relation type: "and" | "or"
       * @param classId {string} The class helping us to identify the html elements
       * @private
       */
      _displayConfigurations: function RuleDetails__createConfigurations(configurations, relation, classId)
      {
         var configurationSectionEl = Dom.getElementsByClassName(classId, "div", Dom.get(this.id + "-body"))[0];
         configurations = configurations ? configurations : [];

         // Hide or display section depending on if at least one configuration exist
         if (configurations.length == 0)
         {
            Dom.addClass(configurationSectionEl, "hidden");
         }
         else
         {
            Dom.removeClass(configurationSectionEl, "hidden");
         }

         // Relation
         var relationEl = Dom.getElementsByClassName("configuration-relation", "div", configurationSectionEl)[0];
         if (configurations.length > 1)
         {
            Dom.removeClass(relationEl, "hidden");
            Dom.removeClass(relationEl, "and");
            Dom.removeClass(relationEl, "or");
            Dom.addClass(relationEl, relation);
         }
         else
         {
            Dom.addClass(relationEl, "hidden");
         }

         // Configurations
         var configurationBodyEl = Dom.getElementsByClassName("configuration-body", "ul", configurationSectionEl)[0],
            configuration = null;
         while(configurationBodyEl.hasChildNodes())
         {
            configurationBodyEl.removeChild(configurationBodyEl.firstChild);
         }
         for (var i = 0, l = configurations.length; i < l; i++)
         {
            configuration = configurations[i];
            var ruleEl = document.createElement("li");
            Dom.addClass(ruleEl, "configuration");
            ruleEl.innerHTML = configuration.text; // Todo create real text here....
            ruleEl = configurationBodyEl.appendChild(ruleEl);            
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
         var url = YAHOO.lang.substitute("rule-edit?folderNodeRef={folderNodeRef}&ruleNodeRef={ruleNodeRef}",
         {
            folderNodeRef: this.options.nodeRef.toString(),
            ruleNodeRef: this.ruleNodeRef.toString()
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
         alert('Not implemented');
      }

   });
})();
