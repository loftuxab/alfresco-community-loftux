(function() {
    
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener,
      Selector = YAHOO.util.Selector,
      Bubbling = YAHOO.Bubbling,
      Cookie = YAHOO.util.Cookie;
    
    YAHOO.namespace('com.alfresco.awe.app');

    YAHOO.com.alfresco.awe.app = function()
    {
      //config.setUpCustomEvents = (['render','show','hide']).concat(config.setUpCustomEvents || []);
      YAHOO.com.alfresco.awe.app.superclass.constructor.apply(this, Array.prototype.slice.call(arguments));
    };
    
    YAHOO.extend(YAHOO.com.alfresco.awe.app, WEF.App,
    {
       init: function AWE_App_init()
       {
         YAHOO.com.alfresco.awe.app.superclass.init.apply(this);
         // init core awe divs
         /*var body = document.getElementsByTagName('body')[0];
         if (!Dom.get('wef'))
         {
            var el = document.createElement('div');
            el.innerHTML =  '<div id="wef" class="wef"><div id="wef-login-panel"></div><div id="wef-panel"></div><div id="wef-ribbon-container" class="wef-ribbon-container"><div id="wef-ribbon" class="wef-ribbon wef-hide" role="toolbar"><div class="hd"><h6>Web Editor</h6></div><div class="bd"></div><div class="ft"></div></div></div></div>';
            body.appendChild(el.firstChild);
         }
         if (!Dom.hasClass(body, 'wef-root-body'))
         {
            Dom.addClass(body, 'wef-root-body');
         }
         if (body.className.indexOf('yui-skin')==-1)
         {
            Dom.addClass(body, 'yui-skin-default');
         }
*/
         //handle events
         Bubbling.on(YAHOO.com.alfresco.awe.app.AWE_EDIT_CONTENT_CLICK_EVENT, function onEditContent_click(e, args) {
            this.loadForm(args[1]);
         }, this);
         
         Bubbling.on('awe-loggedIn', this.onLoggedIn, this, true);
         Bubbling.on('awe-loggedOut', this.onLoggedOut, this, true);

         Bubbling.on('WEF'+WEF.SEPARATOR+'afterRender', this.render);
         this.initAttributes(this.config);
         this.registerEditableContent(this.config.editables);
         
         
         return this;
       },
      
       initAttributes : function AWE_App_initAttributes(attr)
       {
          this.setAttributeConfig('editables', {
            value: attr.editables,
            validator: YAHOO.lang.isArray 
         });
         
         this.setAttributeConfig('loggedInStatus', {
            value: (WEF.getCookieValue(this.config.name, 'loggedInStatus') == 'true'),
            validator: YAHOO.lang.isBoolean
         });
         
         this.on('loggedInStatusChange', this.onLoginStatusChangeEvent);
         
       },
       
       render: function AWE_render()
       {
         Dom.get('wef').innerHTML += '<div id="wef-login-panel"></div><div id="wef-panel"></div>';
       },
      /**
       * Initaliases login module
       *
       * @method login
       *
       * @param o {Object} Callback object to pass to login module
       * @return {Object} Login module object
       *
       */
      login : function AWE_login(o)
      {
         if (YAHOO.lang.isUndefined(this.widgets.loginModule))
         {
            this.widgets.loginModule = new YAHOO.com.alfresco.awe.ui.LoginPanel('wef-login-panel').setOptions({
               templateUrl : Alfresco.constants.URL_SERVICECONTEXT + "modules/login/login",
               destroyPanelOnHide: false
            });
         }
         this.widgets.loginModule.show(o);
      },
      
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
      loadForm : function AWE_loadForm(o)
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
         this.module.getFormPanelInstance('wef-panel').setOptions({
            formName: 'wefPanel',
            formId: o.formId,
            formUri: formUri,
            nodeRef: o.nodeRef,
            domContentId: o.id,
            title: o.title,
            nested: o.nested,
            redirectUrl: o.redirectUrl
         }).show();
      },
      
      /**
       * Registers editable content on page. Adds click events to load form.
       *
       * @method registerEditableContent
       *
       */
      registerEditableContent : function AWE_registerEditableContent(configs)
      {
         var editables = {};
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
                     Bubbling.fire(YAHOO.com.alfresco.awe.app.AWE_EDIT_CONTENT_CLICK_EVENT, o);
                  },
                  editables[config.id].config
               );
            }
         }
         this.set('editables', editables);
      },
      
      getEditableContentMarkers :  function AWE_getEditableContentMarkers()
      {
         return this.get('editables');
      },
      
      /**
       * Event handler that fires when user logs in
       *
       * @param e {Object} Object literal describing previous and new value of
       *                   attribute
       * @param args {Object} Args passed into event
       *
       */
      onLoggedIn: function WEF_UI_Ribbon_onLoggedIn(e, args)
      {
         this.set('loggedInStatus', args[1].loggedIn);
      },
      
      /**
       * Event handler that fires when user logs out
       *
       * @param e {Object} Object literal describing previous and new value of
       *                   attribute
       * @param args {Object} Args passed into event
       *
       */
      onLoggedOut: function WEF_UI_Ribbon_onLoggedOut(e, args)
      {
         this.set('loggedInStatus', args[1].loggedIn);
      },
      
      /*
       * Handler for when login status changes. Enables/disables the logout btn.
       *
       * @param e {Object} Object literal describing previous and new value of
       *                   attribute
       */
      onLoginStatusChangeEvent: function WEF_UI_Ribbon_onLoginStatusChangeEvent(e)
      {
         if (e.newValue === true) {
            this.widgets.toolbars[YAHOO.org.wef.ui.Ribbon.SECONDARY_TOOLBAR].enableButton(this.config.name + YAHOO.org.wef.SEPARATOR + 'logout');
         }
         else {
            this.widgets.toolbars[YAHOO.org.wef.ui.Ribbon.SECONDARY_TOOLBAR].disableButton(this.config.name + YAHOO.org.wef.SEPARATOR + 'logout');
         }
         if (e.prevValue !== e.newValue) {
            WEF.setCookieValue(this.config.name,'loggedInStatus', e.newValue);
         }         
      },
    
      module: {
         getFormPanelInstance : function(id)
         {
            return Alfresco.util.ComponentManager.get(id) || new YAHOO.com.alfresco.awe.ui.FormPanel(id);
         }
      },
      component: {}
               
    });
    YAHOO.com.alfresco.awe.app.AWE_EDIT_CONTENT_CLICK_EVENT = 'AWE_EditContent_click';
})();

WEF.register("com.alfresco.awe", YAHOO.com.alfresco.awe.app, {version: "1.0.1", build: "1"});