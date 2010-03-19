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
 * Data Lists: DataLists component.
 * 
 * Displays a list of datalists
 * 
 * @namespace Alfresco
 * @class Alfresco.component.DataLists
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;

   /**
    * DataLists constructor.
    * 
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.component.DataLists} The new DataLists instance
    * @constructor
    */
   Alfresco.component.DataLists = function(htmlId)
   {
      Alfresco.component.DataLists.superclass.constructor.call(this, "Alfresco.component.DataLists", htmlId, ["button", "container"]);
      
      // Initialise prototype properties
      this.dataLists = {};
      this.containerNodeRef = null;
      
      return this;
   };
   
   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.component.DataLists, Alfresco.component.Base,
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
           * Current siteId.
           * 
           * @property siteId
           * @type string
           * @default ""
           */
          siteId: "",

          /**
           * ContainerId representing root container.
           *
           * @property containerId
           * @type string
           * @default "dataLists"
           */
          containerId: "dataLists",
          
          /**
           * ListId representing currently selected list
           *
           * @property listId
           * @type string
           */
          listId: "",

          /**
           * List types when creating new Data Lists
           *
           * @property listTypes
           * @type Array
           */
          listTypes: []
      },

      /**
       * Data Lists metadata retrieved from the Repository
       *
       * @param dataLists
       * @type Object
       */
      dataLists: null,

      /**
       * NodeRef of the Data Lists container retrieved from the Repository
       *
       * @param containerNodeRef
       * @type Object
       */
      containerNodeRef: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       *
       * @method onReady
       */
      onReady: function DataLists_onReady()
      {
         this.widgets.newList = Alfresco.util.createYUIButton(this, "newListButton", this.onNewList);
         // Retrieve the lists from the specified Site & Container
         this.populateDataLists();
      },
      
      /**
       * Retrieves the Data Lists from the Repo
       *
       * @method populateDataLists
       */
      populateDataLists: function DataLists_populateDataLists()
      {
         var listsContainer = Dom.get(this.id + "-lists"),
            selectedClass = "selected";
         
         listsContainer.innerHTML = "";
         
         /**
          * Success handler for Data Lists request
          * @method fnSuccess
          * @param response {Object} Ajax response object literal
          */
         var fnSuccess = function DataLists_pDL_fnSuccess(response)
         {
            var fnOnClick = function DataLists_pDL_fnOnClick(myDiv, listType)
            {
               return function DataLists_pDL_onClick()
               {
                  var lis = Selector.query("li", listsContainer);
                  Dom.removeClass(lis, selectedClass);
                  Dom.addClass(this, selectedClass);
                  return true;
               };
            };

            try
            {
               var lists = response.json.datalists, container, el, list, i, j;
               this.dataLists = {};
               this.containerNodeRef = new Alfresco.util.NodeRef(response.json.container);
               this.widgets.newList.set("disabled", false);
               
               if (lists.length === 0)
               {
                  listsContainer.innerHTML = this.msg("message.no-lists");
                  this.widgets.newList.fireEvent("click");
               }
               else
               {
                  container = document.createElement("ul");
                  listsContainer.appendChild(container);

                  for (i = 0, j = lists.length; i < j; i++)
                  {
                     list = lists[i];
                     this.dataLists[list.name] = list;
                     el = document.createElement("li");
                     el.innerHTML = '<a title="' + $html(list.description) + '" href="data-lists?list=' + $html(list.name) + '">' + $html(list.title) + '</a>';
                     el.onclick = fnOnClick();
                     container.appendChild(el);
                     if (list.name == this.options.listId)
                     {
                        Dom.addClass(el, "selected");
                        // Select current list
                        YAHOO.Bubbling.fire("dataListChanged",
                        {
                           dataList: list,
                           scrollTo: true
                        });
                     }
                  }
               }
            }
            catch(e)
            {
               listsContainer.innerHTML = '<span class="error">' + this.msg("message.error-unknown") + '</span>';
            }
         };

         /**
          * Failure handler for Data Lists request
          * @method fnFailure
          * @param response {Object} Ajax response object literal
          */
         var fnFailure = function DataLists_pDL_fnFailure(response)
         {
            if (response.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload();
            }
            else
            {
               this.dataLists = null;
               this.containerNodeRef = null;
               this.widgets.newList.set("disabled", true);
               var errorMsg = "";
               try
               {
                  errorMsg = $html(YAHOO.lang.JSON.parse(response.responseText).message);
               }
               catch(e)
               {
                  errorMsg = this.msg("message.error-unknown");
               }
               listsContainer.innerHTML = '<span class="error">' + errorMsg + '</span>';
            }
         };
         
         Alfresco.util.Ajax.jsonGet(
         {
            url: $combine(Alfresco.constants.PROXY_URI, "slingshot/datalists/lists/site", this.options.siteId, this.options.containerId),
            successCallback:
            {
               fn: fnSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: fnFailure,
               scope: this
            }
         });
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * New List button click handler
       *
       * @method onNewList
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewList: function DataLists_onNewList(e, p_obj)
      {
         var destination = this.containerNodeRef.nodeRef,
            selectedClass = "theme-bg-color-2";

         var fnPopulateItemTypes = function DataLists_onNewList_fnPopulateItemTypes(domId, formFieldId, p_form)
         {
            var fnOnClick = function DataLists_oNL_fnOnClick(myDiv, listType)
            {
               return function DataLists_oNL_onClick()
               {
                  var divs = Selector.query("div", domId);
                  Dom.removeClass(divs, selectedClass);
                  Dom.addClass(myDiv, selectedClass);
                  Dom.get(formFieldId).value = listType;
                  p_form.updateSubmitElements();
                  return false;
               };
            };

            var list, el, containerEl = Dom.get(domId);
            
            for (var i = 0, ii = this.options.listTypes.length; i < ii; i++)
            {
               list = this.options.listTypes[i];
               el = document.createElement("div");

               el.innerHTML = '<h4>' + $html(list.title) + '</h4><span>' + $html(list.description) + '</span>';
               el.onclick = fnOnClick(el, list.name);
               containerEl.appendChild(el);
            }
         };

         // Intercept before dialog show
         var doBeforeDialogShow = function DataLists_onNewList_doBeforeDialogShow(p_form, p_dialog)
         {
            Alfresco.util.populateHTML(
               [ p_dialog.id + "-dialogTitle", this.msg("label.new-list.title") ],
               [ p_dialog.id + "-dialogHeader", this.msg("label.new-list.header") ],
               [ p_dialog.id + "-dataListItemType", this.msg("label.item-type") ]
            );
            fnPopulateItemTypes.apply(this, [p_dialog.id + "-itemTypesContainer", p_dialog.id + "-dataListItemType-field", p_form]);

            // Must choose a list type
            var fnValidateListChoice = function DataLists_oNL_dBDS_fnValidateListChoice(field, args, event, form, silent, message)
            {
               return (field.value.length > 0);
            };
            p_form.addValidation(p_dialog.id + "-dataListItemType-field", fnValidateListChoice, null, null, null);

            // Must set a title (UI constraint for usability)
            p_form.addValidation(p_dialog.id + "_prop_cm_title", Alfresco.forms.validation.mandatory, null, "keyup");
         };

         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
         {
            itemKind: "type",
            itemId: "dl:dataList",
            destination: destination,
            mode: "create",
            submitType: "json",
            formId: "datalist-new"
         });

         // Using Forms Service, so always create new instance
         var newList = new Alfresco.module.SimpleDialog(this.id + "-newList");

         newList.setOptions(
         {
            width: "33em",
            templateUrl: templateUrl,
            actionUrl: null,
            destroyOnHide: true,
            doBeforeDialogShow:
            {
               fn: doBeforeDialogShow,
               scope: this
            },
            onSuccess:
            {
               fn: function DataLists_onNewList_success(response)
               {
                  var listName = response.config.dataObj["prop_cm_name"];
                  YAHOO.Bubbling.fire("dataListCreated",
                  {
                     name: listName
                  });
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-list.success", listName)
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function DataLists_onNewList_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-list.failure")
                  });
               },
               scope: this
            }
         }).show();
      }
   });
})();