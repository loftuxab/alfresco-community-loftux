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
 * DispositionEdit component.
 *
 * User can add, edit and remove action (steps) in the disposition schedule
 * and to each action ad and remove events.
 * Meta data properties can be edited on a different page.
 *
 * @namespace Alfresco
 * @class Alfresco.DispositionEdit
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
    * DispositionEdit constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DispositionEdit} The new component instance
    * @constructor
    */
   Alfresco.DispositionEdit = function DispositionEdit_constructor(htmlId)
   {
      Alfresco.DispositionEdit.superclass.constructor.call(this, "Alfresco.DispositionEdit", htmlId, ["button", "container"]);

      return this;
   };

   YAHOO.extend(Alfresco.DispositionEdit, Alfresco.component.Base,
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
          * The nodeRef to the object that owns the disposition schedule that is configured
          *
          * @property nodeRef
          * @type {string}
          */
         nodeRef: null,

         /**
          * The url to the filePlan that is configured
          *
          * @property fileplanUrl
          * @type {string}
          */
         siteId: null,

         /**
          * The events available and information about if they are autmatic or not
          *
          * @property events
          * @type {object} with objects like { label: string, automatic: boolean}
          *       and the event name {string} as the key.
          */
         events: {}
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function DispositionEdit_onReady()
      {
         // Save a reference to important elements
         this.widgets.actionListEl = Dom.get(this.id + "-actionList");
         this.widgets.flowButtons = Dom.get(this.id + "-flowButtons");

         // Save reference to buttons so we can change label and such later
         this.widgets.createButton = Alfresco.util.createYUIButton(this, "createaction-button", this.onCreateActionButtonClick);
         this.widgets.createButton.set("disabled", true);
         this.widgets.doneButton = Alfresco.util.createYUIButton(this, "done-button", this.onDoneActionsButtonClick);

         // Get the templates and remove them from the DOM
         this.widgets.eventTemplateEl = Dom.get(this.id + "-event-template");
         this.widgets.eventTemplateEl.parentNode.removeChild(this.widgets.eventTemplateEl);
         this.widgets.actionTemplateEl = Dom.get(this.id + "-action-template");
         this.widgets.actionTemplateEl.parentNode.removeChild(this.widgets.actionTemplateEl);

         this._loadActions();
      },

      /**
       * Loads actions from the server
       *
       * @method onReady
       * @private
       */
      _loadActions: function DispositionEdit__loadActions()
      {
         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI_RELATIVE + "api/node/" + this.options.nodeRef.replace(":/", "") + "/dispositionschedule",
            successCallback:
            {
               fn: function(response)
               {
                  if(response.json)
                  {
                     var schedule = response.json.data;
                     var actions = schedule.actions ? schedule.actions : [];
                     for(var i = 0; i < actions.length; i++)
                     {
                        var action = actions[i];
                        var actionEl = this._createAction(action);
                        actionEl = this.widgets.actionListEl.appendChild(actionEl);
                        this._setupActionForm(action, actionEl);
                     }
                  }
                  this.widgets.createButton.set("disabled", false);                  
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {                  
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.getActionFailure", this.name)
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Create a action in the list
       *
       * @method _createAction
       * @param action The action info object
       * @private
       */
      _createAction: function DispositionEdit__createAction(action)
      {
         // Clone template
         var actionEl = this.widgets.actionTemplateEl.cloneNode(true);
         var elId = Dom.generateId();
         actionEl.setAttribute("id", elId);

         // Id
         Dom.getElementsByClassName("id", "input", actionEl)[0].value = action.id;

         // Period
         var period = action.period ? action.period.split("|") : [];

         // No
         Dom.getElementsByClassName("no", "div", actionEl)[0].innerHTML = action.index + 1;

         // Description
         Dom.getElementsByClassName("description", "textarea", actionEl)[0].value = action.description;

         // Action
         var actionType = action.name;
         var actionTypeSelect = Dom.getElementsByClassName("action-type", "select", actionEl)[0];
         Alfresco.util.setSelectedIndex(actionTypeSelect, actionType);

         // Period Unit
         var periodUnit = period && period.length > 0 ? period[0] : null;
         var periodUnitSelect = Dom.getElementsByClassName("period-unit", "select", actionEl)[0];
         Alfresco.util.setSelectedIndex(periodUnitSelect, periodUnit);

         // Period Amount
         var periodAmount = period && period.length > 1 ? period[1] : null;
         var periodAmountEl = Dom.getElementsByClassName("period-amount", "input", actionEl)[0];
         periodAmountEl.value = periodAmount ? periodAmount : "";
         periodAmountEl.setAttribute("id", elId + "-periodAmount");

         // Period Action
         var periodAction = action.periodProperty;
         var periodActionSelect = Dom.getElementsByClassName("period-action", "select", actionEl)[0];
         Alfresco.util.setSelectedIndex(periodActionSelect, periodAction);

         // Add event button
         var addEventEl = Dom.getElementsByClassName("addevent", "span", actionEl)[0];
         var addEventButton = Alfresco.util.createYUIButton(this, "addevent-button", null, {}, addEventEl);
         addEventButton.on("click", this.onAddEventButtonClick, actionEl, this);

         // Enable/Disable period & events section
         var periodEnabledCheckBox = Dom.getElementsByClassName("period-enabled", "input", actionEl)[0];
         periodEnabledCheckBox.checked =  periodUnit || periodAmount;
         this._disablePeriodSection(!periodEnabledCheckBox.checked, actionEl);
         var eventsEnabledCheckBox = Dom.getElementsByClassName("events-enabled", "input", actionEl)[0];
         eventsEnabledCheckBox.checked = action.events && action.events.length > 0;
         this._disableEventsSection(!eventsEnabledCheckBox.checked, actionEl, addEventButton);
         if(!periodEnabledCheckBox.checked || !eventsEnabledCheckBox.checked)
         {
            Dom.addClass(actionEl, "relation-disabled");
         }

         // Add listeners to toggle enabling/disabling
         Event.addListener(periodEnabledCheckBox, "click", this.onRelationEnablingCheckBoxClick,
         {
            actionEl: actionEl,
            checkBoxEls: [periodEnabledCheckBox, eventsEnabledCheckBox]
         }, this);
         Event.addListener(periodEnabledCheckBox, "click", function(e, obj)
         {
            var disabled = obj.periodEnabledCheckBox.checked ? false : true;
            this._disablePeriodSection(disabled, actionEl);
         },
         {
            periodEnabledCheckBox: periodEnabledCheckBox
         }, this);
         Event.addListener(eventsEnabledCheckBox, "click", this.onRelationEnablingCheckBoxClick,
         {
            actionEl: actionEl,
            checkBoxEls: [eventsEnabledCheckBox, periodEnabledCheckBox]
         }, this);
         Event.addListener(eventsEnabledCheckBox, "click", function(e, obj)
         {
            var disabled = obj.eventsEnabledCheckBox.checked ? false : true;
            this._disableEventsSection(disabled, actionEl, addEventButton);
         },
         {
            eventsEnabledCheckBox: eventsEnabledCheckBox
         }, this);

         // Relation
         var relationSelect = Dom.getElementsByClassName("relation", "select", actionEl)[0];
         Dom.addClass(actionEl, action.eligibleOnFirstCompleteEvent ? "or" : "and");
         Alfresco.util.setSelectedIndex(relationSelect, action.eligibleOnFirstCompleteEvent + "");
         Event.addListener(relationSelect, "change", this.onRelationSelectChange,
         {
            actionEl: actionEl,
            relationSelect: relationSelect
         }, this);

         // Add listener to display edit and delete action
         var detailsEl = Dom.getElementsByClassName("details", "div", actionEl)[0];
         var editEl = Dom.getElementsByClassName("edit", "span", actionEl)[0];
         Event.addListener(editEl, "click", this.onEditActionClick,
         {
            actionEl: actionEl,
            detailsEl: detailsEl
         }, this);

         var deleteEl = Dom.getElementsByClassName("delete", "span", actionEl)[0];
         Event.addListener(deleteEl, "click", this.onDeleteActionClick, actionEl, this);

         // Add events
         var eventListEl = Dom.getElementsByClassName("events-list", "ul", actionEl)[0];
         for(var i = 0; action.events && i < action.events.length; i++)
         {
            eventListEl.appendChild(this._createEvent(i + 1, action.events[i]));
         }

         // Title
         if(action.title)
         {
            this._setTitle(action.title, actionEl);
         }
         else
         {
            this._refreshTitle(actionEl);
         }

         return actionEl;
      },

      /**
       * Create an action in the list
       *
       * @method _setupActionForm
       * @param action The action info object
       * @param actionEl The action HTMLElement
       * @private
       */
      _setupActionForm: function DispositionEdit__setupActionForm(action, actionEl)
      {
         // Find id
         var elId = actionEl.getAttribute("id");

         // Setup form
         var formEl = Dom.getElementsByClassName("action-form", "form", actionEl)[0];
         formEl.setAttribute("id", elId + "-action-form");
         var actionForm = new Alfresco.forms.Form(elId + "-action-form");

         // Add validation
         actionForm.addValidation(elId + "-periodAmount", Alfresco.forms.validation.number, null, "keyup");
         actionForm.addValidation(elId + "-periodAmount", this._mandatoryPeriodAmount, { actionEl: actionEl }, "keyup");
         var periodEnabledCheckBox = Dom.getElementsByClassName("period-enabled", "input", actionEl)[0];
         Event.addListener(periodEnabledCheckBox, "click", function(e, obj)
         {
            // Make sure save button is updated
            obj.form.updateSubmitElements();
         },
         {
            form: actionForm
         }, this);

         // Create buttons
         var saveActionEl = Dom.getElementsByClassName("saveaction", "span", actionEl)[0];
         var saveActionButton = Alfresco.util.createYUIButton(this, "saveaction-button", null,
         {
            type: "submit"
         }, saveActionEl);
         var cancelEl = Dom.getElementsByClassName("cancel", "span", actionEl)[0];
         var cancelActionButton = Alfresco.util.createYUIButton(this, "cancel-button", null, {}, cancelEl);
         cancelActionButton.on("click", this.onCancelActionButtonClick,
         {
            action: action,
            actionEl: actionEl
         }, this);

         // Setup form buttons
         actionForm.setShowSubmitStateDynamically(true, false);
         actionForm.setSubmitElements(saveActionButton);
         actionForm.doBeforeFormSubmit =
         {
            fn: function(formEl, obj)
            {
               // Merge period value
               var puEl = Dom.getElementsByClassName("period-unit", "select", formEl)[0];
               var paEl = Dom.getElementsByClassName("period-amount", "input", formEl)[0];
               if(!puEl.disabled && !paEl.disabled)
               {
                  var periodEl = Dom.getElementsByClassName("period", "input", formEl)[0];
                  periodEl.value = puEl.options[puEl.selectedIndex].value + "|" + paEl.value;
               }
               // Set form url
               var actionId = Dom.getElementsByClassName("id", "input", formEl)[0].value;
               if(actionId && actionId.length > 0)
               {
                  obj.actionForm.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
                  formEl.attributes.method.nodeValue = Alfresco.util.Ajax.PUT;
                  formEl.attributes.action.nodeValue = Alfresco.constants.PROXY_URI_RELATIVE + "api/node/" + this.options.nodeRef.replace(":/", "") + "/dispositionschedule/dispositionactiondefinitions/" + actionId;
               }
               else
               {
                  obj.actionForm.setAjaxSubmitMethod(Alfresco.util.Ajax.POST);
                  formEl.attributes.method.nodeValue = Alfresco.util.Ajax.POST;
                  formEl.attributes.action.nodeValue = Alfresco.constants.PROXY_URI_RELATIVE + "api/node/" + this.options.nodeRef.replace(":/", "") + "/dispositionschedule/dispositionactiondefinitions";
               }

               // Disable buttons during submit
               obj.saveButton.set("disabled", true);
               obj.cancelButton.set("disabled", true);

               // Display a pengding message
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.msg("message.savingAction", this.name),
                  spanClass: "wait",
                  displayTime: 0
               });
            },
            obj: {
               saveButton: saveActionButton,
               cancelButton: cancelActionButton,
               actionForm: actionForm
            },
            scope: this
         };

         // Submit as an ajax submit (not leave the page), in json format
         actionForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: function(serverResponse, obj)
               {
                  this.widgets.feedbackMessage.destroy();
                  obj.saveButton.set("disabled", false);
                  obj.cancelButton.set("disabled", false);
                  Dom.removeClass(obj.actionEl, "expanded");
                  Dom.addClass(obj.actionEl, "collapsed");

                  // Save new info for future cancels and hide details div
                  Dom.getElementsByClassName("id", "input", actionEl)[0].value = serverResponse.json.data.id;
                  action = YAHOO.lang.merge(action, serverResponse.json.data);
                  var details = Dom.getElementsByClassName("details", "div", actionEl)[0];
                  Dom.setStyle(details, "display", "none");

                  // Refresh the title from the choices
                  this._refreshTitle(obj.actionEl);

                  // Display add step button
                  Dom.removeClass(this.widgets.flowButtons, "hidden");
               },
               obj: {
                  saveButton: saveActionButton,
                  cancelButton: cancelActionButton,
                  actionEl: actionEl
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(serverResponse, obj)
               {
                  this.widgets.feedbackMessage.destroy();
                  obj.saveButton.set("disabled", false);
                  obj.cancelButton.set("disabled", false);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.saveActionFailure", this.name)
                  });
               },
               obj: {
                  saveButton: saveActionButton,
                  cancelButton: cancelActionButton
               },
               scope: this
            }
         });
         actionForm.setSubmitAsJSON(true);
         actionForm.init();
      },

      /**
       * Set the title header
       *
       * @method _setTitle
       * @param title The title to use
       * @param actionEl The action HTMLElement
       */
      _setTitle: function DispositionEdit__createEvent(title, actionEl)
      {
         Dom.getElementsByClassName("title", "div", actionEl)[0].innerHTML = title;
      },

      _mandatoryPeriodAmount: function _mandatoryPeriodAmount(field, args, event, form, silent, message)
      {
         var periodEnabledCheckBox = Dom.getElementsByClassName("period-enabled", "input", args.actionEl)[0];
         return !periodEnabledCheckBox.checked ? true : Alfresco.forms.validation.mandatory(field, args, event, form, silent, message);
      },

      /**
       * Disable the period elements
       *
       * @method _disablePeriodSection
       * @param disabled
       * @param actionEl The action HTMLElement
       */
      _disablePeriodSection: function DispositionEdit__disablePeriodSection(disabled, actionEl)
      {
         Dom.getElementsByClassName("period-amount", "input", actionEl)[0].disabled = disabled;
         Dom.getElementsByClassName("period-unit", "select", actionEl)[0].disabled = disabled;
         Dom.getElementsByClassName("period-action", "select", actionEl)[0].disabled = disabled;
      },

      /**
       * Disable the events elements
       *
       * @method _disableEventsSection
       * @param disabled
       * @param actionEl The action HTMLElement
       * @param addEventButton the add event button to be enabled or disabled
       */
      _disableEventsSection: function DispositionEdit__disableEventsSection(disabled, actionEl, addEventButton)
      {
         var eventsDivEl = Dom.getElementsByClassName("events", "div", actionEl)[0];
         Dom.setStyle(eventsDivEl, "display", disabled ? "none" : "block");
         addEventButton.set("disabled", disabled);
         var eventEls = Dom.getElementsByClassName("action-event-name-value", "select", actionEl);
         for(var i = 0; i < eventEls.length; i++)
         {
            eventEls[i].disabled = disabled;
         }
      },

      /**
       * Set the title header
       *
       * @method _refreshTitle
       * @param actionEl The action HTML element
       */
      _refreshTitle: function DispositionEdit__refreshTitle(actionEl)
      {
         var actionTypeSelect = Dom.getElementsByClassName("action-type", "select", actionEl)[0];
         var periodUnitSelect = Dom.getElementsByClassName("period-unit", "select", actionEl)[0];
         var periodAmountEl = Dom.getElementsByClassName("period-amount", "input", actionEl)[0];
         var title = "";
         if(!periodAmountEl.disabled)
         {
            title = this.msg(
                  "label.title.complex",
                  actionTypeSelect.options[actionTypeSelect.selectedIndex].text,
                  periodAmountEl.value,
                  periodUnitSelect.options[periodUnitSelect.selectedIndex].text
                  );
         }
         else
         {
            title = this.msg(
                  "label.title.simple",
                  actionTypeSelect.options[actionTypeSelect.selectedIndex].text
                  );
         }

         this._setTitle(title, actionEl);
      },

      /**
       * Create a action in the list
       *
       * @method _createEvent
       * @param no The order of the event
       * @param event The event information
       * @private
       */
      _createEvent: function DispositionEdit__createEvent(no, event)
      {
         // Clone template
         var eventEl = this.widgets.eventTemplateEl.cloneNode(true);
         var elId = Dom.generateId();
         eventEl.setAttribute("id", elId);
         Dom.addClass(eventEl, no % 2 == 0 ? "even" : "odd");
         if(no == 1)
         {
            Dom.addClass(eventEl, "first");
         }

         // Event Type
         var eventName = event;
         var eventNameSelect = Dom.getElementsByClassName("action-event-name-value", "select", eventEl)[0];
         var eventType = Alfresco.util.setSelectedIndex(eventNameSelect, eventName);
         Event.addListener(eventNameSelect, "change", this.onEventNameSelectChange,
         {
            eventEl: eventEl,
            eventNameSelect: eventNameSelect
         }, this);

         // Display data
         var automatic = this.options.events[event] ? this.options.events[event].automatic + "" : null;
         var completion = "";
         if(automatic)
         {
            completion = this.msg("label.automatic." + automatic);
         }
         Dom.getElementsByClassName("action-event-completion", "div", eventEl)[0].innerHTML = completion;

         // Add listener to delete event
         var deleteEventEl = Dom.getElementsByClassName("delete", "span", eventEl)[0];
         Event.addListener(deleteEventEl, "click", this.onDeleteEventClick, eventEl, this);

         return eventEl;
      },


      /**
       * Refreshes the action no labels.
       *
       * Ideally li.style.list-style: should be used with decimal and inline
       * so this would be handled automatically but hasn't been due to styling issues.
       *
       * @method _refreshActionList
       * @private
       */
      _refreshActionList: function DispositionEdit__refreshActionList()
      {
         var actionNos = Dom.getElementsByClassName("no", "div", this.widgets.actionListEl);
         for(var i = 0; i < actionNos.length; i++)
         {
            actionNos[i].innerHTML = i + 1;
         }         
      },

      /**
       * Refreshes the event list so first event doesn't display the relation.
       *
       * @method _refreshEventList
       * @param eventList The event list HTMLElement
       * @private
       */
      _refreshEventList: function DispositionEdit__refreshEventList(eventList)
      {
         var events = eventList.getElementsByTagName("li");
         for(var i = 0; i < events.length; i++)
         {
            Dom.removeClass(events[i], "even");
            Dom.removeClass(events[i], "odd");
            Dom.removeClass(events[i], "first");
            Dom.addClass(events[i], (i + 1) % 2 == 0 ? "even" : "odd");
            if(i == 0)
            {
               Dom.addClass(events[i], "first");
            }
         }
      },

      /**
       * Called when user toggles one of the checkboxes related to
       *
       * @method onRelationEnablingCheckBoxClick
       * @param e click event object
       * @param obj callback object containg action info & HTMLElements
       */
      onRelationEnablingCheckBoxClick: function DispositionEdit_onRelationEnablingCheckBoxClick(e, obj)
      {
         for(var i = 0; i < obj.checkBoxEls.length; i++)
         {
            if(!obj.checkBoxEls[i].checked)
            {
               Dom.addClass(obj.actionEl, "relation-disabled");
               return;
            }
         }
         Dom.removeClass(obj.actionEl, "relation-disabled");
      },

      /**
       * Called when user changes the relation option select
       *
       * @method onRelationSelectChange
       * @param e click event object
       * @param obj callback object containg action info & HTMLElements
       */
      onRelationSelectChange: function DispositionEdit_onRelationSelectChange(e, obj)
      {
         var relation = obj.relationSelect.options[obj.relationSelect.selectedIndex].value;
         Dom.removeClass(obj.actionEl, "or");
         Dom.removeClass(obj.actionEl, "and");
         Dom.addClass(obj.actionEl, relation == "true" ? "or" : "and");
      },

      /**
       * Called when user changes the event name option select
       *
       * @method onEventNameSelectChange
       * @param e click event object
       * @param obj callback object containg action info & HTMLElements
       */
      onEventNameSelectChange: function DispositionEdit_onEventNameSelectChange(e, obj)
      {
         var eventName = obj.eventNameSelect.options[obj.eventNameSelect.selectedIndex].value;
         var automatic = this.options.events[eventName] ? this.options.events[eventName].automatic + "" : "";
         var completion = "";
         if(automatic)
         {
            completion = this.msg("label.automatic." + automatic);
         }
         Dom.getElementsByClassName("action-event-completion", "div", obj.eventEl)[0].innerHTML = completion;

      },

      /**
       * Called when user clicks the add event icon
       *
       * @method onAddEventButtonClick
       * @param e click event object
       * @param actionEl The action HTMLElement
       */
      onAddEventButtonClick: function DispositionEdit_onAddEventButtonClick(e, actionEl)
      {
         var eventListEl = Dom.getElementsByClassName("events-list", "ul", actionEl)[0];
         var no = eventListEl.getElementsByTagName("li").length + 1;

         // Create new event
         var eventEl = this._createEvent(no, {event: 0, type: "" })
         eventListEl.appendChild(eventEl);

         // Find last event
         var eventNameSelect = Dom.getElementsByClassName("action-event-name-value", "select", eventEl)[0]; 
         this.onEventNameSelectChange(null, { eventEl: eventEl, eventNameSelect: eventNameSelect });
      },

      /**
       * Called when user clicks the delete event icon
       *
       * @method onDeleteEventClick
       * @param e click event object
       * @param eventEl The event HTMLElement
       */
      onDeleteEventClick: function DispositionEdit_onEditClick(e, eventEl)
      {
         var parent = eventEl.parentNode;
         parent.removeChild(eventEl);
         this._refreshEventList(parent);
      },

      /**
       * Called when user clicks the edit action icon
       *
       * @method onEditClick
       * @param e click event object
       * @param obj callback object containg action info & HTMLElements
       */
      onEditActionClick: function DispositionEdit_onEditClick(e, obj)
      {
            Alfresco.util.Anim.fadeIn(obj.detailsEl);
            Dom.removeClass(obj.actionEl, "collapsed");
            Dom.addClass(obj.actionEl, "expanded");
      },

      /**
       * Called when user clicks the delete action icon
       *
       * @method onDeleteActionClick
       * @param e click event object
       * @param actionEl THe action HTMLElement
       */
      onDeleteActionClick: function DispositionEdit_onDeleteActionClick(e, actionEl)
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message("title.deleteAction", this.name),
            text: Alfresco.util.message("label.confirmDeleteAction", this.name),
            noEscape: true,
            buttons: [
               {
                  text: Alfresco.util.message("button.yes", this.name),
                  handler: function DispositionEdit_delete()
                  {
                     this.destroy();
                     me._onDeleteActionConfirmedClick.call(me, actionEl);
                  }
               },
               {
                  text: Alfresco.util.message("button.no", this.name),
                  handler: function DispositionEdit_cancel()
                  {
                     me.deletePromptActive = false;
                     this.destroy();
                  },
                  isDefault: true
               }]
         });
      },

      /**
       * Called when user clicks the delete action icon
       *
       * @method onDeleteActionClick
       * @param actionEl THe action HTMLElement
       */
      _onDeleteActionConfirmedClick: function DispositionEdit_onDeleteActionClick(actionEl)
      {
         var actionId = Dom.getElementsByClassName("id", "input", actionEl)[0].value;
         var feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.deletingAction"),
            spanClass: "wait",
            displayTime: 0
         });

         // user has confirmed, perform the actual delete
         Alfresco.util.Ajax.jsonDelete(
         {
            url: Alfresco.constants.PROXY_URI_RELATIVE + "api/node/" + this.options.nodeRef.replace(":/", "") + "/dispositionschedule/dispositionactiondefinitions/" + actionId,
            dataObj: {},
            successCallback:
            {
               fn: function(response)
               {
                  feedbackMessage.destroy();
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.deleteActionSuccess")
                  });
                  actionEl.parentNode.removeChild(actionEl);
                  this._refreshActionList();
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {
                  feedbackMessage.destroy();
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.deleteActionFailure", this.name)
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Called when user clicks the cancel action button
       *
       * @method onCancelActionButtonClick
       * @param e click event object
       * @param obj callback object containg action info & HTMLElements
       */
      onCancelActionButtonClick: function DispositionEdit_onCancelActionButtonClick(e, obj)
      {
         var actionId = Dom.getElementsByClassName("id", "input", obj.actionEl)[0].value;
         if(actionId && actionId.length > 0)
         {
            /**
             * It is a previous action, cancel it by removing the action element
             * from the dom and insert a new fresh one by using the template and
             * the original data.
             */
            var newActionEl = this._createAction(obj.action)
            obj.actionEl.parentNode.insertBefore(newActionEl, obj.actionEl);
            obj.actionEl.parentNode.removeChild(obj.actionEl);
            this._setupActionForm(obj.action, newActionEl);
            this._refreshActionList();
         }
         else
         {
            // It was an unsaved action, just remove it
            obj.actionEl.parentNode.removeChild(obj.actionEl);
         }
         Dom.removeClass(this.widgets.flowButtons, "hidden");
      },

      /**
       * Called when user clicks the cancel action button
       *
       * @method onCreateActionButtonClick
       * @param e click event object
       * @param obj callback object containg action info & HTMLElements
       */
      onCreateActionButtonClick: function DispositionEdit_onCreateActionButtonClick(e, obj)
      {
         var action = {
            id: "",
            index: this.widgets.actionListEl.childNodes.length - 2,
            title: this.msg("label.title.new"),
            name: "",
            type: "",
            period : null,
            periodProperty: null,
            description: "",
            eligibleOnFirstCompleteEvent: true,
            events: []
         };
         var newActionEl = this._createAction(action);
         this.widgets.actionListEl.appendChild(newActionEl);
         this._setupActionForm(action, newActionEl);
         this.onEditActionClick(null,
         {
            detailsEl: Dom.getElementsByClassName("details", "div", newActionEl)[0],
            actionEl: newActionEl
         });
         Dom.addClass(this.widgets.flowButtons, "hidden");
      },

      /**
       * Fired when the user clicks the Cancel button.
       * Takes the user back to the details edit page without saving anything.
       *
       * @method onDoneActionsButtonClick
       * @param event {object} a "click" event
       */
      onDoneActionsButtonClick: function DispositionEdit_onDoneActionsButtonClick(event)
      {
         // Disable buttons to avoid double submits or cancel during post
         this.widgets.doneButton.set("disabled", true);

         // Send the user to this page again without saving changes
         document.location.href = Alfresco.constants.URL_CONTEXT + "page/site/" + this.options.siteId + "/record-category-details?nodeRef=" + this.options.nodeRef;
      }

   });
})();