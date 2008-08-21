/**
 * CommentList component.
 * 
 * Displays a list of comments.
 * 
 * @namespace Alfresco
 * @class Alfresco.CommentList
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
    * CommentList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.CommentList} The new Comment instance
    * @constructor
    */
   Alfresco.CommentList = function(htmlId)
   {
      this.name = "Alfresco.CommentList";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["event", "editor", "element", "dom"], this.onComponentsLoaded, this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("setCommentedNode", this.onSetCommentedNode, this);
      YAHOO.Bubbling.on("refreshComments", this.refreshComments, this);
      return this;
   }
   
   Alfresco.CommentList.prototype =
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
          * @default "blog"
          */
         containerId: "blog",
         
         /**
          * Node reference of the item to comment about
          */
         itemNodeRef: null,
         
         /**
          * Title of the item to comment about.
          * TODO: This is used for activity feed and should not be necessary here
          */
         itemTitle: null,
         
         /**
          * Name of the item to comment about.
          * TODO: This is used for activity feed and should not be necessary here
          */
         itemName: null,
         
         /**
          * Width to use for comment editor
          */
         width: 700,
         
         /**
          * Height to use for comment editor
          */
         height: 180
      },
      
      /**
       * Object containing data about the currently edited
       * comment.
       */
      editData: {
         editDiv : null,
         viewDiv : null,
         row : -1,
         data : null,
         widgets : {}
      },
      
      /**
       * Comments data
       */
      commentsData: null,
      
      /**
       * Set multiple initialization options at once.
       * 
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function CommentList_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setMessages: function CommentList_setMessages(obj)
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
      onComponentsLoaded: function CommentList_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function CommentList_onReady()
      { 
         var me = this;
         
         // Hook action events for the comments
         var fnActionHandlerDiv = function CommentList_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = owner.className;
               //var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  var commentElem = Dom.getAncestorByClassName(owner, 'comment');
                  var index = parseInt(commentElem.id.substring((me.id + '-comment-view-').length));
                  me[action].call(me, index);
                  args[1].stop = true;
               }
            }
      		 
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("blogcomment-action", fnActionHandlerDiv);

         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onCommentElementMouseEntered, this.onCommentElementMouseExited);
      },      
      
      
      /**
       * Called by another component to set the node for which comments should be displayed
       */
      onSetCommentedNode: function CommentList_onSetCommentedNode(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.itemNodeRef !== null) && (obj.itemName !== null) && (obj.itemTitle !== null))
         {
            this.options.itemNodeRef = obj.itemNodeRef;
            this.options.itemName = obj.itemName;
            this.options.itemTitle = obj.itemTitle;
            this._loadCommentsList();
         }
      },
    
      /**
       * Forces the comments list to fresh by reloading the data from the server
       */
      refreshComments: function CommentList_onFilterChanged(layer, args)
      {
         if (this.options.itemNodeRef && this.options.itemName && this.options.itemTitle)
         {
            this._loadCommentsList();
         }
      },
      
      /**
       * Loads the comments for the provided nodeRef and refreshes the ui
       */
      _loadCommentsList: function CommentList__loadCommentsList()
      {   
         // construct the url to call
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/node/{nodeRef}/comments",
         {
            nodeRef: this.options.itemNodeRef.replace(":/", "")
         });
         
         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            successCallback:
            {
               fn: this.loadCommentsSuccess,
               scope: this
            },
            failureMessage: this._msg("message.loadComments.failure")
         });
         
      },

      /**
       * Load comments ajax request success handler.
       */
      loadCommentsSuccess: function CommentsList_loadCommentsSuccess(response)
      {
         // make sure any edit data is cleared
         this._hideEditView();
          
         var comments = response.json.items;

         // Get the elements to update
         var bodyDiv = Dom.get(this.id + "-body");
         var titleDiv = Dom.get(this.id + "-title");
         var commentDiv = Dom.get(this.id + "-comments");
         
         // temporarily hide the container node
         bodyDiv.setAttribute("style", "display:none");
         
         // update the list name
         if (comments.length > 0)
         {
            titleDiv.innerHTML = comments.length + " Comments";
         }
         else
         {
            titleDiv.innerHTML = "No comments so far";
         }
         
         // Update the list elements
         var html = '';
         for (var x=0; x < comments.length; x++)
         {
            html += this.renderComment(x, comments[x]);
         }
         commentDiv.innerHTML = html;
         bodyDiv.removeAttribute("style");
         
         // init mouse over
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'comment', 'div');
         
         // keep a reference to the loaded data
         this.commentsData = comments;
      },

      /**
       * Edit comment action links handler.
       */
      onEditComment: function BlogComment_onEditComment(row)
      {
         var data = this.commentsData[row];
         this._loadForm(row, data);
      },

      /**
       * Delete comment action links handler.
       */
      onDeleteComment: function BlogComment_onEditComment(row)
      {
         var data = this.commentsData[row];
         this._deleteComment(row, data);
      },


      // Action implementation
      
      /**
       * Implementation of the comment deletion action
       */
      _deleteComment: function BlogComment__deleteComment(row, data)
      {
         // ajax request success handler
         var onDeleted = function BlogComment_onDeleted(response, object)
         {          
            // reload the comments list
            YAHOO.Bubbling.fire("refreshComments", {});
         };
          
         // put together the url displayed in the activity feed
         var browseItemUrl = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/blog-postview?container={container}&postId={postId}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            postId: this.options.itemName
         });

         // put together the request url to delete the comment
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "/api/comment/node/{nodeRef}/?site={site}&container={container}&itemTitle={itemTitle}&browseItemUrl={browseItemUrl}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            nodeRef: data.nodeRef.replace(":/", ""),
            itemTitle: this.options.itemTitle,
            browseItemurl: Alfresco.util.encodeHTML(browseItemUrl)
         });
         
         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "DELETE",
            responseContentType : "application/json",
            successMessage: this._msg("message.delete.success"),
            successCallback:
            {
               fn: onDeleted,
               scope: this
            },
            failureMessage: this._msg("message.delete.failure")
         });
      },


      // Form management

      /**
       * Loads the comment edit form
       */
      _loadForm: function BlogComment__loadForm(row, data)
      {
         // we always load the template through an ajax request
         var formId = this.id + "-edit-comment-" + row;
         
         // Load the UI template from the server
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "modules/blog/comments/edit-comment",
            dataObj:
            {
               htmlid: formId
            },
            successCallback:
            {
               fn: this.onFormLoaded,
               scope: this,
               obj: {formId: formId, row: row, data: data}
            },
            failureMessage: this._msg("message.loadeditform.failure"),
            execScripts: true
         });
      },

      /**
       * Event callback when comment form has been loaded
       *
       * @method onFormLoaded
       * @param response {object} Server response from load form XHR request
       */
      onFormLoaded: function BlogComment_onFormLoaded(response, obj)
      {
         // get the data and formId of the loaded form
         var row = 0;
         row = obj.row;
         var data = obj.data;
         var formId = obj.formId;
         
         // make sure no other forms are displayed
         this._hideEditView();
       
         // find the right divs to insert the html into
         var viewDiv = Dom.get(this.id + "-comment-view-" + row);
         var editDiv = Dom.get(this.id + "-comment-edit-" + row);
         
         // insert the html
         editDiv.innerHTML = response.serverResponse.responseText;
         
         // insert current values into the form
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/comment/node/{nodeRef}",
         {
            nodeRef: data.nodeRef.replace(':/', '')
         });
         Dom.get(formId + "-form").setAttribute("action", actionUrl);
         Dom.get(formId + "-site").setAttribute("value", this.options.siteId);
         Dom.get(formId + "-container").setAttribute("value", this.options.containerId);
         Dom.get(formId + "-itemTitle").setAttribute("value", this.options.itemTitle);
         var browseUrl = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/blog-postview?postId={itemName}",
         {
            site: this.options.siteId,
            itemName: this.options.itemName
         });
         Dom.get(formId + "-browseItemUrl").setAttribute("value", browseUrl);
         Dom.get(formId + "-content").value = data.content;
         
         // show the form and hide the view
         Dom.addClass(viewDiv, "hidden");
         Dom.removeClass(editDiv, "hidden");

         // store the edit data locally
         this.editData = {
            viewDiv: viewDiv,
            editDiv: editDiv,
            row: row,
            widgets : {},
            formId: formId
         }
             
         // and finally register the form handling
         this._registerEditCommentForm(row, data, formId);
      },

      /**
       * Registers the form with the html (that should be available in the page)
       * as well as the buttons that are part of the form.
       */
      _registerEditCommentForm: function BlogComment__registerEditCommentForm(row, data, formId)
      {
         // register the okButton
         this.editData.widgets.okButton = new YAHOO.widget.Button(formId + "-submit", {type: "submit"});
         
         // register the cancel button
         this.editData.widgets.cancelButton = new YAHOO.widget.Button(formId + "-cancel", {type: "button"});
         this.editData.widgets.cancelButton.subscribe("click", this.onEditFormCancelButtonClick, this, true);
         
         // instantiate the simple editor we use for the form
         this.editData.widgets.editor = new YAHOO.widget.SimpleEditor(formId + '-content', {
            height: this.options.height + 'px',
            width: this.options.width + 'px',
            dompath: false, //Turns on the bar at the bottom
            animate: false, //Animates the opening, closing and moving of Editor windows
            toolbar:  Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg)
         });
         this.editData.widgets.editor._render();
         
         // create the form that does the validation/submit
         var commentForm = new Alfresco.forms.Form(formId + "-form");
         commentForm.setShowSubmitStateDynamically(true, false);
         commentForm.setSubmitElements(this.editData.widgets.okButton);
         commentForm.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         commentForm.setAJAXSubmit(true,
         {
            successMessage: this._msg("message.savecomment.success"),
            successCallback:
            {
               fn: this.onEditFormSubmitSuccess,
               scope: this
            },
            failureMessage: this._msg("message.savecomment.failure")
         });
         commentForm.setSubmitAsJSON(true);
         commentForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               //Put the HTML back into the text area
               this.editData.widgets.editor.saveHTML();
            },
            scope: this
         }
         commentForm.init();
      },
      
      /**
       * Edit form ajax request success handler
       */
      onEditFormSubmitSuccess: function BlogComment_onCreateFormSubmitSuccess(response, object)
      {
         // the response contains the new data for the comment. Render the comment html
         // and insert it into the view element
         this.commentsData[this.editData.row] = response.json.item;
         var html = this.renderCommentView(this.editData.row, response.json.item);
         this.editData.viewDiv.innerHTML = html;
            
         // hide the form and display an information message
         this._hideEditView();
      },
      
      /**
       * Form cancel button click handler
       */
      onEditFormCancelButtonClick: function BlogComment_onEditFormCancelButtonClick(type, args)
      {
          this._hideEditView();
      },

      /**
       * Renders a comment element.
       * Each comment element consists of an edit and a view div.
       */
      renderComment: function BlogComment_renderComment(index, data)
      {
         // add a div for the comment edit form
         var html = '';
         html += '<div id="' + this.id + '-comment-edit-' + index + '" class="hidden"></div>';
         
         // output the view
         var rowClass = index % 2 == 0 ? "even" : "odd";
         html += '<div class="comment ' + rowClass + '" id="' + this.id + '-comment-view-' + index + '">';
         html += this.renderCommentView(index, data);
         html += '</div>';
         
         return html;
      },
      
      /**
       * Renders the content of the comment view div.
       */
      renderCommentView: function BlogComment_renderCommentView(index, data)
      {
         var html = '';
         
         // actions
         html += '<div class="nodeEdit">'
         if (data.permissions.edit)
         {
            html += '<div class="onEditComment"><a href="#" class="blogcomment-action">' + this._msg("action.edit") + '</a></div>';
         }
         if (data.permissions["delete"])
         {
            html += '<div class="onDeleteComment"><a href="#" class="blogcomment-action">' + this._msg("action.delete") + '</a></div>';
         }
         html += '</div>';
  
         // avatar image
         html += '<div class="authorPicture">' + Alfresco.util.people.generateUserAvatarImg(data.author) + '</div>'
  
         // comment info and content
         html += '<div class="nodeContent"><div class="userLink">' + Alfresco.util.people.generateUserLink(data.author);
         html += this._msg("comment.said") + ':';
         if (data.isUpdated)
         {
            html += '<span class="nodeStatus">(' + this._msg("comment.updated") + ')</span>';
         }
         html += '</div>'
         html += '<div class="content yuieditor">' + data.content + '</div>'
         html += '</div>';

         // footer
         html += '<div class="commentFooter">'
         html += '<span class="nodeFooterBlock">'
         html += '<span class="nodeAttrLabel">' + this._msg("comment.postedOn") + ': ';
         html += Alfresco.util.formatDate(data.createdOn);
         html += '</span></span></div>';
         
         return html;
      },
      
      
      // mouse over
      
      /** Called when the mouse enters into a list item. */
      onCommentElementMouseEntered: function CommentList_onCommentElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem, null );
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
      
      /** Called whenever the mouse exits a list item. */
      onCommentElementMouseExited: function CommentList_onCommentElementMouseExited(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.removeClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem , null );
         YAHOO.util.Dom.removeClass(editBloc, 'showEditBloc');
      },

   
      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Makes sure that all forms get removed and if available the hidden content
       * elements displayed again.
       */
      _hideEditView: function CommentList__hideEditView()
      {
         if (this.editData.editDiv != null)
         {
            // hide edit div and remove form
            Dom.addClass(this.editData.editDiv, "hidden");
            this.editData.editDiv.innerHTML = "";
            this.editData.editDiv = null;
         }
         if (this.editData.viewDiv != null)
         {
            // display view div
            Dom.removeClass(this.editData.viewDiv, "hidden");
            this.editData.viewDiv = null;
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
      _msg: function CommentList__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.CommentList", Array.prototype.slice.call(arguments).slice(1));
      }
      
   };
})();
