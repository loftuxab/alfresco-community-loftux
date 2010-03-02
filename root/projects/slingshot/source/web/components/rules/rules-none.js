/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * RulesNone component.
 * When a folder has no rules this component lets the user create new rules
 * or link the folder to another folders rule set.
 *
 * @namespace Alfresco
 * @class Alfresco.RulesNone
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;

   /**
    * FolderPath constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RulesNone} The new FolderPath instance
    * @constructor
    */
   Alfresco.RulesNone = function(htmlId)
   {
      Alfresco.RulesNone.superclass.constructor.call(this, "Alfresco.RulesNone", htmlId, ["button"]);

      return this;
   };

   YAHOO.extend(Alfresco.RulesNone, Alfresco.component.Base,
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
          * @type string
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
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RulesNone_onReady()
      {
         // Create buttons
         this.widgets.createRuleButton = Alfresco.util.createYUIButton(this, "createRule-button", this.onCreateRuleButtonClick);
         this.widgets.linkToRuleSetButton = Alfresco.util.createYUIButton(this, "linkToRuleSet-button", this.onLinkToRuleSetButtonClick);
      },


      /**
       * Called when user clicks on the create rule button.
       * Takes the user to the new rule page.
       *
       * @method onCreateRuleButtonClick
       * @param type
       * @param args
       */
      onCreateRuleButtonClick: function RulesNone_onCreateRuleButtonClick(type, args)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{siteId}/rule-edit?nodeRef={nodeRef}",
         {
            siteId: this.options.siteId,
            nodeRef: this.options.nodeRef
         });         
         window.location.href = url;
      },


      /**
       * Called when user clicks on the create rule button.
       * Takes the user to the new rule page.
       *
       * @method onLinkToRuleSetButtonClick
       * @param type
       * @param args
       */
      onLinkToRuleSetButtonClick: function RulesNone_onLinkToRuleSetButtonClick(type, args)
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
      }

   });
})();
