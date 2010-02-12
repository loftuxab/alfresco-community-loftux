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
 * Rules Property Picker.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.RulesPropertyPicker
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;
   
   Alfresco.module.RulesPropertyPicker = function(htmlId)
   {
      // Call super class constructor
      Alfresco.module.RulesPropertyPicker.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.module.RulesPropertyPicker";
      Alfresco.util.ComponentManager.reregister(this);

      // Instance proeprties
      this.preferencesService = new Alfresco.service.Preferences();

      // Override options to add an "Other" tab and an extra "Show in menu" column
      var me = this;
      this.rulePropertySettings = {};
      this.options.extendedTemplateUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/rules/property-picker";
      this.options.tabs.push(
      {
         id: "other",
         listItems: [
            {
               id: "applied-aspects",
               type: "applied-aspects"
            },
            {
               id: "script-returns-true",
               type: "script-returns-true"
            },
            {
               id: "type",
               type: "type"
            }
         ]
      });
      this.options.dataTableColumnDefinitions.push(
      {
         key: "id",
         sortable: false,
         formatter: function (elCell, oRecord, oColumn, oData)
         {
            // Make sure we call the renderer with a scope set to the component (rather than the datatable)
            me._formatShowInMenu(elCell, oRecord, oColumn, oData);
         }
      });

      return this;
   };


   /**
   * Alias to self
   */
   var RPP = Alfresco.module.RulesPropertyPicker;

   /**
   * View Mode Constants
   */
   YAHOO.lang.augmentObject(RPP,
   {
      /**
       * Says that property shall be visible in default menu.
       *
       * @property SHOW
       * @type string
       * @final
       * @default "show"
       */
      PROPERTY_SHOW: "show",

      /**
       * Says that property shall be hidden in default menu.
       *
       * @property HIDE
       * @type string
       * @final
       * @default "hide"
       */
      PROPERTY_HIDE: "hide"
   }),

   YAHOO.extend(Alfresco.module.RulesPropertyPicker, Alfresco.module.PropertyPicker,
   {
      /**
       * Preferences service used to tore which properties that shall be showed or hidden from the menu.
       *
       * @property preferencesService
       * @type {Alfresco.service.Preferences}
       */
      preferencesService: null,

      /**
       * The users rule property settings that decides which properties
       * that should be displayed as default in the condition menu.
       *
       * @property rulePropertySettings
       * @type {object}
       */
      rulePropertySettings: {},

      /**
       * Event callback when superclass' dialog template has been loaded.
       *
       * @method onTemplateLoaded
       * @override
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function RPP_onTemplateLoaded(response)
      {
         // Load the UI template, which will bring in additional i18n-messages from the server
         Alfresco.util.Ajax.request(
         {
            url: this.options.extendedTemplateUrl,
            dataObj:
            {
               htmlid: this.id
            },
            successCallback:
            {
               fn: this.onExtendedTemplateLoaded,
               obj: response,
               scope: this
            },
            failureMessage: this.msg("message.load.template.error", this.options.extendedTemplateUrl),
            execScripts: true
         });
      },

      /**
       * Event callback when this class' template has been loaded
       *
       * @method onExtendedTemplateLoaded
       * @override
       * @param response {object} Server response from load template XHR request
       */
      onExtendedTemplateLoaded: function RPP_onExtendedTemplateLoaded(response, superClassResponse)
      {
         // Inject the template from the XHR request into a new DIV element and insert it when showDialog() is called
         var tmpEl = document.createElement("div");
         tmpEl.setAttribute("style", "display:none");
         tmpEl.innerHTML = response.serverResponse.responseText;
         this.widgets.rulesContainerEl = Dom.getFirstChild(tmpEl);

         // Load the users rules property settings before calling the super classes onTemplateLoaded method
         this.preferencesService.request(Alfresco.service.Preferences.RULE_PROPERTY_SETTINGS,
         {
            successCallback:
            {
               fn: function(p_oResponse, superClassResponse)
               {
                  // Save users rule property settings
                  this.rulePropertySettings = Alfresco.util.findValueByDotNotation(p_oResponse.json, "org.alfresco.share.rule.properties", {});

                  // Let the original template get rendered.
                  Alfresco.module.RulesPropertyPicker.superclass.onTemplateLoaded.call(this, superClassResponse);
               },
               scope: this,
               obj: superClassResponse
            },
            failureMessage: this.msg("message.load.rulePropertySettings.error")
         });
      },

      /**
       * Gets a message from this class or the superclass
       *
       * @method msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @override
       */
      msg: function RPP_msg(messageId)
      {
         var result = Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1))
         if (result == messageId)
         {
            result = Alfresco.util.message(messageId, "Alfresco.module.PropertyPicker", Array.prototype.slice.call(arguments).slice(1));
         }
         return result;
      },


      /**
       * Internal formatter for the show in menu column
       *
       * @method _formatShowInMenu
       * @method renderCellAvatar
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       * @private
       */
      _formatShowInMenu: function RPP__formatShowInMenu(elCell, oRecord, oColumn, oData)
      {
         var propertyData = oRecord.getData(),
            checkBoxEl = document.createElement("input");
         checkBoxEl.setAttribute("type", "checkbox");
         if (this.rulePropertySettings[propertyData.id] == RPP.PROPERTY_SHOW)
         {
            checkBoxEl.setAttribute("checked", "true");
         }
         else
         {
            checkBoxEl.removeAttribute("checked");
         }
         Event.addListener(checkBoxEl, "change", this.onCheckBoxClick,
         {
            checkBoxEl: checkBoxEl,
            propertyData: propertyData
         }, this);
         elCell.appendChild(checkBoxEl);
      },


      /**
       * Called when the user toggles the "Show in menu" checkbox
       *
       * @method onCheckBoxClick
       * @param p_oEvent THe change event
       * @param p_oObj The data object from the row
       * @private
       */
      onCheckBoxClick: function RPP__formatShowInMenu(p_oEvent, p_oObj)
      {
         // Disable checkbox
         p_oObj.checkBoxEl.setAttribute("disabled", "true");

         // Save the new state on obj that is passed around
         p_oObj.state = p_oObj.checkBoxEl.checked ? RPP.PROPERTY_SHOW : RPP.PROPERTY_HIDE;
         this.rulePropertySettings[p_oObj.propertyData.id] = p_oObj.state;

         var responseConfig =
         {
            failureCallback:
            {
               fn: function(p_oResponse, p_oObj)
               {
                  // Display error message
                  var propertyData = p_oObj.propertyData;
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.addFavouriteProperty.failure", propertyData.label)
                  });

                  // Enable checkbox and reset values it to its previous state
                  p_oObj.checkBoxEl.removeAttribute("disabled");
                  if (p_oObj.checkBoxEl.checked)
                  {
                     this.rulePropertySettings = RPP.PROPERTY_HIDE;                      
                     p_oObj.checkBoxEl.removeAttribute("checked");
                  }
                  else
                  {
                     this.rulePropertySettings = RPP.PROPERTY_SHOW;
                     p_oObj.checkBoxEl.setAttribute("checked", "true");
                  }

               },
               obj: p_oObj,
               scope: this
            },
            successCallback:
            {
               fn: function(p_oResponse, p_oObj)
               {
                  // Enable checkbox again and tell other components about the change
                  p_oObj.checkBoxEl.removeAttribute("disabled");
                  YAHOO.Bubbling.fire("rulePropertySettingsChanged",
                  {
                     property: p_oObj.propertyData,
                     state: p_oObj.state
                  });
               },
               scope: this,
               obj: p_oObj
            }
         };

         // Add or remove property as a favourite
         this.preferencesService.set(Alfresco.service.Preferences.RULE_PROPERTY_SETTINGS + "." + p_oObj.propertyData.id,
               p_oObj.state, responseConfig);
      },

      /**
       * Internal show dialog function
       * @method _showDialog
       * @private
       */
      _showDialog: function RPP__showDialog()
      {
         // Add class so we can override styles in css
         Dom.addClass(this.widgets.dialog.body.parentNode, "rules-property-picker");

         // Show dialog as usual
         Alfresco.module.RulesPropertyPicker.superclass._showDialog.call(this);
      }

   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.RulesPropertyPicker("null");
})();

