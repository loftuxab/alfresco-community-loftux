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
 * RuleConfig template.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleConfig
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
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML,
       $hasEventInterest = Alfresco.util.hasEventInterest,
       $combine = Alfresco.util.combinePaths;

   /**
    * RuleConfig constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RuleConfig} The new RuleConfig instance
    * @constructor
    */
   Alfresco.RuleConfig = function RuleConfig_constructor(htmlId)
   {
      Alfresco.RuleConfig.superclass.constructor.call(this, "Alfresco.RuleConfig", htmlId, ["button"]);

      // Instance variables
      this._configDefs = {};
      this._datePickerConfigDefMap = {};

      // Decoupled event listeners
      YAHOO.Bubbling.on("mandatoryControlValueUpdated", this.onDatePickerMandatoryControlValueUpdated, this);

      return this;
   };

   YAHOO.extend(Alfresco.RuleConfig, Alfresco.component.Base,
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
          * The type of configs that are manipulated
          *
          * @property ruleConfigType
          * @type string
          */
         ruleConfigType: null,

         /**
          * The id-name to use when accepting ruleConfig-objects in displayRuleConfigs and
          * when returning ruleConfig-objects in getRuleConfigs.
          *
          * @property ruleConfigDefinitionKey
          * @type string
          * @default "name"
          */         
         ruleConfigDefinitionKey: "name",
         
         /**
          * Describes how the menu items shall be ordered and grouped.
          * Note that this is NOT the rule configs that will appear in the menu, it is a representation of how the
          * select element shall group its options where each group contains of pattern objects that will match a
          * rule config (or item) and therefore place the rule config in that specific group.
          *
          * @property menuMap
          * @type array
          */
         menuMap: [],

         /**
          * The rule configs that shall be selectable in the menu (types, conditions or actions).
          * These will be placed inside the select menu and be organised/ordered as described in the menuMap.
          *
          * @property ruleConfigDefinitions
          * @type array
          */
         ruleConfigDefinitions: [],

         /**
          * Customisations that may modify the default ui rendering of a rule config.
          *
          * @property customisationsMap
          * @type array
          */
         customisationsMap: [],

         /**
          * The rule constraints that shall be selectable in drop downs for some parameters.
          * Contains the constraint name as attribute keys and an array of constraint values as the attribute value.
          *
          * @property constraints
          * @type object
          */
         constraints: {},

         /**
          * The form created by the outside component that uses this config handler.
          * This component will hook in to the forms validation process to enable/disable its buttons.
          *
          * @property form
          * @type object
          */
         form: {}
      },

      /**
       * Used when a menu is built to save the config for each menu item so it can be passed into the renderer if selected.
       *
       * @property _configDefs
       * @type {object}
       * @private
       */
      _configDefs: null,

      /**
       * Each date picker that is created will have its associated configDef mapped here so the bubbling event handler
       * will know if the date picker belongs to this component and can provide the configDef to the
       * _updateSubmitElements method.
       *
       * @property _datePickerConfigDefMap
       * @type {object}
       */
      _datePickerConfigDefMap: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RuleConfig_onReady()
      {
         // Enabling checkbox
         var enableCheckboxEl = Dom.get(this.id + "-" + this.options.ruleConfigType + "-checkbox");
         if (enableCheckboxEl)
         {
            Event.addListener(enableCheckboxEl, "click", function(p_oEvent, p_oEnableCheckboxEl)
            {
               var configsEl = Dom.get(this.id + "-configs");
               if (p_oEnableCheckboxEl.checked)
               {
                  Dom.removeClass(configsEl, "hidden");
                  this._toggleDisableOnElements(Selector.query("[param]", configsEl), false);
                  this._toggleDisableOnElements(Selector.query("select.config-name", configsEl), false);
               }
               else
               {
                  Dom.addClass(configsEl, "hidden");
                  this._toggleDisableOnElements(Selector.query("[param]", configsEl), true);
                  this._toggleDisableOnElements(Selector.query("select.config-name", configsEl), true);
               }
               this._updateSubmitElements();
            }, enableCheckboxEl, this);
         }

         // Relation menus
         var relationButtonEl = Dom.get(this.id + "-" + this.options.ruleConfigType + "-menubutton");
         if (relationButtonEl)
         {
            this.widgets.relationButton = new YAHOO.widget.Button(relationButtonEl,
            {
               type: "menu",
               menu: this.id + "-" + this.options.configName + "-menubuttonselect",
               menualignment: ["tr", "br"]
            });
         }

         // Save reference to config template
         this.widgets.configTemplateEl = Dom.get(this.id + "-configTemplate");

         // Create select menu template that will be used for each config
         this.widgets.selectTemplateEl = this._createSelectMenu();

         // Tell other components that this component is ready
         YAHOO.Bubbling.fire("ruleConfigReady",
         {
            eventGroup: this
         });
      },

      /**
       * Returns a list of all the current ruleConfigs as an array of ruleConfig objects.
       *
       * I.e
       * [
       *    {
       *       "<the value of this.options.ruleConfigDefinitionKey>": "<the selected option value in the select menu>",
       *       "parameterValues":
       *       {
       *          "paramName1": "paramValue1",
       *          "paramName2": "paramValue2"
       *       }
       *    }
       * ]
       *
       * @method getRuleConfigs
       * @return {array} An array of ruleConfig objects
       */
      getRuleConfigs: function RuleConfig_getRuleConfigs()
      {
         // Empty result
         var configs = [];

         if (!Dom.hasClass(this.id + "-configs", "hidden"))
         {
            // Add configs
            var configEls = Selector.query('li.config', this.id + "-body"),
                  configEl,
                  configDef,
                  config;
            for (var ci = 0, cil = configEls.length; ci < cil; ci++)
            {
               configEl = configEls[ci];

               // Find config & name
               configDef = this._getSelectedConfigDef(configEl);
               if (configDef)
               {
                  config = {};
                  config[this.options.ruleConfigDefinitionKey] = configDef.name;
                  config.parameterValues = this._getParameters(configDef);
                  configs.push(config);
               }

            }
         }
         return configs;
      },

      /**
       * Displays ruleConfig rows as described in ruelConfigs.
       *
       * Expects the following format of the ruleConfig array:
       * [
       *    {
       *       "<match the value of this.options.ruleConfigDefinitionKey>": "<the ruleConfig name to select in the select menu>",
       *       "parameterValues":
       *       {
       *          "paramName1": "paramValue1",
       *          "paramName2": "paramValue2"
       *       }
       *    }
       * ]
       *
       * Note! Before this method is called the config body will be empty.
       * This method shall be called after the "ruleConfigReady" event has been fired.
       *
       * @method displayRuleConfigs
       * @param ruleConfigs {array} An array of rule configurations
       */
      displayRuleConfigs: function RuleConfig_displayRulConfigs(ruleConfigs)
      {
         Dom.get(this.id + "-configs").innerHTML = "";
         if (!ruleConfigs || ruleConfigs.length == 0)
         {
            ruleConfigs = [ {} ];
         }
         var ruleConfig,
            configEl;
         for (var i = 0, il = ruleConfigs.length; i < il; i++)
         {
            ruleConfig = ruleConfigs[i];
            configEl = this._createConfigUI(ruleConfig, this.widgets.selectTemplateEl.cloneNode(true), null);
            this._createConfigParameterUI(ruleConfig, configEl);
         }
         this._refreshRemoveButtonState();
      },

      
      /**
       * EVENT HANDLERS
       */

      /**
       * Called from the "+" link to create another value for multi valued parameter
       *
       * @method onAddMoreParameterIconClick
       * @param p_oEvent {object} The click event
       * @param p_oParameterCtx {object} References to paramDef, configDef, ruleConfig
       */
      onAddExtraParameterIconClick: function RuleConfig_onAddMoreParameterIconClick(p_oEvent, p_oParameterCtx)
      {
         // Create a container for the extra parameter
         var extraparamEl = document.createElement("span"),
            paramDef = p_oParameterCtx.paramDef;
         // Add new parameter container to the left of the add button
         p_oParameterCtx.addButton.parentNode.insertBefore(extraparamEl, p_oParameterCtx.addButton);

         // Add another parameter ui control
         var ruleConfig = p_oParameterCtx.ruleConfig,
            value = ruleConfig.parameterValues ? ruleConfig.parameterValues[paramDef.name] : null;
         var el = p_oParameterCtx.paramRenderer.fn.call(this, extraparamEl, paramDef, p_oParameterCtx.configDef, value, ruleConfig);
         Dom.addClass(el, "param");

         // Add a delete button for the new parameter control
         var deleteButton = document.createElement("span");
         deleteButton.setAttribute("title", this.msg("button.deleteExtraParameter", paramDef.displayLabel ? paramDef.displayLabel: paramDef.name));
         Dom.addClass(deleteButton, "delete-extra-parameter-button");
         deleteButton.innerHTML = "-";
         Dom.setStyle(deleteButton, "width", "10px");
         Dom.setStyle(deleteButton, "height", "10px");
         Event.addListener(deleteButton, "click", this.onDeleteExtraParameterIconClick, extraparamEl, this);
         extraparamEl.appendChild(deleteButton);

         this._updateSubmitElements(p_oParameterCtx.configDef);
      },

      /**
       * Called from the "-" link to remove a value for multi valued parameter
       *
       * @method onDeleteExtraParameterIconClick
       * @param p_oEvent {object} The click event
       * @param p_oExtraparamEl {HTMLElement}
       */
      onDeleteExtraParameterIconClick: function RuleConfig_onDeleteExtraParameterIconClick(p_oEvent, p_oExtraparamEl)
      {
         p_oExtraparamEl.parentNode.removeChild(p_oExtraparamEl);
      },

      /**
       * Called when the user selects an option int the ruleConfig select menu
       *
       * @method onConfigNameSelectChange
       * @param p_oEvent {object} The change event
       * @param configEl {HTMLElement} Contains the ruleConfig and configEl objects
       */
      onConfigNameSelectChange: function RuleConfig_onConfigNameSelectChange(p_oEvent, configEl)
      {
         this._createConfigParameterUI({}, configEl);
      },

      /**
       * Called when the user clicks on an "+"/add rule config button
       *
       * @method onAddConfigButtonClick
       * @param p_oEvent {object} The click event
       * @param p_eConfig {HTMLDivElement} the config element the button belongs to
       */
      onAddConfigButtonClick: function RuleConfig_onAddConfigButtonClick(p_oEvent, p_eConfig)
      {
         var configEl = this._createConfigUI({}, this.widgets.selectTemplateEl.cloneNode(true), p_eConfig);
         this._createConfigParameterUI({}, configEl);
         this._refreshRemoveButtonState();
      },

      /**
       * Called when the user clicks on an "-"/remove rule config button
       *
       * @method onRemoveConfigButtonClick
       * @param p_oEvent {object} The click event
       * @param p_eConfig {HTMLDivElement} the config element the button belongs to
       */
      onRemoveConfigButtonClick: function RuleConfig_onRemoveConfigButtonClick(p_oEvent, p_eConfig)
      {
         p_eConfig.parentNode.removeChild(p_eConfig);
         this._refreshRemoveButtonState();
      },

      /**
       * Called when a date has been selected from a date picker.
       * Will cause the forms validation to run.
       *
       * @method onDatePickerMandatoryControlValueUpdated
       * @param layer
       * @param args
       */
      onDatePickerMandatoryControlValueUpdated: function RuleConfig_onDatePickerMandatoryControlValueUpdated(layer, args)
      {
         var configDef = this._datePickerConfigDefMap[args[1].id];
         if (configDef)
         {
            this._updateSubmitElements(configDef);
         }
      },

      
      /**
       * PRIVATE OR PROTECTED METHODS
       */

      /**
       * Will set the disabled attribute to the value of "disabled" for the elements in p_aEls
       *
       * @method _toggleDisableOnElements
       * @param p_aEls {array} An array of HTMLElements
       * @param p_bDisable {boolean} True if elements shall be disabled
       * @private
       */
      _toggleDisableOnElements: function RuleConfig__toggleDisableOnElements(p_aEls, p_bDisable)
      {
         for (var i = 0, il = p_aEls.length; i < il; i ++)
         {
            if (p_bDisable)
            {
               p_aEls[i].setAttribute("disabled", true);
            }
            else
            {
               p_aEls[i].removeAttribute("disabled");
            }
         }
      },

      /**
       * Called when all data that is needed for the menu has been loaded.
       *
       * Will walk through the this.options.menuMap descriptor twice to create the menu, menu groups and items by
       * inserting the configDefs from this.options.ruleConfigDefinitions into a select-menu and its opt-groups.
       *
       * 1. First pass will ask for objects using only pattern objects WITHOUT a wildcard attribute.
       * 2. Seconds pass will ask for objects using only pattern objects WITH at least one wild card attribute.
       *
       * @method _createSelectMenu
       * @return {HTMLSelectElement} The created menu
       * @private
       */
      _createSelectMenu: function RuleConfig__createSelectMenu()
      {
         // Used to see if a menu item already has been added
         var alreadyAdded = {};

         // Create menu items & groups from the menu options (make sure to make a copy so we can alter it)
         var menuMapOpt = Alfresco.util.deepCopy(this.options.menuMap),
            groupOpt,
            itemOpt,
            itemPatternOpt,
            hasWildcard,
            menuItems,
            menuItem,
            menuItemKey;

         // Make 2 passes though the options, first collect all exact matches then the ones matching wildcards
         for (var pass = 1; pass <=2; pass++)
         {
            for (var gi = 0, gil = menuMapOpt.length; gi < gil; gi++)
            {
               groupOpt = menuMapOpt[gi];
               for (var gii = 0, giil = groupOpt.length; gii < giil; gii++)
               {
                  itemOpt = groupOpt[gii];
                  for (var itemTypeOpt in itemOpt)
                  {
                     if (itemOpt.hasOwnProperty(itemTypeOpt))
                     {
                        itemPatternOpt = itemOpt[itemTypeOpt];
                        hasWildcard = false;
                        for (var itemPatternAttributeOpt in itemPatternOpt)
                        {
                           if (itemTypeOpt != "_menuItems" &&
                               itemPatternOpt.hasOwnProperty(itemPatternAttributeOpt) &&
                               itemPatternOpt[itemPatternAttributeOpt].indexOf("*") > -1)
                           {
                              hasWildcard = true;
                              break;
                           }
                        }

                        if ((pass == 1 && !hasWildcard) || (pass == 2 && hasWildcard))
                        {
                           // Add internal variable for storing the real menu items                           
                           itemOpt["_menuItems"] = [];
                           menuItems = this._getConfigItems(itemTypeOpt, itemPatternOpt);
                           for (var mii = 0, mil = menuItems.length; mii < mil; mii++)
                           {
                              menuItem = menuItems[mii];
                              menuItemKey = menuItem.type + "_" + menuItem.id;
                              if (!alreadyAdded[menuItemKey])
                              {
                                 // Add item if it hasn't already been used
                                 itemOpt["_menuItems"].push(menuItem);
                                 alreadyAdded[menuItemKey] = true;
                              }
                              if (!this._configDefs[menuItemKey])
                              {
                                 // Save descriptor so we can look it up when menu changes
                                 this._configDefs[menuItemKey] = menuItem.descriptor;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         // Create a select element that later will be cloned so it can be used individually use in the configs
         var selectEl = document.createElement("select"),
            optGroupEl,
            optionEl;
         selectEl.setAttribute("name", "-");
         Dom.addClass(selectEl, "config-name");
         for (gi = 0, gil = menuMapOpt.length; gi < gil; gi++)
         {
            groupOpt = menuMapOpt[gi];
            optGroupEl = null;
            for (gii = 0, giil = groupOpt.length; gii < giil; gii++)
            {
               itemOpt = groupOpt[gii];
               menuItems = itemOpt["_menuItems"];
               for (mii = 0, miil = menuItems.length; mii < miil; mii++)
               {
                  if (!optGroupEl)
                  {
                     // The optGroup shall only be created if there was at least 1 option
                     optGroupEl = document.createElement("optgroup");
                     selectEl.appendChild(optGroupEl);
                  }
                  menuItem = menuItems[mii];
                  optionEl = document.createElement("option");
                  optionEl.setAttribute("value", menuItem.id);
                  optionEl.appendChild(document.createTextNode(menuItem.label));
                  optionEl.setAttribute("rel", menuItem.type);
                  optGroupEl.appendChild(optionEl);
               }
            }
         }
         return selectEl;
      },

      /**
       * Called from _createSelectMenu to get the configDef for a menu item.
       *
       * @method _getConfigItems
       * @param itemType
       * @param itemPatternObject
       * @return {array} Menu item objects (as described below) representing a configDef (or item)
       *                 matching all attributes in itemPatternObject.
       * @protected
       * {
       *    id: string,
       *    label: string,
       *    descriptor: object
       * }
       */
      _getConfigItems: function RuleConfig__getConfigItems(itemType, itemPatternObject)
      {
         var results = [],
            ruleConfigDef;
         if (itemType == this.options.ruleConfigType)
         {
            for (var ci = 0, cil = this.options.ruleConfigDefinitions.length; ci < cil; ci++)
            {
               ruleConfigDef = this.options.ruleConfigDefinitions[ci];
               if (Alfresco.util.objectMatchesPattern(ruleConfigDef, itemPatternObject))
               {
                  results.push(
                  {
                     id: ruleConfigDef.name,
                     type: this.options.ruleConfigType,
                     label: ruleConfigDef.displayLabel,
                     descriptor: ruleConfigDef
                  });
               }
            }
         }
         else if (itemType == "item")
         {
            results.push(
            {
               id: itemPatternObject.id,
               type: "item",
               label: this.msg("menu.item." + itemPatternObject.id),
               descriptor: itemPatternObject
            });
         }
         return results;
      },

      /**
       * Called to get the constraint options for a constraint.
       *
       * @method _getConstraintValues
       * @param p_sConstraintName
       * @param p_oRuleConfig
       * @return {array} rule constraint values
       */
      _getConstraintValues: function RuleConfig__getConstraintValues(p_sConstraintName, p_oRuleConfig)
      {
         return this.options.constraints[p_sConstraintName];
      },
      
      /**
       * Creates a config row with the parameters and the parameter values
       *
       * @method _createConfigUI
       * @param p_oRuleConfig {object} Rule config descriptor object
       * @param p_oSelectEl {HTMLSelectElement} The select menu to use
       * @param p_eRelativeConfigEl {object}
       * @protected
       */
      _createConfigUI: function RuleConfig__createConfigUI(p_oRuleConfig, p_oSelectEl, p_eRelativeConfigEl)
      {
         if (p_oSelectEl.length > 0)
         {
            // Add config element
            var configEl = this.widgets.configTemplateEl.cloneNode(true);
            Alfresco.util.generateDomId(configEl);
            if (p_eRelativeConfigEl)
            {
               p_eRelativeConfigEl.parentNode.insertBefore(configEl, p_eRelativeConfigEl.nextSibling);
            }
            else
            {
               Dom.get(this.id + "-configs").appendChild(configEl);
            }

            // Add config name/type drop down
            if (!p_oSelectEl.getAttribute("id"))
            {
               Alfresco.util.generateDomId(p_oSelectEl);
            }
            Event.addListener(p_oSelectEl, "change", this.onConfigNameSelectChange, configEl, this);
            Selector.query('div.name', configEl)[0].appendChild(p_oSelectEl);

            // Set values
            Selector.query('input[name=id]', configEl)[0].value = p_oRuleConfig.id ? p_oRuleConfig.id : "";
            Alfresco.util.setSelectedIndex(p_oSelectEl, p_oRuleConfig[this.options.ruleConfigDefinitionKey]);

            // Create add button
            var actionsEl = Selector.query('div.actions', configEl)[0];
            var addButton = new YAHOO.widget.Button(
            {
               label: "+",
               container: actionsEl,
               type: "push"
            });
            addButton.on("click", this.onAddConfigButtonClick, configEl, this);
            addButton.addClass("add-config");

            // Create remove button
            var removeButton = new YAHOO.widget.Button(
            {
               label: "-",
               container: actionsEl,
               type: "push"
            });
            removeButton.on("click", this.onRemoveConfigButtonClick, configEl, this);
            removeButton.addClass("remove-config");

            // Return element
            return configEl;
         }
      },

      /**
       *
       *
       * @method _createConfigParameterUI
       * @param p_oRuleConfig {object} Rule config descriptor object
       * @param configEl {HTMLLIElement} Rule config descriptor object
       */
      _createConfigParameterUI: function RuleConfig__createConfigParameterUI(p_oRuleConfig, configEl)
      {
         // Remove old ui
         configEl.removeAttribute("id");
         var paramsEl = Selector.query('div.parameters', configEl)[0];

         var configDef = this._getSelectedConfigDef(configEl);
         if (configDef)
         {
            /**
             * Create a copy of the configDef and give it a unique id that also will
             * be applied to the configEl as well to assist ui management
             */
            configDef = Alfresco.util.deepCopy(configDef);
            configDef._id = Alfresco.util.generateDomId(configEl);

            // Find the correct customisation renderer
            var selectEl = Selector.query('select', configEl)[0],
               optionEl = selectEl.options[selectEl.selectedIndex],
               configCustomisation = this._getConfigCustomisation(optionEl.getAttribute("rel"), configDef);

            if (!configCustomisation || !configCustomisation.manual)
            {
               // If Customisations is manual it wants to handle cleanup by itself (or leave the old parameters)
               paramsEl.innerHTML = "";
            }
            if (configCustomisation)
            {
               configDef = configCustomisation.fn.call(this, configDef, p_oRuleConfig, configEl, paramsEl);
            }

            // Render new parameter ui if any
            if (configDef && configDef.parameterDefinitions)
            {
               var paramDef,
                  paramRenderer,
                  value;
               for (var i = 0, il = configDef.parameterDefinitions.length; i < il; i++)
               {
                  paramDef = configDef.parameterDefinitions[i];
                  paramRenderer = this._getParamRenderer(paramDef.type);
                  value = p_oRuleConfig.parameterValues ? p_oRuleConfig.parameterValues[paramDef.name] : null;
                  if (!paramRenderer)
                  {
                     // There is no renderer for the parameter type
                     var errorSpan = document.createElement("span");
                     Dom.addClass(errorSpan, "error");
                     errorSpan.innerHTML = this.msg("label.noRendererForType", paramDef.type);
                     paramsEl.appendChild(errorSpan);
                     continue;
                  }
                  // Create element for parameter
                  var paramEl = document.createElement("span");
                  Dom.addClass(paramEl, "menutype_" + optionEl.getAttribute("rel"));
                  Dom.addClass(paramEl, "menuname_" + optionEl.value);
                  Dom.addClass(paramEl, "paramtype_" + paramDef.type.replace(":", "_"));
                  Dom.addClass(paramEl, "paramname_" + paramDef.name);
                  paramsEl.appendChild(paramEl);

                  if (paramRenderer.manual)
                  {
                     // Renderer wants to implement the "contraint":s- and "multiValued"-support for the parameter
                     paramRenderer.fn.call(this, paramEl, paramDef, configDef, value, p_oRuleConfig);
                  }
                  else
                  {
                     var controlEl;
                     if (paramDef.constraint)
                     {
                        /**
                         * Implement support for the "constraint" by using a select element
                         * that will be multi-valued depending on the paramDef.
                         */
                        var constraintOptions = this._getConstraintValues(paramDef.constraint, p_oRuleConfig);
                        controlEl = this._createSelect(paramEl, configDef, paramDef, constraintOptions, value);
                     }
                     else
                     {
                        // Render the type specific ui control
                        controlEl = paramRenderer.fn.call(this, paramEl, paramDef, configDef, value, p_oRuleConfig);
                        if (paramDef.isMultiValued && paramDef._type != "hidden")
                        {
                           var addButton = document.createElement("span");
                           addButton.setAttribute("title", this.msg("button.addExtraParameter", paramDef.displayLabel ? paramDef.displayLabel: paramDef.name));
                           Dom.addClass(addButton, "add-extra-parameter-button");
                           addButton.innerHTML = "+";
                           Dom.setStyle(addButton, "width", "10px");
                           Dom.setStyle(addButton, "height", "10px");
                           paramEl.appendChild(addButton);
                           Event.addListener(addButton, "click", this.onAddExtraParameterIconClick,
                           {
                              paramRenderer: paramRenderer,
                              paramDef: paramDef,
                              configDef: configDef,
                              ruleConfig: p_oRuleConfig,
                              addButton: addButton
                           }, this);
                        }
                     }
                     Dom.addClass(controlEl, "param");
                     if (paramDef._type != "hidden")
                     {
                        // Display a label left to the parameter if displayLabel is present
                        this._createLabel(paramDef.displayLabel, controlEl);
                     }
                  }
               }
            }
         }

         // Make sure form is re-validated
         this._updateSubmitElements(configDef);
      },

      /**
       * Looks up the configDef depending on the selection in the select menu
       * and makes a deepCopy of the object and sets the internal "_id" from
       * the configEl.
       *
       * @method _getSelectedConfigDef
       * @param configEl
       */
      _getSelectedConfigDef: function (configEl)
      {
         // Find the correct config definition by looking in the config type menu
         var selectEl = Selector.query('select', configEl)[0];
         if (selectEl.selectedIndex > -1)
         {
            var optionEl = selectEl.options[selectEl.selectedIndex];
            var configDef = this._configDefs[optionEl.getAttribute("rel") + "_" + optionEl.value];
            if (configDef)
            {
               configDef._id = configEl.getAttribute("id");
               return configDef;
            }
         }
         return null;
      },

      /**
       * @method getConfigCustomisation
       * @param itemType
       * @param configDef
       * @return {object} A RuleConfig parameter renderer
       * @protected
       */
      _getConfigCustomisation: function RuleConfig__getConfigCustomisation(itemType, configDef)
      {
         var customisationOpt,
            customisationOptValues,
            customisationOptPattern,
            hasWildcard;

         // Make 2 passes though the options, first collect all exact matches then the ones matching wildcards
         for (var pass = 1; pass <= 2; pass++)
         {
            for (var gi = 0, gil = this.options.customisationsMap.length; gi < gil; gi++)
            {
               customisationOpt = this.options.customisationsMap[gi];
               if (customisationOpt.hasOwnProperty(itemType))
               {
                  customisationOptValues = customisationOpt[itemType];
                  if (YAHOO.lang.isArray(customisationOptValues))
                  {
                     customisationOptPattern = customisationOptValues[0];
                     hasWildcard = this._hasWildcard(customisationOptValues);
                     if ((pass == 1 && !hasWildcard) || (pass == 2 && hasWildcard))
                     {
                        if (Alfresco.util.objectMatchesPattern(configDef, customisationOptPattern))
                        {
                           return this.customisations[customisationOptValues[1]];
                        }
                     }
                  }
               }
            }
         }
         return null;
      },

      /**
       * @method _getParamRenderer
       * @param paramDefType
       * @return {object} A RuleCOnfig parameter renderer
       */
      _getParamRenderer: function RuleConfig__getParamRenderer(paramDefType)
      {
         return this.renderers[paramDefType];
      },
      
      /**
       * @method _refreshRemoveButtonState
       */
      _refreshRemoveButtonState: function RuleConfig__refreshRemoveButtonState()
      {
         var configsEl = Dom.get(this.id + "-configs");
         if (Selector.query("li", configsEl).length > 1)
         {
            Dom.removeClass(configsEl, "single");
         }
         else
         {
            Dom.addClass(configsEl, "single");
         }
      },

      /**
       * Returns true if
       *
       * @method _hasWildcard
       * @param obj {object} The click event
       * @return {boolean} true if obj contains an attribute with "*" as the value
       */
      _hasWildcard: function RuleConfig__hasWildcard(obj)
      {
         for (var attr in obj)
         {
            if (obj.hasOwnProperty(attr) && obj[attr] == "*")
            {
               return true;
            }
         }
         return false;
      },

      /**
       * CUSTOMISATIONS
       */

      customisations: {

      },

      /**
       * RENDERERS
       */

      renderers:
      {
         "d:any":
         {
            fn:function (containerEl, paramDef, configDef, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:text":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:mltext":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:content":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:int":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               // todo limit validator to int's limit
               return this._createInputText(containerEl, configDef, paramDef, [Alfresco.forms.validation.number], value);
            }
         },

         "d:long":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               // todo limit validator to long's limit
               return this._createInputText(containerEl, configDef, paramDef, [Alfresco.forms.validation.number], value);
            }
         },

         "d:float":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               // todo add float validator
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:double":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               // todo add double validator
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:date":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               return this._createDatePicker(containerEl, configDef, paramDef, [], value, false);
            }
         },

         "d:datetime":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               return this._createDatePicker(containerEl, configDef, paramDef, [], value, true);
            }
         },

         "d:boolean":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               this._createSelect(containerEl, configDef, paramDef, [
                  {
                     value: "true",
                     displayLabel: this.msg("label.yes")
                  },
                  {
                     value: "false",
                     displayLabel: this.msg("label.no")
                  }
               ], value)
            }
         },

         "d:qname":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:noderef":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [Alfresco.forms.validation.nodeRef], value);
            }
         },

         "d:path":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:category":
         {
            manual: true,
            fn: function (containerEl, paramDef, configDef, value)
            {
               // todo display category picker
            }
         },

         "d:locale":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         },

         "d:version":
         {
            fn: function (containerEl, paramDef, configDef, value)
            {
               return this._createInputText(containerEl, configDef, paramDef, [], value);
            }
         }
      },

      
      /**
       * RENDERER HELPERS
       */

      _createInputText: function (containerEl, configDef, paramDef, validators, value)
      {
         if (paramDef._type == "hidden")
         {
            return this._createInputOfType(containerEl, configDef,paramDef, validators, value, "hidden");
         }
         else
         {
            return this._createInputOfType(containerEl, configDef,paramDef, validators, value, "text");
         }
      },

      _createInputOfType: function (containerEl, configDef, paramDef, validators, value, type)
      {
         var el = document.createElement("input");
         el.setAttribute("type", type);
         el.setAttribute("name", "-");
         el.setAttribute("title", paramDef.displayLabel ? paramDef.displayLabel : paramDef.name);
         el.setAttribute("param", paramDef.name);
         el.setAttribute("value", value ? value : "");
         containerEl.appendChild(el);
         if (paramDef.isMandatory)
         {
            this._addValidation(el, Alfresco.forms.validation.mandatory, configDef);
         }
         for (var i = 0, il = validators ? validators.length : 0; i < il; i++)
         {
            this._addValidation(el, validators[i], configDef);
         }
         return el;
      },

      _createLabel: function (text, forEl)
      {
         if (text && forEl)
         {
            var id = forEl.getAttribute("id") ? forEl.getAttribute("id") : Alfresco.util.generateDomId(forEl),
                  labelEl = document.createElement("label");
            labelEl.setAttribute("for", id);
            labelEl.appendChild(document.createTextNode(text + ":"));
            forEl.parentNode.insertBefore(labelEl, forEl);
            return labelEl;
         }
      },
      
      _createSelect: function (containerEl, configDef, paramDef, constraintOptions, value)
      {
         if (paramDef._type == "hidden")
         {
            return this._createInputOfType(containerEl, configDef, paramDef, [], value, "hidden");
         }
         else
         {
            var selectEl = document.createElement("select");
            selectEl.setAttribute("name", "-");
            selectEl.setAttribute("title", paramDef.displayLabel ? paramDef.displayLabel : paramDef.name);
            selectEl.setAttribute("param", paramDef.name);
            if (paramDef.isMultiValued)
            {
               selectEl.setAttribute("multiple", "true");
               selectEl.setAttribute("size", "3");
            }
            if (containerEl)
            {
               containerEl.appendChild(selectEl);
            }
            if (!paramDef.isMandatory)
            {
               selectEl.appendChild(document.createElement("option"));
            }
            if (constraintOptions)
            {
               var constraintOption,
                     optionEl;
               for (var i = 0, l = constraintOptions.length; i < l; i++)
               {
                  constraintOption = constraintOptions[i];
                  optionEl = document.createElement("option");
                  optionEl.setAttribute("value", constraintOption.value);
                  optionEl.appendChild(document.createTextNode(constraintOption.displayLabel));
                  if (constraintOption.value == value)
                  {
                     optionEl.setAttribute("selected", "true");
                  }
                  selectEl.appendChild(optionEl);
               }
            }
            return selectEl;
         }
      },

      _createDatePicker: function (containerEl, configDef, paramDef, constraintOptions, value, showTime)
      {
         if (paramDef._type == "hidden")
         {
            // todo Add in custom validator that checks value against date pattern
            return this._createInputOfType(containerEl, configDef, paramDef, [], "hidden");
         }
         else
         {
            var valueEl = this._createInputOfType(containerEl, configDef, paramDef, [], value, "hidden"),
                  valueId = valueEl.getAttribute("id") ? valueEl.getAttribute("id") : Alfresco.util.generateDomId(valueEl);
            containerEl.appendChild(valueEl);

            var datePickerParentEl = document.createElement("span");
            Dom.setStyle(datePickerParentEl, "position", "relative");
            var datePickerEl = document.createElement("div"),
                  datePickerId = Alfresco.util.generateDomId(datePickerEl);
            Dom.addClass(datePickerEl, "datepicker");

            var datePickerIconEl = document.createElement("a");
            Alfresco.util.setDomId(datePickerIconEl, datePickerId + "-icon");
            var datePickerImgEl = document.createElement("img");
            datePickerImgEl.setAttribute("src", Alfresco.constants.URL_CONTEXT + "components/form/images/calendar.png");
            Dom.addClass(datePickerImgEl, "datepicker-icon");
            datePickerIconEl.appendChild(datePickerImgEl);

            var displayDateEl = document.createElement("input");
            displayDateEl.setAttribute("name", "-");
            displayDateEl.setAttribute("title", paramDef.displayLabel ? paramDef.displayLabel : paramDef.name);
            displayDateEl.setAttribute("type", "text");
            Dom.addClass(displayDateEl, "datepicker-date");
            Alfresco.util.setDomId(displayDateEl, datePickerId + "-date");
            containerEl.appendChild(displayDateEl);

            if (showTime)
            {
               var displayTimeEl = document.createElement("input");
               displayTimeEl.setAttribute("name", "-");
               displayTimeEl.setAttribute("type", "text");
               Dom.addClass(displayTimeEl, "datepicker-time");
               Alfresco.util.setDomId(displayTimeEl, datePickerId + "-time");            
               containerEl.appendChild(displayTimeEl);
            }

            containerEl.appendChild(datePickerIconEl);
            containerEl.appendChild(datePickerParentEl);
            datePickerParentEl.appendChild(datePickerEl);
            var options = {
               showTime: showTime,
               mandatory: paramDef.isMandatory,
               currentValue: (value && value != "") ? value : null 
            };
            var datePicker = new Alfresco.DatePicker(datePickerId, valueId).setOptions(options).setMessages(
            {
               "form.control.date-picker.choose": "",
               "form.control.date-picker.entry.date.format": this.msg("form.control.date-picker.entry.date.format"),
               "form.control.date-picker.display.date.format": this.msg("form.control.date-picker.display.date.format"),
               "form.control.date-picker.entry.time.format": this.msg("form.control.date-picker.entry.time.format"),
               "form.control.date-picker.display.time.format": this.msg("form.control.date-picker.display.time.format")
            });
            this._datePickerConfigDefMap[datePicker.id] = configDef;
            return valueEl;
         }
      },
      
      _createButton: function (containerEl, paramDef, configDef, ruleConfig, onClickHandler)
      {
         var buttonEl = document.createElement("button");
         Alfresco.util.generateDomId(buttonEl);
         containerEl.appendChild(buttonEl);
         var button = new YAHOO.widget.Button(buttonEl,
         {
            type: "button",
            label: paramDef._buttonLabel
         });
         button.on("click", onClickHandler, {
            configDef: configDef,
            ruleConfig: ruleConfig,
            paramDef: paramDef,
            containerEl: containerEl
         }, this);
         return button;
      },

      /**
       * Populate a folder path from a nodeRef.
       *
       * @method _createPathSpan
       * @param containerEl {HTMLelement} Element within which the new span tag will be created
       * @param id {string} Dom ID to be given to span tag
       * @param nodeRef {string} NodeRef of folder
       */
      _createPathSpan: function (containerEl, id, nodeRef)
      {
         var pathEl = document.createElement("span");
         Dom.setStyle(pathEl, "margin", "0 0.5em");
         Alfresco.util.setDomId(pathEl, id);
         if (nodeRef)
         {
            pathEl.innerHTML = this.msg("message.loading");
            // Find the path for the nodeRef
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.PROXY_URI + "api/forms/picker/items",
               dataObj:
               {
                  items: [nodeRef]
               },
               successCallback:
               {
                  fn: function (response, pathEl)
                  {
                     if (response.json !== undefined)
                     {
                        var folderDetails = response.json.data.items[0],
                           path = $combine(folderDetails.displayPath, folderDetails.name);
                        pathEl.innerHTML = $html(path);                        
                     }
                  },
                  obj: pathEl,
                  scope: this
               },
               failureCallback:
               {
                  fn: function (response, obj)
                  {
                     alert('failure');
                  },
                  obj: pathEl,
                  scope: this
               }
            });
         }
         containerEl.appendChild(pathEl);
         return pathEl;
      },
      
      _addValidation: function (el, validator, configDef)
      {
         if (el && validator && this.options.form)
         {
            var id = el.getAttribute("id") ? el.getAttribute("id") : Alfresco.util.generateDomId(el),
               validationArgs = {
                  configDef: configDef,
                  me: this,
                  handler: validator
               };

            // Add validator to forms runtime
            this.options.form.addValidation(id, function (field, args, event, form, silent, message)
            {
               var valid = args.handler(field, args, event, form, silent, message);
               if (!valid)
               {
                  YAHOO.util.Dom.addClass(args.configDef._id, "invalid");
               }
               else if (valid && YAHOO.util.Dom.hasClass(args.configDef._id, "invalid"))
               {
                  // Make sure all other fields are valid as well before we display it as valid
                  if (args.me._validateConfigDef(args.configDef))
                  {
                     YAHOO.util.Dom.removeClass(args.configDef._id, "invalid");
                  }
               }
               return valid;
            }, validationArgs, "keyup");

            /**
             * ...but also group together validator with other validators for the same config
             * so we can run all of them to decide if the config as a whole is valid or not.
             */
            if (!configDef._validations)
            {
               configDef._validations = [];
            }
            configDef._validations.push(
            {
               fieldId: id,
               args: validationArgs,
               handler: validator
            });
         }
      },

      /**
       * Will update the forms submit elements
       *
       * @mehtod _updateSubmitElements
       * @param configDef {object} (Optional) Will validate this configDef before updating submit elements
       * @protected
       */
      _updateSubmitElements: function (configDef)
      {
         if (this.options.form)
         {
            if (configDef)
            {
               if (this._validateConfigDef(configDef))
               {
                  Dom.removeClass(configDef._id, "invalid");
               }
               else
               {
                  Dom.addClass(configDef._id, "invalid");
               }
            }
            this.options.form.updateSubmitElements();
         }
      },

      _validateConfigDef: function (configDef)
      {
         if (YAHOO.lang.isArray(configDef._validations))
         {
            for (var i = 0, il = configDef._validations.length, validation; i < il; i++)
            {
               validation = configDef._validations[i];
               var el = Dom.get(validation.fieldId);
               if (!el.disabled && !validation.handler(el, validation.args, "keyup", this, true, null))
               {
                  return false;
               }
            }
         }
         return true;
      },

      _getParameters: function (configDef)
      {
         var configEl = Dom.get(configDef._id),
            paramEls = Selector.query("[param]", configEl),
            params = {},
            paramEl,
            paramName,
            paramValue,
            paramDef,
            previousValue;
         for (var i = 0, il = paramEls.length; i < il; i++)
         {
            paramEl = paramEls[i];
            paramName = paramEl.getAttribute("param");
            paramValue = this._getValue(paramEl);
            paramDef = this._getParamDef(this._getSelectedConfigDef(configEl), paramName);
            if (paramDef && paramDef.isMultiValued && paramValue && !YAHOO.lang.isArray(paramValue))
            {
               paramValue = [paramValue];
            }
            previousValue = params[paramName];
            if (YAHOO.lang.isArray(previousValue))
            {
               if (YAHOO.lang.isArray(paramValue))
               {
                  paramValue = previousValue.concat(paramValue);
               }
               else if (paramValue != null)
               {
                  paramValue = previousValue.push(paramValue);
               }
            }
            if (paramName && paramValue)
            {
               params[paramName] = paramValue;
            }
         }
         return params;
      },

      _getValue: function(el)
      {
         var tagName = el.tagName.toLowerCase();
         if (tagName == "select")
         {
            if (el.getAttribute("multiple"))
            {
               return el.options[el.selectedIndex].value;
            }
            else
            {
               var values = [];
               for (var i = 0, il = el.options.length; i < il; i++)
               {
                  if (el.options[i].selected)
                  {
                     values.push(el.options[i].value);
                  }
               }
               return values;
            }
         }
         else if (tagName == "checkbox" || tagName == "radio")
         {
            if (el.value)
            {
               return el.checked ? el.value : null;
            }
            else
            {
               return el.checked ? true : false;
            }
         }
         return el.value && el.value.length > 0 ? el.value : null;
      },

      _getParamDef: function (configDef, paramName)
      {
         if (configDef.parameterDefinitions)
         {
            for (var i = 0, il = configDef.parameterDefinitions.length, paramDefinition; i < il; i++)
            {
               paramDefinition = configDef.parameterDefinitions[i];
               if (paramDefinition.name == paramName)
               {
                  return paramDefinition;
               }
            }
         }
         return null;
      },

      _setHiddenParameter: function (configDef, ruleConfig, paramName, paramValue)
      {
         var paramEls = Selector.query("[param=" + paramName + "]", configDef._id),
            paramDef = this._getParamDef(configDef, paramName);
         if (paramDef.isMultiValued && YAHOO.lang.isArray(paramValue))
         {
            // Remove previous hidden input elements that won't be needed ...
            for (var phei = paramEls.length, nhel = paramValue.length, phe; phei > nhel; i--)
            {
               // Remove the previous hidden element from array and Dom
               phe = paramEls.pop();
               phe.parentNode.removeChild(phe);
            }
            // Set values (and create new input elements if needed)
            for (var pvi = 0, pvil = paramValue.length, paramEl, peil = paramEls.length; pvi < pvil; pvi++)
            {
               if (pvi >= peil)
               {
                  paramEl = this._getParamRenderer(paramDef.type).fn.call(this, paramEls[0].parentNode, paramDef, configDef, paramValue[pvi], ruleConfig);
                  Dom.addClass(paramEl, "param");
               }
               else
               {
                  paramEls[pvi].value = paramValue[pvi];
               }
            }
         }
         else
         {
            paramEls[0].value = paramValue;
         }
      },

      _hideParameters: function (parameterDefinitions)
      {
         if (parameterDefinitions)
         {
            for (var i = 0, il = parameterDefinitions.length, paramDef; i < il; i++)
            {
               paramDef = parameterDefinitions[i];
               paramDef._type = "hidden";
            }
         }
      }

   });
})();