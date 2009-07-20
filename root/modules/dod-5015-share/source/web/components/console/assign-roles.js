/**
 * Alfresco top-level RM namespace.
 * 
 * @namespace Alfresco
 * @class Alfresco.RM
 */
Alfresco.Admin = Alfresco.Admin || {};
Alfresco.Admin.RM = Alfresco.Admin.RM || {};
/**
 * RM Assign Roles component
 * 
 * @namespace Alfresco
 * @class Alfresco.RM.References
 */
(function RM_Assign_Roles()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Sel = YAHOO.util.Selector;


   /**
    * RM Assign Roles componentconstructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MyDocuments} The new component instance
    * @constructor
    */
   Alfresco.Admin.RM.AssignRoles = function RM_Assign_Roles_constructor(htmlId)
   {
      Alfresco.Admin.RM.AssignRoles.superclass.constructor.call(this, "Alfresco.Admin.RM.AssignRoles", htmlId, ["button", "dom", "datasource", "datatable", "paginator", "event"]);
      return this;
   };
    
    YAHOO.extend(Alfresco.Admin.RM.AssignRoles, Alfresco.component.Base,
   {
      
      /**
       * Initialises event listening and custom events
       *  
       */
      initEvents : function RM_Assign_Roles_initEvents()
      {
         
         return this;
      },
      onShowType: function RM_Assign_Roles_onShowType(sType, aArgs, p_obj)
      {
         var domEvent = aArgs[0];
         var eventTarget = aArgs[1];

         // Get the function related to the clicked item
         var fn = Alfresco.util.findEventClass(eventTarget);

         if (fn && (typeof this[fn] == "function"))
         {
            this[fn].call(this);
         }
         Event.preventDefault(domEvent);
      },
      onShowGroups: function RM_Assign_Roles_onShowGroups()
      {
         alert('showGroups');
      },
      onShowUsers: function RM_Assign_Roles_onShowUsers()
      {
         alert('showUsers');
      },
      onShowUsersGroups: function RM_Assign_Roles_onShowUsersAndGroups()
      {
         alert('showUsersGroups');
      },
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       * 
       */
      onReady: function RM_Assign_Roles_onReady()
      {
         this.initEvents();
         var showTypeMenu  = this.widgets['showType'] = Alfresco.util.createYUIButton(this, "showType", this.onShowType,
           {
              type: "menu", 
              menu: "showTypeMenu",
              disabled: false
           }
         );
         // var showTypeMenu = this.getWidget('showType');
         // Clear the lazyLoad flag and fire init event to get menu rendered into the DOM
         showTypeMenu.getMenu().lazyLoad = false;
         showTypeMenu.getMenu().initEvent.fire();
         showTypeMenu.getMenu().render();
         var buttons = Sel.query('button',this.id).concat(Sel.query('input[type=submit]',this.id));;;
         // Create widget button while reassigning classname to src element (since YUI removes classes). 
         // We need the classname so we can identify what action to take when it is interacted with (event delegation).
         for (var i=0, len = buttons.length; i<len; i++)
         {
          var button= buttons[i];
          if (button.id.indexOf('-button')==-1)
          {
              var id = button.id.replace(this.id+'-','');
              this.widgets[id] = new YAHOO.widget.Button(button.id)._button.className=button.className;
          }
         }
         var DS = this.widgets['rolesDataSource'] =new YAHOO.util.DataSource(
         [
              {name:"role name", role:"role1"},
              {name:"role name", role:"role2"},
              {name:"role name", role:"role1"},
              {name:"role name", role:"role3"}
         ]);
         DS.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
         DS.responseSchema = {
            fields: ["name","role"]
         };
         var DT = this.widgets['rolesDataTable'] = new YAHOO.widget.DataTable("assignRoleDT",
             [
               {key:"name", label:this.msg('label.name'),sortable:true, resizeable:true},
               {
                  key:"role", 
                  sortable:true,
                  label:this.msg('label.role'),                 
                  editor: new YAHOO.widget.DropdownCellEditor(
                     {
                        dropdownOptions:["role1","role2","role3"],
                        LABEL_CANCEL:this.msg('label.cancel'),
                        LABEL_SAVE:this.msg('label.save'),                        
                        asyncSubmitter:function(fnCallback, oNewValue)
                        {
                           var record = this.getRecord(),
                           column = this.getColumn(),
                           oldValue = this.value,
                           datatable = this.getDataTable();

                           oldValue = this.value;
                           
                           
                           YAHOO.util.Connect.asyncRequest(
                           'GET',
                           'url',
                              {
                                 success:function(o) 
                                 {
                                    var r = YAHOO.lang.JSON.parse(o.responseText);
                                    if (r.replyCode == 201) {
                                       fnCallback(true, r.data);
                                    } else {
                                       alert(r.replyText);
                                       fnCallback();
                                    }
                                 },
                                 failure:function(o) 
                                 {
                                    alert(o.statusText);
                                    callback();
                                 },
                                 scope:this
                              }
                           );
                           
                        }
                     
                     })
               }
            ], DS);
         DT.subscribe("cellClickEvent", DT.onEventShowCellEditor);         
      }

   });
})();    