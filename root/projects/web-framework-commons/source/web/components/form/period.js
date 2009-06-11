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
 * Period component.
 * 
 * @namespace Alfresco
 * @class Alfresco.Period
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
    * Period constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @param {String} currentValueHtmlId The HTML id of the parent element
    * @return {Alfresco.Period} The new Period instance
    * @constructor
    */
   Alfresco.Period = function(htmlId, currentValueHtmlId)
   {
      // Mandatory properties
      this.name = "Alfresco.Period";
      this.id = htmlId;
      this.currentValueHtmlId = currentValueHtmlId;

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button"], this.onComponentsLoaded, this);
      
      // Initialise prototype properties
      this.widgets = {};
      this.periodDefinitions = {};

      return this;
   };
   
   Alfresco.Period.prototype =
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
          * The current value
          *
          * @property currentValue
          * @type string
          */
         currentValue: "",
         
         /**
          * Array of objects representing the period data
          * returned from the server
          * 
          * @property data
          * @type Array of objects
          */
         data: [],
         
         /**
          * Flag to determine whether the picker is in disabled mode
          *
          * @property disabled
          * @type boolean
          * @default false
          */
         disabled: false
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,
      
      /**
       * Object containing all period definitions keyed by period type.
       * 
       * @property periodDefinitions
       * @type object
       */
      periodDefinitions: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Period} returns 'this' for method chaining
       */
      setOptions: function Period_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Period} returns 'this' for method chaining
       */
      setMessages: function Period_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function Period_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Period_onReady()
      {
         this._setupInitialState();
         this._setupEventHandlers();
      },
      
      /**
       * Sets up the inital state of the UI.
       * 
       * @method _setupInitialState
       * @private
       */
      _setupInitialState: function Period__setupInitialState()
      {
         // create the period definition data structure
         for (var p = 0; p < this.options.data.length; p++)
         {
            this.periodDefinitions[this.options.data[p].type] = this.options.data[p];
         }
         
         // split the current value into it's parts
         var parts = this.options.currentValue.split("|");
         var periodType = this.options.currentValue;
         var expression = 1;
         if (parts.length == 2)
         {
            periodType = parts[0];
            expression = parts[1];
         }
         
         // get the period definition for the current value
         var periodDef = this.periodDefinitions[periodType];
         
         if (this.options.disabled)
         {
            // populate the view mode span with a human readable representation
            // of the current value
            var displayValue = this.options.currentValue;
            if (periodDef !== undefined)
            {
               if (periodDef.hasExpression)
               {
                  displayValue = expression;
                  displayValue += " ";
                  displayValue += periodDef.label;
                  if (expression > 1)
                  {
                     displayValue += "s";
                  }
               }
               else
               {
                  displayValue = periodDef.label;
               }
            }

            // update the span to show the display value
            Dom.get(this.id).innerHTML = $html(displayValue);
         }
         else
         {
            // populate the drop down list with period options
            var periodOptions = "<option value=\"\"";
            if (periodType === "")
            {
               periodOptions += " selected=\"selected\"";
            }
            periodOptions += ">";
            periodOptions += this._msg("form.notset");
            periodOptions += "</option>";
            
            var def = null;
            for (var p = 0; p < this.options.data.length; p++)
            {
               def = this.options.data[p];
               periodOptions += "<option value=\"";
               periodOptions += def.type;
               periodOptions += "\"";
               if (periodType === def.type)
               {
                  periodOptions += " selected=\"selected\"";
               }
               periodOptions += ">";
               periodOptions += def.label;
               periodOptions += "</option>";
            }
            
            Dom.get(this.id + "-type").innerHTML = periodOptions;
            
            // populate the expression field with the current value
            // or hide it completely if there isn't an expression
            if (periodDef !== undefined && periodDef.hasExpression)
            {
               Dom.get(this.id + "-expression").value = expression;
            }
         }
      },
      
      /**
       * Sets up the event handlers to handle change events.
       * 
       * @method _setupEventHandlers
       * @private
       */
      _setupEventHandlers: function Period__setupEventHandlers()
      {
         // add an onchange event listener to the select drop down
         Event.addListener(this.id + "-type", "change", this._handleDataChange, this, true);
         
         // add an onkeyup event listender to the expression field
         Event.addListener(this.id + "-expression", "keyup", this._handleDataChange, this, true);
      },
      
      /**
       * Handles any change to the control i.e. the drop down list or the
       * expression field. If the current control data is valid the hidden
       * field is updated with the data represented in the submission format.
       * 
       * @method _handleDataChange
       * @param event The event that occurred
       * @private
       */
      _handleDataChange: function Period__handleDataChange(event)
      {
         // get the current values and construct the new value
         var type = Dom.get(this.id + "-type").value;
         var expression = Dom.get(this.id + "-expression").value;
         var newValue = type + "|" + expression;
         var valueValid = true;
         
         if (type === "")
         {
            newValue = "";
            Dom.get(this.id + "-expression").value = "";
         }
         else
         {
            var periodDef = this.periodDefinitions[type];
            if (periodDef.hasExpression)
            {
               if (expression === "")
               {
                  expression = 1;
                  newValue += expression;
               }
               else if (isNaN(expression))
               {
                  valueValid = false;
               }
            }
            else
            {
               newValue = type;
               Dom.get(this.id + "-expression").value = "";
            }
         }
         
         if (valueValid)
         {
            YAHOO.util.Dom.get(this.currentValueHtmlId).value = newValue;
            
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Hidden field '" + this.currentValueHtmlId + "' updated to '" + newValue + "'");
         }
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function Period__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.Period", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
