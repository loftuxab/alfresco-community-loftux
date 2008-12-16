/**
 * LinksView component.
 *
 * Component to view a link
 *
 * @namespace Alfresco
 * @class Alfresco.LinksView
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
    * LinksView constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.LinksView} The new LinksView instance
    * @constructor
    */
   Alfresco.LinksView = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.LinksView";
      this.id = htmlId;

      /* Initialise prototype properties */
      this.widgets = {};
      this.tagId =
      {
         id: 0,
         tags: {}
      };

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["json", "connection", "event", "button", "menu"], this.onComponentsLoaded, this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);

      return this;
   }

   Alfresco.LinksView.prototype =
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

         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "links"
          */
         containerId: "links",

         /**
          * Id of the displayed link.
          */
         linkId: ""
      },

      /**
       * Stores the data displayed by this component
       */
      linksData: null,

      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
      widgets : null,

      /**
       * Object literal used to generate unique tag ids
       *
       * @property tagId
       * @type object
       */
      tagId: null,

      /**
       * Tells whether an action is currently ongoing.
       *
       * @property busy
       * @type boolean
       * @see setBusy/releaseBusy
       */
      busy: false,

      /**
       * True if publishing actions should be displayed
       *
       * @property showPublishingActions
       * @type boolean
       */
      showPublishingActions: false,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function LinksView_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.LinksView} returns 'this' for method chaining
       */
      setMessages: function LinksView_setMessages(obj)
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
      onComponentsLoaded: function LinksView_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function LinksView_onReady()
      {
         var me = this;

         // Hook action events.
         var fnActionHandlerDiv = function LinksView_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = "";
               action = owner.className;
               if (typeof me[action] == "function")
               {
                  me[action].call(me, me.linksData.name);
                  args[1].stop = true;
               }
            }
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("link-action-link-div", fnActionHandlerDiv);

         // Hook tag clicks
         Alfresco.util.tags.registerTagActionHandler(this);

         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onLinkElementMouseEntered, this.onLinkElementMouseExited, this);

         // load the post data
         this._loadLinksData();
      },

      /**
       * Loads the comments for the provided nodeRef and refreshes the ui
       */
      _loadLinksData: function LinksView__loadLinksData()
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/links/link/site/{site}/{container}/{linkId}",
         {
            site : this.options.siteId,
            container: this.options.containerId,
            linkId: this.options.linkId
         });

         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            successCallback:
            {
               fn: this.loadLinksDataSuccess,
               scope: this
            },
            failureMessage: this._msg("message.loadpostdata.failure")
         });
      },

      /**
       * Success handler for a link request. Updates the UI using the link data
       * provided in the response object.
       *
       * @param response {object} the ajax request response
       */
      loadLinksDataSuccess: function LinksView_loadLinksDataSuccess(response)
      { 
         // store the returned data locally
         var data = response.json.item
         this.linksData = data;
         

         // get the container div to insert the the post into
         var viewDiv = Dom.get(this.id + '-link-view-div');

         // render the link and insert it into the div
         var html = this.renderLinks(data);
         viewDiv.innerHTML = html;

         // attach the rollover listeners
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'link', 'div');

         // inform interested comment components about the loaded link
         this.sendCommentedNodeEvent();
      },

      /**
       * Sends out a setCommentedNode bubble event.
       */
      sendCommentedNodeEvent: function LinksView_sendCommentedNodeEvent()
      {
         var eventData =
         {
            nodeRef: this.linksData.nodeRef,
            title: this.linksData.title,
            page: "links-view",
            pageParams:
            {
               linkId: this.linksData.name
            }
         }
         YAHOO.Bubbling.fire("setCommentedNode", eventData);
      },
      /**
       * Renders the links post given a link data object returned by the server.
       */
      renderLinks: function LinksView_renderLinks(data)
      {   
         var me = this;
         // preformat some values
         var linksViewUrl = me.generateLinksViewUrl(this.options.siteId, this.options.containerId, data.name);
         var statusLabel = Alfresco.util.links.generateLinksStatusLabel(this, data);
         var authorLink = Alfresco.util.people.generateUserLink(data.author);

         var html = '';
         html += '<div id="' + this.id + '-linksview" class="node post linksview">'
         html += Alfresco.util.links.generateLinksActions(this, data, 'div', this.showPublishingActions);

         // content
         html += '<div class="nodeContent">';
         html += '<div class="nodeTitle"><a href="' + linksViewUrl + '">' + $html(data.title) + '</a> ';
         html += '<span class="nodeStatus">' + statusLabel + '</span>';
         html += '</div>';

         html += '<div class="nodeURL">';
         html += '<span class="nodeURLValue>' + data.url + '</span>';
         html += '</div>';

         html += '<div class="published">';
         if (! data.isDraft)
         {
            html += '<span class="nodeAttrLabel">' + this._msg("post.publishedOn") + ': </span>';
            html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.releasedOn) + '</span>';
            html += '<span class="separator">&nbsp;</span>';
         }

         html += '<span class="nodeAttrLabel">' + this._msg("post.author") + ': </span>';
         html += '<span class="nodeAttrValue">' + authorLink + '</span>';

         if (data.isPublished && data.postLink != undefined && data.postLink.length > 0)
         {
            html += '<span class="separator">&nbsp;</span>';
            html += '<span class="nodeAttrLabel">' + this._msg("post.externalLink") + ': </span>';
            html += '<span class="nodeAttrValue"><a target="_blank" href="' + data.postLink + '">' + this._msg("post.clickHere") + '</a></span>';
         }

         html += '<span class="separator">&nbsp;</span>';
         html += '<span class="nodeAttrLabel tag">' + this._msg("post.tags") + ': </span>';
         if (data.tags.length > 0)
         {
            for (var x=0; x < data.tags.length; x++)
            {
               if (x > 0)
               {
                  html += ', ';
               }
               html += Alfresco.util.tags.generateTagLink(this, data.tags[x]);
            }
         }
         else
         {
            html += '<span class="nodeAttrValue">' + this._msg("post.noTags") + '</span>';
         }
         html += '</div>'

         html += '<div class="content yuieditor">' + Alfresco.util.stripUnsafeHTMLTags(data.description) + '</div>';
         html += '</div></div>';
         return html;
      },

      /**
      * Generate a view url for a given site, link id.
      *
      * @param linkId the id/name of the post
      * @return an url to access the post
      */
      generateLinksViewUrl: function LinksView_generateLinksViewUrl(site, container, linkId)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/links-view?container={container}&linkId={linkId}",
         {
            site: site,
            container: container,
            linkId: linkId
         });
         return url;
      },

      // Actions

      /**
       * Tag selected handler.
       *
       * @method onTagSelected
       * @param tagId {string} Tag name.
       * @param target {HTMLElement} Target element clicked.
       */
      onTagSelected: function LinksView_onTagSelected(layer, args)
      { 
         var obj = args[1];
         if (obj && (obj.tagName !== null))
         {
            var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/links?container={container}&filterId={filterId}&filterOwner={filterOwner}&filterData={filterData}",
            {
               site: this.options.siteId,
               container: this.options.containerId,
               filterId: "tag",
               filterOwner: "Alfresco.LinkTags",
               filterData: obj.tagName
            });
            window.location = url;
         }
      },

       /**
       * Link deletion implementation
       *
       * @method onDeleteLink
       * @param linkId {string} the id of the link to delete
       */
      onDeleteLink: function LinksView_onDeleteLink(linkId)
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: this._msg("message.confirm.delete", $html(this.linksData.title)),
            buttons: [
            {
               text: this._msg("button.delete"),
               handler: function LinksView_onDeleteLink_delete()
               {
                  this.destroy();
                  me._deleteLinkConfirm.call(me, me.linksData.name);
               },
               isDefault: true
            },
            {
               text: this._msg("button.cancel"),
               handler: function LinksView_onDeleteLink_cancel()
               {
                  this.destroy();
               }
            }]
         });
      },

      /**
       * Link deletion implementation
       *
       * @method _deleteLinkConfirm
       * @param linkId {string} the id of the link to delete
       */
      _deleteLinkConfirm: function LinksView__deleteLinkConfirm(linkId)
      {
         // show busy message
         if (! this._setBusy(this._msg('message.wait')))
         {
            return;
         }

         // ajax request success handler
         var onDeletedSuccess = function LinksView_onDeletedSuccess(response)
         {
            // remove busy message
            this._releaseBusy();

            // load the link list page
            var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/links?container={container}",
            {
               site: this.options.siteId,
               container: this.options.containerId
            });
            window.location = url;
         };

         // get the url to call
         
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/links/delete/site/{site}/{container}",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });

         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "POST",
            requestContentType : "application/json",
            responseContentType : "application/json",
            successMessage: this._msg("message.delete.success"),
            successCallback:
            {
               fn: onDeletedSuccess,
               scope: this
            },
            failureMessage: this._msg("message.delete.failure"),
            failureCallback:
            {
               fn: function(response) { this._releaseBusy(); },
               scope: this
            },
            dataObj :
            {
               items : [linkId]
            }
         });
      },

       /**
       * Loads the edit post form and displays it instead of the content
       * The div class should have the same name as the above function (onEditLinks)
       */
      onEditLink: function LinksView_onEditNode(linkId)
      {  
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/links-linkedit?container={container}&linkId={linkId}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            linkId: linkId
         });
         window.location = url;
      },

      // mouse hover functionality

      /** Called when the mouse enters into a list item. */
      onLinkElementMouseEntered: function LinksView_onLinkElementMouseEntered(layer, args)
      {
         // make sure the user sees at least one action, otherwise we won't highlight
         var permissions = this.linksData.permissions;
         if (! (permissions.edit || permissions["delete"]))
         {
            return;
         }

         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
      },

      /** Called whenever the mouse exits a list item. */
      onLinkElementMouseExited: function LinksView_onLinkElementMouseExited(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.removeClass(elem, 'overNode');
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Displays the provided busyMessage but only in case
       * the component isn't busy set.
       *
       * @return true if the busy state was set, false if the component is already busy
       */
      _setBusy: function LinksList__setBusy(busyMessage)
      {
         if (this.busy)
         {
            return false;
         }
         this.busy = true;
         this.widgets.busyMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: busyMessage,
            spanClass: "wait",
            displayTime: 0
         });
         return true;
      },

      /**
       * Removes the busy message and marks the component as non-busy
       */
      _releaseBusy: function LinksList__releaseBusy()
      {
         if (this.busy)
         {
            this.widgets.busyMessage.destroy();
            this.busy = false;
            return true;
         }
         else
         {
            return false;
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
      _msg: function LinksView_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.LinksView", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
