/*
 *** Alfresco.WikiToolbar
*/
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   Alfresco.WikiToolbar = function(containerId)
   {
      this.name = "Alfresco.WikiToolbar";
      this.id = containerId;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.componentsLoaded, this);

      return this;
   };

   Alfresco.WikiToolbar.prototype =
   {

      /**
       * Object container for initialization options
       *
       * @property options
       * @type {object} object literal
       */
      options:
      {
         /**
        * Sets the current site for this component.
        *
        * @property siteId
        * @type string
        */
         siteId: null,

         /**
           * The title of the current page.
           *
           * @property title
           * @type string
           */
         title: null,

         /**
          * Indicating if back link is used
          *
          * @property showBackLink
          * @type {string}
          */
         showBackLink: false         
      },


      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setOptions: function DV_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocListTree} returns 'this' for method chaining
       */
      setMessages: function(obj)
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
      componentsLoaded: function()
      {
         Event.onContentReady(this.id, this.init, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method init
       */
      init: function()
      {
         // Create button
         var createButton = Alfresco.util.createYUIButton(this, "create-button", this.onNewPageClick);

         // Delete button
         Alfresco.util.createYUIButton(this, "delete-button", this.onDeleteClick);
         Alfresco.util.createYUIButton(this, "rename-button", this.onRenameClick);
         Alfresco.util.createYUIButton(this, "rssFeed-button", null,
         {
            type: "link"
         });
         
         // Labels
         var confirmText = Alfresco.util.message("panel.confirm.delete-msg", this.name);
         
         this.deleteDialog = new YAHOO.widget.SimpleDialog("deleteDialog", 
         {
            width: "20em",
            fixedcenter: true,
            visible: false,
            draggable: false,
            modal: true,
            close: true,
            text: '<div class="yui-u"><br />' + confirmText + '<br /><br /></div>',
            constraintoviewport: true,
            buttons: [
            {
               text: Alfresco.util.message("button.delete", this.name),
               handler:
               {
                  fn: this.onConfirm,
                  scope: this
               }
            },
            {
               text: Alfresco.util.message("button.cancel", this.name),
               handler:
               {
                  fn: this.onCancel,
                  scope: this
               },
               isDefault: true
            }]
         });
         
         var headerText = Alfresco.util.message("panel.confirm.header", this.name);
         this.deleteDialog.setHeader(headerText);
         this.deleteDialog.render(document.body);
         
         // Create the rename panel
         var renamePanel = Dom.get(this.id + "-renamepanel");
         var clonedRenamePanel = renamePanel.cloneNode(true);
         renamePanel.parentNode.removeChild(renamePanel);
         
         this.renamePanel = new YAHOO.widget.Panel(clonedRenamePanel,
         {
            width: "320px",
            visible: false,
            draggable: false,
            constraintoviewport: true,
            fixedcenter: true,
            modal: true
         });
         this.renamePanel.render(document.body);
         
         var renameSaveButton = Alfresco.util.createYUIButton(this, "rename-save-button", null,
         {
            type: "submit"
         });
         
         var renameForm = new Alfresco.forms.Form(this.id + "-renamePageForm");
         renameForm.addValidation(this.id + "-renameTo", Alfresco.forms.validation.mandatory, null, "blur");
         renameForm.addValidation(this.id + "-renameTo", Alfresco.forms.validation.nodeName, null, "keyup");
         renameForm.setShowSubmitStateDynamically(true);
         renameForm.setSubmitElements(renameSaveButton);
         renameForm.ajaxSubmitMethod = Alfresco.util.Ajax.POST;
         renameForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onPageRenamed,
               scope: this
            },
            failureMessage: "Page rename failed"
         });        
         renameForm.setSubmitAsJSON(true);
         renameForm.applyTabFix();
         renameForm.init();
         
         // Listen for when an event has been updated
         YAHOO.Bubbling.on("deletePage", this.onDeletePage, this);
      },


      /**
       * Dispatches the browser to the create wiki page
       *
       * @method onNewPageClick
       * @param e {object} DomEvent
       */
      onNewPageClick: function (e)
      {
         var url = Alfresco.constants.URL_CONTEXT + "page/site/" + this.options.siteId + "/wiki-create";
         if(!this.options.showBackLink)
         {
            url += "?listViewLinkBack=true";
         }
         window.location.href = url;
      },

      /**
       * Kicks off a page delete confirmation dialog.
       * Fired when a delete link is clicked - 
       * primarily the "delete" link on the listing page.
       *
       * @method onDeletePage
       * @param e {object} DomEvent
       */      
      onDeletePage: function(e, args)
      {
         var title = args[1].title;
         if (title)
         {
            this.options.title = title;
            this.deleteDialog.show();
         }
      },
      
      /**
       * Fired when the user confirms that they want to delete a page. 
       * Kicks off a DELETE request to the Alfresco repo to remove an event.
       *
       * @method onConfirm
       * @param e {object} DomEvent
       */
      onConfirm: function(e)
      {
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.DELETE,
            url: Alfresco.constants.PROXY_URI + "slingshot/wiki/page/" + this.options.siteId + "/" + this.options.title + "?page=wiki",
            successCallback:
            {
               fn: this.onPageDeleted,
               scope: this
            },
            failureMessage: Alfresco.util.message("load.fail", this.name)
         });
      },
      
      /**
       * Fired when the user decides not to delete a page.
       * Hides the confirmation dialog.
       *
       * @method onCancel
       * @param e {object} DomEvent
       */
      onCancel: function(e)
      {
         this.deleteDialog.hide();
      },
      
      /**
       * Callback handler then gets invoked when a page is 
       * successfully deleted.
       * 
       * @method onPageDeleted
       * @param e {object} DomEvent
       */
      onPageDeleted: function(e)
      {
         this.deleteDialog.hide();
         // Redirect to the wiki landing page
         window.location =  Alfresco.constants.URL_CONTEXT + "page/site/" + this.options.siteId + "/wiki";
      },
      
      /**
       * Event handler for the rename button in the toolbar.
       * Pops up the rename dialog.
       *
       * @method onRenameClick
       * @param e {object} DomEvent
       */
      onRenameClick: function(e)
      {
         this.renamePanel.show();

         // Clear the text field any previously entered values
         var newNameField = document.getElementById(this.id + "-renameTo");
         newNameField.value = "";
         
         // Fix Firefox caret issue
         var formElement = Dom.get(this.id + "-renamePageForm");
         Alfresco.util.caretFix(formElement);

         // Register the ESC key to close the dialog
         if (!this.escapeListener)
         {
            this.escapeListener = new YAHOO.util.KeyListener(document,
            {
               keys: YAHOO.util.KeyListener.KEY.ESCAPE
            },
            {
               fn: function(id, keyEvent)
               {
                  // Undo Firefox caret issue
                  Alfresco.util.undoCaretFix(formElement);
                  this.renamePanel.hide();
               },
               scope: this,
               correctScope: true
            });
         }
         this.escapeListener.enable();

         // Set focus to fileName input
         newNameField.focus();
      },

      /**
       * Event handler for save button on the page rename panel.
       * Submits the (new) name of the page to the repo.
       *
       * @method onRenameSaveClick
       * @param e {object} DomEvent
       */      
      onRenameSaveClick: function(e)
      {
         var data = {};
      
         var newNameField = document.getElementById(this.id + "-renameTo");
         if (newNameField)
         {
            data["name"] = newNameField.value.replace(/\s+/g, "_");
         }
         
         // Submit PUT request 
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.POST,
            url: Alfresco.constants.PROXY_URI + "/slingshot/wiki/page/" + this.options.siteId + "/" + encodeURIComponent(this.options.title),
            requestContentType: Alfresco.util.Ajax.JSON,
            dataObj: data,
            successCallback:
            {
               fn: this.onPageRenamed,
               scope: this
            },
            failureMessage: "Page update failed"
         });

         // Undo Firefox caret issue
         var formElement = Dom.get(this.id + "-renamePageForm");
         Alfresco.util.undoCaretFix(formElement);
         
         if (this.escapeListener)
         {
            this.escapeListener.disable();
         }

         this.renamePanel.hide();
      },
      
      /**
       * Gets called when a page is successfully renamed.
       * Sets the window location to the URL of the new page.
       *
       * @method onPageRenamed
       * @param e {object} DomEvent
       */      
      onPageRenamed: function(e)
      {
         var response = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
         if (response)
         {
            if (!YAHOO.lang.isUndefined(response.name))
            {
               // Change the location bar
               window.location = Alfresco.constants.URL_CONTEXT + "page/site/" + this.options.siteId + "/wiki-page?title=" + encodeURIComponent(response.name);
            } 
            else
            {
               // A problem occurred
               var errorMsg = "Rename failed: ";
               if (!YAHOO.lang.isUndefined(response.error))
               {
                  errorMsg += response.error;
               }
               else
               {
                  errorMsg += "Unknown error occurred."
               }
               
               Alfresco.util.PopupManager.displayPrompt(
               {
                  text: errorMsg
               });
            }
           
         }
      },
      
      /**
       * Event handler for the delete button in the toolbar.
       * Pops up the delete confirmation dialog.
       *
       * @method onDeleteClick
       * @param e {object} DomEvent
       */
      onDeleteClick: function(e)
      {
         this.deleteDialog.show();
      },


      /**
       * Action handler for the configure blog button
       */
      onConfigureBlog: function BlogPostList_onConfigureBlog(e, p_obj)
      {
         // load the module if not yet done
         if (!this.modules.configblog)
         {
            this.modules.configblog = new Alfresco.module.ConfigBlog(this.id + "-configblog");
         }

         this.modules.configblog.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId
         });

         this.modules.configblog.showDialog();

         Event.preventDefault(e);
      }      

   };

})();   
