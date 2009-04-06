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
 * Dashboard MyTasks component.
 * 
 * @namespace Alfresco
 * @class Alfresco.MyTasks
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
   
   /**
    * Dashboard MyTasks constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.MyTasks} The new component instance
    * @constructor
    */
   Alfresco.MyTasks = function MyTasks_constructor(htmlId)
   {
      this.name = "Alfresco.MyTasks";
      this.id = htmlId;

      // Initialise prototype properties
      this.widgets = {};

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container"], this.onComponentsLoaded, this);

      return this;
   }

   Alfresco.MyTasks.prototype =
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
          * Currently active filter.
          * 
          * @property activeFilter
          * @type string
          * @default "all"
          */
         activeFilter: "all"
      },
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,

      /**
       * Task list DOM container.
       * 
       * @property taskList
       * @type object
       */
      taskList: null,

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setMessages: function DL_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function MyTasks_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function MyTasks_onReady()
      {
         var me = this;

         // "All" filter
         this.widgets.all = new YAHOO.widget.Button(this.id + "-all",
         {
            type: "checkbox",
            value: "all",
            checked: true
         });
         this.widgets.all.on("checkedChange", this.onAllCheckedChanged, this.widgets.all, this);
         
         // "Invites" filter
         this.widgets.invites = new YAHOO.widget.Button(this.id + "-invites",
         {
            type: "checkbox",
            value: "invites",
            checked: false
         });
         this.widgets.invites.on("checkedChange", this.onInvitesCheckedChanged, this.widgets.invites, this);
         
         // DueDate dropdown filter
         this.widgets.dueOn = new YAHOO.widget.Button(this.id + "-dueOn",
         {
            type: "split",
            menu: this.id + "-dueOn-menu"
         });
         this.widgets.dueOn.on("click", this.onDateFilterClicked, this, true);
         this.widgets.dueOn.getMenu().subscribe("click", function (p_sType, p_aArgs)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               me.widgets.dueOn.set("label", menuItem.cfg.getProperty("text"));
               me.onDateFilterChanged.call(me, p_aArgs[1]);
            }
         });
         this.widgets.dueOn.value = "today";

         // Hook events for task transitions
         var fnTaskHandler = function MyTasks_fnTaskHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "span");
            if (owner !== null)
            {
               var taskTransition = owner.className;
               me.onTransitionTask.call(me, taskTransition);
               args[1].stop = true;
            }
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("task-transition", fnTaskHandler);
         
         // The task list container
         this.taskList = Dom.get(this.id + "-taskList");
         
         // Populate the task list
         this.populateTaskList("all");
      },
      
      /**
       * Populate the task list via Ajax request
       * @method populateTaskList
       */
      populateTaskList: function MyTasks_populateTaskList(filter)
      {
         // Load the task list
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/dashlets/my-tasks/list",
            dataObj:
            {
               filter: filter
            },
            successCallback:
            {
               fn: this.onTasksLoaded,
               scope: this,
               obj: filter
            },
            failureCallback:
            {
               fn: this.onTasksLoadFailed,
               scope: this
            },
            scope: this
         });
      },
      
      /**
       * Task list loaded successfully
       * @method onTasksLoaded
       * @param p_response {object} Response object from request
       */
      onTasksLoaded: function MyTasks_onTasksLoaded(p_response, p_obj)
      {
         this.options.activeFilter = p_obj;
         this.taskList.innerHTML = p_response.serverResponse.responseText;
      },

      /**
       * Task list load failed
       * @method onTasksLoadFailed
       */
      onTasksLoadFailed: function MyTasks_onTasksLoadFailed()
      {
         this.taskList.innerHTML = '<div class="detail-list-item first-item last-item"><span>' + this._msg("label.load-failed") + '</span></div>';
      },
      
      /**
       * Sets the active filter highlight in the UI
       * @method setActiveFilter
       * @param filter {string} The current filter
       */
      setActiveFilter: function MyTasks_setActiveFilter(filter)
      {
         switch (filter)
         {
            case "all":
               this.widgets.invites.set("checked", false, true);
               Dom.removeClass(this.widgets.dueOn.get("element"), "yui-checkbox-button-checked");
               break;

            case "invites":
               this.widgets.all.set("checked", false, true);
               Dom.removeClass(this.widgets.dueOn.get("element"), "yui-checkbox-button-checked");
               break;
            
            default:
               this.widgets.all.set("checked", false, true);
               this.widgets.invites.set("checked", false, true);
               Dom.addClass(this.widgets.dueOn.get("element"), "yui-checkbox-button-checked");
               break;
         }
         
      },
      
      /**
       * Task transitioned successfully
       * @method onTaskTransitioned
       */
      onTaskTransitioned: function MyTasks_onTaskTransitioned()
      {
         this.populateTaskList(this.options.activeFilter);
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * All tasks
       * @method onAllCheckedChanged
       * @param p_oEvent {object} Button event
       * @param p_obj {object} Button
       */
      onAllCheckedChanged: function MyTasks_onAllCheckedChanged(p_oEvent, p_obj)
      {
         this.setActiveFilter("all");
         this.populateTaskList("all");
         p_obj.set("checked", true, true);
      },

      /**
       * Invite tasks
       * @method onInvitesCheckedChanged
       * @param p_oEvent {object} Button event
       * @param p_obj {object} Button
       */
      onInvitesCheckedChanged: function MyTasks_onInvitesCheckedChanged(p_oEvent, p_obj)
      {
         this.setActiveFilter("invites");
         this.populateTaskList("invites");
         p_obj.set("checked", true, true);
      },

      /**
       * Date button clicked event handler
       * @method onDateFilterClicked
       * @param p_oEvent {object} Dom event
       */
      onDateFilterClicked: function MyTasks_onDateFilterClicked(p_oEvent)
      {
         var filter =  this.widgets.dueOn.value;
         this.setActiveFilter(filter);
         this.populateTaskList(filter);
      },
      
      /**
       * Date drop-down changed event handler
       * @method onDateFilterChanged
       * @param p_oMenuItem {object} Selected menu item
       */
      onDateFilterChanged: function MyTasks_onDateFilterChanged(p_oMenuItem)
      {
         var filter = p_oMenuItem.value;
         this.widgets.dueOn.value = filter;
         this.setActiveFilter(filter);
         this.populateTaskList(filter);
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR ACTIONS
       * Disconnected event handlers for action event notification
       */

      /**
       * Task transition event handler
       * @method onTransitionTask
       * @param taskTransition {string} TaskId and TransitionId separated by space
       */
      onTransitionTask: function MyTasks_onTransitionTask(taskTransition)
      {
         var params = taskTransition.split(" ");
         var taskId = null;
         var transitionId = null;
         
         if (params.length == 1)
         {
            if (params[0].indexOf("jbpm$") != -1)
            {
               taskId = params[0];
               transitionId = "";
            }
         }
         else if (params.length == 2)
         {
            if (params[0].indexOf("jbpm$") != -1)
            {
               taskId = params[0];
               transitionId = "/" + params[1];
            }
            else
            {
               transitionId = "/" + params[0];
               taskId = params[1];
            }

         }
         
         if (taskId !== null)
         {
            var url = YAHOO.lang.substitute("api/workflow/task/end/{taskId}{transitionId}",
            {
               taskId: taskId,
               transitionId: transitionId
            });

            // Transition the task
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + url,
               method: "post",
               successCallback:
               {
                  fn: this.onTaskTransitioned,
                  scope: this
               },
               successMessage: this._msg("transition.success"),
               failureMessage: this._msg("transition.failure"),
               scope: this
            });
         }
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function MyTasks__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.MyTasks", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
