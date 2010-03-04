/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
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
    * Preferences
    */
   var PREFERENCES_MYTASKS = "org.alfresco.share.mytasks",
       PREF_FILTER = PREFERENCES_MYTASKS + ".filter";
   
   /**
    * Constants
    */
   var FILTER_ALL = "all";
   var FILTER_INVITES = "invites";
   
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
      this.activeTaskTransitions = {};

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container"], this.onComponentsLoaded, this);

      // Preferences service
      this.preferencesService = new Alfresco.service.Preferences();

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
         activeFilter: FILTER_ALL
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
       *
       *
       * @property: activeTaskTransitions
       * @type: object
       */
      activeTaskTransitions: null,

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
            value: FILTER_ALL,
            checked: false
         });
         this.widgets.all.on("checkedChange", this.onAllCheckedChanged, this.widgets.all, this);
         
         // "Invites" filter
         this.widgets.invites = new YAHOO.widget.Button(this.id + "-invites",
         {
            type: "checkbox",
            value: FILTER_INVITES,
            checked: false
         });
         this.widgets.invites.on("checkedChange", this.onInvitesCheckedChanged, this.widgets.invites, this);
         
         // DueDate dropdown filter
         this.widgets.dueOn = new YAHOO.widget.Button(this.id + "-dueOn",
         {
            type: "split",
            menu: this.id + "-dueOn-menu",
            lazyloadmenu: false
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
         
         // Load preferences to override default filter
         this.preferencesService.request(PREF_FILTER,
         {
            successCallback:
            {
               fn: function(p_oResponse)
               {
                  var filterPreference = Alfresco.util.findValueByDotNotation(p_oResponse.json, PREF_FILTER, null);
                  if (filterPreference !== null)
                  {
                     // test for the two well known boolean filters - else a due date is specified
                     // therefore manually update the selected menu button label and highlight
                     switch (filterPreference)
                     {
                        case FILTER_ALL:
                           this.widgets.all.set("checked", true, true);
                           break;
                        
                        case FILTER_INVITES:
                           this.widgets.invites.set("checked", true, true);
                           break;
                        
                        default:
                        {
                           this.widgets.dueOn.value = filterPreference;
                           
                           // set the correct menu label
                           var menuItems = this.widgets.dueOn.getMenu().getItems();
                           for (index in menuItems)
                           {
                              if (menuItems.hasOwnProperty(index))
                              {
                                 if (menuItems[index].value === filterPreference)
                                 {
                                    this.widgets.dueOn.set("label", menuItems[index].cfg.getProperty("text"));
                                    Dom.addClass(this.widgets.dueOn.get("element"), "yui-checkbox-button-checked");
                                    break;
                                 }
                              }
                           }
                           break;
                        }
                     }
                     this.populateTaskList(filterPreference);
                  }
                  else
                  {
                     this.widgets.all.set("checked", true, true);
                     this.populateTaskList(FILTER_ALL);
                  }
               },
               scope: this
            },
            failureCallback:
            {
               fn: function()
               {
                  this.widgets.all.set("checked", true, true);
                  this.populateTaskList(FILTER_ALL);
               },
               scope: this
            }
         });
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
         // Reset transition so we don't stop any transitions from being sent
         this.activeTaskTransitions = {};
         this.options.activeFilter = p_obj;
         this.taskList.innerHTML = p_response.serverResponse.responseText;
      },

      /**
       * Task list load failed
       * @method onTasksLoadFailed
       */
      onTasksLoadFailed: function MyTasks_onTasksLoadFailed()
      {
         // Reset transition so we don't stop any transitions from being sent
         this.activeTaskTransitions = {};
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
            case FILTER_ALL:
               this.widgets.invites.set("checked", false, true);
               Dom.removeClass(this.widgets.dueOn.get("element"), "yui-checkbox-button-checked");
               break;

            case FILTER_INVITES:
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
       * @method onTaskTransitionSuccess
       */
      onTaskTransitionSuccess: function MyTasks_onTaskTransitionSuccess()
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
         this.setActiveFilter(FILTER_ALL);
         this.populateTaskList(FILTER_ALL);
         p_obj.set("checked", true, true);
         this.preferencesService.set(PREF_FILTER, FILTER_ALL);
      },

      /**
       * Invite tasks
       * @method onInvitesCheckedChanged
       * @param p_oEvent {object} Button event
       * @param p_obj {object} Button
       */
      onInvitesCheckedChanged: function MyTasks_onInvitesCheckedChanged(p_oEvent, p_obj)
      {
         this.setActiveFilter(FILTER_INVITES);
         this.populateTaskList(FILTER_INVITES);
         p_obj.set("checked", true, true);
         this.preferencesService.set(PREF_FILTER, FILTER_INVITES);
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
         this.preferencesService.set(PREF_FILTER, filter);
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

         if (taskId !== null && !this.activeTaskTransitions[taskId])
         {
            // Set this taskId to true so we don't send multiple calls for the same task
            this.activeTaskTransitions[taskId] = true;

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
                  fn: function()
                  {
                     this.onTaskTransitionSuccess();
                  },
                  scope: this
               },
               successMessage: this._msg("transition.success"),
               failureCallback:
               {
                  fn: function()
                  {
                     this.activeTaskTransitions[taskId] = false
                  },
                  scope: this
               },
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
