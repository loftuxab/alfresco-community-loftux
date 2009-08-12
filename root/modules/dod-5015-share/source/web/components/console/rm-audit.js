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
       Sel = YAHOO.util.Selector;


   /**
    * RM Audit componentconstructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RM_Audit} The new component instance
    * @constructor
    */
   Alfresco.RM_Audit = function RM_Audit_constructor(htmlId)
   {
      Alfresco.RM_Audit.superclass.constructor.call(this, "Alfresco.RM_Audit", htmlId,["button", "container", "datasource", "datatable", "json"]);
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
                  rule : 'button#audit-stop-button',
                  o : {
                        handler:this.onStopLog,
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
               },
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
         }

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
         if (this.options.viewMode==Alfresco.RM_Audit.VIEW_MODE_DEFAULT)
         {
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
              
         }
         var buttons = Sel.query('button',this.id).concat(Sel.query('input[type=submit]',this.id));
         // Create widget button while reassigning classname to src element (since YUI removes classes). 
         // We need the classname so we can identify what action to take when it is interacted with (event  delegation).
         for (var i=0, len = buttons.length; i<len; i++)
         {
          var button= buttons[i];
          if (button.id.indexOf('-button')==-1)
          {
              var id = button.id.replace(this.id+'-','');
              this.widgets[id] = new YAHOO.widget.Button(button.id)._button.className=button.className;
          }
         }       
         //Sets up datatable. Could do with a generic helper
         //Might need a cell formatter for timestamp
         var DS = this.widgets['rolesDataSource'] = new YAHOO.util.LocalDataSource(
         [
              {timestamp:"12:00:02 16 July 2009", user:"A user",event:"Created record",role:"Records Manager"},
              {timestamp:"12:00:02 16 July 2009", user:"A user",event:"Created record",role:"Records Manager"},
              {timestamp:"12:00:02 16 July 2009", user:"A user",event:"Created record",role:"Records Manager"},
              {timestamp:"12:00:02 16 July 2009", user:"A user",event:"Created record",role:"Records Manager"}
         ]);
         
         DS.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
         DS.responseSchema = {
            fields: ["timestamp","user","role","event","role"]
         };
         var DT = this.widgets['rolesDataTable'] = new YAHOO.widget.DataTable("auditDT",
             [
               {key:"timestamp", label:this.msg('label.timestamp'), sortable:true, resizeable:true},
               {key:"user", label:this.msg('label.user'),  sortable:true, resizeable:true},
               {key:"role", label:this.msg('label.role'),  sortable:true, resizeable:true},
               {key:"event", label:this.msg('label.event'),  sortable:true, resizeable:true}
               // Showing x - n entries in log (caption gets updated via set(method) unless we're using ScrollTable)
            ], DS, {caption:this.msg('label.pagination','1', '4')});
      },
            
      /**
       * Handler for stop log button
       *  
       */      
      onStopLog: function RM_Audit_onStopLog()
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg('label.stop-log-title'),
            text: this.msg('label.stop-log-confirmation'),
            buttons: [
            {
               text: this.msg('label.yes'), // To early to localize at this time, do it when called instead
               handler: function()
               {
                  me._stopLog();
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
         window.open(Alfresco.constants.URL_CONTEXT+'page/site/rm/rmaudit', 'Audit_Log', 'resizable=yes,location=no,menubar=no,scrollbars=yes,status=yes,width=400,height=400');
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
      },
      
      /**
       * Clears logs via ajax call and gives user feedback
       *  
       */      
      _clearLog: function RM_Audit_clearLog()
      {
         Alfresco.util.PopupManager.displayMessage({
            text: this.msg('label.clearing-log-message') + ' (onSuccess/Failure() will hide this message)',
            spanClass: 'message',
            modal: true,
            noEscape: true
         });         
      },
      
      /**
       * Stops logs via ajax call and gives user feedback
       *  
       */
      _stopLog: function RM_Audit_stopLog()
      {
         Alfresco.util.PopupManager.displayMessage({
            text: this.msg('label.stopping-log-message') + ' (onSuccess/Failure() will hide this message)',
            spanClass: 'message',
            modal: true,
            noEscape: true
         });
      }
   });
})();