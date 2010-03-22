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
 * Alfresco webditor plugin
 * @module AWE
 *  
 */
(function() 
{
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       KeyListener = YAHOO.util.KeyListener,
       Selector = YAHOO.util.Selector,
       Bubbling = YAHOO.Bubbling,
       Cookie = YAHOO.util.Cookie,
       WebEditor = YAHOO.org.springframework.extensions.webeditor;

   YAHOO.namespace('org.alfresco.awe.app');

   /**
    * Alfresco webeditor plugin constructor
    * @constructor
    * @class AWE.app
    * @namespace YAHOO.org.alfresco
    * @extends WEF.App
    */
   YAHOO.org.alfresco.awe.app = function()
   {
      YAHOO.org.alfresco.awe.app.superclass.constructor.apply(this, Array.prototype.slice.call(arguments));
   };

   YAHOO.extend(YAHOO.org.alfresco.awe.app, WEF.App,
   {
      init: function AWE_App_init()
      {
         YAHOO.org.alfresco.awe.app.superclass.init.apply(this);

         // handle events
         // edit content icon event
         Bubbling.on(YAHOO.org.alfresco.awe.app.AWE_EDIT_CONTENT_CLICK_EVENT, function onEditContent_click(e, args) 
         {
            this.loadForm(args[1]);
         }, this);

         // login/logoff
         Bubbling.on('awe'+WEF.SEPARATOR+'loggedIn', this.onLoggedIn, this, true);
         Bubbling.on('awe'+WEF.SEPARATOR+'loggedout', this.onLoggedOut, this, true);

         Bubbling.on(this.config.name + WebEditor.SEPARATOR + 'quickeditClick', this.onQuickEditClick, this, true);
         Bubbling.on(this.config.name + WebEditor.SEPARATOR + 'show-hide-edit-markersClick', this.onShowHideClick, this, true);

         Bubbling.on(this.config.name + WebEditor.SEPARATOR + 'loggedoutClick', this.onLogoutClick, this, true);

         this.initAttributes(this.config);
         this.registerEditableContent(this.config.editables);
         return this;
      },

      initAttributes : function AWE_App_initAttributes(attr)
      {
         this.setAttributeConfig('editables',
         {
            value: attr.editables,
            validator: YAHOO.lang.isArray 
         });

         this.setAttributeConfig('loggedInStatus',
         {
            value: (WEF.getCookieValue(this.config.name, 'loggedInStatus') == 'true'),
            validator: YAHOO.lang.isBoolean
         });

         this.on('loggedInStatusChange', this.onLoginStatusChangeEvent);
      },

      render: function AWE_render()
      {
         // innerHTML causes issues with rendering so use DOM
         var wefEl = Dom.get('wef'),
             div = document.createElement('div');

         div.id = 'wef-login-panel';
         wefEl.appendChild(div);

         div = document.createElement('div');
         div.id = 'wef-panel';
         wefEl.appendChild(div);

         // get the current context path
         var contextPath = WEF.get("contextPath");
         
         var tb  = WebEditor.module.Ribbon.addToolbar('WEF-'+WebEditor.ui.Ribbon.PRIMARY_TOOLBAR+'-root',
         {
            id: 'WEF-'+WebEditor.ui.Ribbon.PRIMARY_TOOLBAR+'-root',
            name: 'WEF-'+WebEditor.ui.Ribbon.PRIMARY_TOOLBAR+'-root',
            label: '<img src="' + contextPath + '/res/awe/images/edit.png" alt="'+ this.getMessage('awe.toolbar-tab-label') +'" />',
            title: this.getMessage('awe.toolbar-tab-label'),
            content: '',
            active: true,
            pluginOwner:this
         }, WebEditor.ui.Toolbar);

         tb.addButtons(
         [
            {
               type: 'menu',
               label: '<img src="' + contextPath + '/res/awe/images/quick-edit.png" alt="'+ this.getMessage('awe.toolbar-quick-edit-icon-label') +'" />',
               title: this.getMessage('awe.toolbar-quick-edit-icon-label'),
               value: this.config.name + WebEditor.SEPARATOR + 'quickedit',
               id: this.config.name + WebEditor.SEPARATOR + 'quickedit',
               icon: true,
               menu: function renderEditableContentMenu(markers)
               {
                  var menuConfig = [];
                  for (var p in markers) 
                  {
                     menuConfig.push(
                     {
                        text: markers[p].title,
                        value: markers[p]
                     });
                  }
                  return menuConfig;
               }(this.config.editables)
            },
            {
               type: 'push',
               label: '<img src="' + contextPath + '/res/awe/images/toggle-edit-off.png" alt="'+ this.getMessage('awe.toolbar-toggle-markers-icon-label') +'" />',
               title: this.getMessage('awe.toolbar-toggle-markers-icon-label'),
               value: this.config.name + WebEditor.SEPARATOR + 'show-hide-edit-markers',
               id: this.config.name + WebEditor.SEPARATOR + 'show-hide-edit-markers',
               icon: true
            }
         ]);
         tb.getButtonById(this.config.name + WebEditor.SEPARATOR + 'quickedit').getMenu().subscribe('mouseover', this.onQuickEditMouseOver, this, true);
         tb = WebEditor.module.Ribbon.getToolbar(WebEditor.ui.Ribbon.SECONDARY_TOOLBAR);
         tb.addButtons(
         [ 
            {
               type: 'push',
               label: this.getMessage('awe.toolbar-logout-label'),
               title: this.getMessage('awe.toolbar-logout-label'),
               value: 'loggedout',
               id: this.config.name + WebEditor.SEPARATOR + 'loggedout',
               icon: true,
               disabled: true
            }
         ]);

         this.refresh(['loggedInStatus']);
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
      login: function AWE_login(o)
      {
         if (YAHOO.lang.isUndefined(this.widgets.loginModule))
         {
            this.widgets.loginModule = new YAHOO.org.alfresco.awe.ui.LoginPanel('wef-login-panel').setOptions(
            {
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
       *                   e.g 
       *                   {
       *                      id: 'elementId' // Id of content element,
       *                      nodeRef: '..'   // NodeRef of content
       *                   }
       */
      loadForm : function AWE_loadForm(o)
      {
         // formId is optional so use appropriate substitute string
         var formUri = null;
         if (o.formId)
         {
            formUri = YAHOO.lang.substitute(WEF.get("contextPath") + '/service/components/form?itemKind=node&itemId={nodeRef}&formId={formId}&nodeRef={nodeRef}&redirect={redirectUrl}',o);
         }
         else
         {
            formUri = YAHOO.lang.substitute(WEF.get("contextPath") + '/service/components/form?itemKind=node&itemId={nodeRef}&nodeRef={nodeRef}&redirect={redirectUrl}',o);
         }

         this.module.getFormPanelInstance('wef-panel').setOptions(
         {
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
                  elem: elem,
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
                  Bubbling.fire(YAHOO.org.alfresco.awe.app.AWE_EDIT_CONTENT_CLICK_EVENT, o);
               },
               editables[config.id].config);
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
      onLoggedIn: function AWE_onLoggedIn(e, args)
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
      onLoggedOut: function AWE_onLoggedOut(e, args)
      {
         this.set('loggedInStatus', false);
      },

      /*
       * Handler for when login status changes. Enables/disables the logout btn.
       *
       * @param e {Object} Object literal describing previous and new value of
       *                   attribute
       */
      onLoginStatusChangeEvent: function AWE_onLoginStatusChangeEvent(e)
      {
         var btn = WebEditor.module.Ribbon.getToolbar(WebEditor.ui.Ribbon.SECONDARY_TOOLBAR).getButtonById(this.config.name + WebEditor.SEPARATOR + 'loggedout');
         if (e.newValue === true) 
         {
            btn.set('disabled', false);
         }
         else 
         {
            btn.set('disabled', true);
         }

         if (e.prevValue !== e.newValue)
         {
            WEF.setCookieValue(this.config.name,'loggedInStatus', e.newValue);
         }
      },

      onQuickEditClick: function AWE_onQuickEditClick(e, args)
      {
         this.loadForm(args[1]);
      },

      onShowHideClick: function AWE_onShowHideClick(e, args)
      {
         var editMarkers = Selector.query('span.alfresco-content-marker'),
             butImg = Dom.get(args[1]+'-button').getElementsByTagName('img')[0];
         
         this.onShowHideClick.isHidden = this.onShowHideClick.isHidden || false;
         
         if (this.onShowHideClick.isHidden) 
         {
            Dom.setStyle(editMarkers, 'display', '');
            this.onShowHideClick.isHidden = false;
            butImg.src = butImg.src.replace('-on.png','-off.png');
         }
         else
         {
            Dom.setStyle(editMarkers, 'display', 'none');
            this.onShowHideClick.isHidden = true;
            butImg.src = butImg.src.replace('-off.png','-on.png');           
         }
      },

      onLogoutClick: function AWE_onLogoutClick(e, args)
      {
         var ribbonObj = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message('message.logout-confirmation-title','org.alfresco.awe.ui.LoginPanel'),
            text: Alfresco.util.message('message.logout-confirmation','org.alfresco.awe.ui.LoginPanel'),
            buttons: 
            [
               {
                  text: 'OK',
                  handler: function()
                  {
                     var config = 
                     {
                        url: WEF.get("contextPath") + '/page/dologout',
                        method: "GET",
                        successCallback: 
                        {
                           fn: function logoutSuccess(e)
                           {
                              ribbonObj.onLoggedOut.call(ribbonObj, e, [null, {loggedIn: false}]);
                              this.hide();
                              this.destroy();
                           },
                           scope: this
                        },
                        failureCallback: 
                        {
                           fn: function logoutFailure(e)
                           {
                              this.hide();
                              this.destroy();
                           },
                           scope: this
                        }
                     };
                     Alfresco.util.Ajax.request(config);
                  }
               },
               {
                  text: 'Cancel',
                  handler: function()
                  {
                     this.hide();
                     this.destroy();
                  }
               }
            ]
         },
         Dom.get('wef'));
      },
      
      onHelp: function AWE_onHelp()
      {
         window.open('http://www.alfresco.com/help/32/labs/sharehelp/','wefhelp');
      },

      onQuickEditMouseOver: function AWE_onQuickEditMouseOver(e, args)
      {
         if (args.length>0)
         {
            var targetContentEl = (args[1].value.nested) ? Dom.get(args[1].value.id).parentNode : Dom.get(args[1].value.id),
                targetContentElRegion = Dom.getRegion(targetContentEl), 
                fadeIn = function fade(el)
                {
                   var anim = new YAHOO.util.ColorAnim(el,
                   {
                      backgroundColor: 
                      {
                         from: '#ffffff',
                         to: '#FFFF99',
                         duration: '0.5'
                      }
                   });
                   anim.onComplete.subscribe(function(el)
                   {
                      return function()
                      {
                         fadeOut(el);
                      };
                   }(el));
                   anim.animate();
                },
                fadeOut = function fade(el)
                {
                   var anim = new YAHOO.util.ColorAnim(el, 
                   {
                      backgroundColor: 
                      {
                         from: '#FFFF99',
                         to: '#ffffff',
                         duration: '0.5'
                      }
                   });
                   anim.animate();
                };

            // if not visible in viewport
            if (!(targetContentElRegion.intersect(Dom.getClientRegion())))
            {
               if (this.scrollAnimation)
               {
                  this.scrollAnimation.stop();
               }

               //set up animation
               this.scrollAnimation = new YAHOO.util.Scroll( (YAHOO.env.ua.webkit) ? document.body : document.documentElement, 
               {
                  scroll: 
                  {
                     to: [0, Math.max(0, targetContentElRegion.top - 125)]
                  }
               }, 1, YAHOO.util.Easing.easeOut);

               this.scrollAnimation.onComplete.subscribe(function(el)
               {
                  return function()
                  {
                     fadeIn(el);
                  };
               }(targetContentEl));

               this.scrollAnimation.animate();
            }
            else
            {
               fadeIn(targetContentEl);
            }
         }
      }, 
      module: 
      {
         getFormPanelInstance : function(id)
         {
            return Alfresco.util.ComponentManager.get(id) || new YAHOO.org.alfresco.awe.ui.FormPanel(id);
         }
      },
      component: {}
   });

   YAHOO.org.alfresco.awe.app.AWE_EDIT_CONTENT_CLICK_EVENT = 'AWE_EditContent_click';
})();

WEF.register("org.alfresco.awe", YAHOO.org.alfresco.awe.app, {version: "1.0.1", build: "1"});