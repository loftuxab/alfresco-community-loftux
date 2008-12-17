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

/*
 *** Alfresco.EventInfo
*/
(function()
{
   Alfresco.EventInfo = function(containerId)
   {
      this.name = "Alfresco.EventInfo";
      this.id = containerId;

      this.panel = null;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.EventInfo.prototype =
   {
      /**
       * EventInfo instance.
       *
       * @property panel
       * @type Alfresco.EventInfo
       */
      panel: null,
      
      /**
       * A reference to the current event. 
       * !!CHANGE ME!!
       *
       * @property event
       * @type object
       */
      event: null,
      
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
        siteId: "",
      /**
       * Stores the URI of the event IF an edit is happening 
       *
       * @property eventURI
       * @type String
       */
        eventUri: null,
        displayDate: null
      },      
      
      
       /**
        * Set multiple initialization options at once.
        *
        * @method setOptions
        * @param obj {object} Object literal specifying a set of options
        */
       setOptions: function (obj)
       {
          this.options = YAHOO.lang.merge(this.options, obj);
          return this;
       },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function EventInfo_onComponentsLoaded()
      {
         /* Shortcut for dummy instance */
         if (this.id === null)
         {
            return;
         }
      },

      /**
       * Renders the event info panel. 
       *
       * @method show
       * @param event {object} JavaScript object representing an event
       */
      show: function EventInfo_show(event)
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/info",
            dataObj:
            { 
               "htmlid": this.id,
               "uri": "/" + event.uri
            },
            successCallback:
            {
               fn: this.templateLoaded,
               scope: this
            },
            failureMessage: "Could not load event info panel",
            execScripts: true
         });

         this.event = event;
      },

      /**
       * Fired when the event info panel has loaded successfully.
       *
       * @method templateLoaded
       * @param response {object} DomEvent
       */
      templateLoaded: function EventInfo_templateLoaded(response)
      {
         var div = document.getElementById("eventInfoPanel");
         div.innerHTML = response.serverResponse.responseText;

         this.panel = new YAHOO.widget.Panel(div,
         {
            modal: true,
            fixedcenter: true,
            visible: false,
            constraintoviewport: true,
            width: "35em"
         });
         this.panel.render(document.body);

         // Buttons
         Alfresco.util.createYUIButton(this, "delete-button", this.onDeleteClick);
         Alfresco.util.createYUIButton(this, "edit-button", this.onEditClick);
         Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelClick);

         // Display the panel
         this.panel.show();
      },
      
      /**
       * Fired when the use selected the "Cancel" button.
       *
       * @method onCancelClick
       * @param e {object} DomEvent
       */
      onCancelClick: function EventInfo_onCancelClick(e)
      {
         this._hide();
      },
      
      /**
       * Fired when the user selects the "Edit" button.
       *
       * @method onEventClick
       * @param e {object} DomEvent
       */
      onEditClick: function(e)
      {
          this._hide();
          this.eventDialog = Alfresco.util.DialogManager.registerDialog('CalendarView.editEvent');
          this.eventDialog.id = this.id+ "-editEvent";
          // add the tags that are already set on the post
          if (this.eventDialog.tagLibrary == undefined)
          {
             this.eventDialog.tagLibrary = new Alfresco.module.TagLibrary( this.eventDialog.id);
             this.eventDialog.tagLibrary.setOptions({ siteId: this.options.siteId });
          }
         
         var options = 
         {
              site : this.options.siteId,
              displayDate :this.options.displayDate,
              actionUrl : Alfresco.constants.PROXY_URI + this.options.eventUri + "?page=calendar",
              templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/add-event",
              templateRequestParams : {
                     site : this.options.siteId,
                     uri : '/'+this.options.eventUri
              },
              doBeforeFormSubmit : 
              {
                fn : function(form, obj)
                     {                           
                       // Update the tags set in the form
                       this.tagLibrary.updateForm(this.id + "-form", "tags");
                       // Avoid submitting the input field used for entering tags
                       var tagInputElem = YAHOO.util.Dom.get(this.id + "-tag-input-field");
                       if (tagInputElem)
                       {
                          tagInputElem.disabled = true;
                       }
                       var errorEls = YAHOO.util.Dom.getElementsByClassName('error',null,YAHOO.util.Dom.get(this.id + "-form"));
                       
                       for (var i = 0; i <errorEls.length;i++)
                       {
                         YAHOO.util.Dom.removeClass(errorEls[i],'error');                           
                       }

                     },
                scope:this.eventDialog
              },
              doBeforeAjaxRequest : {
                  fn : function(p_config, p_obj) 
                       {
                           p_config.method = Alfresco.util.Ajax.PUT
                           if (p_config.dataObj.tags)
                           {
                             p_config.dataObj.tags = p_config.dataObj.tags.join(' ');
                           }

                           this.form.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
                           
                           return true;
                       },
                  scope : this.eventDialog
              },
              doBeforeDialogShow : {
                  fn : function()
                         {
                            var editEvent = Alfresco.util.ComponentManager.findFirst("Alfresco.CalendarView").editEvent;
                            var Dom = YAHOO.util.Dom;
                            
                            var dts  = Alfresco.util.fromISO8601(editEvent.getData('dtstart'));
                            var dte  = Alfresco.util.fromISO8601(editEvent.getData('dtend'));
                            // Pretty formatting
                            var dateStr = Alfresco.util.formatDate(dts, "dddd, d mmmm yyyy");
                            Dom.get("fd").value = dateStr;
                            var dateStr = Alfresco.util.formatDate(dte, "dddd, d mmmm yyyy");
                            Dom.get("td").value = dateStr;
                            Dom.get(this.id+"-from").value = Dom.get("fd").value;
                            Dom.get(this.id+"-to").value = Dom.get("td").value;
                            
                            //init taglib
                            this.tagLibrary.initialize();
                            var tags = YAHOO.util.Dom.get(this.id + "-tag-input-field").value;
                            YAHOO.util.Dom.get(this.id + "-tag-input-field").value = '';
                            this.tagLibrary.setTags(tags.split(' '));
                            this.form.errorContainer=null;
                         },
                 scope : this.eventDialog
              },
              onSuccess : {
                 fn : this.onEdited,
                 scope : this
              },
              onFailure : {
                 fn : function() 
                 {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                       text: Alfresco.util.message('message.edited.failure','Alfresco.CalendarView')
                    });
                },
                scope : this
             }
          };
          this.eventDialog.setOptions(options);
          this.eventDialog.show();
              
      },
      /**
        * Called when an event is successfully edited.
        *
        * @method onDeleted
        * @param e {object} DomEvent
        */
       onEdited: function(o)
       {
         this.panel.hide();
         YAHOO.Bubbling.fire('eventEdited',
         {
            id: this.options.event, // so we know which event we are dealing with
            data : o.json.data
         });
         this.panel.destroy();
         this.eventDialog.dialog.destroy();
       },
      /**
       * Fired when the delete #calendarendpicker button, #calendarpicker button {calendar.css (line 114)
       background:transparent url(images/calendar-16.png) no-repeat scroll is clicked. Kicks off a DELETE request
       * to the Alfresco repo to remove an event.
       *
       * @method onDeleteClick
       * @param e {object} DomEvent
       */
      onDeleteClick: function EventInfo_onDeleteClick(e)
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: this._msg("message.confirm.delete", this.event.name),
            buttons: [
            {
               text: this._msg("button.delete"),
               handler: function DL_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm.call(me, record);
               }
            },
            {
               text: this._msg("button.cancel"),
               handler: function DL_onActionDelete_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },
      
      /**
       * Delete Event confirmed.
       * Kicks off a DELETE request to the Alfresco repo to remove an event.
       *
       * @method _onDeleteConfirm
       * @private
       */
      _onDeleteConfirm: function EventInfo_onDeleteConfirm()
      {
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.DELETE,
            url: Alfresco.constants.PROXY_URI + this.event.uri + "?page=calendar",
            successCallback:
            {
               fn: this.onDeleted,
               scope: this
            },
            failureMessage: this._msg("message.delete.failure", this.event.name),
         });
      },
      
      /**
       * Called when an event is successfully deleted.
       *
       * @method onDeleted
       * @param e {object} DomEvent
       */
      onDeleted: function EventInfo_onDeleted(e)
      {
         this._hide();

         YAHOO.Bubbling.fire('eventDeleted',
         {
            name: this.event.name, // so we know which event we are dealing with
            from: this.event.from // grab the events for this date and remove the event
         });         
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function EventInfo__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.EventInfo", Array.prototype.slice.call(arguments).slice(1));
      },

      /**
       * Hides the panel and calls onClose callback if present
       *
       * @method _hide
       * @param e {object} DomEvent
       * @private
       */
      _hide: function EventInfo__hide()
      {
         this._hide();
         var callback = this.options.onClose;
         if (callback && typeof callback.fn == "function")
         {
            // Call the onClose callback in the correct scope
            callback.fn.call((typeof callback.scope == "object" ? callback.scope : this), callback.obj);
         }
      }
   };
})();



/**
 * Tags entry field validation handler, tests that the given field's value is a valid.
 * This is identical to the test for the name for a node in the repository minus the requirement
 * that there must not be any white space; tags are separated by white space.
 *
 * @method nodeName
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @static
 */
// Ensure namespaces exist
Alfresco.module.event =  Alfresco.module.event || {}; 
Alfresco.module.event.validation = Alfresco.module.event.validation || {};
 
Alfresco.module.event.validation.tags = function mandatory(field, args, event, form, silent)
{
   if (!args)
   {
      args = {};
   }

   args.pattern = /([\"\*\\\>\<\?\/\:\|]+)|([\.]?[\.]+$)/;
   args.match = false;

   return Alfresco.forms.validation.regexMatch(field, args, event, form, silent); 
};
