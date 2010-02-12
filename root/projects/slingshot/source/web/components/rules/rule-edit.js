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
 * RuleEdit component.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleEdit
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * RuleEdit constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RuleEdit} The new RuleEdit instance
    * @constructor
    */
   Alfresco.RuleEdit = function RuleEdit_constructor(htmlId)
   {
      Alfresco.RuleEdit.superclass.constructor.call(this, "Alfresco.RuleEdit", htmlId, []);

      YAHOO.Bubbling.on("ruleConfigReady", this.onRuleConfigReady, this);

      return this;
   };

   YAHOO.extend(Alfresco.RuleEdit, Alfresco.component.Base,
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
          * nodeRef of folder who's rules are being viewed
          *
          * @property nodeRef
          * @type Alfresco.util.NodeRef
          */
         folderNodeRef: null,

         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Full info about the rule being edited
          *
          * @property rule
          * @type object
          */
         rule: null,

         /**
          * An object describing an emtpy rule
          *
          * @property emptyRule
          * @type object
          */
         ruleTemplate:
         {
            title: "",
            description: "",
            ruleType: ["inbound"],
            applyToChildren: false,
            executeAsynchronously: false,
            disabled: false,
            action:
            {
               actionDefinitionName: "composite-action",
               actions: [
                  {
                     actionDefinitionName: "",
                     parameterValues: {}
                  }
               ],
               conditions: [
                  {
                     actionDefinitionName: "",
                     parameterValues: {}
                  }
               ],
               compensatingAction:
               {
                  actionDefinitionName: "executeScript",
                  parameterValues:
                  {
                     scriptLocation: ""
                  }
               }
            }
         }
      },

      /**
       * This is where the rule configs will be stored: type (event), condition (if & unless) & action (action).
       * Will first contain the componentId as key and a false boolean value to indicate that a component is being
       * loaded but hasn't fired its "ruleConfigReady" event. When the config component has fired the event
       * onRuleConfigReady method will replace the boolean value with the component instance to
       * indicate the config component is ready to be used.
       *
       * @property ruleConfigs
       * @type object
       */
      ruleConfigs: {},

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RuleEdit_onReady()
      {

         // Create & Edit menues & buttons
         this.widgets.createButton = Alfresco.util.createYUIButton(this, "create-button", null,
         {
            type: "submit"
         });
         this.widgets.createAnotherButton = Alfresco.util.createYUIButton(this, "createAnother-button", null,
         {
            type: "submit"
         });
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "save-button", null,
         {
            type: "submit"
         });
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

         // Load rule config components
         this._loadRuleConfigs([
            { component: "components/rules/config/type", name: "ruleConfigType", dataObj: {}},
            { component: "components/rules/config/condition", name: "ruleConfigIfCondition", dataObj: { mode: "if" }},
            { component: "components/rules/config/condition", name: "ruleConfigUnlessCondition", dataObj: { mode: "unless" }},
            { component: "components/rules/config/action", name: "ruleConfigAction", dataObj: {}}
         ]);
      },

      /**
       * Load rule config components and insert them inside this component
       *
       * @method _loadRuleConfigs
       * @param {array} ruleConfigs array with objects describing from where to load the components and where to insert them
       */
      _loadRuleConfigs: function RuleEdit__loadRuleConfigs(ruleConfigs)
      {
         if (ruleConfigs && ruleConfigs.length > 0)
         {
            var ruleConfig = ruleConfigs[0],
               ruleConfigComponentId = this.id + "-" + ruleConfig.name;
            this.ruleConfigs[ruleConfigComponentId] = false;
            ruleConfig.dataObj.htmlid = ruleConfigComponentId;
            Alfresco.util.Ajax.request({
               url: Alfresco.constants.URL_SERVICECONTEXT + ruleConfig.component,
               dataObj: ruleConfig.dataObj,
               successCallback:
               {
                  fn: function (response){
                     // Insert config components html to this component
                     Dom.get(this.id + "-" + ruleConfig.name).innerHTML = response.serverResponse.responseText;

                     // Get the rest of the configs
                     this._loadRuleConfigs(ruleConfigs.splice(1));
                  },
                  scope: this
               },
               execScripts: true
            });
         }
         else
         {
            this.onRuleConfigsLoaded();
         }
      },

      /**
       * Called then the rule config components have been loaded and inserted 
       *
       * @method onRuleConfigsLoaded
       */
      onRuleConfigsLoaded: function RuleEdit_onRuleConfigsLoaded()
      {
         // Remove config loading message and display configs
         Dom.addClass(this.id + "-configsMessage", "hidden");
         Dom.removeClass(this.id + "-configsContainer", "hidden");

         // Form definition
         var form = new Alfresco.forms.Form(this.id + "-rule-form");
         this.widgets.form = form;
         this.widgets.formEl = Dom.get(this.id + "-rule-form");
         form.setSubmitElements([this.widgets.createButton, this.widgets.createAnotherButton, this.widgets.saveButton]);
         form.setShowSubmitStateDynamically(true);
         form.setSubmitAsJSON(true);
         form.doBeforeFormSubmit =
         {
            fn: function()
            {
               var ruleId = Dom.get(this.id + "-id").value,
                  url = Alfresco.constants.PROXY_URI + "api/node/" + this.options.folderNodeRef.uri + "/ruleset/rules",
                  successCallback;
               if (ruleId.length > 0)
               {
                  this.widgets.formEl.attributes.action.nodeValue = url + "/" + ruleId;
                  this.widgets.form.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
                  successCallback = {
                     fn: this.onRuleUpdate,
                     scope: this
                  };
               }
               else
               {
                  this.widgets.formEl.attributes.action.nodeValue = url;
                  this.widgets.form.setAjaxSubmitMethod(Alfresco.util.Ajax.POST);
                  successCallback = {
                     fn: this.onRuleCreated,
                     scope: this
                  };
               }
               this.widgets.form.setAJAXSubmit(true,
               {
                  successCallback: successCallback,
                  failureCallback:
                  {
                     fn: this.onSaveRuleFailed,
                     scope: this
                  }
               });
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message("message.creating", this.name),
                  spanClass: "wait",
                  displayTime: 0
               });
            },
            obj: null,
            scope: this
         };
         form.doBeforeAjaxRequest =
         {
            fn: function(p_oConfig)
            {
               // Adjust the obj to fit the webscripts
               var rule = p_oConfig.dataObj;
               rule.ruleType = [];
               var ruleConfigTypes = this.ruleConfigs[this.id + "-ruleConfigType"].getRuleConfigs();
               for (var i = 0, il = ruleConfigTypes.length; i < il; i++)
               {
                  rule.ruleType.push(ruleConfigTypes[i].name);
               }
               rule.action.conditions = this.ruleConfigs[this.id + "-ruleConfigIfCondition"].getRuleConfigs();
               rule.action.conditions.concat(this.ruleConfigs[this.id + "-ruleConfigUnlessCondition"].getRuleConfigs());
               rule.action.actions = this.ruleConfigs[this.id + "-ruleConfigAction"].getRuleConfigs();
               if (rule.action.compensatingAction.parameterValues.scriptLocation.length == 0)
               {
                  // Remove attribute so it doesn't get sent to the server
                  delete rule.action.compensatingAction;
               }
               return true;
            },
            obj: null,
            scope: this
         };

         // Form field validation
         form.addValidation(this.id + "-title", Alfresco.forms.validation.mandatory, null, "keyup");

         // Note" The form will be initialized when the ruleConfig components are ready.
      },

      /**
       *
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters
       */
      onRuleConfigReady: function RuleEdit_onRuleConfigReady(layer, args)
      {
         // Check the event is directed towards this instance
         var configComponent = args[1].eventGroup,
             configComponentId = configComponent.id;
         if (YAHOO.lang.isBoolean(this.ruleConfigs[configComponentId]))
         {
            // The config component belongs to this component and is ready
            // Save reference to config component instance
            this.ruleConfigs[configComponentId] = configComponent;
         }

         // Check if all config components are ready, if so display the rules rule config sections
         if (!this.ruleConfigs[this.id + "-ruleConfigType"] ||
            !this.ruleConfigs[this.id + "-ruleConfigIfCondition"] ||
            !this.ruleConfigs[this.id + "-ruleConfigUnlessCondition"] ||
            !this.ruleConfigs[this.id + "-ruleConfigAction"])
         {
            // Not all config components are ready
            return;
         }


         // All config components are ready, display rule info
         if (this.options.rule)
         {
            this.displayRule(this.options.rule);
         }
         else
         {
            this.displayRule(this.options.ruleTemplate);
         }
      },

      /**
       * Display the rule
       *
       * @method displayRule
       * @param rule {object} An object describing the rule
       */
      displayRule: function RuleEdit_displayRule(rule)
      {
         var ruleConfig = null;
         Dom.get(this.id + "-id").value = rule.id ? rule.id : "";

         // Text fields
         Dom.get(this.id + "-title").value = rule.title;
         Dom.get(this.id + "-description").value = rule.description;

         // Transform types into a config object for event section
         var typeConfigs = [];
         for (var i = 0, il = rule.ruleType; i < il; i++)
         {
            typeConfigs.push(
            {
               name: rule.ruleType[i]
            });
         }

         // Initialise type config
         ruleConfig = this.ruleConfigs[this.id + "-ruleConfigType"];
         ruleConfig.setOptions(
         {
            form: this.widgets.form
         });
         ruleConfig.displayRuleConfigs(typeConfigs);

         // Add all conditions to if OR unless config sections
         var ifConditionConfigs = [],
            unlessConditionConfigs = [],
            config;
         for (i = 0, il = rule.action.conditions; i < il; i++)
         {
            config = rule.action.conditions[i];
            if (config.invertCondition)
            {
               unlessConditionConfigs.push(config);
            }
            else
            {
               ifConditionConfigs.push(config);
            }
         }

         // Initialise if condition config
         ruleConfig = this.ruleConfigs[this.id + "-ruleConfigIfCondition"];
         ruleConfig.setOptions(
         {
            form: this.widgets.form,
            ruleConfigDefinitionKey: "actionDefinitionName"
         });
         ruleConfig.displayRuleConfigs(ifConditionConfigs);

         // Initialise unless condition config
         ruleConfig = this.ruleConfigs[this.id + "-ruleConfigUnlessCondition"];
         ruleConfig.setOptions(
         {
            form: this.widgets.form,
            ruleConfigDefinitionKey: "actionDefinitionName"
         });
         ruleConfig.displayRuleConfigs(unlessConditionConfigs);

         // Add actions to action section and initilise action config
         ruleConfig = this.ruleConfigs[this.id + "-ruleConfigAction"];
         ruleConfig.setOptions(
         {
            form: this.widgets.form,
            ruleConfigDefinitionKey: "actionDefinitionName"
         });
         ruleConfig.displayRuleConfigs(rule.action.actions);

         // Checkboxes
         Dom.get(this.id + "-disabled").checked = rule.disabled;
         Dom.get(this.id + "-applyToChildren").checked = rule.applyToChildren;
         Dom.get(this.id + "-executeAsynchronously").checked = rule.executeAsynchronously;

         // Compensating script
         var scriptLocation = Alfresco.util.findValueByDotNotation(rule, "action.compensatingAction.parameterValues.scriptLocation", null);
         if (scriptLocation)
         {
            Alfresco.util.setSelectedIndex(Dom.get(this.id + "-scriptLocation"), scriptLocation);
         }

         // Finally initialise the form
         this.widgets.form.init();
      },

      /**
       * Called when user clicks the cancel button.
       * Takes the user to the folder rules page.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function RuleEdit_onCancelButtonClick(type, args)
      {
         var url = YAHOO.lang.substitute("folder-rules?nodeRef={nodeRef}",
         {
            nodeRef: this.options.folderNodeRef.toString()
         });
         window.location.href = url;
      },

      /**
       * @method onRuleCreated
       * @param response
       */
      onRuleCreated: function RE_onRuleCreated(response)
      {
         this.widgets.feedbackMessage.destroy();
         alert('created');
      },

      /**
       * @method onRuleUpdate
       * @param response
       */
      onRuleUpdate: function RE_onRuleUpdate(response)
      {
         this.widgets.feedbackMessage.destroy();
         alert('update');
      },

      /**
       * @method onSaveRuleFailed
       * @param response
       */
      onSaveRuleFailed: function RE_onSaveRuleFailed(response)
      {
         this.widgets.feedbackMessage.destroy();
         alert('failed');
      }

   });
})();
