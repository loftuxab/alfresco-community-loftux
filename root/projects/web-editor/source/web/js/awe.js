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
if (!this.AWE) {
    this.AWE = {};
}

(function(){
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener,
      Selector = YAHOO.util.Selector,
      Bubbling = YAHOO.Bubbling,
      Cookie = YAHOO.util.Cookie;


   /**
    * AWE top level object
    * @namespace
    * @constructor AWE
    */
   AWE = (function(){
      var name = 'AWE';
      var loginModule = null;
      var editables = {};
      var loginDomElId = 'awe-login-panel';

      var init = function AWE_init()
      {
         // init core awe divs
         var body = document.getElementsByTagName('body')[0];
         if (!Dom.get('awe'))
         {
            var el = document.createElement('div');
            el.innerHTML =  '<div id="awe" class="awe"><div id="awe-login-panel"></div><div id="awe-panel"></div><div id="awe-ribbon-container" class="awe-ribbon-container"><div id="awe-ribbon" class="awe-ribbon" role="toolbar"><div class="hd"><h6>Web Editor</h6></div><div class="bd"></div><div class="ft"></div></div></div></div>';
            body.appendChild(el.firstChild);
         }
         if (!Dom.hasClass(body, 'awe-root-body'))
         {
            Dom.addClass(body, 'awe-root-body');
         }
         if (body.className.indexOf('yui-skin')==-1)
         {
            Dom.addClass(body, 'yui-skin-default');
         }
         //handle events
         Bubbling.on(AWE.constants.AWE_EDIT_CONTENT_CLICK_EVENT, function onEditContent_click(e, args) {
            AWE.loadForm(args[1]);
         });
         
         return this;
      };


      
      /**
       * Initaliases login module
       *
       * @method login
       *
       * @param o {Object} Callback object to pass to login module
       * @return {Object} Login module object
       *
       */
      var login = function AWE_login(o)
      {
         if (loginModule===null)
         {
            loginModule = new awe_loginModule(loginDomElId).setOptions({
               templateUrl : Alfresco.constants.URL_SERVICECONTEXT + "modules/login/login",
               destroyPanelOnHide: false
            });

         }
         loginModule.show(o);
      };

      /**
       * Loads a form
       *
       * @method loadForm
       * @param o {object} Config object; must have an dom element id and a nodeRef properties
       *                   e.g {
       *                          id: 'elementId' // Id of content element,
       *                          nodeRef: '..'   // NodeRef of content
       *                   }
       */
      var loadForm = function AWE_loadForm(o)
      {
         // formId is optional so use appropriate substitute string
         var formUri = null;
         if (o.formId)
         {
            formUri = YAHOO.lang.substitute('/awe/service/components/form?itemKind=node&itemId={nodeRef}&formId={formId}&nodeRef={nodeRef}&redirect={redirectUrl}',o);
         }
         else
         {
            formUri = YAHOO.lang.substitute('/awe/service/components/form?itemKind=node&itemId={nodeRef}&nodeRef={nodeRef}&redirect={redirectUrl}',o);
         }
         
         AWE.module.getFormPanelInstance('awe-panel').setOptions({
            formName: 'testPanel',
            //currently points to html id of text container instead of form id
            formId: o.formId,
            formUri: formUri,
            nodeRef: o.nodeRef,
            domContentId: o.id,
            title: o.title,
            nested: o.nested,
            redirectUrl: o.redirectUrl
         }).show();
      };

      /**
       * Registers editable content on page. Adds click events to load form.
       *
       * @method registerEditableContent
       *
       * @param configs {Array} An array of config objects
       *
       */
      var registerEditableContent = function AWE_registerEditableContent(configs)
      {
         for (var i=0,len = configs.length; i<len; i++)
         {
            var config = configs[i];
            var id = config.id;
            var elem = Selector.query('a', Dom.get(id), true);
            if (elem)
            {
                editables[config.id] = 
                {
                   elem:elem,
                   config:
                   {
                     id: id,
                     title: config.title,
                     formId: config.formId,
                     redirectUrl: config.redirectUrl,
                     nodeRef: config.nodeRef,
                     nested: config.nested
                  }
                };

                Event.on(elem, 'click', function AWE_EDIT_CONTENT_CLICK_EVENT(e, o)
                  {
                     Event.preventDefault(e);
                     Bubbling.fire(AWE.constants.AWE_EDIT_CONTENT_CLICK_EVENT, o);
                  },
                  editables[config.id].config
               );
            }
         }
      };

      var getEditableContentMarkers =  function AWE_getEditableContentMarkers()
      {
         return editables;
      };

      return {
         init:init,
         login: login,
         registerEditableContent:registerEditableContent,
         getEditableContentMarkers:getEditableContentMarkers,
         loadForm: loadForm,
         module: {},
         component: {},
         constants: {}
      };
   })();

   AWE.constants.AWE_EDIT_CONTENT_CLICK_EVENT = 'AWE_EditContent_click';

   AWE.component.Panel = function AWE_Panel_constructor(name, containerId, components)
   {
      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null)
      {
         throw new Error("An instance of ' + name + ' already exists.");
      }

      AWE.component.Panel.superclass.constructor.call(this, name, containerId, ["button", "container", "connection", "selector", "json"]);
      this.init();
      return this;
   };

   YAHOO.extend(AWE.component.Panel, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       * @default {}
       */
      options:{
         /**
          * Flag denoting whether to destroy panel after panel is hidden,
          * forcing a reload of panel template if shown again
          *
          * @type boolean
          *
          */
         destroyPanelOnHide: true,
         /**
          * Flag denoting whether to grab focus on first input element when
          * panel is shown
          *
          * @type boolean
          */
         focusFirstInputElement: true
      },

      /**
       *
       */
      init: function AWE_Panel_init()
      {

      },
      /**
       * Shows the login dialog to the user.
       *
       * @method show
       */
      show: function AWE_Panel_show(callback)
      {
         //set callback object reference if specified
         //it will be called on success.
         if (callback)
         {
            this.callback = callback;
         }

         if (this.widgets.panel)
         {
            /**
             * The panel gui has been showed before and its gui has already
             * been loaded and created
             */
            this._showPanel();
         }
         else
         {
            /**
             * Load the gui from the server and let the templateLoaded() method
             * handle the rest.
             */
            Alfresco.util.Ajax.request(
            {
               url: this.options.templateUrl,
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               execScripts: true,
               failureMessage: "Could not load create " + this.name + " template"
            });
         }
      },

      /**
       * function AwePanel_hide
       *
       */
      hide: function Awe_Panel_hide()
      {
         this.widgets.panel.hide();
         if (this.options.destroyPanelOnHide)
         {
            this.widgets.panel.destroy();
            this.widgets.panel = null;
         }
      },
      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function Awe_Panel__showPanel()
      {
         // Show the upload panel
         this.widgets.panel.show();
         
         // Firefox insertion caret fix
         Alfresco.util.caretFix(this.id + "-form");

         // Register the ESC key to close the dialog
         var escapeListener = new KeyListener(document,
         {
            keys: KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this.hide();
            },
            scope: this,
            correctScope: true
         });
         escapeListener.enable();
         
         // Set the focus on the first field
         var firstInputEl = Selector.query('input', this.id, true);

         if (firstInputEl)
         {
            firstInputEl.focus();
         }
      }
   });
   /**
    * Login module constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.CreateSite} The new DocumentList instance
    * @constructor
    */
   var awe_loginModule = function(containerId)
   {
      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null)
      {
         throw new Error("An instance of Alfresco.module.Login already exists.");
      }

      awe_loginModule.superclass.constructor.call(this, "Alfresco.module.Login", containerId, ["button", "container", "connection", "selector", "json"]);
      return this;
   };

   YAHOO.extend(awe_loginModule, AWE.component.Panel,
   {
      /**
       * Inits the login dialog
       *
       */
      init: function AWE_login_init(config)
      {
         this.callback = null;
      },

      /**
       * Called when the Login html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a panel and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object
       */
      onTemplateLoaded: function AWE_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         if (this.widgets.panel)
         {
            this.widgets.panel.destroy();
            this.widgets.panel = null;
         }
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var panelDiv = Dom.getFirstChild(containerDiv);

         this.widgets.panel = new YAHOO.widget.Panel(panelDiv, {
            modal: true,
            constraintoviewport: true,
            draggable: false,
            fixedcenter: "contained",
            close: true,
            visible: false
         });
         this.widgets.panel.render(Dom.get(this.id));
         this.widgets.btnLogin = new YAHOO.widget.Button(this.id+'-btn-login');
         // Commented out as a this.configChangedEvenet is null error occurs
         // this.widgets.panel.subscribe('hide', this.hide, this, true)
         var formEl = Dom.get(this.id + "-form");
         //form submit handler
         Event.on(formEl, 'submit', function login_module_submit(e){
            Event.preventDefault(e);
            var config = {
               url: formEl.action,
               method: "POST",
               dataForm:'awe-login-panel-form',
//
//               dataObj:
//               {
//                  username : Dom.getElementsByClassName('awe-login-username', 'input', this.id)[0].value,
//                  password : Dom.getElementsByClassName('awe-login-password', 'input', this.id)[0].value
//               },
               successCallback:
               {
                  fn: this.onLoginSuccess,
                  scope: this
               },
               failureCallback:
               {
                  fn: this.onLoginFailure,
                  scope: this
               }
            };
            Alfresco.util.Ajax.request(config);
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: Alfresco.util.message("message.logging-on", this.name),
               spanClass: "wait",
               displayTime: 0
            });
            this.widgets.panel.hide();
            return false;
         },
         this,
         true);

         // Show the panel
         this._showPanel();
      },

      /**
       * Called when a login attempt is successful
       * Saves the login ticket
       *
       * @method onloginSuccess
       * @param response
       */
      onLoginSuccess: function AWE_onLoginSuccess(response)
      {
         this.widgets.feedbackMessage.destroy();
         if (this.callback!==null)
         {
            this.callback.fn.call(this.callback.scope || window);
         }
         Bubbling.fire('awe-loggedIn',{loggedIn:true});
      },

      /**
       * Called when a login attempt fails
       * Displays login failure message and then login panel. Also resets ticket
       * variable
       * @method onloginFailure
       * @param response
       */
      onLoginFailure: function AWE_onLoginFailure(response)
      {
         this.ticket = null;
         var usernameField = Dom.get(this.id+'-username');
         var that = this;
         this.widgets.feedbackMessage.destroy();
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message("message.login.failure", this.name),
            text: Alfresco.util.message("message.login.failure", this.name),
            buttons: [
            {
               text: Alfresco.util.message("button.ok"),
               handler: function error_onOk()
               {
                  this.destroy();
                  that.widgets.panel.show();
                  usernameField.focus();
                  usernameField.select();
               },
               isDefault: true
            }]
         });

      }
   });

/**
    * FormPanel constructor.
    *
    * @param containerId {string} A unique id for this component
    * @return {AWE.FormPanel} The new DocumentList instance
    * @constructor
    */
   AWE.component.FormPanel = function(containerId)
   {
      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null)
      {
         throw new Error("An instance of AWE.component.FormPanel already exists.");
      }

      AWE.component.FormPanel.superclass.constructor.call(this, "AWE.component.FormPanel", containerId, ["button", "container", "connection", "selector", "json"]);

      return this;
   };

   YAHOO.extend(AWE.component.FormPanel, AWE.component.Panel,
   {
      /**
       * Shows the dialog
       *
       * @method show
       */
      show: function AweFormPanel_show()
      {
         if (this.widgets.panel)
         {
            /**
             * The panel gui has been showed before and its gui has already
             * been loaded and created
             */
            this._showPanel();
         }
         else
         {
            /**
             * Load the gui from the server and let the templateLoaded() method
             * handle the rest.
             */
            if (this.options.formUri)
            {
               Alfresco.util.Ajax.request(
               {
                  url: this.options.formUri,
                  noReloadOnAuthFailure: true,
                  dataObj:
                  {
                     htmlid: this.id+'-'+this.options.formName,
                     showCancelButton:'true'
                  },
                  successCallback:
                  {
                     fn: this.onTemplateLoaded,
                     scope: this
                  },
                  failureCallback:
                  {
                     fn: function(args)
                     {
                        if (args.serverResponse.status == 401)
                        {
                           AWE.login(
                           {
                              fn: function AWE_FormPanel_ReloadAfterLogin()
                              {
                                 AWE.loadForm(
                                 {
                                    id: this.options.domContentId,
                                    formId: this.options.formId,
                                    nodeRef: this.options.nodeRef,
                                    title: this.options.title,
                                    nested: this.options.nested,
                                    redirectUrl: this.options.redirectUrl
                                 });
                              },
                              scope: this
                           });
                        }
                     },
                     scope: this
                  },
                  execScripts: true
               });
            }

         }
      },

//      hide: function AweFormPanel_hidePanel()
//      {
//        this.widgets.panel.hide();
//        this.widgets.panel.destroy();
//        this.widgets.panel = null;
//      },

      /**
       * Called when the FormPanel html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a panel and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object
       */
      onTemplateLoaded: function AweFormPanel_onTemplateLoaded(response)
      {
         if (this.widgets.panel)
         {
            this.widgets.panel.destroy();
            this.widgets.panel = null;
         }
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var panelDiv = Dom.getFirstChildBy(containerDiv, function(el) { return el.nodeName.toLowerCase() == 'div';});
         this.widgets.panel = new YAHOO.widget.Panel(panelDiv, {
            width:'420px',
            modal: true,
            constraintoviewport: true,
            draggable: true,
            fixedcenter: "contained",
            close: true,
            visible: false
         });
         this.widgets.panel.setHeader(this.options.title);
         this.widgets.panel.render(Dom.get(this.id));

         //add hide handler
//         this.widgets.panel.hideEvent.subscribe(this.hide, this, true);

         YAHOO.Bubbling.on('beforeFormRuntimeInit', function(e, args) {
            var form = args[1].runtime;
            var formComponent = args[1].component;
            formComponent.buttons.cancel.subscribe('click', this.onCancelButtonClick, this, true);
            //set up UI update
            form.doBeforeFormSubmit =
            {
               fn: function()
               {
                  this.widgets.panel.hide();
                  this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
                  {
                     text: Alfresco.util.message("message.saving"),
                     spanClass: "wait",
                     displayTime: 0
                  });
               },
               obj:null,
               scope:this
            };
         },
         this);
         // Show the panel
         this._showPanel();
      },

      /**
       * Called when user clicks on the cancel button.
       * Closes the FormPanel panel.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function AweFormPanel_onCancelButtonClick(type, args)
      {
         this.hide();
      },

      onUpdateContentUI: function AweFormPanel_onUpdateContentUI(args)
      {
         var contentElem = Dom.get(this.options.domContentId);
         var container  = document.createElement('div');
         container.innerHTML = args.serverResponse.responseText;
         //remove childnodes of src content sparing edit link
         var contentChildren = contentElem.childNodes;
         for (var i=contentChildren.length-1;i>=0;i--)
         {
            var el = contentChildren[i];
            if (!Selector.test(el, 'span.awe-edit'))
            {
               contentElem.removeChild(el);
            }
         }
         //add to dom
         contentChildren = container.childNodes;
         for (var i=contentChildren.length-1;i>=0;i--)
         {
            contentElem.insertBefore(contentChildren[i], Dom.getFirstChild(contentElem));
         }
      }
   });

   
   AWE.module.getFormPanelInstance = function(id)
   {
      return Alfresco.util.ComponentManager.get(id) || new AWE.component.FormPanel(id);
   };

   
})();

