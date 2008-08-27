/**
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * Document Library "Details" module for Document Library.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.DoclibWorkflow
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;


   Alfresco.module.DoclibWorkflow = function(htmlId)
   {
      // Mandatory properties
      this.name = "Alfresco.module.DoclibWorkflow";
      this.id = htmlId;

      // Initialise prototype properties
      this.widgets = {};
      this.modules = {};

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "json", "calendar"], this.onComponentsLoaded, this);

      return this;
   };
   
   Alfresco.module.DoclibWorkflow.prototype =
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",

         /**
          * Files to be included in workflow
          *
          * @property: files
          * @type: object
          * @default: null
          */
         files: null,

         /**
          * Width for the dialog
          *
          * @property: width
          * @type: integer
          * @default: 50em
          */
         width: "50em"
      },
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,

      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: null,

      /**
       * Container element for template in DOM.
       * 
       * @property containerDiv
       * @type DOMElement
       */
      containerDiv: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.module.DoclibWorkflow} returns 'this' for method chaining
       */
      setOptions: function DLW_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.module.DoclibWorkflow} returns 'this' for method chaining
       */
      setMessages: function DLW_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DLW_onComponentsLoaded()
      {
      },

      /**
       * Main entry point
       * @method showDialog
       */
      showDialog: function DLW_showDialog()
      {
         if (!this.containerDiv)
         {
            // Decoupled event listeners
            YAHOO.Bubbling.on("personSelected", this.onPersonSelected, this);

            // Load the UI template from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/workflow",
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load Document Library Workflow template",
               execScripts: true
            });
         }
         else
         {
            // Show the dialog
            this._showDialog();
         }
      },

      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function DLW_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         this.containerDiv = document.createElement("div");
         this.containerDiv.setAttribute("style", "display:none");
         this.containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var dialogDiv = Dom.getFirstChild(this.containerDiv);
         while (dialogDiv && dialogDiv.tagName.toLowerCase() != "div")
         {
            dialogDiv = Dom.getNextSibling(dialogDiv);
         }
         
         // Create and render the YUI dialog
         this.widgets.dialog = new YAHOO.widget.Panel(dialogDiv,
         {
            modal: true,
            draggable: false,
            fixedcenter: true,
            close: true,
            visible: false,
            width: this.options.width
         });

         this.widgets.dialog.render(document.body);

         // Load the People Finder component from the server
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/people-finder",
            dataObj:
            {
               htmlid: this.id + "-peoplefinder",
               site: this.options.siteId,
               minSearchTermLength: 0
            },
            successCallback:
            {
               fn: this.onPeopleFinderLoaded,
               scope: this
            },
            failureMessage: "Could not load People Finder component",
            execScripts: true
         });
      },

      /**
       * Event callback when People Finder component has been loaded
       *
       * @method onPeopleFinderLoaded
       * @param response {object} Server response from load component XHR request
       */
      onPeopleFinderLoaded: function DLW_onPeopleFinderLoaded(response)
      {
         var me = this;

         // Inject the component from the XHR request into it's placeholder DIV element
         var finderDiv = Dom.get(this.id + "-peoplefinder");
         finderDiv.innerHTML = response.serverResponse.responseText;

         // Find the People Finder by container ID
         this.modules.peopleFinder = Alfresco.util.ComponentManager.find(
         {
            id: this.id + "-peoplefinder"
         })[0];
         // Set the correct options for our use
         this.modules.peopleFinder.setOptions(
         {
            compactMode: true,
            singleSelectMode: true
         });

         // Due date checkbox
         Event.addListener(this.id + "-dueDate-checkbox", "click", this.onDueDate, this, true);

         // OK button
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok", null,
         {
            type: "submit"
         });

         // Cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onCancel);

         // Form definition
         this.modules.form = new Alfresco.forms.Form(this.id + "-form");
         
         // Validation
         // Selected people: at least one
         this.modules.form.addValidation(this.id + "-peopleselected", function DLW_oPFL_validSelectedPeople(field, args, event, form, silent)
         {
            return (me.widgets.dataTable.getRecordSet().getLength() > 0);
         }, null, "keyup");
         // Comment: mandatory value
         this.modules.form.addValidation(this.id + "-comment", Alfresco.forms.validation.mandatory, null, "keyup");
         this.modules.form.setShowSubmitStateDynamically(true, false);

         // OK button submits the form
         this.modules.form.setSubmitElements(this.widgets.okButton);

         // JSON submit type, but we'll be intercepting the actual Ajax request
         this.modules.form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onFailure,
               scope: this
            }
         });
         this.modules.form.setSubmitAsJSON(true);
         this.modules.form.doBeforeAjaxRequest =
         {
            fn: this.doBeforeAjaxRequest,
            scope: this
         }

         // Setup a DataSource for the selected people list
         this.widgets.dataSource = new YAHOO.util.DataSource([]); 
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY; 
         this.widgets.dataSource.responseSchema =
         { 
            fields: ["userName", "firstName", "lastName"]
         };

         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.InvitationList class (via the "me" variable).
          */

         /**
          * Person custom datacell formatter
          *
          * @method renderCellPerson
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellPerson = function DLW_renderCellPerson(elCell, oRecord, oColumn, oData)
         {
            var name = oRecord.getData("firstName") + " " + oRecord.getData("lastName");
            var desc = '<h3 class="name">' + $html(name) + '</h3>';
            elCell.innerHTML = desc;
         };

         /**
          * Remove selected person custom datacell formatter
          *
          * @method renderCellRemove
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellRemove = function DLW_renderCellRemove(elCell, oRecord, oColumn, oData)
         {  
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var desc = '<a href="#" class="remove-person" title="' + me._msg("tooltip.remove-person") + '"></a>';
            elCell.innerHTML = desc;
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "userName", label: "User", sortable: false, formatter: renderCellPerson
         },
         {
            key: "remove", label: "Remove", sortable: false, formatter: renderCellRemove, width: 16
         }];

         // DataTable definition
         YAHOO.widget.DataTable.MSG_EMPTY = "";
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-peopleselected", columnDefinitions, this.widgets.dataSource);

         // Hook remove person action click events
         var fnRemoveHandler = function DLW_fnRemoveHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var target, rowId, record, userName;
               
               target = args[1].target;
               rowId = target.offsetParent;
               record = me.widgets.dataTable.getRecord(rowId);
               if (record)
               {
                  userName = record.getData("userName");
                  YAHOO.widget.DataTable.MSG_EMPTY = "";
                  me.widgets.dataTable.deleteRow(rowId);
                  YAHOO.Bubbling.fire("personDeselected",
                  {
                     userName: userName
                  });
                  me.modules.form.updateSubmitElements();
               }
               args[1].stop = true;
            }
      		 
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("remove-person", fnRemoveHandler);

         // Hook date click events
         var fnDueDateClick = function DLW_fnDueDateClick(layer, args)
         {
            me.widgets.calendarOverlay.show();
            args[1].stop = true;
         }
         YAHOO.Bubbling.addDefaultAction("due-date", fnDueDateClick);

         // Pre-render our pop-up calendar into an overlay
         this.widgets.calendar = new YAHOO.widget.Calendar(this.id + "-calendar",
         {
            iframe: false,
            hide_blank_weeks: true,
            mindate: new Date()
         });

         this.widgets.calendarOverlay = new YAHOO.widget.Dialog(this.id + "-calendarOverlay",
         {
            close: false,
            context: [this.id + "-dueDate", "tl", "tl"],
            draggable: false,
            visible: false,
            width: "200px"
         });
         this.widgets.calendar.render();
         this.widgets.calendarOverlay.render();
         
         this.widgets.calendarOverlay.hide();

         // Tell Dialog it's contents have changed, Currently used by container for IE6/Safari2 to sync underlay size
         this.widgets.calendar.renderEvent.subscribe(function()
         {
            me.widgets.dialog.fireEvent("changeContent");
         });
         this.widgets.calendar.selectEvent.subscribe(this.onDueDateSelected, this, true);
         
         // Show the dialog
         this._showDialog();
      },

      /**
       * Override function for intercepting AJAX form submission.
       * Returning false from the override will prevent the Forms Runtime from submitting the data.
       */
      doBeforeAjaxRequest: function DLW_doBeforeAjaxRequest(p_config, p_obj)
      {
         var files, multipleFiles = [], multiplePeople = [];

         // Single/multi files into array of nodeRefs
         if (YAHOO.lang.isArray(this.options.files))
         {
            files = this.options.files;
         }
         else
         {
            files = [this.options.files];
         }
         for (var i = 0, j = files.length; i < j; i++)
         {
            multipleFiles.push(files[i].nodeRef);
         }

         // Single/multi people into array of userNames
         var recordSet = this.widgets.dataTable.getRecordSet();
         for (i = 0, j = recordSet.getLength(); i < j; i++)
         {
            multiplePeople.push(recordSet.getRecord(i).getData("userName"));
         }

         // Add the files and users to the config the Forms Runtime created from parsing the form
         p_config.dataObj.nodeRefs = multipleFiles;
         p_config.dataObj.people = multiplePeople;
         
         // Make the Ajax request
         Alfresco.util.Ajax.jsonRequest(p_config);
         
         // Prevent the Forms Runtime from making the Ajax request
         return false;
      },
      
      /**
       * Workflow form submit success handler
       *
       * @method onSuccess
       * @param p_data {object} Server response object
       */
      onSuccess: function DLW_onSuccess(p_data)
      {
         var result;
         var successCount = p_data.json.successCount;
         var failureCount = p_data.json.failureCount;

         this.widgets.dialog.hide();

         // Did the operation succeed?
         if (!p_data.json.overallSuccess)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.workflow.failure")
            });
            return;
         }

         YAHOO.Bubbling.fire("filesWorkflowed",
         {
            successCount: successCount,
            failureCount: failureCount
         });
         
         for (var i = 0, j = p_data.json.totalResults; i < j; i++)
         {
            result = p_data.json.results[i];
            
            if (result.success)
            {
               YAHOO.Bubbling.fire(result.type == "folder" ? "folderWorkflowed" : "fileWorkflowed",
               {
                  multiple: true,
                  nodeRef: result.nodeRef
               });
            }
         }

         Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.workflow.success", successCount)
         });
      },

      /**
       * Workflow form submit failure handler
       *
       * @method onFailure
       * @param p_data {object} Server response object
       */
      onFailure: function DLW_onFailure(p_data)
      {
         this.widgets.dialog.hide();

         Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.workflow.failure")
         });
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR ACTIONS
       * Disconnected event handlers for action event notification
       */

      /**
       * Person selected handler
       *
       * @method onPersonSelected
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onPersonSelected: function DLW_onPersonSelected(layer, args)
      {   
         var obj = args[1];
         // Should be a person in the arguments
         if (obj && (obj.userName !== null))
         {
            var person =
            {
               userName: obj.userName,
               firstName: obj.firstName,
               lastName: obj.lastName,
               email: obj.email
            }

            // Add the user to the selected list
            this.widgets.dataTable.addRow(person);
            this.modules.form.updateSubmitElements();
         }
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Dialog Cancel button event handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function DLW_onCancel(e, p_obj)
      {
         this.widgets.calendarOverlay.hide();
         this._hideDialog();
      },

      /**
       * Due Date checkbox click event handler
       *
       * @method onDueDate
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onDueDate: function DLW_onDueDate(e, p_obj)
      {
         var checked = Event.getTarget(e).checked;
         if (checked)
         {
            this.widgets.calendarOverlay.show();
         }
         else
         {
            this.widgets.calendarOverlay.hide();
            Dom.get(this.id + "-dueDate").innerHTML = this._msg("label.due-date.none");
            Dom.get(this.id + "-date").value = "";
         }
      },
      
      /**
       * Due Date calendar date selected event handler
       *
       * @method onDueDateSelected
       * @param p_type {string} Event type
       * @param p_args {array} Event arguments
       * @param p_obj {object} Object passed back from subscribe method
       */
      onDueDateSelected: function DLW_onDueDateSelected(p_type, p_args, p_obj)
      {
         var selected = p_args[0];
         var selDate = this.widgets.calendar.toDate(selected[0]);
         Dom.get(this.id + "-dueDate").innerHTML = '<a href="#" class="due-date">' + Alfresco.util.formatDate(selDate, this._msg("format.due-date")) + '</a>';
         Dom.get(this.id + "-date").value = selDate.toString();
         this.widgets.calendarOverlay.hide();
         this.modules.form.updateSubmitElements();
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Internal show dialog function
       * @method _showDialog
       */
      _showDialog: function DLW__showDialog()
      {
         // Grab the form element
         var formElement = Dom.get(this.id + "-form");

         // Submission Url
         formElement.attributes.action.nodeValue = Alfresco.constants.PROXY_URI + "slingshot/doclib/action/assign-workflow";
         
         // Dialog title
         var titleDiv = Dom.get(this.id + "-title");
         if (YAHOO.lang.isArray(this.options.files))
         {
            titleDiv.innerHTML = this._msg("title.multi", this.options.files.length)
         }
         else
         {
            var fileSpan = '<span class="light">' + this.options.files.displayName + '</span>';
            titleDiv.innerHTML = this._msg("title.single", fileSpan)
         }

         // Clear results DataTable
         YAHOO.widget.DataTable.MSG_EMPTY = "";
         var recordCount = this.widgets.dataTable.getRecordSet().getLength();
         this.widgets.dataTable.deleteRows(0, recordCount);
         
         // Clear people finder
         this.modules.peopleFinder.clearResults();

         // Clear date
         Dom.get(this.id + "-dueDate-checkbox").checked = false;
         Dom.get(this.id + "-dueDate").innerHTML = this._msg("label.due-date.none");
         Dom.get(this.id + "-date").value = "";

         // Clear comment
         Dom.get(this.id + "-comment").value = "";

         // Initialise the Forms Runtime
         this.modules.form.init();

         // Show the dialog
         this.widgets.dialog.show();

         // Fix Firefox caret issue
         Alfresco.util.caretFix(this.id + "-form");

         // We're in a popup, so need the tabbing fix
         this.modules.form.applyTabFix();
         
         // Register the ESC key to close the dialog
         var escapeListener = new YAHOO.util.KeyListener(document,
         {
            keys: YAHOO.util.KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this.onCancel();
            },
            scope: this,
            correctScope: true
         });
         escapeListener.enable();

         // Set focus to workflow type input
         Dom.get(this.id + "-type").focus();
      },

      /**
       * Hide the dialog, removing the caret-fix patch
       *
       * @method _hideDialog
       * @private
       */
      _hideDialog: function DLW__hideDialog()
      {
         // Grab the form element
         var formElement = Dom.get(this.id + "-form");

         // Undo Firefox caret issue
         Alfresco.util.undoCaretFix(formElement);
         this.widgets.dialog.hide();
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
       _msg: function DLTB__msg(messageId)
       {
          return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
       }
   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.DoclibWorkflow(null);