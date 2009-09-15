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
      this.showingFilter = false;
      //search filter person
      this.activePerson = "";
      //query parameters for datasource
      this.queryParams = {};
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
                  rule : 'button.audit-toggle',
                  o : {
                        handler:this.onToggleLog,
                        scope : this
                  }
               },
               {
                  rule : 'button.audit-clear',
                  o : {
                        handler:this.onClearLog,
                        scope : this
                  }
               },
               {
                  rule : 'button.audit-view',
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
               rule : 'button.audit-specifyfilter',
               o : {
                     handler:this.onSpecifyFilterLog,
                     scope : this
               }
            },
            {
               rule : 'a.personFilterRemove img',
               o : {
                     handler:this.onRemoveFilter,
                     scope : this
               }
            },
            {
               rule : 'button.audit-export',
               o : {
                  handler: this.onExportLog,
                  scope : this
               }
            },
            {
               rule : 'button.audit-declare-record',
               o : {
                  handler: this.onDeclareRecord,
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
         YAHOO.Bubbling.on('PersonFilterActivated',this.onPersonFilterActivated, this);
         YAHOO.Bubbling.on('PersonFilterDeactivated',this.onPersonFilterDeactivated,this);
         YAHOO.Bubbling.on('AuditRecordLocationSelected', this.onAuditRecordLocationSelected, this);
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
         //an audit log for node and not in console (all nodes)
         if (this.options.nodeRef)
         {
            var nodeRef = this.options.nodeRef.split('/');
            this.dataUri = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/node/{store_type}/{store_id}/{id}/rmauditlog", { store_type: nodeRef[0], store_id: nodeRef[1], id: nodeRef[2] });
         }
         else {
            this.dataUri = Alfresco.constants.PROXY_URI+'api/rma/admin/rmauditlog';
         }
         //if not in full mode, then we want to restrict to 20
         if (this.options.viewMode==Alfresco.RM_Audit.VIEW_MODE_DEFAULT)
         {
            this.dataUri+='?size=20';
         }
         //Sets up datatable.
         var DS = this.widgets['auditDataSource'] = new YAHOO.util.DataSource(this.dataUri);
         
         DS.responseType = YAHOO.util.DataSource.TYPE_JSON;
         DS.responseSchema = {
            resultsList:'data.entries',
            fields: ["timestamp","fullName","userRole","event"],
            metaFields: {
               "enabled": "data.enabled",
               "stopDate": "data.stopped",
               "startDate": "data.started"
            }
         };

         //date cell formatter
         var renderCellDate = function RecordsResults_renderCellDate(elCell, oRecord, oColumn, oData)
         {
            if (oData)
            {
               elCell.innerHTML = Alfresco.util.formatDate(Alfresco.util.fromISO8601(oData));
            }
         };

         var DT = this.widgets['auditDataTable'] = new YAHOO.widget.DataTable(this.id+"-auditDT",
             [
               {key:"timestamp", label:this.msg('label.timestamp'), formatter: renderCellDate, sortable:true, resizeable:true},
               {key:"fullName", label:this.msg('label.user'),  sortable:true, resizeable:true},
               {key:"userRole", label:this.msg('label.role'),  sortable:true, resizeable:true},
               {key:"event", label:this.msg('label.event'),  sortable:true, resizeable:true}
            ], 
            DS, 
            {
               caption:this.msg('label.pagination','0')
            }
         );
         //so we can update caption to list number of results
         this.widgets['auditDataSource'].subscribe('responseParseEvent', this.updateUI, this, true);

         this.widgets['status-date'] = Dom.get(this.id+'-status-date');

         this.validAuditDates = (this.options.startDate!=="");
         if (this.validAuditDates)
         {
            if (this.options.viewMode==Alfresco.RM_Audit.VIEW_MODE_COMPACT)
            {
               Dom.get(this.id+'-from-date').innerHTML += ' ' + formatDate(fromISO8601(this.options.startDate),   Alfresco.thirdparty.dateFormat.masks.fullDatetime);
               Dom.get(this.id+'-to-date').innerHTML += ' ' + formatDate(fromISO8601(this.options.stopDate),   Alfresco.thirdparty.dateFormat.masks.fullDatetime);  
            }
            else
            {
               this.toggleUI();            
            }  
         }        
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
         this.widgets['status-date'].innerHTML = this.msg(statusMessage,formatDate(fromISO8601(statusDate),   Alfresco.thirdparty.dateFormat.masks.fullDatetime));         
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
            return window.open(Alfresco.constants.URL_CONTEXT+'page/site/' + this.options.siteId + '/rmaudit', 'Audit_Log', 'resizable=yes,location=no,menubar=no,scrollbars=yes,status=yes,width=400,height=400');
         };
         // haven't yet opened window yet
         if (!this.fullLogWindowReference)
         {
            this.fullLogWindowReference = openAuditLogWindow.call(this);
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
               this.fullLogWindowReference = openAuditLogWindow.call(this);
            }
         }
      },
      
      /**
       * Handler for export log button. Exports log
       *  
       */      
      onExportLog: function RM_Audit_onExportLog()
      {
         // console.log(arguments.callee.name);
      },

      /**
       * Handler for declare as record log button. Declares log as record
       *  
       */      
      onDeclareRecord: function RM_Audit_onDeclareRecord()
      {  
         //show location dialog
         if (!this.modules.selectAuditRecordLocation)
         {
            this.modules.selectAuditRecordLocation = new Alfresco.module.SelectAuditRecordLocation(this.id + "-copyMoveFileTo");
            Alfresco.util.addMessages(Alfresco.messages.scope[this.name], "Alfresco.module.SelectAuditRecordLocation");            
         }


         this.modules.selectAuditRecordLocation.setOptions(
            {
               mode: 'file',
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: '',
               files: {}
            }).showDialog();
            
      }, 
  
      onAuditRecordLocationSelected : function RM_Audit_AuditRecordLocationSelected(e, args)
      {
         var me = this;
         var dataObj = {
            destination: args[1].nodeRef
         };

         if (this.activePerson)
         {
            dataObj.user = this.activePerson.userName;
         }

         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmauditlog",
            dataObj : dataObj,
            successCallback:
            {
               fn: function(serverResponse)
               {
                  // apply current property values to form
                  if (serverResponse.json)
                  {
                     var data = serverResponse.json.data;

                     if (data.success)
                     {
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                           title: this.msg('label.declare-record'),
                           text: this.msg("label.declared-log-message"),
                           noEscape: true,
                           buttons: [
                           {
                              text: this.msg('button.view-record'),
                              handler: function viewRecordHandler()
                              {
                                 window.location.href = Alfresco.constants.URL_PAGECONTEXT+'site/' +  me.options.siteId + '/documentlibrary?nodeRef='+data.nodeRef;
                              }
                           },
                           {
                              text: this.msg('button.ok'),
                              handler: function()
                              {
                                 this.destroy();
                              },
                              isDefault: true
                           }               
                           ]
                        });
                     }
                     else
                     {
                        Alfresco.util.PopupManager.displayMessage({
                           text: this.msg('message.declare-log-fail'),
                           spanClass: 'message',
                           modal: true,
                           noEscape: true,
                           displayTime: 1
                        });                        
                     }
                  }
               },
               scope: this
            },
            // failureMessage: me.msg("message.declare-log-fail")
            failureCallback: {
               fn: function fail_declare_record(o)
               {
                  if (o.json.status.code==400)
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: me.msg('label.declare-record'),
                        text: o.json.message,
                        buttons: [
                        {
                           text: me.msg('button.ok'), 
                           handler: function()
                           {
                              this.destroy();
                           },
                           isDefault: false
                        }
                        ]
                     });                     
                  }
               }
            }
         });         
      },
      

      /**
       * Handler for specify button. Shows the fields in order for user to
       * filter results. Needs to be replaced by people picker
       *  
       */      
      onSpecifyFilterLog: function RM_Audit_onSpecifyFilterLog()
      {
         if (!this.showingFilter)
         {
            Dom.addClass(this.widgets['people-finder'], 'active');
            this.modules.peopleFinder.clearResults();
            this.widgets['specifyfilter'].set('label',Alfresco.util.message('label.button-cancel', 'Alfresco.RM_Audit'));
            this.showingFilter = true;            
         }
         else
         {
            Dom.removeClass(this.widgets['people-finder'], 'active');
            this.widgets['specifyfilter'].set('label',this.msg('label.button-specify'));
            this.showingFilter = false;
         }
      },
            
      /**
       * Handler for when a person is selected
       *  
       */
      onPersonSelected: function RM_Audit_onPersonSelected(e, args)
      {
         Dom.addClass(Sel.query('.personFilter',this.id)[0], 'active');
         var person = args[1];
         this._changeFilterText(person.firstName + ' ' + person.lastName);
         this.widgets['specifyfilter'].set('label',this.msg('label.button-specify'));
         Dom.removeClass(this.widgets['people-finder'],'active');
         this.showingFilter = false;
         YAHOO.Bubbling.fire('PersonFilterActivated',{person:person}); 
      },
      
      /**
       * Changes the text displayed to show how the audit log is being filtered
       * 
       * @param {text} String object Text to update UI with. If empty string, UI is updated with default label
       */
      _changeFilterText: function(text)
      {
         var el = Sel.query('.personFilter span',this.id)[0];
         el.innerHTML = (text !== "") ? text : this.msg('label.default-filter');
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
         this.widgets['people-finder'] = finderDiv;
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
         Dom.removeClass(Sel.query('.personFilter',this.id)[0], 'active');
         this._changeFilterText('');
         YAHOO.Bubbling.fire('PersonFilterDeactivated',{person:null});
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
                     window.location.reload();
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

      onPersonFilterActivated: function RM_Audit_personFilterActivated(e,args)
      {
         this.activePerson = args[1].person;
         this.queryParams.user = this.activePerson.userName;
         this._query();
      },

      onPersonFilterDeactivated: function RM_Audit_personFilterDeactivated(e,args)
      {
         this.activePerson = "";
         delete this.queryParams.user;  
         this._query();       
      },
      
      _buildQuery : function RM_Audit__buildQuery()
      {
         var qs = Alfresco.util.toQueryString(this.queryParams);
         //if we are already using a qs parameter so we need to make it append friendly
         if ((qs !== "") && this.dataUri.indexOf('?')!=-1)
         {
            qs = '&' + qs.split('?')[1];            
         }
         return qs;
      },
      
      _query : function RM_Audit__reQuery()
      {
         var q = this._buildQuery();
         // Sends a request to the DataSource for more data
         var oCallback = {
            success : this.widgets['auditDataTable'].onDataReturnInitializeTable,
            failure : this.widgets['auditDataTable'].onDataReturnInitializeTable,
            scope : this.widgets['auditDataTable']
         };

         this.widgets['auditDataTable'].getDataSource().sendRequest(q, oCallback);
      },
      
      updateUI : function RM_updateCaption(o)
      {
         var response = o.response;
         this.options.enabled = response.meta.enabled;
         this.options.startDate = response.meta.startDate;
         this.options.stopDate = response.meta.stopDate;
         if (this.options.viewMode==Alfresco.RM_Audit.VIEW_MODE_DEFAULT)
         {
            this.toggleUI();
         }

         //update caption
         this.widgets['auditDataTable']._elCaption.innerHTML = this.msg('label.pagination', response.results.length); 
      }
   });
})();