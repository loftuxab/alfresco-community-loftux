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
 * RuleConfigCondition.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleConfigCondition
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
       $hasEventInterest = Alfresco.util.hasEventInterest;
   
   Alfresco.RuleConfigCondition = function(htmlId)
   {
      Alfresco.RuleConfigCondition.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.RuleConfigCondition";
      Alfresco.util.ComponentManager.reregister(this);

      // Instance variables
      this.options = YAHOO.lang.merge(this.options, Alfresco.RuleConfigCondition.superclass.options);
      this.customisations = YAHOO.lang.merge(this.customisations, Alfresco.RuleConfigCondition.superclass.customisations);
      this.renderers = YAHOO.lang.merge(this.renderers, Alfresco.RuleConfigCondition.superclass.renderers);
      this.previousConfigNameSelections = {};

      // Decoupled event listeners
      YAHOO.Bubbling.on("rulePropertySettingsChanged", this.onRulePropertySettingsChanged, this);

      return this;
   };

   YAHOO.extend(Alfresco.RuleConfigCondition, Alfresco.RuleConfig,
   {

      previousConfigNameSelections: {},

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The properties to display in the menu
          *
          * @property properties
          * @type {array}
          * @default []
          */
         properties: [],

         /**
          * The constraints for comparing properties contains all evaluator, regardless of what property type
          * they are applicable to. This map specifies which types an evaluator is valid for.
          *
          * Note!
          * If an evaluator isn't specified that evaluator will be displayed for the property regardless
          * of the property type.
          *
          * @property propertyEvaluratorMap
          * @type {object}
          * @default {}
          */
         propertyEvaluatorMap: {},

         /**
          * The config definition that will be used by/specialized by each property
          *
          * @property comparePropertyValueDefinition
          * @type {object}
          * @mandatory
          */
         comparePropertyValueDefinition: {},

         /**
          * The config definition that will be used by/specialized by the mime type property
          *
          * @property compareMimeTypeDefinition
          * @type {object}
          * @mandatory
          */
         compareMimeTypeDefinition: {}
      },


      /**
       * Called when the user changed what properties to display in the "Show more..." dialog (aka rules picker) 
       *
       * @method onReady
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onRulePropertySettingsChanged: function RuleConfigCondition_onRulePropertySettingsChanged(layer, args)
      {
         // Refresh all menues to hide/remove the property
         var values = args[1];
         if (values)
         {
            var newProperty = {
               id: values.property.id,
               type: values.property.type,
               displayLabel: values.property.label
            };
            var show = values.state == Alfresco.module.RulesPropertyPicker.PROPERTY_SHOW;
            for (var i = 0, il = this.options.properties.length, property; i < il; i++)
            {
               property = this.options.properties[i];
               if (property.id == newProperty.id)
               {
                  // The propery was found; add or remove it after loop
                  property._hidden = !show;
                  break;
               }
            }
            if (i == il && show)
            {
               // Add the property since it wasn't added before
               this.options.properties.push(newProperty);
            }
            this.widgets.selectTemplateEl = this._createSelectMenu();

            // Look through all menus to and add or remove it if it isn't selected
            var configEls = Selector.query('li.config', this.id + "-body"),
                  configEl,
                  selectEl;
            for (var ci = 0, cil = configEls.length; ci < cil; ci++)
            {
               configEl = configEls[ci];
               selectEl = Selector.query('select', configEl)[0];
               for (var oi = 0, oil = selectEl.options.length, option; oi < oil; oi++)
               {
                  option = selectEl.options[oi];
                  if (this._isComparePropertyDefinition(option.value) && option.getAttribute("rel") == "property_" + newProperty.id)
                  {
                     if (!option.selected)
                     {
                        if (!show)
                        {
                           // Remove the element since it wasn't selected
                           option.parentNode.removeChild(option);
                           this._selectPreviousConfigName(selectEl);
                        }
                     }
                     break;
                  }
               }
               if (show && oi == oil)
               {
                  // The property is currently not in the menu, add it by replacing the old menu with a new one
                  var selectedOption = selectEl.options[selectEl.selectedIndex],
                     selectedConfigName = selectedOption.value,
                     selectedRelPropertyName = selectedOption.getAttribute("rel");
                  var newSelectEl = this.widgets.selectTemplateEl.cloneNode(true);

                  // Since we have created a new select menu, the history of previous selections must taken from the old menu
                  this.previousConfigNameSelections[Alfresco.util.generateDomId(newSelectEl)] = this.previousConfigNameSelections[selectEl.getAttribute("id")];

                  // Remove the old select menu and add the new one
                  selectEl.parentNode.removeChild(selectEl);
                  Event.addListener(newSelectEl, "change", this.onConfigNameSelectChange, configEl, this);
                  Selector.query('div.name', configEl)[0].appendChild(newSelectEl);

                  // Select the previous choice
                  this._selectConfigName(newSelectEl, selectedConfigName, selectedRelPropertyName);
               }
            }
         }
      },

      /**
       * Called to get the config for a menu item.
       *
       * @method _getConfigItems
       * @param itemType
       * @param itemPatternObject
       * @param displayHiddenPropertyId {object} Custom parameter
       * @return {array} Menu item objects (as described below) representing a config (or item)
       *                 matching all attributes in itemPatternObject.
       * {
       *    id: string,
       *    label: string,
       *    descriptor: object
       * }
       * @override
       */
      _getConfigItems: function RuleConfigCondition__getConfigItems(itemType, itemPatternObject, displayHiddenPropertyId)
      {
         if (itemType == "property")
         {
            var results = [];
            for (var ci = 0, cil = this.options.properties.length, property; ci < cil; ci++)
            {
               property = this.options.properties[ci];
               if (!property._hidden || displayHiddenPropertyId)
               {
                  if (Alfresco.util.objectMatchesPattern(property, itemPatternObject))
                  {
                     // Create a menu item with a "faked" config definition for the property
                     results.push(this._createPropertyConfigDef(property));
                  }
               }
            }
            return results;
         }
         return Alfresco.RuleConfigCondition.superclass._getConfigItems.call(this, itemType, itemPatternObject);
      },

      /**
       * Called to get the constraint options for a constraint.
       *
       * @method _getConstraintValues
       * @param p_sConstraintName
       * @param p_oRuleConfig
       * @return {array} rule constraint values
       * @override
       */
      _getConstraintValues: function RuleConfigCondition__getConstraintValues(p_sConstraintName, p_oRuleConfig)
      {
         var values = Alfresco.RuleConfigCondition.superclass._getConstraintValues.call(this, p_sConstraintName, p_oRuleConfig);
         if (this._isComparePropertyDefinition(p_oRuleConfig[this.options.ruleConfigDefinitionKey]))
         {
            var propertyName = p_oRuleConfig.parameterValues.property,
               contentProperty = p_oRuleConfig.parameterValues["content-property"];
            if (contentProperty)
            {
               propertyName += "." + contentProperty;
            }
            var propertyType,
               evaluatorMap = this.options.propertyEvaluatorMap,
               propertyTypes,
               filteredValues = [];
            for (var i = 0, il = this.options.properties.length; i < il; i++)
            {
               if (this.options.properties[i].id == propertyName)
               {
                  propertyType = this.options.properties[i].type;
                  break;
               }
            }
            for (var ei = 0, eil = values.length; propertyType && ei < eil; ei++)
            {
               propertyTypes = evaluatorMap[values[ei].value];
               if (propertyTypes)
               {
                  // The evaluator was specified
                  for (var pi = 0, pil = propertyTypes.length; pi < pil; pi++)
                  {
                     if (propertyTypes[pi] == propertyType)
                     {
                        // The evaluator allows the type
                        filteredValues.push(values[ei]);
                        break;
                     }
                  }
               }
               else
               {
                  filteredValues.push(values[ei]);
               }
            }
            return filteredValues;
         }
         return values;
      },

      /**
       * @method _createPropertyConfigDef
       * @param property {object}
       * @return {object} A configDef based on info from property
       */
      _createPropertyConfigDef: function RuleConfigCondition__createPropertyConfigDef(property)
      {
         var descriptor;
         if (property.id.indexOf(".") > property.id.indexOf(":"))
         {
            // This is a transient property, modify the type
            if (property.id.split(".")[1] == "MIME_TYPE")
            {
               // use the specific mime type comparator
               descriptor = Alfresco.util.deepCopy(this.options.compareMimeTypeDefinition);
               this._getParamDef(descriptor, "property")._type = "hidden";
            }
         }
         if (!descriptor)
         {
            // Use the standard property comparator config definition
            descriptor = Alfresco.util.deepCopy(this.options.comparePropertyValueDefinition);
            this._getParamDef(descriptor, "property")._type = "hidden";
            this._getParamDef(descriptor, "content-property")._type = "hidden";
            this._getParamDef(descriptor, "operation").displayLabel = "";
            this._getParamDef(descriptor, "operation").isMandatory = true;
            this._getParamDef(descriptor, "value").displayLabel = "";
            this._getParamDef(descriptor, "value").type = property.type;
            descriptor.parameterDefinitions.reverse();
         }

         var propertyConfigDef = {
            id: descriptor.name,
            type: "property_" + property.id,
            label: property.displayLabel,
            descriptor: descriptor
         };
         return  propertyConfigDef;
      },

      /**
       * @method _createRuleConfig
       * @param propertyId {string}
       * @return {object} A ruleConfig based on info from property
       */
      _createRuleConfig: function RuleConfigCondition__createRuleConfig(propertyId)
      {
         var propertyTokens = propertyId.split("."),
            property = propertyTokens[0],
            contentProperty = propertyTokens.length > 1 ? propertyTokens[1] : null;
         var ruleConfig = {
            parameterValues:
            {
               "property": property
            }
         };
         ruleConfig[this.options.ruleConfigDefinitionKey] = this.options.comparePropertyValueDefinition.name;
         if (contentProperty)
         {
            ruleConfig.parameterValues["content-property"] = contentProperty;
            if (contentProperty == "MIME_TYPE")
            {
               ruleConfig[this.options.ruleConfigDefinitionKey] = this.options.compareMimeTypeDefinition.name;
            }
         }
         return ruleConfig;
      },

      /**
       * @method onConfigNameSelectChange
       * @param p_oEvent {object} The change event
       * @param p_eConfigEl {HTMLElement} Contains the rule configEl objects
       * @override
       */
      onConfigNameSelectChange: function RuleConfigCondition_onConfigNameSelectChange(p_oEvent, p_eConfigEl)
      {
         // Get or create the list of the 3 last config name selections
         var selectEl = p_oEvent.target,
            optionEl = selectEl.options[selectEl.selectedIndex],
            selectElDomId = selectEl.getAttribute("id");
         if (!selectElDomId)
         {
            selectElDomId = Alfresco.util.generateDomId(selectEl);
         }
         var previousSelections = this.previousConfigNameSelections[selectElDomId];
         if (!previousSelections)
         {
            previousSelections = [{}, {}];
            this.previousConfigNameSelections[selectElDomId] = previousSelections;
         }

         // Move the previous value to the end of the list and store the new selection
         if (previousSelections[0].value != optionEl.value || previousSelections[0].rel != optionEl.getAttribute("rel"))
         {
            previousSelections[1] = previousSelections[0];
            previousSelections[0] = {
               rel: optionEl.getAttribute("rel"),
               value: optionEl.value
            };
         }

         if (this._isComparePropertyDefinition(optionEl.value))
         {
            // Don't call super class since we want to invoke _createConfigParameterUI our selves
            var propertyId = optionEl.getAttribute("rel").substring("property".length + 1),
               ruleConfig = this._createRuleConfig(propertyId);
            this._createConfigParameterUI(ruleConfig, p_eConfigEl);
         }
         else
         {
            // Let super class handle default case
            Alfresco.RuleConfigCondition.superclass.onConfigNameSelectChange.call(this, p_oEvent, p_eConfigEl);
         }
      },

      /**
       * Since all properties share "compare-property-value" as the config type
       * we must make sure the specific property is set in the drop down when the initial value.
       * We do this by looking at the "rel" attribute of the option element were we have combined the
       * normal menu item type (property) with the specific property name.
       *
       *
       * @method _createConfigUI
       * @param p_oRuleConfig {object} Rule config descriptor object
       * @param p_oSelectEl {HTMLSelectElement} The select menu to clone and display
       * @param p_eRelativeConfigEl {object}
       * @override
       */
      _createConfigUI: function RuleConfigCondition__createConfigUI(p_oRuleConfig, p_oSelectEl, p_eRelativeConfigEl)
      {
         // Super class will handle item & conditions....
         var configEl = Alfresco.RuleConfigCondition.superclass._createConfigUI.call(this, p_oRuleConfig, p_oSelectEl, p_eRelativeConfigEl);

         // ... but if it is a property we need to re-select the config type to select the specific property
         var configDefinitionName = p_oRuleConfig[this.options.ruleConfigDefinitionKey];
         if (this._isComparePropertyDefinition(configDefinitionName))
         {
            // Find select element and the property parameter
            var propertyName = p_oRuleConfig.parameterValues.property;
            if (p_oRuleConfig.parameterValues["content-property"])
            {
               propertyName += "." + p_oRuleConfig.parameterValues["content-property"];
            }
            this._selectConfigName(p_oSelectEl, configDefinitionName, "property_" + propertyName);
         }

         // Return configEl like super class
         return configEl;
      },

      /**
       * @method _selectConfigName
       * @param selectEl {HTMLSelectElement} The select element
       * @param configDefName {string} The
       * @param relPropertyName {string} The string "property_" concatenated with the property name/id taken from the data dictionary
       * @private
       */
      _selectConfigName: function RuleConfigCondition__selectConfigName(selectEl, configDefName, relPropertyName)
      {
         for (var adi = 0, adil = selectEl.options.length; adi < adil; adi++)
         {
            if (selectEl.options[adi].value == configDefName &&
               (!relPropertyName || selectEl.options[adi].getAttribute("rel") == relPropertyName))
            {
               selectEl.selectedIndex = adi;
               break;
            };
         }
         // Nothing was selected, select the first option instead
         if (adi == adil)
         {
            selectEl.selectedIndex = 0;
         }
      },

      /**
       * @method _selectPreviousConfigName
       * @param selectEl {HTMLSelectElement} The select element
       * @private
       */
      _selectPreviousConfigName: function RuleConfigCondition__selectPreviousConfigName(selectEl)
      {
         var previousSelections = this.previousConfigNameSelections[selectEl.getAttribute("id")],
               previousSelectedConfigDef = previousSelections && previousSelections.length > 1 ? previousSelections[1] : null;
         if (previousSelectedConfigDef && previousSelectedConfigDef.value)
         {
            this._selectConfigName(selectEl, previousSelectedConfigDef.value, previousSelectedConfigDef.rel);
         }
      },

      /**
       * @method _isComparePropertyDefinition
       * @param configDefinitionName {string} The config definition name to compare
       * @private
       */
      _isComparePropertyDefinition: function RuleConfigCondition__isComparePropertyDefinition(configDefinitionName)
      {
         return (configDefinitionName == this.options.comparePropertyValueDefinition.name ||
                  configDefinitionName == this.options.compareMimeTypeDefinition.name);
      },

      /**
       * CUSTOMISATIONS
       */

      customisations:
      {
         /**
          * Category picker
          */
         InCategoryCondition:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push(
               {
                  type: "arcc:category-picker"
               });
               return configDef;
            }
         },

         /**
          * Tag picker
          */
         HasTagCondition:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push(
               {
                  type: "arcc:tag-picker"
               });
               return configDef;
            }
         },

         /**
          * Show more shall not render any parameter ui by itself so keep the paramDefinitions empty.
          * Instead display a dialog and if:
          *
          * a) A property was selected add that property to the menu with "compare-property-value"
          *    as rule config definition name and make sure it gets selected.
          *
          * b) Cancel was clicked, make sure to reselect the previous selected option in the menu.
          */
         ShowMore:
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function(configDefinition, p_oRuleConfig, configEl, paramsEl)
            {
               this.customisations.ShowMore.currentCtx =
               {
                  configEl: configEl
               };

               if (!this.widgets.showMoreDialog)
               {
                  this.widgets.showMoreDialog = new Alfresco.module.RulesPropertyPicker(this.id + "-showMoreDialog");

                  YAHOO.Bubbling.on("propertiesSelected", function (layer, args)
                  {
                     if ($hasEventInterest(this.widgets.showMoreDialog, args))
                     {
                        // Add property to this menu
                        var property = args[1].selectedProperty;

                        property = {
                           id: property.id,
                           type: property.type,
                           displayLabel: property.label,
                           _hidden: true
                        };

                        if (property)
                        {
                           // Add property to list of properties if its a new one
                           var properties = this.options.properties;
                           for (var i = 0, il = properties.length; i < il; i++)
                           {
                              if (properties[i].id == property.id)
                              {                                 
                                 break;
                              }
                           }
                           if (i == il)
                           {
                              // Its a new property, add it
                              properties.push(property);
                           }

                           /**
                            * Create a new select drop down, based on the current/temporary properties so it becomes
                            * specific for this config row
                            */
                           var configEl = this.customisations.ShowMore.currentCtx.configEl,
                              newSelectEl = this._createSelectMenu(property.id),
                              selectEl = Selector.query('select', configEl)[0];

                           // Since we have created a new select menu, the history of previous selections must taken from the old menu
                           this.previousConfigNameSelections[Alfresco.util.generateDomId(newSelectEl)] = this.previousConfigNameSelections[selectEl.getAttribute("id")];

                           // Create a ruleConfig and replace the current configEl and with a new one based on ruleConfig
                           var ruleConfig = this._createRuleConfig(property.id);
                           var newConfigEl = this._createConfigUI(ruleConfig, newSelectEl, configEl);
                           this.customisations.ShowMore.currentCtx.configEl = newConfigEl;
                           configEl.parentNode.removeChild(configEl);
                           this._createConfigParameterUI(ruleConfig, newConfigEl);
                        }
                     }
                  }, this);
                  YAHOO.Bubbling.on("propertySelectionCancelled", function (layer, args)
                  {
                     if ($hasEventInterest(this.widgets.showMoreDialog, args))
                     {
                        // Reselect the previous choice in the menu
                        var configEl = this.customisations.ShowMore.currentCtx.configEl;
                        this._selectPreviousConfigName(Selector.query('select', configEl)[0]);
                     }
                  }, this);
               }
               this.widgets.showMoreDialog.showDialog();
               return null;
            }
         }

      },

      /**
       * RENDERERS
       */

      renderers:
      {
         /**
          * Category Picker
          */
         "arcc:category-picker":
         {
            currentCtx: {},
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this.renderers["arcc:category-picker"].currentCtx =
               {
                  configDef: configDef,
                  ruleConfig: ruleConfig,
                  paramDef: paramDef
               };
               var picker = new Alfresco.module.ControlWrapper(Alfresco.util.generateDomId());
               picker.setOptions(
               {
                  type: "category",
                  container: containerEl,
                  controlParams:
                  {
                     multipleSelectMode: false
                  },
                  fnValueChanged:
                  {
                     fn: function(obj)
                     {
                        var ctx = this.renderers["arcc:category-picker"].currentCtx;
                        this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "category-aspect", "cm:classifiable");
                        this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "category-value", obj.selectedItems[0]);
                        this._updateSubmitElements(ctx.configDef);
                     },
                     scope: this
                  }
               });
               picker.show();
            }
         },

         /**
          * Tag Picker
          */
         "arcc:tag-picker":
         {
            currentCtx: {},
            text: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, value);
            },
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this.renderers["arcc:tag-picker"].currentCtx =
               {
                  configDef: configDef,
                  ruleConfig: ruleConfig,
                  paramDef: paramDef
               };
               var picker = new Alfresco.module.ControlWrapper(Alfresco.util.generateDomId());
               picker.setOptions(
               {
                  type: "tag",
                  container: containerEl,
                  controlParams:
                  {
                     multipleSelectMode: false
                  },
                  fnValueChanged:
                  {
                     fn: function(obj)
                     {
                        var ctx = this.renderers["arcc:tag-picker"].currentCtx;
                        this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "tag", obj.selectedItems[0]);
                        this._updateSubmitElements(ctx.configDef);
                     },
                     scope: this
                  }
               });
               picker.show();
            }
         }
      }
   });
})();
