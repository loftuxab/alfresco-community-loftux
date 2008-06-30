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
 * DiscussionsTopicList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DiscussionsTopicList
 */
(function()
{
   /**
    * DiscussionsTopicList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DiscussionsTopicList} The new DiscussionsTopicList instance
    * @constructor
    */
   Alfresco.DiscussionsTopicList = function(htmlId)
   {
      this.name = "Alfresco.DiscussionsTopicList";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container", "dom", "event", "history"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DiscussionsTopicList.prototype =
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
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",
         
         containerId: "discussions",
         
         /**
          * Position inside the pages
          */
         pos: 0,
         
         /**
          * Size of elements to show
          */
         size: 10,
         
         filter: "",
         
         tag: "",
         
         viewmode: ""
      },
   	
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function DiscussionsTopicList_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      setMessages: function DiscussionsTopic_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DiscussionsTopicList_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DiscussionsTopicList_onReady()
      {
         // used by the filter, tag and view buttons to set options
         YAHOO.Bubbling.on("onSetTopicListParams", this.onSetTopicListParams, this);

         // Links - PENDING: this should be done in a more generic way
         YAHOO.util.Event.addListener("simple-list-view", "click",
            function simpleListViewClicked(e) {
               var owner = YAHOO.Bubbling.fire('onSetTopicListParams', {viewmode: 'simple'});
               return true;
            }
         );
         YAHOO.util.Event.addListener("detailed-list-view", "click",
            function detailedListViewClicked(e) {
               var owner = YAHOO.Bubbling.fire('onSetTopicListParams', {viewmode: 'details'});
               return true;
            }
         ); 
          
         // Event on Paginator
         var me = this;
         YAHOO.Bubbling.addDefaultAction("pagerItem", function Discussions_onTopicPaginationClicked(layer, args)
         {
         	// TODO: Changer filter to use current filter-
            //var owner = YAHOO.Bubbling.fire('onSetTopicListParams', {page : args[1].el.id, filter : 'new'});
            //return true;
            var elem = args[1].anchor;
            if (elem.id.substring(0, "GoToPrevious".length) == "GoToPrevious") {
                me.options.pos = me.options.pos - me.options.size;
                me._reloadData();
            }
            else if (elem.id.substring(0, "GoToNext".length) == "GoToNext") {
                me.options.pos = me.options.pos + me.options.size;
                me._reloadData();
            }
            else if (elem.id.substring(0, "GoToPage-".length) == "GoToPage-") {
                var page = elem.id.substring("GoToPage-".length);
                me.options.pos = (page-1) * me.options.size;
                me._reloadData();
            }
         });
         
         // Hook action events
         YAHOO.Bubbling.addDefaultAction("action-link", function DiscussionsTopicList_filterAction(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = owner.className;
               var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  // extract the id from the element
                  var elemId = owner.id.substring((action + "-").length);
                  me[action].call(me, elemId);
                  args[1].stop = true;
               }
            }
      		 
            return true;
         });
         
         // initialize the mouse over functionality for the list
         this.initMouseOverListeners();
         
      },

      
      
      /**
       * Attaches mouseover/mouseout events with the specified listContainerId.
       * Whenever one of its children (that have the class "className") is hovered,
       * bubbling events are fired.
       * 
       * @param listContainerId The id of the container that holds the hovered elements
       * @param className the classname of the hover elements
       */
     /* _attachListCellMouseListener: function(listContainerId, className)
      {
         var mouseOverHandler = function(event)
         {
            // get the element, search the ancestors if necessary to get the right element
            var elem = YAHOO.util.Event.getTarget(event);
            if (! YAHOO.util.Dom.hasClass(elem, className))
            {
                elem = YAHOO.util.Dom.getAncestorByClassName(elem, className);
            }
            if (elem !== null)
            {
                YAHOO.Bubbling.fire('onListElementMouseEntered', {event : event, owner : elem});
                return true;
            }
         }
             
         var mouseExitHandler = function (event)
         {
            // only proceed if the actual list element is the target
            var elem = YAHOO.util.Event.getTarget(event);
            if (YAHOO.util.Dom.hasClass(elem, className))
            {
                YAHOO.Bubbling.fire('onListElementMouseExited', {event : event, owner : elem});
                return true;
            }
         };

         var containerElem = YAHOO.util.Dom.get(listContainerId);
         YAHOO.util.Event.addListener(containerElem, 'mouseover', mouseOverHandler);
         YAHOO.util.Event.addListener(containerElem, 'mouseout', mouseExitHandler);
      },*/
      
      /** Initializes the mouseover listeners for the topic list */
/*      initMouseOverListeners: function DiscussionsTopicList_initMouseOverListeners()
      {
         this._attachListCellMouseListener(this.id + "-topiclist", 'node');
      },*/
      
      
      /**
       * Attaches a listener to all passed elements.
       */
      _attachRolloverListener: function(elem, mouseOverEventName, mouseOutEventName)
      {  
         var eventElem = elem;
         
         var mouseOverHandler = function(e)
         {
             // find out whether we actually moved inside the 
             if (! e) var e = window.event;
             var relTarg = e.relatedTarget || e.fromElement;
             while (relTarg != null && relTarg != eventElem && relTarg.nodeName != 'BODY') {
                relTarg = relTarg.parentNode
             }
             if (relTarg == eventElem) return;
             
             // the mouse entered the element, fire an event to inform about it
             YAHOO.Bubbling.fire(mouseOverEventName, {event : e, target : eventElem});
         };
         
         var mouseOutHandler = function(e)
         {
             // find out whether we actually moved inside the 
             if (! e) var e = window.event;
             var relTarg = e.relatedTarget || e.toElement;
             while (relTarg != null && relTarg != eventElem && relTarg.nodeName != 'BODY') {
                relTarg = relTarg.parentNode
             }
             if (relTarg == eventElem) return;
             
             // the mouse exited the element, fire an event to inform about it
             YAHOO.Bubbling.fire(mouseOutEventName, {event : e, target : eventElem});
         };
         
         YAHOO.util.Event.addListener(elem, 'mouseover', mouseOverHandler);
         YAHOO.util.Event.addListener(elem, 'mouseout', mouseOutHandler);
      },
      
      firstMouseOverInit: true,
      
      initMouseOverListeners: function DiscussionsTopicList_initMouseOverListeners()
      {
         var mouseEnteredBubbleEventName = 'onTopicListElementMouseEntered';
         var mouseExitedBubbleEventName = 'onTopicListElementMouseExited';
         var divs = YAHOO.util.Dom.getElementsByClassName('topic', 'div');
         for (var x=0; x < divs.length; x++) {
             this._attachRolloverListener(divs[x], mouseEnteredBubbleEventName, mouseExitedBubbleEventName);
         }
         
         if (this.firstMouseOverInit) {
            this.firstMouseOverInit = false;
            // manage mouse hover/exit
            YAHOO.Bubbling.on(mouseEnteredBubbleEventName, this.onListElementMouseEntered, this);
            YAHOO.Bubbling.on(mouseExitedBubbleEventName, this.onListElementMouseExited, this);
         }
      },
      
      /** Called when the mouse enters into a list item. */
      onListElementMouseEntered: function DiscussionsTopicList_onListElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem, null );
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
      
      /** Called whenever the mouse exits a list item. */
      onListElementMouseExited: function DiscussionsTopicList_onListElementMouseExited(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.removeClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem , null );
         YAHOO.util.Dom.removeClass(editBloc, 'showEditBloc');
      },

      /** Sends the user to the topic view page. */
      _gotoTopic: function DiscussionsTopicList__gotoTopic(topicId, isEdit)
      {
          var url = Alfresco.constants.URL_CONTEXT;
          url += "page/discussions-topicview?site=" + this.options.siteId + "&topicId=" + topicId;
          if (isEdit)
          {
              url += "&edit=true";
          }
          window.location = url;
      },

      /**
       * Action handler for the view action
       */
      onViewNode: function DiscussionsTopicList_onViewTopic(id)
      {
          this._gotoTopic(id, false);
      },
      
      onEditNode: function DiscussionsTopicList_onViewTopic(id)
      {
          this._gotoTopic(id, true);
      },
      
      /**
       * Deletes a topic.
       */
      onDeleteNode: function DiscussionsTopic_onDeleteTopic(id)
      {
         // make an ajax request to delete the topic
         // we can directly go to alfresco for this
         Alfresco.util.Ajax.request(
		   {
		      url: Alfresco.constants.PROXY_URI + "forum/post/site/" + this.options.siteId + "/" + this.options.containerId + "/" /* ADD PATH */ + id,
		      method: "DELETE",
		      responseContentType : "application/json",
		      /*dataObj:
		      {
		         site   : this.options.siteId,
		         container : "discussions",
		         path : this.options.topicId
		      },*/
		      successCallback:
		      {
		         fn: this._onDeleted,
		         scope: this
		      },
		      failureMessage: this._msg("topic.msg.failedDelete")
		   });
      },

      _onDeleted: function DiscussionsTopic__onDeleted(response)
      {
         if (response.json.error == undefined)
         {
            // reload the list
            this._reloadData();
            Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.deleted")});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.unableDelete") + response.json.error});
         }
      },
      
      

	  /**
	   * Updates the filter and reloads the page
	   */ 
	  onSetTopicListParams: function Discussions_onTopicCategoryChange(layer, args)
	  {
         // check the filter or tag
         if (args[1].filter != undefined)
         {
            this.options.filter = args[1].filter;
            this.options.tag = "";
         }
         else if (args[1].tag != undefined)
         {
            this.options.tag = args[1].tag;
            this.options.filter = "";
         }
         
         // check the viewmode param
         if (args[1].viewmode != undefined)
         {
             this.options.viewmode = args[1].viewmode;
         }
         
         
         // reload the table
         this._reloadData();
	  },
      
      _reloadData: function Discussions__reloadData() 
      {
      	Alfresco.util.Ajax.request(
		   {
		      url: Alfresco.constants.URL_SERVICECONTEXT + "modules/discussions/topiclist/get-topics",
		      responseContentType : "application/json",
		      dataObj:
		      {
		         site    : this.options.siteId,
		         pos     : this.options.pos,
		         size    : this.options.size,
		         filter  : this.options.filter,
		         tag     : this.options.tag,
		         viewmode: this.options.viewmode
		      },
		      successCallback:
		      {
		         fn: this._processData,
		         scope: this
		      },
		      failureMessage: this._msg("topic.msg.failedLoad1")
		   });
      },
      
      _processData: function Discussions__processData(response)
	  {
	     // first check whether we got an error back
	     if (response.json.error != undefined)
	     {
	        Alfresco.util.PopupManager.displayMessage({text: this._msg("topic.msg.failedLoadData") + response.json.error});
	     }
	     else
	     {
	        // update paginator
  	        var elem = YAHOO.util.Dom.get(this.id + "-paginator");
            elem.innerHTML = response.json.paginatorHtml;
	        
	        // update the internal position counter
	        this.options.pos = response.json.startIndex;
	         
	        // update title
	        var elem = YAHOO.util.Dom.get(this.id + "-listtitle");
            elem.innerHTML = response.json.listTitle;
	         
	        // update list
	        var elem = YAHOO.util.Dom.get(this.id + "-topiclist");
            elem.innerHTML = response.json.listHtml;
            this.initMouseOverListeners();
            
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
      _msg: function DiscussionsTopicList_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DiscussionsTopicList", Array.prototype.slice.call(arguments).slice(1));
      }

   };
})();