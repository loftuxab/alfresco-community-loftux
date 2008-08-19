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
         activeFilter: "all",
      },
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},

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
         
         // Hook events for filters
         var fnFilterHandler = function MyTasks_fnFilterHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "span");
            if (owner !== null)
            {
               var filter = owner.className;
               me.onFilterChanged.call(me, filter);
               args[1].stop = true;
            }
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("task-filter", fnFilterHandler);

         this.widgets.dueOnMenu = new YAHOO.widget.Menu(this.id + "-dueDate-menu",
         {
            context: [this.id + "-filter-dueOn", "tl", "bl"]
         });
         this.widgets.dueOnMenu.render();

         // Hook menu events for Due Date menu
         var fnMenuHandler = function MyTasks_fnMenuHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByClassName(args[1].anchor, "yuimenuitem");
            if (owner !== null)
            {
               var dateFilter = owner.className;
               me.onDateFilter.call(me, dateFilter);
               args[1].stop = true;
            }
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("yuimenuitemlabel", fnMenuHandler);
         
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
         this.setActiveFilter("all");
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
         this.taskList.innerHTML = this._msg("label.load-failed");
      },
      
      /**
       * Sets the active filter highlight in the UI
       * @method setActiveFilter
       * @param filter {string} The current filter
       * @param dateFilter {string} The date filter subtype
       */
      setActiveFilter: function MyTasks_setActiveFilter(filter, dateFilter)
      {
         var filters = YAHOO.util.Selector.query("a.task-filter", this.id);
         var elFilter, parent;
         for (var i = 0, j = filters.length; i < j; i++)
         {
            elFilter = filters[i];
            parent = elFilter.parentNode;
            if (Dom.hasClass(parent, filter))
            {
               if (filter == "dueOn")
               {
                  var dateMsg = this._msg("filter.due-on." + dateFilter);
                  Dom.get(this.id + "-filter-dueOn").innerHTML = this._msg("filter.due-on", dateMsg);
               }
               Dom.addClass(elFilter, "active");
            }
            else
            {
               Dom.removeClass(elFilter, "active");
            }
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
       * BUBBLING LIBRARY EVENT HANDLERS FOR ACTIONS
       * Disconnected event handlers for action event notification
       */

      /**
       * Filter changed event handler
       * @method onFilterChanged
       * @param filter {string} Filter name
       */
      onFilterChanged: function MyTasks_onFilterChanged(filter)
      {
         switch (filter)
         {
            case "dueOn":
               this.widgets.dueOnMenu.show();
               break;

            default:
               this.setActiveFilter(filter);
               this.populateTaskList(filter);
               break;
         }
      },

      /**
       * Date filter changed event handler
       * @method onDateFilter
       * @param dateFilter {string} Date filter name
       */
      onDateFilter: function MyTasks_onDateFilter(dateFilter)
      {
         this.widgets.dueOnMenu.hide();
         var filters = dateFilter.split(" ");
         var filter;
         
         for (var i = 0, j = filters.length; i < j; i++)
         {
            filter = filters[i];
            switch (filter)
            {
               // Deliberate falling through...
               case "today":
               case "tomorrow":
               case "this-week":
               case "next-week":
                  this.setActiveFilter("dueOn", filter);
                  this.populateTaskList(filter);
                  break;
            }
         }
      },

      /**
       * Task transition event handler
       * @method onTransitionTask
       * @param taskTransition {string} TaskId and TransitionId separated by space
       */
      onTransitionTask: function MyTasks_onTransitionTask(taskTransition)
      {
         var params = taskTransition.split(" ");
         if (params.length == 2)
         {
            var taskId, transitionId;
            if (params[0].indexOf("jbpm$") != -1)
            {
               taskId = params[0];
               transitionId = params[1];
            }
            else
            {
               transitionId = params[0];
               taskId = params[1];
            }

            var url = YAHOO.lang.substitute("api/workflow/task/end/{taskId}/{transitionId}",
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
      },
   };
})();
