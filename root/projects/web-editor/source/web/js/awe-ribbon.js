/**
 *
 *
 * @module AWE.Ribbon
 * @constructor
 *
 * @param htmlid {String} Id of element to use as ribbon. Defaults to
 *                        awe-ribbon if not specified.
 */
(function () {
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener,
      Selector = YAHOO.util.Selector,
      Element = YAHOO.util.Element,
      Bubbling = YAHOO.Bubbling,
      Cookie = YAHOO.util.Cookie;

   AWE.Ribbon = function AWE_Ribbon(htmlid, name, attr)
   {
      this.id = htmlid || 'awe-ribbon';
      this.name = (name || 'AWE.Ribbon')
      this._domFriendlyName = this.name.replace(/\./g,'-');
      this.services = {};
      this.config = {};
      this.widgets = {
         toolbars : {}
      };
   }

   YAHOO.extend(AWE.Ribbon, Element, {

      /**
       * Ribbon init function. Sets up any required services or events.
       *
       *
       **/
      init : function AWE_Ribbon(attr)
      {
         //loadPreferences()
         AWE.Ribbon.superclass.init.call(this,Dom.get(this.id), attr);
         this.initServices();
         
         // initialise events
//         Bubbling.on(AWE.Ribbon.AWE_PLUGIN_COMPONENTSLOADED_EVENT, function AWE_Ribbon_Plugins_componentsloaded_event() {
//            console.log(arguments.callee.name, arguments);
//         }, this);
      },

      initServices : function AWE_Ribbon_init_services() {
         if (!this.services.prefs)
         {
            //initialise services
            this.services.prefs = new Alfresco.service.Preferences();
         }
      },

      initAttributes : function AWE_Ribbon_init_attributes(attr)
      {
         AWE.Ribbon.superclass.initAttributes.call(this, attr);

         this.setAttributeConfig('position', {
            value:AWE.Ribbon.POSITION_TOP,
            validator: function AWE_Ribbon_position_attribute_validator(value)
            {
               return ('top,left,right'.indexOf(value)!=-1);
            }
         });

         this.setAttributeConfig('mode', {
            value:'single',
            validator: function AWE_Ribbon_mode_attribute_validator(value)
            {
               return ('single,multi'.indexOf(value)!=-1);
            }
         });

         this.setAttributeConfig('loggedInStatus', {
            value:(Cookie.get('awe-loginStatus')=='true'),
            validator: YAHOO.lang.isBoolean
         })
         this.on('positionChange', this.onPositionChangeEvent);
         this.on('modePositionChange', this.onModeChangeEvent);
         this.on('loggedInStatusChange', this.onLoginStatusChangeEvent);
      },

      /**
       * Renders ribbon and its plugins
       *
       */
      render : function AWE_Ribbon_render()
      {
         this.renderRibbon();
//         renderPlugins();
         this.widgets.ribbon.show();
      },

      /**
       * Renders ribbon
       *
       */
      renderRibbon : function AWE_Ribbon_renderRibbon()
      {
         if (!Dom.get(this.id))
         {
            console.log('render ribbon');
         }
         else
         {
            var panelConfig = {
               visible: false,
               draggable: false,
               underlay:'none',
               close:false
            };
            // only make it center for ie browsers that don't support fixed
            // positioning. Note: center() method of panel needs to be
            // overridden for ribbon instance (see below)
            if (YAHOO.env.ua.ie && YAHOO.env.ua.ie<7)
            {
               panelConfig.fixedCenter = true;
            }

            var ribbon = this.widgets.ribbon = new YAHOO.widget.Panel(this.id, panelConfig);

            // override panel.center() so that ribbon is redrawn on page scroll
            // at top of viewport.
            if (YAHOO.env.ua.ie && YAHOO.env.ua.ie<7)
            {
               this.widgets.ribbon.center = function ribbon_center()
               {
                   var nViewportOffset = YAHOO.widget.Overlay.VIEWPORT_OFFSET,
                      elementHeight = this.element.offsetHeight,
                      viewPortHeight = Dom.getViewportHeight(),
                      x,
                      y;

                     y=Dom.getDocumentScrollTop();
                     this.cfg.setProperty("y", parseInt(y, 10));
                     this.cfg.refireEvent("iframe");
                  };
            }
            var header = this.widgets.ribbonHeader = new Element(ribbon.header);
            var body = this.widgets.ribbonBody = new Element(ribbon.body);
            var footer = this.widgets.ribbonFooter = new Element(ribbon.footer);
            var container = this.widgets.ribbonContainer = new Element(ribbon.element.parentNode);
            container.addClass('awe-ribbon-orientation-' + this.get('position'));
            Dom.addClass([ribbon.header,ribbon.body,ribbon.footer],'awe-ribbon-module');

            if (this.get('mode')=='single')
            {
               this.addToolbar({
                 buttonType: 'advanced',
                 buttons: [
                     { group: 'editcontent',
                         buttons: [
                             { type: 'menu', label: 'Quick Edit', value: this._domFriendlyName + AWE.SEPARATOR + 'quickedit', id: this._domFriendlyName + AWE.SEPARATOR + 'quickedit',
                                 menu: function renderEditableContentMenu(markers)
                                 {
                                    var menuConfig = [];
                                    for (var p in markers)
                                    {
                                       menuConfig.push(
                                       {
                                          text: Selector.query('img', markers[p].elem, true).alt,
                                          value: markers[p].config
                                       });
                                    }
                                    return menuConfig;
                                 }(AWE.getEditableContentMarkers())
                             },
                             { type: 'push', label: 'Show/Hide edit markers', value: this._domFriendlyName + AWE.SEPARATOR + 'show-hide-edit-markers', id:  this._domFriendlyName + AWE.SEPARATOR + 'show-hide-edit-markers' },
                         ]
                     }
                 ]
               },
               AWE.Ribbon.PRIMARY_TOOLBAR); // add to main body
               this.addToolbar({
                 buttonType: 'advanced',
                 buttons: [
                     { group: 'textstyle',
                         buttons: [
                             { type: 'menu', label: 'Ribbon placement', value: this._domFriendlyName + AWE.SEPARATOR +  'ribbon-placement', id: 'ribbon-placement',
                                 menu: [
                                  { text: 'top', value: AWE.Ribbon.POSITION_TOP},
                                  { text: 'left', value: AWE.Ribbon.POSITION_LEFT},
                                  { text: 'right', value: AWE.Ribbon.POSITION_RIGHT},
                                 ]
                             },
                             { type: 'push', label: 'Help', value: this._domFriendlyName  + AWE.SEPARATOR + 'help', id: this._domFriendlyName + AWE.SEPARATOR + 'help'},
                             { type: 'push', label: 'Logout', value: this._domFriendlyName  + AWE.SEPARATOR + 'logout', id: this._domFriendlyName + AWE.SEPARATOR + 'logout', disabled:true}
                         ]
                     }
                 ]
               },
               AWE.Ribbon.SECONDARY_TOOLBAR); // add to footer.
            }
            else
            {
               console.log('create tabview as container for tabbed toolbars');
            }

            //handler for show/hide edit markers
            this.widgets.toolbars[AWE.Ribbon.PRIMARY_TOOLBAR].on(this._domFriendlyName + AWE.SEPARATOR +  'quickeditClick', function quickEdit(e)
            {
               AWE.loadForm(e.button.value);
            });
            this.widgets.toolbars[AWE.Ribbon.PRIMARY_TOOLBAR].on(this._domFriendlyName + AWE.SEPARATOR +  'show-hide-edit-markersClick', function showhidemarkers(e)
            {
               var editMarkers = Selector.query('span.awe-edit');
               if (Dom.hasClass(editMarkers[0], 'awe-edit-hidden'))
               {
                  Dom.setStyle(editMarkers, 'display', 'block');
               }
               else
               {
                  Dom.setStyle(editMarkers, 'display', 'none');
               }

            });

            
            ribbon.render();

            this.refresh(['position','loggedInStatus']);
            YAHOO.Bubbling.on(this._domFriendlyName + AWE.SEPARATOR + 'ribbon-placementClick', this.onRibbonPlacementClick, this, true);
            YAHOO.Bubbling.on(this._domFriendlyName + AWE.SEPARATOR + 'helpClick', this.onHelpClick, this, true);
            YAHOO.Bubbling.on(this._domFriendlyName + AWE.SEPARATOR + 'logoutClick', this.onLogoutClick, this, true);
            YAHOO.Bubbling.on('awe-loggedIn', this.onLoggedIn, this, true);
            YAHOO.Bubbling.on('awe-loggedOut', this.onLoggedOut, this, true);
//            YAHOO.Bubbling.on(this._domFriendlyName + '::ribbon-placementClick', this.onHelp, this, true);
         }
      },
      
      /**
       * Adds toolbar to ribbon.
       *
       * @param config {Object} Configuration for object
       * @param location {String} Name of location. if specified is name of
       * tab if ribbon is in multi mode. If not specified then a single toolbar
       * is added.
       *
       * @return {YAHOO.widget.Toolbar} Toolbar reference
       *
       */
      addToolbar: function AWE_Ribbon_addToolbar(config, location)
      {         
         var location = location || AWE.Ribbon.PRIMARY_TOOLBAR,
             tbar = null,
             toolbarContainer = (location && location === AWE.Ribbon.SECONDARY_TOOLBAR) ? this.widgets.ribbonFooter : this.widgets.ribbonBody;

         //add primary toolbar
         if (this.widgets.toolbars[location]) // destroy existing toolbar first
         {
            this.widgets.toolbars[location].destroy();
         }
         tbar = this.widgets.toolbars[location] = new YAHOO.widget.Toolbar(
            toolbarContainer.appendChild(
               document.createElement('div')
            ),
            config
         );
         
         for (var i=0, len = config.buttons.length;i<len;i++)
         {
            var group = config.buttons[i];
            for (var j=0, groupButtonsLen = group.buttons.length;j<groupButtonsLen;j++)
            {
               var button = group.buttons[j];
               var eventName  = this._domFriendlyName + AWE.SEPARATOR + button.value.split(AWE.SEPARATOR).pop() + 'Click';
               this.widgets.toolbars[location].on(eventName, function buildEventHandler(evtName)
               {
                  return function(e)
                  {
                     //strip namespace from button value
                     //if required to set state of button etc get button via id
                     // so id must be specified in button toolbar config
                     e.button.value = e.button.value.split(AWE.SEPARATOR).pop();
                     Bubbling.fire(evtName, e);
                  }
               }(eventName));
            }
         }
//       The following code is useful for those buttons that require the button
//       to stay in a selected state. TODO Move this logic out to doAfterPluginActivate ?
//         tbar.on('buttonClick', function AWE_Ribbon_generic_button_handler(o)
//         {
//            console.log(arguments.callee.name, arguments);
//            //this should really fire valueClickEvent;
//            var value = o.button.value;
//
//            if (this.lastButton && this.lastButton===o.button)
//            {
//               tbar.deselectButton(o.button.id);
//               this.lastButton = null;
//            }
//            else
//            {
//               tbar.deselectAllButtons();
//               tbar.selectButton(value);
//               this.lastButton = o.button;
//            }
//         });
         return tbar;
      },

      /**
       * Add button to specified button group and specified toolbar
       *
       * @param buttonConfig {Object} An object literal for the button config
       * @param buttonGroup {String} An name of the button group to add the button
       *                             to.
       * @param location {String} The name of the toolbar to add the button to.
       *
       * @return {Boolean} Success/failure of addition
       */
      addButton: function AWE_Ribbon_addButton(buttonConfig, buttonGroup, location)
      {
        var location = location || AWE.Ribbon.PRIMARY_TOOLBAR;
        if (!this.widgets.toolbars[location])
        {
           return false;
        }
        // add 'orphaned' button
        if (!buttonGroup)
        {
          this.widgets.toolbars[location].addButton(buttonConfig);
        }
        // add button to group
        else
        {
         this.widgets.toolbars[location].addButtonToGroup(buttonConfig, buttonGroup);
        }
        return true;
      },

      /**
       * Add buttons to specified button group and specified toolbar
       *
       * @param buttonConfig {Array} An array  of object literal for the button
       *                             config
       * @param buttonGroup {String} An name of the button group to add the button
       *                             to.
       * @param location {String} The name of the toolbar to add the button to.
       *
       * @return {Boolean} Success/failure of addition
       */
      addButtons: function AWE_Ribbon_addButtons(buttonConfigs, buttonGroup, location)
      {
        for (var i = 0, len = i; i < len; i++)
        {
           this.addButton(buttonConfigs[i], buttonGroup, location);
        }
      },

      onRibbonPlacementClick: function AWE_Ribbon_onRibbonPlacementClick(e, args)
      {
         this.set('position', args[1].button.value);
      },


      onHelpClick: function AWE_Ribbon_onHelpClick(e, args)
      {
         alert('help');
      },

      onLogoutClick: function AWE_Ribbon_onLogoutClick(e, args)
      {
         var ribbonObj = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title:'Logout?',
            text: 'Are you sure you want to log out?',
            buttons: [
               {
                  text:'OK',
                  handler:function() {
                     var config = {
                        url: '/awe/page/dologout',
                        method: "GET",
                        successCallback:
                        {
                           fn: function logoutSuccess(e)
                           {
                              ribbonObj.onLoggedOut.call(ribbonObj, e, [null,{loggedIn:false}]);
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
                  text:'Cancel',
                  handler: function(){
                     this.hide();
                     this.destroy();
                  }}
            ]
         });
        //show logout dialog
      },

      onLoggedIn: function AWE_Ribbon_onLoggedIn(e, args)
      {
         Cookie.set('awe-loginStatus', args[1].loggedIn);
         this.set('loggedInStatus', args[1].loggedIn);
      },

      onLoggedOut: function AWE_Ribbon_onLoggedOut(e, args)
      {
         Cookie.set('awe-loginStatus', args[1].loggedIn);
         this.set('loggedInStatus', args[1].loggedIn);
      },

      /*
       * Handler for when login status changes. Enables/disables the logout btn.
       *
       * @param e {Object} Object literal describing previous and new value of
       *                   attribute
       */
      onLoginStatusChangeEvent: function AWE_Ribbon_onLoginStatusChangeEvent(e)
      {
         if (e.newValue===true)
         {
            this.widgets.toolbars[AWE.Ribbon.SECONDARY_TOOLBAR].enableButton(this._domFriendlyName + AWE.SEPARATOR + 'logout');
         }
         else
         {
            this.widgets.toolbars[AWE.Ribbon.SECONDARY_TOOLBAR].disableButton(this._domFriendlyName + AWE.SEPARATOR + 'logout');
         }
      },

      /*
       * Change handler for position attribute. Moves the ribbon around
       * the screen adjusting the margin certain elements appropiately
       *
       * @param e {Object} Object literal describing previous and new value of
       *                   attribute
       */
      onPositionChangeEvent: function AWE_Ribbon_onPositionChangeEvent(e)
      {
         var container = this.widgets.ribbonContainer,
             bodyEl = document.getElementsByTagName('body')[0];

         //if position has changed, change class
         if (e.prevValue!==e.newValue)
         {
            container.removeClass(('awe-ribbon-orientation-'+e.prevValue))
            container.addClass('awe-ribbon-orientation-'+e.newValue)
         }
         
         if (e.newValue === AWE.Ribbon.POSITION_TOP && !this._originalBodyMarginTop)
         {
            //reset body height
            this.widgets.ribbonBody.setStyle('height', 'inherit')

            // save original margin as any position changes to ribbon *might*
            // change the margins value
            this._originalBodyMarginTop = Dom.getStyle(bodyEl, 'margin-top');

            // offset body element by height of ribbon if position is at top.
            Dom.setStyle(
               bodyEl,
               'margin-top',
               parseInt(Dom.getStyle(bodyEl,'margin-top')) +
                  parseInt(Dom.getStyle(this.id,'height'))+'px'
            );

            // reset any padding (left or right) on body
            this._originalBodyMarginLeft = this._originalBodyMarginRight = null;
            Dom.setStyle(bodyEl, 'margin-left', this._originalBodyMarginLeft)
            Dom.setStyle(bodyEl, 'margin-right', this._originalBodyMarginRight);
            this.widgets.ribbonContainer.setStyle('margin-right', 0);
            this.widgets.ribbonContainer.setStyle('margin-left', 0);
         }
         else if (e.newValue !== AWE.Ribbon.POSITION_TOP)
         {
            //reset body margin-top
            Dom.setStyle(bodyEl, 'margin-top', this._originalBodyMarginTop);
            this._originalBodyMarginTop = null;

            //resize toolbar to viewport height minus header and footer heights
            this.widgets.ribbonBody.setStyle('height',
               ( Dom.getViewportHeight() - (
                  this.widgets.ribbonHeader.get('offsetHeight') +
                     this.widgets.ribbonFooter.get('offsetHeight')
                 ) - 11
               )+'px'
            );

            // offset body element by width of ribbon if position is not at top.
            if (e.newValue === AWE.Ribbon.POSITION_RIGHT && !this._originalBodyMarginRight)
            {
               //save original margin right
               this._originalBodyMarginRight = Dom.getStyle(bodyEl, 'margin-right');
               Dom.setStyle(
                  bodyEl,
                  'margin-right',
                  parseInt(Dom.getStyle(bodyEl,'margin-right')) +
                     parseInt(Dom.getStyle(this.id,'width'))+'px'
               );
               // set negative margin
               this.widgets.ribbonContainer.setStyle('margin-right', 1- parseInt(Dom.getStyle(this.widgets.ribbonContainer.getElementsByTagName('div')[0], 'width')) + 'px');
               //reset
               this.widgets.ribbonContainer.setStyle('margin-left', 0);
               Dom.setStyle(
                  bodyEl,
                  'margin-left',
                  this._originalBodyMarginLeft
               );
               this._originalBodyMarginLeft = null;
            }
            if (e.newValue === AWE.Ribbon.POSITION_LEFT && !this._originalBodyMarginLeft)
            {
               //save original margin left
               this._originalBodyMarginLeft = Dom.getStyle(bodyEl, 'margin-left');

               Dom.setStyle(
                  bodyEl,
                  'margin-left',
                  parseInt(Dom.getStyle(bodyEl,'margin-left')) +
                     parseInt(Dom.getStyle(this.id,'width'))+'px'
               );

               // set negative margin
               this.widgets.ribbonContainer.setStyle('margin-left', 1- parseInt(Dom.getStyle(this.widgets.ribbonContainer.getElementsByTagName('div')[0], 'width')) + 'px');
               //reset
               this.widgets.ribbonContainer.setStyle('margin-right', 0);
               Dom.setStyle(
                  bodyEl,
                  'margin-right',
                  this._originalBodyMarginRight
               );
               this._originalBodyMarginRight = null;
            }

         }
      },

      /**
       * Change handler for mode attribute. This attribute specifies whether the
       * ribbon uses a single toolbar or a tabbed toolbar for its primary toolbar.
       *
       * @param e {Object} Object literal describing previous and new value of
       *                   attribute
       *
       */
      onModeChangeEvent: function AWE_Ribbon_onModeChangeEvent(e)
      {
         console.log(arguments.callee.name, arguments);
      }    
   });

   AWE.SEPARATOR = '--';
   AWE.Ribbon.POSITION_LEFT = 'left';
   AWE.Ribbon.POSITION_RIGHT = 'right';
   AWE.Ribbon.POSITION_TOP = 'top';
   AWE.Ribbon.PRIMARY_TOOLBAR = 'primary';
   AWE.Ribbon.SECONDARY_TOOLBAR = 'secondary';
})();