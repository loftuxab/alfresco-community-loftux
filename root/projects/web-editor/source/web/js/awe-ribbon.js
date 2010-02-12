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
      this.name = (name || 'AWE.Ribbon');
      this._domFriendlyName = this.name.replace(/\./g,'-');
      this.services = {};
      this.config = {};
      this.widgets = {
         toolbars : {}
      };
   };

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
            value:(this.getCookieValue('loggedInStatus')=='true'),
            validator: YAHOO.lang.isBoolean
         });
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
         Event.addListener(window, "resize", function AWE_Ribbon_onResize() {
            this.set('position', this.get('position'));
         }, this, true);
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
                     { group: AWE.Ribbon.PRIMARY_TOOLBAR,
                         buttons: [
                             { type: 'menu', label: 'Quick Edit', value: this._domFriendlyName + AWE.SEPARATOR + 'quickedit', id: this._domFriendlyName + AWE.SEPARATOR + 'quickedit',
                                 menu: function renderEditableContentMenu(markers)
                                 {
                                    var menuConfig = [];
                                    for (var p in markers)
                                    {
                                       menuConfig.push(
                                       {
                                          text: (Selector.query('img', markers[p].elem, true).alt).replace(/</g, '&lt;').replace(/>/g, '&gt;'),
                                          value: markers[p].config
                                       });
                                    }
                                    return menuConfig;
                                 }(AWE.getEditableContentMarkers())
                             },
                             { type: 'push', label: 'Show/Hide edit markers', value: this._domFriendlyName + AWE.SEPARATOR + 'show-hide-edit-markers', id:  this._domFriendlyName + AWE.SEPARATOR + 'show-hide-edit-markers' }
                         ]
                     }
                 ]
               },
               AWE.Ribbon.PRIMARY_TOOLBAR); // add to main body
               this.addToolbar({
                 buttonType: 'advanced',
                 buttons: [
                     { group: AWE.Ribbon.SECONDARY_TOOLBAR,
                         buttons: [
                             { type: 'menu', label: 'Ribbon placement', value: this._domFriendlyName + AWE.SEPARATOR +  'ribbon-placement', id:this._domFriendlyName + AWE.SEPARATOR +  'ribbon-placement',
                                 menu: [
                                  { text: 'top', value: AWE.Ribbon.POSITION_TOP},
                                  { text: 'left', value: AWE.Ribbon.POSITION_LEFT},
                                  { text: 'right', value: AWE.Ribbon.POSITION_RIGHT}
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
            //get ribbon position from cookie if available otherwise reset to initial config value
            this.set('position', this.getCookieValue('ribbon-position') || this.get('position'));
            ribbon.render();

            // Refresh any attributes here
            this.refresh(['loggedInStatus']);

            Bubbling.on(this._domFriendlyName + AWE.SEPARATOR + 'quickeditClick', this.onQuickEditClick, this, true);
            Bubbling.on(this._domFriendlyName + AWE.SEPARATOR + 'show-hide-edit-markersClick', this.onShowHideClick, this, true);   
            Bubbling.on(this._domFriendlyName + AWE.SEPARATOR + 'ribbon-placementClick', this.onRibbonPlacementClick, this, true);
            Bubbling.on(this._domFriendlyName + AWE.SEPARATOR + 'helpClick', this.onHelpClick, this, true);
            Bubbling.on(this._domFriendlyName + AWE.SEPARATOR + 'logoutClick', this.onLogoutClick, this, true);
            Bubbling.on('awe-loggedIn', this.onLoggedIn, this, true);
            Bubbling.on('awe-loggedOut', this.onLoggedOut, this, true);
            //add mouseove behaviour
            this.widgets.toolbars[AWE.Ribbon.PRIMARY_TOOLBAR].getButtonById(this._domFriendlyName+ AWE.SEPARATOR +'quickedit').getMenu().subscribe('mouseover', this.onQuickEditMouseOver, this, true);
         }
      },
      
      onQuickEditMouseOver: function AWE_Ribbon_onQuickEditMouseOver(e, args)
      {
         var targetContentEl = (args[1].value.nested) ? Dom.get(args[1].value.id).parentNode : Dom.get(args[1].value.id);
         var targetContentElRegion = Dom.getRegion(targetContentEl),
             fadeIn = function fade(el){
                var anim = new YAHOO.util.ColorAnim(el, {
                   backgroundColor: {
                       from:'#ffffff',
                       to: '#FFFF99',
                       duration:'0.5'
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
             fadeOut = function fade(el){
                var anim = new YAHOO.util.ColorAnim(el, 
                {
                   backgroundColor: 
                   {
                       from: '#FFFF99',
                       to: '#ffffff',
                       duration:'0.5'                       
                   } 
               });
               anim.animate();
             };
         //if not visible in viewport
         if (!(targetContentElRegion.intersect(Dom.getClientRegion())))
         {
            if (this.scrollAnimation)
            {
               this.scrollAnimation.stop();
            }
            //set up animation
            this.scrollAnimation = new YAHOO.util.Scroll((YAHOO.env.ua.gecko) ? document.documentElement : document.body,
               {
                  scroll:
                  {
                     to: [0, Math.max(0,targetContentElRegion.top-50)]
                  }
               },
               1,
               YAHOO.util.Easing.easeOut
             );
             
             this.scrollAnimation.onComplete.subscribe(function(el){
                return function()
                {
                   fadeIn(el);
                };
             }(targetContentEl));
             this.scrollAnimation.animate();
         }
         else {
            fadeIn(targetContentEl);
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
         
         tbar.on('buttonClick', function AWE_Ribbon_generic_button_handler(args)
         {
           //strip out button id (namespace) from value
           if (YAHOO.lang.isString(args.button.value))
           {
              args.button.value = args.button.value.split(AWE.SEPARATOR).pop();              
           }
           Bubbling.fire(args.button.id + args.type.replace('button',''), args);
         // The following code is useful for those buttons that require the button
         // to stay in a selected state. TODO Move this logic out to doAfterPluginActivate ?           
         // var value = o.button.value;
           // 
           // if (this.lastButton && this.lastButton===o.button)
           // {
           //    tbar.deselectButton(o.button.id);
           //    this.lastButton = null;
           // }
           // else
           // {
           //    tbar.deselectAllButtons();
           //    tbar.selectButton(value);
           //    this.lastButton = o.button;
           // }
         });
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

      onQuickEditClick : function AWE_Ribbon_onQuickEditClick(e, args)
      {
         AWE.loadForm(args[1].button.value);
      },

      onShowHideClick : function AWE_Ribbon_onShowHideClick(e, args)
      {
         var editMarkers = Selector.query('span.alfresco-content-marker');
         this.onShowHideClick.isHidden = this.onShowHideClick.isHidden || false;
         if (this.onShowHideClick.isHidden)
         {
            Dom.setStyle(editMarkers, 'display', '');
            this.onShowHideClick.isHidden = false;
         }
         else
         {
            Dom.setStyle(editMarkers, 'display', 'none');
            this.onShowHideClick.isHidden = true;
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
      
      /**
       * Sets value of specified sub cookie
       * 
       * @param name {String} Name of specified sub cookie to set
       * @param value Value of specified sub-cookie
       * 
       * @return
       */
      setCookieValue: function AWE_Ribbon_setCookieValue(name, value)
      {
        var data = Cookie.getSubs('awe') || {};
        data[name] = value;
        return Cookie.setSubs('awe',data);
      },
      
      /**
       * Returns value of specified sub-cookie
       * 
       * @param name {String} (Optional) Name of specified sub cookie to retrieve. If no name specified then returns full cookie
       * @return Value of specified sub-cookie or full cookie if no name specified.
       */
      getCookieValue: function AWE_Ribbon_saveCookieValue(name)
      {
        return (YAHOO.lang.isUndefined(name)) ? Cookie.getSubs('awe') : Cookie.getSub('awe', name);
      },

      /**
       * Event handler that fires when user logs in
       *  
       * @param e {Object} Object literal describing previous and new value of
       *                   attribute
       * @param args {Object} Args passed into event
       * 
       */
      onLoggedIn: function AWE_Ribbon_onLoggedIn(e, args)
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
      onLoggedOut: function AWE_Ribbon_onLoggedOut(e, args)
      {
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
         if (e.prevValue!==e.newValue)
         {
            this.setCookieValue('loggedInStatus', e.newValue);            
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
            container.removeClass(('awe-ribbon-orientation-'+e.prevValue));
            container.addClass('awe-ribbon-orientation-'+e.newValue);
         }
         
         if (e.newValue === AWE.Ribbon.POSITION_TOP && !this._originalBodyMarginTop)
         {
            //reset body height
            this.widgets.ribbonBody.setStyle('height', 'inherit');

            // save original margin as any position changes to ribbon *might*
            // change the margins value
            this._originalBodyMarginTop = Dom.getStyle(bodyEl, 'margin-top');

            // offset body element by height of ribbon if position is at top.
            Dom.setStyle(
               bodyEl,
               'margin-top',
               parseInt(Dom.getStyle(bodyEl,'margin-top'),10) +
                  (parseInt(Dom.getStyle(this.id,'height'),10))*1.5+'px'
            );

            // reset any padding (left or right) on body
            this._originalBodyMarginLeft = this._originalBodyMarginRight = null;
            Dom.setStyle(bodyEl, 'margin-left', this._originalBodyMarginLeft);
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
                  parseInt(Dom.getStyle(bodyEl,'margin-right'),10) +
                     parseInt(Dom.getStyle(this.id,'width'),10)+'px'
               );
               // set negative margin
               this.widgets.ribbonContainer.setStyle('margin-right', 1- parseInt(Dom.getStyle(this.widgets.ribbonContainer.getElementsByTagName('div')[0], 'width'),10) + 'px');
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
                  parseInt(Dom.getStyle(bodyEl,'margin-left'),10) +
                     parseInt(Dom.getStyle(this.id,'width'),10)+'px'
               );

               // set negative margin
               this.widgets.ribbonContainer.setStyle('margin-left', 1- parseInt(Dom.getStyle(this.widgets.ribbonContainer.getElementsByTagName('div')[0], 'width'),10) + 'px');
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
         this.setCookieValue('ribbon-position', e.newValue);
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