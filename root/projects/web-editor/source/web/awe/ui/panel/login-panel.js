(function() 
{   
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener,
      Selector = YAHOO.util.Selector,
      Bubbling = YAHOO.Bubbling;   

   YAHOO.namespace('org.alfresco.awe.ui.LoginPanel');

   /**
    * Login module constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.CreateSite} The new DocumentList instance
    * @constructor
    */
   YAHOO.org.alfresco.awe.ui.LoginPanel = function(containerId)
   {
      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null)
      {
         throw new Error("An instance of Alfresco.module.Login already exists.");
      }
   
      YAHOO.org.alfresco.awe.ui.LoginPanel.superclass.constructor.call(this, 'com.alfresco.awe.ui.LoginPanel', containerId, ["button", "container", "connection", "selector", "json"]);
      return this;
   };
   
   YAHOO.extend(YAHOO.org.alfresco.awe.ui.LoginPanel, YAHOO.org.alfresco.awe.ui.Panel,
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
         Event.on(formEl, 'submit', function login_module_submit(e)
         {
            Event.preventDefault(e);
            var config = {
               url: formEl.action,
               method: "POST",
               dataForm:'wef-login-panel-form',
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
})();

WEF.register("org.alfresco.awe.ui.login-panel", YAHOO.org.alfresco.awe.ui.LoginPanel, {version: "1.0", build: "1"});