/**
 * RM Audit component
 * 
 * @namespace Alfresco
 * @class Alfresco.RM_Audit
 */
(function RM_Audit()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Sel = YAHOO.util.Selector,
       formatDate = Alfresco.util.formatDate,
       fromISO8601 = Alfresco.util.fromISO8601;


   /**
    * RM Audit componentconstructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RM_Audit} The new component instance
    * @constructor
    */
   Alfresco.RM_Audit = function RM_Audit_constructor(htmlId)
   {
      Alfresco.RM_Audit.superclass.constructor.call(this, "Alfresco.RM_Audit", htmlId,["button", "container", "datasource", "datatable", "paginator", "json"]);
      Alfresco.util.ComponentManager.register(this);
      return this;
   };

   YAHOO.lang.augmentObject(Alfresco.RM_Audit,
   {
      VIEW_MODE_DEFAULT: "",
      VIEW_MODE_COMPACT: "COMPACT"
   });    
      
   YAHOO.extend(Alfresco.RM_Audit, Alfresco.component.Base,
   {
      
      /**
       * Initialises event listening and custom events
       *  
       */
      initEvents : function RM_Audit_initEvents()
      {
         Event.on(this.id,'click',this.onInteractionEvent,null,this);
         //register event
         if (this.options.viewMode==Alfresco.RM_Audit.VIEW_MODE_DEFAULT)
         {
            this.registerEventHandler('click',[
               {
                  rule : 'button#audit-toggle-button',
                  o : {
                        handler:this.onToggleLog,
                        scope : this
                  }
               },
               {
                  rule : 'button#audit-clear-button',
                  o : {
                        handler:this.onClearLog,
                        scope : this
                  }
               },
               {
                  rule : 'button#audit-view-button',
                  o : {
                        handler:this.onViewLog,
                        scope : this
                  }
               }
            ]);
         }
         //people picker functionality
         this.registerEventHandler('click',
         [
            {
               rule : 'button#audit-specifyfilter-button',
               o : {
                     handler:this.onSpecifyFilterLog,
                     scope : this
               }
            },
            {
               rule : 'a#personFilterRemove img',
               o : {
                     handler:this.onRemoveFilter,
                     scope : this
               }
            }
         ]);
         return this;
      },
      
     /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       * 
       */
      onReady: function RM_Audit_onReady()
      {
         this.initEvents();
         // Load the People Finder component from the server
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/people-finder",
            dataObj:
            {
               htmlid: this.id + "-peoplefinder"
            },
            successCallback:
            {
               fn: this.onPeopleFinderLoaded,
               scope: this
            },
            failureMessage: "Could not load People Finder component",
            execScripts: true
         });
         // Decoupled event listeners
         YAHOO.Bubbling.on("personSelected", this.onPersonSelected, this);
         YAHOO.Bubbling.on('PersonFilterActivated',this.personFilterActivated);
         YAHOO.Bubbling.on('PersonFilterDeactivated',this.personFilterDeactivated);

         
         var buttons = Sel.query('button',this.id).concat(Sel.query('input[type=submit]',this.id));
         // Create widget button while reassigning classname to src element (since YUI removes classes). 
         // We need the classname so we can identify what action to take when it is interacted with (event  delegation).
         for (var i=0, len = buttons.length; i<len; i++)
         {
          var button= buttons[i];
          if (button.id.indexOf('-button')==-1)
          {
              var id = button.id.replace(this.id+'-','');
              this.widgets[id] = new YAHOO.widget.Button(button.id);
              this.widgets[id]._button.className=button.className;
          }
         }       
         //Sets up datatable. Could do with a generic helper
         //Might need a cell formatter for timestamp
         var DS = this.widgets['auditDataSource'] = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI+'api/rma/admin/rmauditlog');
         

         
         DS.responseType = YAHOO.util.DataSource.TYPE_JSON;
         DS.responseSchema = {
            resultsList:'data.entries',
            fields: ["timestamp","user","role","event"]
         };
         var DT = this.widgets['rolesDataTable'] = new YAHOO.widget.DataTable("auditDT",
             [
               {key:"timestamp", label:this.msg('label.timestamp'), sortable:true, resizeable:true},
               {key:"user", label:this.msg('label.user'),  sortable:true, resizeable:true},
               {key:"role", label:this.msg('label.role'),  sortable:true, resizeable:true},
               {key:"event", label:this.msg('label.event'),  sortable:true, resizeable:true}
            ], 
            DS, 
            {
               caption:this.msg('label.pagination','20')
            }
         );
         
         if (this.options.viewMode==Alfresco.RM_Audit.VIEW_MODE_COMPACT)
         {
            Dom.get('audit-from-date').innerHTML += ' ' + formatDate(fromISO8601(this.options.startDate),   Alfresco.thirdparty.dateFormat.masks.fullDatetime);
            Dom.get('audit-to-date').innerHTML += ' ' + formatDate(fromISO8601(this.options.stopDate),   Alfresco.thirdparty.dateFormat.masks.fullDatetime);  
         }
         else
         {
            this.toggleUI();            
         }

         this.pollData();
      },
      /**
       * Poll data every x seconds based on options configuration
       *  
       */
      pollData: function pollData()
      {
        // Set up polling 
        var pollCallback = { 
            success: this.widgets['auditDataSource'].onDataReturnInitializeTable, 
            failure: function() { 
                YAHOO.log("Polling failure", "error"); 
            }, 
            scope: this.widgets['auditDataSource'] 
        };
        this.widgets['auditDataSource'].setInterval(this.options.pollInterval, null, pollCallback); 
      },
      /**
       * Updates the UI to show status of UI and start/stop buttons
       *  
       */
      toggleUI: function toggleUI()
      {
         //get started/stopped (status) time
         var statusDate = (this.options.enabled) ? this.options.startDate : this.options.stopDate;
         var statusMessage = (this.options.enabled) ? 'label.started-at' : 'label.stopped-at';
         Dom.get('audit-status-date').innerHTML = this.msg(statusMessage,formatDate(fromISO8601(statusDate),   Alfresco.thirdparty.dateFormat.masks.fullDatetime));         
         //update start/stop button
         if (this.options.viewMode==Alfresco.RM_Audit.VIEW_MODE_DEFAULT)
         {   
            this.widgets['toggle'].set('value',this.options.enabled);
            this.widgets['toggle'].set('label',(this.options.enabled)? this.msg('label.button-stop') : this.msg('label.button-start'));
            
         }

      },
      /**
       * Handler for start/stop log button
       *  
       */      
      onToggleLog: function onToggleLog()
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: (this.options.enabled) ? this.msg('label.stop-log-title') : this.msg('label.start-log-title'),
            text: (this.options.enabled) ? this.msg('label.stop-log-confirmation') : this.msg('label.start-log-confirmation'),
            buttons: [
            {
               text: this.msg('label.yes'), 
               handler: function()
               {
                  me._toggleLog();
                  this.destroy();
               },
               isDefault: false
            },
            {
               text: this.msg('label.no'), 
               handler: function()
               {
                  this.destroy();
               },
               isDefault: false
            }
            ]
         });         
      },
      
      /**
       * Handler for clear log button.
       *  
       */
      onClearLog: function RM_Audit_onClearLog()
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg('label.clear-log-title'),
            text: this.msg('label.clear-log-confirmation'),
            buttons: [
            {
               text: this.msg('label.yes'), // To early to localize at this time, do it when called instead
               handler: function()
               {
                  me._clearLog();
                  this.destroy();
               },
               isDefault: false
            },
            {
               text: this.msg('label.no'), // To early to localize at this time, do it when called instead
               handler: function()
               {
                  this.destroy();
               },
               isDefault: false
            }
            ]
         });
      },
      
      /**
       * Handler for view log button. Displays log in new window
       *  
       */      
      onViewLog: function RM_Audit_onViewLog()
      {
         var openAuditLogWindow = function openAuditLogWindow()
         {
            return window.open(Alfresco.constants.URL_CONTEXT+'page/site/rm/rmaudit', 'Audit_Log', 'resizable=yes,location=no,menubar=no,scrollbars=yes,status=yes,width=400,height=400');
         };
         // haven't yet opened window yet
         if (!this.fullLogWindowReference)
         {
            this.fullLogWindowReference = openAuditLogWindow();
         }
         else
         {
            // window has been opened already and is still open, so focus and reload it.
            if (!this.fullLogWindowReference.closed)
            {
               this.fullLogWindowReference.focus();
               this.fullLogWindowReference.location.reload();
            }
            //had been closed so reopen window
            else
            {
               this.fullLogWindowReference = openAuditLogWindow();
            }
         }
      },
      
      /**
       * Handler for specify button. Shows the fields in order for user to
       * filter results. Needs to be replaced by people picker
       *  
       */      
      onSpecifyFilterLog: function RM_Audit_onSpecifyFilterLog()
      {
         Dom.addClass('audit-peoplefinder', 'active');
         this.modules.peopleFinder.clearResults();                 
      },
            
      /**
       * Handler for when a person is selected
       *  
       */
      onPersonSelected: function RM_Audit_onPersonSelected(e, args)
      {
         Dom.addClass('personFilter', 'active');
         var person = args[1];
         this._changeFilterText(person.firstName + ' ' + person.lastName);
         Dom.removeClass('audit-peoplefinder','active'); 
         YAHOO.Bubbling.fire('PersonFilterActivated'); 
      },
      
      /**
       * Changes the text displayed to show how the audit log is being filtered
       * 
       * @param {text} String object Text to update UI with. If empty string, UI is updated with default label
       */
      _changeFilterText: function(text)
      {
         var el = Sel.query('#personFilter span',this.id)[0];
         el.innerHTML = (text != "") ? text : this.msg('label.default-filter');
      },
      
      
      /**
       * Handler for when people finder has finished loading 
       * 
       * @param {text} String object HTML template response from people-finder call
       */ 
      onPeopleFinderLoaded: function RM_Audit_onPeopleFinderLoaded(response)
      {
         // Inject the component from the XHR request into it's placeholder DIV element
         var finderDiv = Dom.get(this.id + "-peoplefinder");
         finderDiv.innerHTML = response.serverResponse.responseText;
         // Find the People Finder by container ID
         this.modules.peopleFinder = Alfresco.util.ComponentManager.get(this.id + "-peoplefinder");
         // Set the correct options for our use
         this.modules.peopleFinder.setOptions(
         {
            viewMode: Alfresco.PeopleFinder.VIEW_MODE_COMPACT,
            singleSelectMode: true,
            showSelf: true
         });
      },
      
      /**
       * Remove filter handler
       * 
       * Removes filtered user's name from UI
       * 
       * @param {e} Event 
       * @param {args} Object Event arguments
       */
      onRemoveFilter: function RM_Audit_RemoveFilter(e, args)
      {
         Dom.removeClass('personFilter', 'active');
         this._changeFilterText('');
         YAHOO.Bubbling.fire('PersonFilterDeactivated');
      },
      
      /**
       * Clears logs via ajax call and gives user feedback
       *  
       */      
      _clearLog: function RM_Audit_clearLog()
      {
         var me = this;
         Alfresco.util.PopupManager.displayMessage({
            text: this.msg('label.clearing-log-message'),
            spanClass: 'message',
            modal: true,
            noEscape: true,
            displayTime: 1
         });
         Alfresco.util.Ajax.jsonDelete(
         {
            url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmauditlog",
            successCallback:
            {
               fn: function(serverResponse)
               {
                  // apply current property values to form
                  if (serverResponse.json)
                  {
                     var data = serverResponse.json.data;
                     this.options.enabled = data.enabled;
                      Alfresco.util.PopupManager.displayMessage({
                        text: this.msg('label.cleared-log-message'),
                        spanClass: 'message',
                        modal: true,
                        noEscape: true,
                        displayTime: 1
                     });
                  }
               },
               scope: this
            },
            failureMessage: me.msg("message.clear-log-fail")
         });   
      },
      
      /**
       * toggles logs via ajax call and gives user feedback
       *  
       */
      _toggleLog: function RM_Audit_toggleLog()
      {
         var me = this;
         Alfresco.util.PopupManager.displayMessage({
            text: (this.options.enabled) ? this.msg('label.stopping-log-message'): this.msg('label.starting-log-message'),
            spanClass: 'message',
            modal: true,
            noEscape: true,
            displayTime: 1
         });

         Alfresco.util.Ajax.jsonPut(
         {
            url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmauditlog",
            dataObj:
            {
               enabled:!this.options.enabled
            },
            successCallback:
            {
               fn: function(serverResponse)
               {
                  // apply current property values to form
                  if (serverResponse.json)
                  {
                     var data = serverResponse.json.data;
                     this.options.enabled = data.enabled;
                     me.toggleUI();
                     Alfresco.util.PopupManager.displayMessage({
                        text: (this.options.enabled) ? this.msg('label.started-log-message'): this.msg('label.stopped-log-message'),
                        spanClass: 'message',
                        modal: true,
                        noEscape: true,
                        displayTime: 1
                     });
                  }
               },
               scope: this
            },
            failureMessage: me.msg((this.options.enabled) ? "message.stop-log-fail" : "message.start-log-fail")
         });
         
      },
      personFilterActivated: function(e,args)
      {
         console.log(arguments);
      },
      personFilterDeactivated: function(e,args)
      {
         console.log(arguments);
      }
      
   });
})();