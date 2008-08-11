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
    * Comment constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.Comment} The new Comment instance
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
         siteId: "",
         containerId: "",
         
         /**
          * Information about the node that is commented.
          * This data needs to be passed in through the setCommetedNode bubble event
          */
         itemNodeRef: "",
         itemTitle: "",
         itemName: ""
      },
      
      /** Object containing data about the currently edited
       * comment.
       * 
       */
      editData: {
         editDiv : null,
         viewDiv : null,
         row : -1,
         data : null,
         widgets : {}
      },
      
      
      /**
       * Displayed data.
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
       * Comment list functionality
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
         // request the comment for the node
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/node/{nodeRef}/comments",
         {
            nodeRef: this.options.itemNodeRef.replace(":/", "")
         });
         Alfresco.util.Ajax.request(
         {
            url: url,
            successCallback:
            {
               fn: this.loadCommentsSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.loadCommentsFailed,
               scope: this
            }
         });
         
      },

      loadCommentsFailed: function DLD_loadCommentsFailed(response)
      {
         // Display success message anyway
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.details.failed")
         });
      },

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
       * Actions
       */

      onEditComment: function BlogComment_onEditComment(row)
      {
         var data = this.commentsData[row];
         this._loadForm(row, data);
      },

      onDeleteComment: function BlogComment_onEditComment(row)
      {
         var data = this.commentsData[row];
         this._deleteComment(row, data);
      },


      /**
       *  Action implementations
       */
      
      _deleteComment: function BlogComment__deleteComment(row, data)
      {  
         // put together the url displayed in the activity feed
         var browseItemUrl = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/blog-postview?container={container}&postId={postId}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            postId: this.options.itemName
         });

         // put together the request url to update the comment
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "/api/comment/node/{nodeRef}/?site={site}&container={container}&itemTitle={itemTitle}&browseItemUrl={browseItemUrl}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            nodeRef: data.nodeRef.replace(":/", ""),
            itemTitle: this.options.itemTitle,
            browseItemurl: browseItemUrl
         });
         
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "DELETE",
            responseContentType : "application/json",
            successCallback:
            {
               fn: this._onDeleted,
               scope: this
            },
            failureMessage: this._msg("comments.msg.failedDeleted")
         });
      },
      
      _onDeleted: function BlogComment__onDeleted(response, object)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.unableDeleted", response.json.error)});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.deleted")});
          
            // reload the comments list
            YAHOO.Bubbling.fire("refreshComments", {});
         }
      },


      // Form management

      /** Loads and initializes the blog post edit form. */
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
            failureMessage: "Could not load comment edit form",
            execScripts: true
         });
      },

      /**
       * Event callback when dialog template has been loaded
       *
       * @method onFormLoaded
       * @param response {object} Server response from load template XHR request
       */
      onFormLoaded: function BlogComment_onFormLoaded(response, obj)
      {
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
         Dom.get(formId + "-nodeRef").setAttribute("value", data.nodeRef);
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

         // set the edit data
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
             height: '180px',
             width: '700px',
             dompath: false, //Turns on the bar at the bottom
             animate: false, //Animates the opening, closing and moving of Editor windows
             toolbar:  Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg)
         });
         this.editData.widgets.editor.render();
         
         // create the form that does the validation/submit
         var commentForm = new Alfresco.forms.Form(formId + "-form");
         commentForm.setShowSubmitStateDynamically(true, false);
         commentForm.setSubmitElements(this.editData.widgets.okButton);
         commentForm.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         commentForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onEditFormSubmitSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onEditFormSubmitFailure,
               scope: this
            }
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
      
      onEditFormSubmitSuccess: function BlogComment_onCreateFormSubmitSuccess(response, object)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.submitErrorReturn", response.json.error)});
         }
         else
         {
            // the response contains the new data for the comment. Render the comment html
            // and insert it into the view element
            this.commentsData[this.editData.row] = response.json.item;
            var html = this.renderCommentView(this.editData.row, response.json.item);
            this.editData.viewDiv.innerHTML = html;
            
            // hide the form and display an information message
            this._hideEditView();  
            Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.commentUpdated")});
         }
      },
      
      onEditFormSubmitFailure: function BlogComment_onCreateFormSubmitFailure(response)
      {
         Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.formSubmitFailed")});
      },
      
      
      onEditFormCancelButtonClick: function(type, args)
      {
          this._hideEditView();
      },
      
      /**
       * Makes sure that all forms get removed and if available the hidden content
       * elements displayed again.
       */
      _hideEditView: function()
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
      

      /*
       * Rendering related functions
       */       
       
      /**
       * Returns the display name given a person object
       * @param person an object with userName, optionally with firstName and lastName.
       * @return the display name of the person
       */
      _generateUserDisplayName : function BlogPostList_getUserDisplayName(person)
      {
            var displayName = person.userName;
            if ((person.firstName != undefined && person.firstName.length > 0) ||
                (person.lastName != undefined && person.lastName.length > 0))
            {
               displayName = '';
               if (person.firstName != undefined)
               {
                  displayName = person.firstName + ' ';
               }
               if (person.lastName != undefined)
               {
                  displayName += person.lastName;
               }
            }
            return displayName;
      },
      
      _generateUserAvatarImg: function CommentList_generateUserAvatarImg(person)
      {
          if (person.avatarRef)
          {
             var avatarUrl = Alfresco.constants.PROXY_URI + 'api/node/' + person.avatarRef.replace('://','/') + '/content/thumbnails/avatar?c=queue&amp;ph=true';
          }
          else
          {
             var avatarUrl = Alfresco.constants.URL_CONTEXT + 'components/images/no-user-photo-64.png'
          }
          return '<img src="' + avatarUrl + '" alt="' + person.username + '-avatar-image" />';
      },

      renderComment: function(index, data)
      {
         var html = '';
         // add a div for the comment edit form
         html += '<div id="' + this.id + '-comment-edit-' + index + '" class="hidden"></div>';
         
         // output the view
         var rowClass = index % 2 == 0 ? "even" : "odd";
         html += '<div class="comment ' + rowClass + '" id="' + this.id + '-comment-view-' + index + '">';
         html += this.renderCommentView(index, data);
         html += '</div>';
         
         return html;
      },
      
      renderCommentView: function(index, data)
      {
         var html = '';
         
         // actions
         html += '<div class="nodeEdit">'
         if (data.permissions.edit)
         {
            html += '<div class="onEditComment"><a href="#" class="blogcomment-action">' + this._msg("comments.action.edit") + '</a></div>';
         }
         if (data.permissions["delete"])
         {
            html += '<div class="onDeleteComment"><a href="#" class="blogcomment-action">' + this._msg("comments.action.delete") + '</a></div>';
         }
         html += '</div>';
  
         // avatar image
         html += '<div class="authorPicture">' + this._generateUserAvatarImg(data.author) + '</div>'
  
         // comment info and content
         html += '<div class="nodeContent"><div class="userLink">' + this._generateUserDisplayName(data.author);
         html += this._msg("comments.said") + ':';
         if (data.isUpdated)
         {
            html += '<span class="nodeStatus">(' + this._msg("comments.updated") + ')</span>';
         }
         html += '</div>'
         html += '<div class="content yuieditor">' + data.content + '</div>'
         html += '</div>';

         // footer
         html += '<div class="commentFooter">'
         html += '<span class="nodeFooterBlock">'
         html += '<span class="nodeAttrLabel">' + this._msg("comments.footer.postedOn") + ': ';
         html += Alfresco.util.formatDate(data.createdOn);
         html += '</span></span></div>';
         
         return html;
      },
      
      /** Called when the mouse enters into a list item. */
      onCommentElementMouseEntered: function CommentList_onListElementMouseEntered(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'overNode');
         var editBloc = YAHOO.util.Dom.getElementsByClassName( 'nodeEdit' , null , elem, null );
         YAHOO.util.Dom.addClass(editBloc, 'showEditBloc');
      },
      
      /** Called whenever the mouse exits a list item. */
      onCommentElementMouseExited: function CommentList_onListElementMouseExited(layer, args)
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
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function CommentList_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.CommentList", Array.prototype.slice.call(arguments).slice(1));
      }
      
   };
})();
