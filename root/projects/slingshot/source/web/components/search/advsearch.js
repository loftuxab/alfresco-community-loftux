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
 * Advanced Search component.
 * 
 * @namespace Alfresco
 * @class Alfresco.AdvancedSearch
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
    * Advanced Search constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.AdvancedSearch} The new AdvancedSearch instance
    * @constructor
    */
   Alfresco.AdvancedSearch = function(htmlId)
   {
      Alfresco.AdvancedSearch.superclass.constructor.call(this, "Alfresco.AdvancedSearch", htmlId, ["button", "container"]);
      
      YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.AdvancedSearch, Alfresco.component.Base,
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
          * Current siteId
          * 
          * @property siteId
          * @type string
          */
         siteId: "",
         
         /**
          * Search Form objects, for example:
          * {
          *    id: "advanced-search",
          *    type: "cm:content",
          *    label: "Content",
          *    description: "All types of content"
          * }
          * 
          * @property searchForms
          * @type Array
          */
         searchForms: []
      },
      
      /**
       * Currently visible Search Form object
       */
      currentForm: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ADVSearch_onReady()
      {
         var me = this,
            domId = this.id + "-form-list",
            elList = Dom.get(domId);
         
         // search YUI button
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.onSearchClick);
         
         // generate list of forms
         var fnOnFormClick = function ADVSearch_fnOnFormClick(form)
         {
            return function onClick()
            {
               // update selected form label, description
               var elSelected = Dom.get(me.id + "-selected-form-link");
               var desc = $html(form.description);
               elSelected.innerHTML = '<div class="form-type-name">' + $html(form.label) + '</div>' +
                                      '<div class="form-type-description">' + (desc.length !== 0 ? desc : "&nbsp;") + '</div>';
               
               // hide list of forms now user has selected something
               Dom.addClass(elList.parentNode, "hidden");
               
               // render the appropriate form template
               me.renderFormTemplate(form);
               
               return false;
            };
         };
         
         var elItem, elLink;
         for (var i=0, j=this.options.searchForms.length, form; i<j; i++)
         {
            form = this.options.searchForms[i];
            elItem = document.createElement("li");
            elLink = document.createElement("a");
            elLink.href = "#";
            elLink.onclick = fnOnFormClick(form);
            var desc = $html(form.description);
            elLink.innerHTML = '<div class="form-type-name">' + $html(form.label) + '</div>' +
                               '<div class="form-type-description">' + (desc.length !== 0 ? desc : "&nbsp;") + '</div>';
            elItem.appendChild(elLink);
            elList.appendChild(elItem);
            
            if (i === 0)
            {
               // update initially selected item
               Dom.get(this.id + "-selected-form-type").innerHTML = $html(form.label);
               desc = $html(form.description);
               Dom.get(this.id + "-selected-form-desc").innerHTML = (desc.length !== 0 ? desc : "&nbsp;");
               
               // event hander for the item showing the currently selected
               // drops down and hides the list when clicked
               var elSelected = Dom.get(me.id + "-selected-form-link");
               elSelected.onclick = function()
               {
                  if (Dom.hasClass(elList.parentNode, "hidden"))
                  {
                     Dom.removeClass(elList.parentNode, "hidden");
                  }
                  else
                  {
                     Dom.addClass(elList.parentNode, "hidden");
                  }
               };
            }
            else
            {
               Dom.addClass(elItem, "item");
            }
         }
         
         // render initial form template
         this.renderFormTemplate(this.options.searchForms[0]);
         
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },

      /**
       * DEFAULT ACTION EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * Loads or retrieves from cache the Form template for a given content type
       * 
       * @method renderFormTemplate
       * @param form {Object} Form descriptor to render template for
       */
      renderFormTemplate: function ADVSearch_renderFormTemplate(form)
      {
         // update current form state
         this.currentForm = form;
         
         var containerDiv = Dom.get(this.id + "-forms");
         
         var visibleFormFn = function()
         {
            // hide visible form if any
            for (var i=0, c=containerDiv.children; i<c.length; i++)
            {
               if (!Dom.hasClass(c[i], "hidden"))
               {
                  Dom.addClass(c[i], "hidden");
                  break;
               }
            }
            
            // display cached form element
            Dom.removeClass(form.htmlid, "hidden");
            
            // reset focus to search input textbox
            Dom.get(this.id + "-search-text").focus();
         };
         
         if (!form.htmlid)
         {
            // generate child container div for this form
            var htmlid = this.id + "_" + containerDiv.children.length;
            var formDiv = document.createElement("div");
            formDiv.id = htmlid;
            Dom.addClass(formDiv, "hidden");
            Dom.addClass(formDiv, "share-form");
            
            // cache htmlid so we know the form is present on the form
            form.htmlid = htmlid;
            
            // load the form component for the appropriate type
            var formUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind=type&itemId={itemId}&formId={formId}&mode=edit&showSubmitButton=false&showCancelButton=false",
            {
               itemId: form.type,
               formId: form.id
            });
            var formData =
            {
               htmlid: htmlid
            };
            Alfresco.util.Ajax.request(
            {
               url: formUrl,
               dataObj: formData,
               successCallback:
               {
                  fn: function ADVSearch_onFormTemplateLoaded(response)
                  {
                     // Inject the template from the XHR request into the child container div
                     formDiv.innerHTML = response.serverResponse.responseText;
                     containerDiv.appendChild(formDiv);
                     
                     visibleFormFn.call(this);
                  },
                  scope: this
               },
               failureMessage: "Could not load form component '" + formUrl + "'.",
               scope: this,
               execScripts: true
            });
         }
         else
         {
            visibleFormFn.call(this);
         }
      },
      
      /**
       * Event handler that gets fired when user clicks the Search button.
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param obj {object} Object passed back from addListener method
       */
      onSearchClick: function ADVSearch_onSearchClick(e, obj)
      {
         // retrieve form data structure directly from the runtime
         var formData = this.currentForm.runtime.getFormData();
         
         // TODO: search based on supplied keywords and form data fields
      },
      
      /**
       * Event handler called when the "beforeFormRuntimeInit" event is received
       */
      onBeforeFormRuntimeInit: function CreateContentMgr_onBeforeFormRuntimeInit(layer, args)
      {
         // extract the current form runtime - so we can reference it later
         this.currentForm.runtime = args[1].runtime;
      }
   });
})();