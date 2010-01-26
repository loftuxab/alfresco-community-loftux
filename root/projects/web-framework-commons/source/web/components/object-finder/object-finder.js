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
 * ObjectFinder component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ObjectFinder
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $hasEventInterest = Alfresco.util.hasEventInterest;
   
   /**
    * ObjectFinder constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @param {String} currentValueHtmlId The HTML id of the parent element
    * @return {Alfresco.ObjectFinder} The new ObjectFinder instance
    * @constructor
    */
   Alfresco.ObjectFinder = function(htmlId, currentValueHtmlId)
   {
      Alfresco.ObjectFinder.superclass.constructor.call(this, "Alfresco.ObjectFinder", htmlId, ["button", "menu", "container", "resize", "datasource", "datatable"]);
      this.currentValueHtmlId = currentValueHtmlId;

      /**
       * Decoupled event listeners
       */
      this.eventGroup = htmlId;
      YAHOO.Bubbling.on("renderCurrentValue", this.onRenderCurrentValue, this);
      YAHOO.Bubbling.on("selectedItemAdded", this.onSelectedItemAdded, this);
      YAHOO.Bubbling.on("selectedItemRemoved", this.onSelectedItemRemoved, this);
      YAHOO.Bubbling.on("parentChanged", this.onParentChanged, this);
      YAHOO.Bubbling.on("parentDetails", this.onParentDetails, this);

      // Initialise prototype properties
      this.pickerId = htmlId + "-picker";
      this.columns = [];
      this.currentValueMeta = [];
      this.selectedItems = [];
      
      this.options.objectRenderer = new Alfresco.ObjectRenderer(this);

      return this;
   };
   
   YAHOO.extend(Alfresco.ObjectFinder, Alfresco.component.Base,
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
          * Instance of an ObjectRenderer class
          *
          * @property objectRenderer
          * @type object
          */
         objectRenderer: null,

         /**
          * The current value
          *
          * @property currentValue
          * @type string
          */
         currentValue: "",
         
         /**
          * The type of the item to find
          *
          * @property itemType
          * @type string
          */
         itemType: "cm:content",

         /**
          * Compact mode flag
          * 
          * @property compactMode
          * @type boolean
          * @default false
          */
         compactMode: false,

         /**
          * Multiple Select mode flag
          * 
          * @property multipleSelectMode
          * @type boolean
          * @default false
          */
         multipleSelectMode: true,
         
         /**
          * Determines whether a link to the target
          * node should be rendered
          *
          * @property showLinkToTarget
          * @type boolean
          * @default false
          */
         showLinkToTarget: false,
         
         /**
          * Template to use for link to target nodes, must
          * be supplied when showLinkToTarget property is
          * set to true
          *
          * @property targetLinkTemplate
          * @type string
          */
         targetLinkTemplate: null,
         
         /**
          *** NOT IMPLEMENTED ***
          * Number of characters required for a search
          * 
          * @property minSearchTermLength
          * @type int
          * @default 3
          */
         minSearchTermLength: 3,
         
         /**
          * Maximum number of items to display in the results list
          * 
          * @property maxSearchResults
          * @type int
          * @default 100
          */
         maxSearchResults: 100,
         
         /**
          * Flag to determine whether the added and removed items
          * should be maintained and posted separately.
          * If set to true (the default) the picker will update
          * a "${field.name}_added" and a "${field.name}_removed"
          * hidden field, if set to false the picker will just
          * update a "${field.name}" hidden field with the current
          * value.
          * 
          * @property maintainAddedRemovedItems
          * @type boolean
          * @default true
          */
         maintainAddedRemovedItems: true,
         
         /**
          * Flag to determine whether the picker is in disabled mode
          *
          * @property disabled
          * @type boolean
          * @default false
          */
         disabled: false,
         
         /**
          * Flag to indicate whether the field is mandatory
          *
          * @property mandatory
          * @type boolean
          * @default false
          */
         mandatory: false,
         
         /**
          * Relative URI of "create new item" data webscript.
          *
          * @property createNewItemUri
          * @type string
          * @default ""
          */
         createNewItemUri: "",
         
         /**
          * Icon type to augment "create new item" row.
          *
          * @property createNewItemIcon
          * @type string
          * @default ""
          */
         createNewItemIcon: ""
      },

      /**
       * Resizable columns
       * 
       * @property columns
       * @type array
       * @default []
       */
      columns: null,

      /**
       * The current value of the association including metadata
       *
       * @property currentValueMeta
       * @type array
       */
      currentValueMeta: null,
      
      /**
       * Single selected item, for when in single select mode
       * 
       * @property singleSelectedItem
       * @type string
       */
      singleSelectedItem: null,

      /**
       * Selected items. Keeps a list of selected items for correct Add button state.
       * 
       * @property selectedItems
       * @type object
       */
      selectedItems: null,

      /**
       * Set multiple initialization options at once.
       *
       * @override
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.ObjectFinder} returns 'this' for method chaining
       */
      setOptions: function ObjectFinder_setOptions(obj)
      {
         Alfresco.ObjectFinder.superclass.setOptions.call(this, obj);
         // TODO: Do we need to filter this object literal before passing it on..?
         this.options.objectRenderer.setOptions(obj);
         
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.ObjectFinder} returns 'this' for method chaining
       */
      setMessages: function ObjectFinder_setMessages(obj)
      {
         Alfresco.ObjectFinder.superclass.setMessages.call(this, obj);
         this.options.objectRenderer.setMessages(obj);
         return this;
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ObjectFinder_onReady()
      {
         this._getCurrentValueMeta();
         
         if (this.options.disabled === false)
         {
            if (this.options.compactMode)
            {
               Dom.addClass(this.pickerId, "compact");
            }
         
            this._createNavigationControls();
            this._createSelectedItemsControls();
            this.widgets.showPicker = Alfresco.util.createYUIButton(this, "showPicker-button", this.onShowPicker);
            this.widgets.ok = Alfresco.util.createYUIButton(this, "ok", this.onOK);
            this.widgets.cancel = Alfresco.util.createYUIButton(this, "cancel", this.onCancel);
            
            // force the generated buttons to have a name of "-" so it gets ignored in
            // JSON submit. TODO: remove this when JSON submit behaviour is configurable
            Dom.get(this.id + "-showPicker-button-button").name = "-";
            Dom.get(this.id + "-ok-button").name = "-";
            Dom.get(this.id + "-cancel-button").name = "-";
            
            this.widgets.dialog = Alfresco.util.createYUIPanel(this.pickerId,
            {
               width: "60em"
            });
            this.widgets.dialog.hideEvent.subscribe(this.onCancel, null, this);
            Dom.addClass(this.pickerId, "object-finder");
         }
      },
      
      /**
       * Show picker button click handler
       *
       * @method onShowPicker
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onShowPicker: function ObjectFinder_onShowPicker(e, p_obj)
      {
         // Register the ESC key to close the dialog
         var escapeListener = new KeyListener(this.pickerId,
         {
            keys: KeyListener.KEY.ESCAPE
         },
         {
            fn: function(eventName, keyEvent)
            {
               this.onCancel();
            },
            scope: this,
            correctScope: true
         });
         escapeListener.enable();

         this.widgets.dialog.show();
         this._createResizer();
         this._populateSelectedItems();
         this.options.objectRenderer.onPickerShow();
         YAHOO.Bubbling.fire("refreshItemList",
         {
            eventGroup: this
         });
         p_obj.set("disabled", true);
         Event.preventDefault(e);
      },

      /**
       * Folder Up Navigate button click handler
       *
       * @method onFolderUp
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFolderUp: function DLTB_onFolderUp(e, p_obj)
      {
         var item = p_obj.get("value");

         YAHOO.Bubbling.fire("parentChanged",
         {
            eventGroup: this,
            label: item.name,
            nodeRef: item.nodeRef
         });
         Event.preventDefault(e);
      },

      /**
       * Create New OK button click handler
       *
       * @method onCreateNewOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCreateNewOK: function DLTB_onCreateNewOK(e, p_obj)
      {
         Event.preventDefault(e);
      },

      /**
       * Create New Cancel button click handler
       *
       * @method onCreateNewCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCreateNewCancel: function DLTB_onCreateNewCancel(e, p_obj)
      {
         Event.preventDefault(e);
      },

      /**
       * Picker OK button click handler
       *
       * @method onOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onOK: function ObjectFinder_onOK(e, p_obj)
      {
         if (this.options.maintainAddedRemovedItems)
         {
            Dom.get(this.id + "-added").value = this.getAddedItems().toString();
            Dom.get(this.id + "-removed").value = this.getRemovedItems().toString();
         }
         
         this.options.currentValue = this.getSelectedItems().toString();
         Dom.get(this.currentValueHtmlId).value = this.options.currentValue;
         this._getCurrentValueMeta();
         
         if (Alfresco.logger.isDebugEnabled())
         {
            Alfresco.logger.debug("Hidden field '" + this.currentValueHtmlId + "' updated to '" + this.options.currentValue + "'");
         }
         
         // inform the forms runtime that the control value has been updated (if field is mandatory)
         if (this.options.mandatory)
         {
            YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
         }
         
         // Dom.setStyle(this.pickerId, "display", "none");
         this.widgets.dialog.hide();
         this.widgets.showPicker.set("disabled", false);
         if (e)
         {
            Event.preventDefault(e);
         }
      },

      /**
       * Picker Cancel button click handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function ObjectFinder_onCancel(e, p_obj)
      {
         this.widgets.dialog.hide();
         this.widgets.showPicker.set("disabled", false);
         if (e)
         {
            Event.preventDefault(e);
         }
      },


      /**
       * PUBLIC INTERFACE
       */

      /**
       * Returns if an item can be selected
       *
       * @method canItemBeSelected
       * @param id {string} Item id (nodeRef)
       * @return {boolean}
       */
      canItemBeSelected: function ObjectFinder_canItemBeSelected(id)
      {
         if (!this.options.multipleSelectMode && this.singleSelectedItem !== null)
         {
            return false;
         }
         return (this.selectedItems[id] === undefined);
      },

      /**
       * Returns currently selected items
       *
       * @method getSelectedItems
       * @return {array}
       */
      getSelectedItems: function ObjectFinder_getSelectedItems()
      {
         var selectedItems = [];

         for (var item in this.selectedItems)
         {
            if (this.selectedItems.hasOwnProperty(item))
            {
               selectedItems.push(this.selectedItems[item].nodeRef);
            }
         }
         return selectedItems;
      },

      /**
       * Returns items that have been added to the current value
       *
       * @method getAddedItems
       * @return {array}
       */
      getAddedItems: function ObjectFinder_getAddedItems()
      {
         var addedItems = [],
            currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));
         
         for (var item in this.selectedItems)
         {
            if (this.selectedItems.hasOwnProperty(item))
            {
               if (!(item in currentItems))
               {
                  addedItems.push(item);
               }
            }
         }
         return addedItems;
      },

      /**
       * Returns items that have been removed from the current value
       *
       * @method getRemovedItems
       * @return {array}
       */
      getRemovedItems: function ObjectFinder_getRemovedItems()
      {
         var removedItems = [],
            currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));
         
         for (var item in currentItems)
         {
            if (currentItems.hasOwnProperty(item))
            {
               if (!(item in this.selectedItems))
               {
                  removedItems.push(item);
               }
            }
         }
         return removedItems;
      },

      
      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Renders current value in reponse to an event
       *
       * @method onRenderCurrentValue
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters
       */
      onRenderCurrentValue: function ObjectFinder_onRenderCurrentValue(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var items = this.currentValueMeta,
               displayValue = "";

            if (items === null)
            {
               displayValue = "<span class=\"error\">" + this.msg("form.control.object-picker.current.failure") + "</span>";            
            }
            else
            {
               var item;
               for (var i = 0, ii = items.length; i < ii; i++)
               {
                  item = items[i];
                  // Special case for tags, which we want to render differently to categories
                  if (item.type == "cm:category" && item.displayPath.indexOf("/categories/Tags") !== -1)
                  {
                     item.type = "tag";
                  }

                  if (this.options.showLinkToTarget && this.options.targetLinkTemplate !== null)
                  {
                     displayValue += this.options.objectRenderer.renderItem(item, 16, 
                        "<div>{icon} <a href='" + this.options.targetLinkTemplate + "'>{name}</a></div>");
                  }
                  else
                  {
                     displayValue += this.options.objectRenderer.renderItem(item, 16, "<div>{icon} {name}</div>");
                  }
               }
            }

            Dom.get(this.id + "-currentValueDisplay").innerHTML = displayValue;
         }
      },

      /**
       * Selected Item Added event handler
       *
       * @method onSelectedItemAdded
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSelectedItemAdded: function ObjectFinder_onSelectedItemAdded(layer, args)
      {   
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.item)
            {
               // Add the item to the selected list
               this.widgets.dataTable.addRow(obj.item);
               this.selectedItems[obj.item.nodeRef] = obj.item;
               this.singleSelectedItem = obj.item;
            }
         }
      },

      /**
       * Selected Item Removed event handler
       *
       * @method onSelectedItemRemoved
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSelectedItemRemoved: function ObjectFinder_onSelectedItemRemoved(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.item)
            {
               delete this.selectedItems[obj.item.nodeRef];
               this.singleSelectedItem = null;
            }
         }
      },
      
      /**
       * Parent changed event handler
       *
       * @method onParentChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onParentChanged: function ObjectFinder_onParentChanged(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.label)
            {
               this.widgets.navigationMenu.set("label", '<div><span class="item-icon"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/images/ajax_anim.gif" width="16" height="16" alt="' + this.msg("message.please-wait") + '"></span><span class="item-name">' + $html(obj.label) + '</span></div>');
            }
         }
      },
      
      /**
       * Parent Details updated event handler
       *
       * @method onParentDetails
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onParentDetails: function ObjectFinder_onParentDetails(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.parent)
            {
               var arrItems = [],
                  item = obj.parent,
                  navButton = this.widgets.navigationMenu,
                  navMenu = navButton.getMenu(),
                  navGroup = navMenu.getItemGroups()[0],
                  indent = "";
               
               // Create array, deepest node first in final array
               while (item)
               {
                  arrItems = [item].concat(arrItems);
                  item = item.parent;
               }

               var i, ii;
               for (i = 0, ii = navGroup.length; i < ii; i++)
               {
                  navMenu.removeItem(0, 0, true);
               }
               
               item = arrItems[arrItems.length - 1];
               navButton.set("label", this.options.objectRenderer.renderItem(item, 16, '<div><span class="item-icon">{icon}</span><span class="item-name">{name}</span></div>'));
               
               // Navigation Up button
               if (arrItems.length > 1)
               {
                  this.widgets.folderUp.set("value", arrItems[arrItems.length - 2]);
                  this.widgets.folderUp.set("disabled", false);
               }
               else
               {
                  this.widgets.folderUp.set("disabled", true);
               }
               
               var menuItem;
               for (i = 0, ii = arrItems.length; i < ii; i++)
               {
                  item = arrItems[i];
                  menuItem = new YAHOO.widget.MenuItem(this.options.objectRenderer.renderItem(item, 16, indent + '<span class="item-icon">{icon}</span><span class="item-name">{name}</span>'),
                  {
                     value: item.nodeRef
                  });
                  menuItem.cfg.addProperty("label",
                  {
                     value: item.name
                  });
                  navMenu.addItem(menuItem, 0);
                  indent += "&nbsp;&nbsp;&nbsp;";
               }
               
               navMenu.render();
            }
         }
      },

      /**
       * Returns Icon datacell formatter
       *
       * @method fnRenderCellIcon
       */
      fnRenderCellIcon: function ObjectFinder_fnRenderCellIcon()
      {
         var scope = this;

         /**
          * Icon datacell formatter
          *
          * @method renderCellIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectFinder_renderCellIcon(elCell, oRecord, oColumn, oData)
         {
            var iconSize = scope.options.compactMode ? 16 : 32;
         
            oColumn.width = iconSize - 6;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            elCell.innerHTML = scope.options.objectRenderer.renderItem(oRecord.getData(), iconSize, '<div class="icon' + iconSize + '">{icon}</div>');
         };
      },

      /**
       * Returns Name / description datacell formatter
       *
       * @method fnRenderCellName
       */
      fnRenderCellName: function ObjectFinder_fnRenderCellName()
      {
         var scope = this;

         /**
          * Name / description datacell formatter
          *
          * @method renderCellName
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectFinder_renderCellName(elCell, oRecord, oColumn, oData)
         {
            var template;
            if (scope.options.compactMode)
            {
               template = '<h3 class="name">{name}</h3>';
            }
            else
            {
               template = '<h3 class="name">{name}</h3><div class="description">{description}</div>';
            }

            elCell.innerHTML = scope.options.objectRenderer.renderItem(oRecord.getData(), 0, template);
         };
      },

      /**
       * Returns Remove item custom datacell formatter
       *
       * @method fnRenderCellRemove
       */
      fnRenderCellRemove: function ObjectFinder_fnRenderCellRemove()
      {
         var scope = this;

         /**
          * Remove item custom datacell formatter
          *
          * @method renderCellRemove
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectFinder_renderCellRemove(elCell, oRecord, oColumn, oData)
         {  
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            elCell.innerHTML = '<a href="#" class="remove-item remove-' + scope.eventGroup + '" title="' + scope.msg("form.control.object-picker.remove-item") + '" tabindex="0"><span class="removeIcon">&nbsp;</span></a>';
         };
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Gets current value metadata from the repository
       *
       * @method _getCurrentValueMeta
       * @private
       */
      _getCurrentValueMeta: function ObjectFinder__getCurrentValueMeta(p_div)
      {
         var arrItems = this.options.currentValue.split(",");
         
         var onSuccess = function OF_rCV_onSuccess(response)
         {
            this.currentValueMeta = response.json.data.items;
            YAHOO.Bubbling.fire("renderCurrentValue",
            {
               eventGroup: this
            });
         };
         
         var onFailure = function OF_rCv_onFailure(response)
         {
            this.currentValueMeta = null;
         };
         
         Alfresco.util.Ajax.jsonRequest(
         {
            url: Alfresco.constants.PROXY_URI + "api/forms/picker/items",
            method: "POST",
            dataObj:
            {
               items: arrItems
            },
            successCallback:
            {
               fn: onSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: onFailure,
               scope: this
            }
         });
      },
      
      /**
       * Creates the UI Navigation controls
       *
       * @method _createNavigationControls
       * @private
       */
      _createNavigationControls: function ObjectFinder__createNavigationControls()
      {
         var me = this;
         
         // Up Navigation button
         this.widgets.folderUp = new YAHOO.widget.Button(this.pickerId + "-folderUp",
         {
            disabled: true
         });
         this.widgets.folderUp.on("click", this.onFolderUp, this.widgets.folderUp, this);

         // Navigation drop-down menu
         this.widgets.navigationMenu = new YAHOO.widget.Button(this.pickerId + "-navigator",
         { 
            type: "menu", 
            menu: this.pickerId + "-navigatorMenu",
            lazyloadmenu: false
         });
         
         // force the generated buttons to have a name of "-" so it gets ignored in
         // JSON submit. TODO: remove this when JSON submit behaviour is configurable
         Dom.get(this.pickerId + "-folderUp-button").name = "-";
         Dom.get(this.pickerId + "-navigator-button").name = "-";

         this.widgets.navigationMenu.getMenu().subscribe("click", function (p_sType, p_aArgs)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               YAHOO.Bubbling.fire("parentChanged",
               {
                  eventGroup: me,
                  label: menuItem.cfg.getProperty("label"),
                  nodeRef: menuItem.value
               });
            }
         });
         
         // Optional "Create New" UI controls
         if (Dom.get(this.pickerId + "-createNew"))
         {
            // Create New - OK button
            this.widgets.createNewOK = new YAHOO.widget.Button(this.pickerId + "-createNewOK",
            {
               disabled: true
            });
            this.widgets.createNewOK.on("click", this.onCreateNewOK, this.widgets.createNewOK, this);

            // Create New - Cancel button
            this.widgets.createNewCancel = new YAHOO.widget.Button(this.pickerId + "-createNewCancel",
            {
               disabled: true
            });
            this.widgets.createNewCancel.on("click", this.onCreateNewCancel, this.widgets.createNewCancel, this);
         }
      },

      /**
       * Creates UI controls to support Selected Items
       *
       * @method _createSelectedItemsControls
       * @private
       */
      _createSelectedItemsControls: function ObjectFinder__createSelectedItemsControls()
      {
         var me = this;

         // Setup a DataSource for the selected items list
         this.widgets.dataSource = new YAHOO.util.DataSource([]); 
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY; 
         this.widgets.dataSource.responseSchema =
         { 
            fields: ["type", "hasChildren", "name", "description", "displayPath", "nodeRef"]
         };

         this.widgets.dataSource.doBeforeParseData = function ObjectFinder_doBeforeParseData(oRequest, oFullResponse)
         {
            var updatedResponse = oFullResponse;
            
            if (oFullResponse && oFullResponse.length > 0)
            {
               var items = oFullResponse.data.items;

               // Special case for tags, which we want to render differently to categories
               var index, item;
               for (index in items)
               {
                  if (items.hasOwnProperty(index))
                  {
                     item = items[index];
                     if (item.type == "cm:category" && item.displayPath.indexOf("/categories/Tags") !== -1)
                     {
                        item.type = "tag";
                     }
                  }
               }

               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse =
               {
                  items: items
               };
            }
            
            return updatedResponse;
         };

         // DataTable defintion
         var columnDefinitions =
         [
            { key: "nodeRef", label: "Icon", sortable: false, formatter: this.fnRenderCellIcon(), width: this.options.compactMode ? 10 : 26 },
            { key: "name", label: "Item", sortable: false, formatter: this.fnRenderCellName() },
            { key: "remove", label: "Remove", sortable: false, formatter: this.fnRenderCellRemove(), width: 16 }
         ];
         
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.pickerId + "-selectedItems", columnDefinitions, this.widgets.dataSource,
         {
            MSG_EMPTY: this.msg("form.control.object-picker.selected-items.empty")
         });

         // Hook remove item action click events
         var fnRemoveItemHandler = function OF_cSIC_fnRemoveItemHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var target, rowId, record;

               target = args[1].target;
               rowId = target.offsetParent;
               record = me.widgets.dataTable.getRecord(rowId);
               if (record)
               {
                  me.widgets.dataTable.deleteRow(rowId);
                  YAHOO.Bubbling.fire("selectedItemRemoved",
                  {
                     eventGroup: me,
                     item: record.getData()
                  });
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("remove-" + this.eventGroup, fnRemoveItemHandler);
      },
      
      /**
       * Populate selected items
       *
       * @method _populateSelectedItems
       * @private
       */
      _populateSelectedItems: function ObjectFinder__populateSelectedItems()
      {
         // Empty results table
         this.widgets.dataTable.set("MSG_EMPTY", this.msg("form.control.object-picker.selected-items.empty"));
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());

         this.selectedItems = {};

         for (var item in this.currentValueMeta)
         {
            if (this.currentValueMeta.hasOwnProperty(item))
            {
               this.selectedItems[this.currentValueMeta[item].nodeRef] = this.currentValueMeta[item];
               YAHOO.Bubbling.fire("selectedItemAdded",
               {
                  eventGroup: this,
                  item: this.currentValueMeta[item]
               });
            }
         }
      },
      
      /**
       * Create YUI resizer widget
       *
       * @method _createResizer
       * @private
       */
      _createResizer: function ObjectFinder__createResizer()
      {
         if (!this.widgets.resizer)
         {
            var size = parseInt(Dom.getStyle(this.pickerId + "-body", "width"), 10);
            this.columns[0] = Dom.get(this.pickerId + "-left");
            this.columns[1] = Dom.get(this.pickerId + "-right");
            this.widgets.resizer = new YAHOO.util.Resize(this.pickerId + "-left",
            {
                handles: ["r"],
                minWidth: 200,
                maxWidth: (size - 200)
            });
            this.widgets.resizer.on("resize", function(e)
            {
                var w = e.width;
                Dom.setStyle(this.columns[0], "height", "");
                Dom.setStyle(this.columns[1], "width", (size - w - 8) + "px");
            }, this, true);

            this.widgets.resizer.fireEvent("resize",
            {
               ev: 'resize',
               target: this.widgets.resizer,
               width: this.widgets.resizer.get("width")
            });
         }
      }
   });
})();


/**
 * ObjectRenderer component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ObjectRenderer
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $hasEventInterest = Alfresco.util.hasEventInterest,
      $combine = Alfresco.util.combinePaths;

   /**
    * Internal constants
    */
   var IDENT_CREATE_NEW = "~CREATE~NEW~";


   /**
    * ObjectRenderer constructor.
    * 
    * @param {object} Instance of the ObjectFinder
    * @return {Alfresco.ObjectRenderer} The new ObjectRenderer instance
    * @constructor
    */
   Alfresco.ObjectRenderer = function(objectFinder)
   {
      this.objectFinder = objectFinder;
      
      Alfresco.ObjectRenderer.superclass.constructor.call(this, "Alfresco.ObjectRenderer", objectFinder.pickerId, ["button", "menu", "container", "datasource", "datatable"]);
      /**
       * Decoupled event listeners
       */
      this.eventGroup = objectFinder.eventGroup;
      YAHOO.Bubbling.on("refreshItemList", this.onRefreshItemList, this);
      YAHOO.Bubbling.on("parentChanged", this.onParentChanged, this);
      YAHOO.Bubbling.on("selectedItemAdded", this.onSelectedItemChanged, this);
      YAHOO.Bubbling.on("selectedItemRemoved", this.onSelectedItemChanged, this);

      // Initialise prototype properties
      this.addItemButtons = {};

      return this;
   };
   
   YAHOO.extend(Alfresco.ObjectRenderer, Alfresco.component.Base,
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
          * Parent node for browsing
          *
          * @property parentNodeRef
          * @type string
          */
         parentNodeRef: "alfresco://company/home",

         /**
          * The type of the item to find
          *
          * @property itemType
          * @type string
          */
         itemType: "cm:content",

         /**
          * Parameters to be passed to the data webscript
          *
          * @property params
          * @type string
          */
         params: "",

         /**
          * Compact mode flag
          * 
          * @property compactMode
          * @type boolean
          * @default false
          */
         compactMode: false,

         /**
          * Maximum number of items to display in the results list
          * 
          * @property maxSearchResults
          * @type int
          * @default 100
          */
         maxSearchResults: 100
      },

      /**
       * Object container for storing button instances, indexed by item id.
       * 
       * @property addItemButtons
       * @type object
       */
      addItemButtons: null,

      /**
       * Create new item input control Dom Id
       * 
       * @property createNewItemId
       * @type string
       */
      createNewItemId: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ObjectRenderer_onReady()
      {
         this._createControls();
      },

      
      /**
       * PUBLIC INTERFACE
       */

      /**
       * The picker has just been shown
       *
       * @method onPickerShow
       */
      onPickerShow: function ObjectRenderer_onPickerShow()
      {
         this.addItemButtons = {};
         Dom.get(this.objectFinder.pickerId).focus();
      },

      /**
       * Generate item icon URL
       *
       * @method getIconURL
       * @param item {object} Item object literal
       * @param size {number} Icon size (16, 32)
       */
      getIconURL: function ObjectRenderer_getIconURL(item, size)
      {
         return Alfresco.constants.URL_CONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(item.name, item.type, size);
      },
      
      /**
       * Render item using a passed-in template
       *
       * @method renderItem
       * @param item {object} Item object literal
       * @param iconSize {number} Icon size (16, 32)
       * @param template {string} String with "{parameter}" style placeholders
       */
      renderItem: function ObjectRenderer_renderItem(item, iconSize, template)
      {
         var me = this;
         
         var renderHelper = function(p_key, p_value, p_metadata)
         {
            if (p_key.toLowerCase() == "icon")
            {
               return '<img src="' + me.getIconURL(item, iconSize) + '" width="' + iconSize + '" alt="' + $html(item.description) + '" title="' + $html(item.name) + '" />'; 
            }
            return $html(p_value);
         };
         
         return YAHOO.lang.substitute(template, item, renderHelper);
      },

      
      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Refresh item list event handler
       *
       * @method onRefreshItemList
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onRefreshItemList: function ObjectRenderer_onRefreshItemList(layer, args)
      {   
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            this._updateItems(this.options.parentNodeRef);
         }
      },

      /**
       * Parent changed event handler
       *
       * @method onParentChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onParentChanged: function ObjectRenderer_onParentChanged(layer, args)
      {   
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.nodeRef)
            {
               this._updateItems(obj.nodeRef);
            }
         }
      },

      /**
       * Selected Item Changed event handler
       * Handles selectedItemAdded and selectedItemRemoved events
       *
       * @method onSelectedItemChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSelectedItemChanged: function ObjectRenderer_onSelectedItemChanged(layer, args)
      {   
         // Check the event is directed towards this instance
         if ($hasEventInterest(this, args))
         {
            var obj = args[1];
            if (obj && obj.item)
            {
               var button;
               for (var id in this.addItemButtons)
               {
                  if (this.addItemButtons.hasOwnProperty(id))
                  {
                     button = this.addItemButtons[id];
                     if (typeof button == "string")
                     {
                        Dom.setStyle(button, "display", this.objectFinder.canItemBeSelected(id) ? "inline" : "none");
                     }
                     else
                     {
                        button.set("disabled", !this.objectFinder.canItemBeSelected(id));
                     }
                  }
               }
            }
         }
      },

      /**
       * Returns Icon datacell formatter
       *
       * @method fnRenderItemIcon
       */
      fnRenderItemIcon: function ObjectRenderer_fnRenderItemIcon()
      {
         var scope = this;
      
         /**
          * Icon datacell formatter
          *
          * @method renderItemIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectRenderer_renderItemIcon(elCell, oRecord, oColumn, oData)
         {
            var iconSize = scope.options.compactMode ? 16 : 32;

            oColumn.width = iconSize - 6;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            // Create New item cell type
            if (oRecord.getData("type") == IDENT_CREATE_NEW)
            {
               Dom.addClass(this.getTrEl(elCell), "create-new-row");
               var obj =
               {
                  type: scope.options.createNewItemIcon,
                  description: scope.msg("form.control.object-picker.create-new")
               };
               elCell.innerHTML = scope.renderItem(obj, iconSize, '<div class="icon' + iconSize + '"><span class="new-item-overlay"></span>{icon}</div>');
               return;
            }

            elCell.innerHTML = scope.renderItem(oRecord.getData(), iconSize, '<div class="icon' + iconSize + '">{icon}</div>');
         };
      },

      /**
       * Returns Name datacell formatter
       *
       * @method fnRenderItemName
       */
      fnRenderItemName: function ObjectRenderer_fnRenderItemName()
      {
         var scope = this;
      
         /**
          * Name datacell formatter
          *
          * @method renderItemName
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectRenderer_renderItemName(elCell, oRecord, oColumn, oData)
         {
            var template = '';

            // Create New item cell type
            if (oRecord.getData("type") == IDENT_CREATE_NEW)
            {
               scope.createNewItemId = Alfresco.util.generateDomId();
               elCell.innerHTML = '<input id="' + scope.createNewItemId + '" type="text" class="create-new-input" tabindex="0" />';
               return;
            }

            if (oRecord.getData("hasChildren"))
            {
               template += '<h3 class="item-name"><a href="#" class="theme-color-1 parent-' + scope.eventGroup + '">{name}</a></h3>';
            }
            else
            {
               template += '<h3 class="item-name">{name}</h3>';
            }
      
            if (!scope.options.compactMode)
            {
               template += '<div class="description">{description}</div>';
            }

            elCell.innerHTML = scope.renderItem(oRecord.getData(), 0, template);
         };
      },

      /**
       * Returns Add button datacell formatter
       *
       * @method fnRenderCellAdd
       */
      fnRenderCellAdd: function ObjectRenderer_fnRenderCellAdd()
      {
         var scope = this;
      
         /**
          * Add button datacell formatter
          *
          * @method renderCellAvatar
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function ObjectRenderer_renderCellAdd(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            
            var containerId = Alfresco.util.generateDomId(),
               button;

            // Create New item cell type
            if (oRecord.getData("type") == IDENT_CREATE_NEW)
            {
               if (scope.options.compactMode)
               {
                  elCell.innerHTML = '<a href="#" class="create-new-item create-new-item-' + scope.eventGroup + '" title="' + scope.msg("form.control.object-picker.create-new") + '" tabindex="0"><span class="createNewIcon">&nbsp;</span></a>';
               }
               else
               {
                  Dom.setStyle(elCell.parentNode, "text-align", "right");
                  elCell.innerHTML = '<span id="' + containerId + '"></span>';
                  button = new YAHOO.widget.Button(
                  {
                     label: scope.msg("button.create"),
                     container: containerId,
                     onclick:
                     {
                        fn: scope.onCreateNewItem,
                        scope: scope
                     }
                  });
               }
               return;
            }

            if (oRecord.getData("selectable"))
            {
               var nodeRef = oRecord.getData("nodeRef");

               if (scope.options.compactMode)
               {
                  var style = "";
                  if (!scope.objectFinder.canItemBeSelected(nodeRef))
                  {
                     style = 'style="display: none"';
                  }
                  elCell.innerHTML = '<a id="' + containerId + '" href="#" ' + style + ' class="add-item add-' + scope.eventGroup + '" title="' + scope.msg("form.control.object-picker.add-item") + '" tabindex="0"><span class="addIcon">&nbsp;</span></a>';
                  scope.addItemButtons[nodeRef] = containerId;
               }
               else
               {
                  Dom.setStyle(elCell.parentNode, "text-align", "right");
                  elCell.innerHTML = '<span id="' + containerId + '"></span>';

                  var onItemAdded = function OR__cC_rCAB_onItemAdded(event, p_obj)
                  {
                     YAHOO.Bubbling.fire("selectedItemAdded",
                     {
                        eventGroup: scope,
                        item: p_obj.getData()
                     });
                  };

                  button = new YAHOO.widget.Button(
                  {
                     type: "button",
                     label: scope.msg("button.add") + " >>",
                     name: containerId + "-button",
                     container: containerId,
                     onclick:
                     {
                        fn: onItemAdded,
                        obj: oRecord,
                        scope: scope
                     }
                  });
                  scope.addItemButtons[nodeRef] = button;

                  if (!scope.objectFinder.canItemBeSelected(nodeRef))
                  {
                     button.set("disabled", true);
                  }
               }
            }
         };
      },

      /**
       * Create New Item button click handler
       *
       * @method onCreateNewItem
       */
      onCreateNewItem: function ObjectFinder_onCreateNewItem()
      {
         var elInput = Dom.get(this.createNewItemId),
            uri = $combine("/", this.options.createNewItemUri).substring(1),
            itemName;
         
         if (elInput)
         {
            itemName = elInput.value;
            if (itemName === null || itemName.length < 1)
            {
                return;
            }
            /**
             * TODO: Validation?!
             */
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.PROXY_URI + uri,
               dataObj:
               {
                  name: itemName
               },
               successCallback:
               {
                  fn: function(p_obj)
                  {
                     var response = p_obj.json;
                     if (response && response.nodeRef)
                     {
                        var item =
                        {
                           type: this.options.itemType,
                           name: response.name,
                           nodeRef: response.nodeRef,
                           selectable: true
                        };

                        if (!response.itemExists)
                        {
                           // Item didn't exist - display success message
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: this.msg("form.control.object-picker.create-new.success", response.name)
                           });
                           // Add the new item to the DataTable
                           this.widgets.dataTable.addRow(item);
                        }

                        // Automatically select the new item
                        YAHOO.Bubbling.fire("selectedItemAdded",
                        {
                           eventGroup: this,
                           item: item
                        });
                     }
                     elInput.value = "";
                  },
                  scope: this
               },
               failureMessage: this.msg("form.control.object-picker.create-new.failure")
            });
         }
      },

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Creates UI controls
       *
       * @method _createControls
       */
      _createControls: function ObjectRenderer__createControls()
      {
         var me = this;

         // DataSource definition  
         var pickerChildrenUrl = Alfresco.constants.PROXY_URI + "api/forms/picker/" + this.options.itemFamily + "/";
         this.widgets.dataSource = new YAHOO.util.DataSource(pickerChildrenUrl);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "items",
             fields: ["type", "hasChildren", "name", "description", "displayPath", "nodeRef", "selectable"]
         };

         this.widgets.dataSource.doBeforeParseData = function ObjectRenderer_doBeforeParseData(oRequest, oFullResponse)
         {
            var updatedResponse = oFullResponse;
            
            if (oFullResponse)
            {
               var items = oFullResponse.data.items;

               // Crop item list to max length if required
               if (items.length > me.options.maxSearchResults)
               {
                  items = items.slice(0, me.options.maxSearchResults-1);
               }
               
               // Add the special "Create new" record if required
               if (me.options.createNewItemUri !== "")
               {
                  items = [{ type: IDENT_CREATE_NEW }].concat(items);
               }
               
               // Special case for tags, which we want to render differently to categories
               var index, item;
               for (index in items)
               {
                  if (items.hasOwnProperty(index))
                  {
                     item = items[index];
                     if (item.type == "cm:category" && item.displayPath.indexOf("/categories/Tags") !== -1)
                     {
                        item.type = "tag";
                        // Also set the parent type to display the drop-down correctly. This may need revising for future type support.
                        oFullResponse.data.parent.type = "tag";
                     }
                  }
               }
               
               // Notify interested parties of the parent details
               YAHOO.Bubbling.fire("parentDetails",
               {
                  eventGroup: me,
                  parent: oFullResponse.data.parent
               });

               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse =
               {
                  items: items
               };
            }
            
            return updatedResponse;
         };

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "nodeRef", label: "Icon", sortable: false, formatter: this.fnRenderItemIcon(), width: this.options.compactMode ? 10 : 26 },
            { key: "name", label: "Item", sortable: false, formatter: this.fnRenderItemName() },
            { key: "add", label: "Add", sortable: false, formatter: this.fnRenderCellAdd(), width: this.options.compactMode ? 16 : 80 }
         ];

         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: this.msg("form.control.object-picker.items-list.loading")
         });

         // Rendering complete event handler
         this.widgets.dataTable.subscribe("renderEvent", function()
         {
            if (this.options.createNewItemUri !== "")
            {
               this.widgets.enterListener = new KeyListener(this.createNewItemId,
               {
                  keys: KeyListener.KEY.ENTER
               },
               {
                  fn: function(eventName, keyEvent, obj)
                  {
                     this.onCreateNewItem();
                     Event.stopEvent(keyEvent[1]);
                     return false;
                  },
                  scope: this,
                  correctScope: true
               }, YAHOO.env.ua.ie > 0 ? KeyListener.KEYDOWN : "keypress");
               this.widgets.enterListener.enable();
            }
         }, this, true);
         
         // Hook add item action click events (for Compact mode)
         var fnAddItemHandler = function OR__cC_fnAddItemHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var target, rowId, record;

               target = args[1].target;
               rowId = target.offsetParent;
               record = me.widgets.dataTable.getRecord(rowId);
               if (record)
               {
                  YAHOO.Bubbling.fire("selectedItemAdded",
                  {
                     eventGroup: me,
                     item: record.getData()
                  });
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("add-" + this.eventGroup, fnAddItemHandler);

         // Hook create new item action click events (for Compact mode)
         var fnCreateNewItemHandler = function OR__cC_fnCreateNewItemHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               me.onCreateNewItem();
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("create-new-item-" + this.eventGroup, fnCreateNewItemHandler);

         // Hook navigation action click events
         var fnNavigationHandler = function OR__cC_fnNavigationHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var target, rowId, record;
         
               target = args[1].target;
               rowId = target.offsetParent;
               record = me.widgets.dataTable.getRecord(rowId);
               if (record)
               {
                  YAHOO.Bubbling.fire("parentChanged",
                  {
                     eventGroup: me,
                     label: record.getData("name"),
                     nodeRef: record.getData("nodeRef")
                  });
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("parent-" + this.eventGroup, fnNavigationHandler);

      },
      
      /**
       * Updates item list by calling data webscript
       *
       * @method _updateItems
       * @param nodeRef {string} Parent nodeRef
       */
      _updateItems: function ObjectRenderer__updateItems(nodeRef)
      {
         // Empty results table
         this.widgets.dataTable.set("MSG_EMPTY", this.msg("form.control.object-picker.items-list.loading"));
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         var successHandler = function ObjectRenderer__uI_successHandler(sRequest, oResponse, oPayload)
         {
            this.options.parentNodeRef = nodeRef;
            this.widgets.dataTable.set("MSG_EMPTY", this.msg("form.control.object-picker.items-list.empty"));
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };
         
         var failureHandler = function ObjectRenderer__uI_failureHandler(sRequest, oResponse)
         {
            if (oResponse.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload();
            }
            else
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  this.widgets.dataTable.set("MSG_ERROR", response.message);
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
               }
               catch(e)
               {
               }
            }
         };
         
         var url = nodeRef.replace("://", "/") + "/children?selectableType=" + this.options.itemType;
         if (this.options.params)
         {
            url += "&" + this.options.params;
         }
         this.widgets.dataSource.sendRequest(url,
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      }
   });
})();