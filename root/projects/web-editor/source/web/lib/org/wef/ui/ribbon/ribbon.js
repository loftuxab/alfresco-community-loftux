(function() {
    
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener,
      Selector = YAHOO.util.Selector,
      Element = YAHOO.util.Element,
      Bubbling = YAHOO.Bubbling,
      Cookie = YAHOO.util.Cookie;
    
   YAHOO.namespace('org.wef.ui.ribbon');

   YAHOO.org.wef.ui.Ribbon = function(config)
   {
      //config.setUpCustomEvents = (['render','show','hide']).concat(config.setUpCustomEvents || []);
     YAHOO.org.wef.ui.Ribbon.superclass.constructor.apply(this, Array.prototype.slice.call(arguments));
   };
       
   YAHOO.extend(YAHOO.org.wef.ui.Ribbon, WEF.Widget,
   {
      init: function WEF_UI_Ribbon_init()
      {
         YAHOO.org.wef.ui.Ribbon.superclass.init.apply(this);
         
         //render ribbon after WEF is rendered
         Bubbling.on('WEF'+WEF.SEPARATOR+'afterRender', this.render);
         
         this.initServices();
         this.initAttributes(this.config);
      },
      initServices: function WEF_UI_Ribbon_initServices()
      {
         if (!this.services.prefs) {
            //initialise services
            this.services.prefs = new Alfresco.service.Preferences();
         }
      },
      initAttributes: function WEF_UI_Ribbon_init_attributes(attr)
      {
         
         this.setAttributeConfig('position', {
            value: YAHOO.org.wef.ui.Ribbon.POSITION_TOP,
            validator: function WEF_UI_Ribbon_position_attribute_validator(value)
            {
               return ('top,left,right'.indexOf(value) != -1);
            }
         });
         
         this.on('positionChange', this.onPositionChangeEvent);
      },
      
      /**
       * Renders ribbon and its plugins
       *
       */
      render: function WEF_UI_Ribbon_render()
      {
         
         this.renderRibbon();
         this.widgets.ribbon.show();
         Event.addListener(window, "resize", function WEF_UI_Ribbon_onResize()
         {
            this.set('position', this.get('position'));
         }, this, true);
      },
      
      /**
       * Renders ribbon
       *
       */
      renderRibbon: function WEF_UI_Ribbon_renderRibbon()
      {
         if (!Dom.get(this.config.id)) {
            Dom.get('wef').innerHTML+='<div id="wef-ribbon-container" class="wef-ribbon-container"><div id="wef-ribbon" class="wef-ribbon wef-hide" role="toolbar"><div class="hd"><h6>Web Editor</h6></div><div class="bd"></div><div class="ft"></div></div></div>';
         }
         var panelConfig = {
            visible: false,
            draggable: false,
            underlay: 'none',
            close: false
         };
         // only make it center for ie browsers that don't support fixed
         // positioning. Note: center() method of panel needs to be
         // overridden for ribbon instance (see below)
         if (YAHOO.env.ua.ie && YAHOO.env.ua.ie < 7) {
            panelConfig.fixedCenter = true;
         }
         
         var ribbon = this.widgets.ribbon = new YAHOO.widget.Panel(this.config.id, panelConfig);
         // override panel.center() so that ribbon is redrawn on page scroll
         // at top of viewport.
         if (YAHOO.env.ua.ie && YAHOO.env.ua.ie < 7) {
            this.widgets.ribbon.center = function ribbon_center()
            {
               var nViewportOffset = YAHOO.widget.Overlay.VIEWPORT_OFFSET, elementHeight = this.element.offsetHeight, viewPortHeight = Dom.getViewportHeight(), x, y;
               
               y = Dom.getDocumentScrollTop();
               this.cfg.setProperty("y", parseInt(y, 10));
               this.cfg.refireEvent("iframe");
            };
         }
         var header = this.widgets.ribbonHeader = new Element(ribbon.header);
         var body = this.widgets.ribbonBody = new Element(ribbon.body);
         var footer = this.widgets.ribbonFooter = new Element(ribbon.footer);
         var container = this.widgets.ribbonContainer = new Element(ribbon.element.parentNode);
         container.addClass('wef-ribbon-orientation-' + this.get('position'));
         Dom.addClass([ribbon.header, ribbon.body, ribbon.footer], 'wef-ribbon-module');

         //get ribbon position from cookie if available otherwise reset to initial config value
         this.set('position', WEF.getCookieValue(this.config.name,'ribbon-position') || this.get('position'));
         ribbon.render();
         // Refresh any attributes here
         this.refresh(['loggedInStatus']);
         Bubbling.on(this.config.name + YAHOO.org.wef.SEPARATOR + 'quickeditClick', this.onQuickEditClick, this, true);
         Bubbling.on(this.config.name + YAHOO.org.wef.SEPARATOR + 'show-hide-edit-markersClick', this.onShowHideClick, this, true);
         Bubbling.on(this.config.name + YAHOO.org.wef.SEPARATOR + 'ribbon-placementClick', this.onRibbonPlacementClick, this, true);
         Bubbling.on(this.config.name + YAHOO.org.wef.SEPARATOR + 'helpClick', this.onHelpClick, this, true);
         Bubbling.on(this.config.name + YAHOO.org.wef.SEPARATOR + 'logoutClick', this.onLogoutClick, this, true);
                  
      },
      
      onQuickEditMouseOver: function WEF_UI_Ribbon_onQuickEditMouseOver(e, args)
      {
         var targetContentEl = (args[1].value.nested) ? Dom.get(args[1].value.id).parentNode : Dom.get(args[1].value.id);
         var targetContentElRegion = Dom.getRegion(targetContentEl), fadeIn = function fade(el)
         {
            var anim = new YAHOO.util.ColorAnim(el, {
               backgroundColor: {
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
         }, fadeOut = function fade(el)
         {
            var anim = new YAHOO.util.ColorAnim(el, {
               backgroundColor: {
                  from: '#FFFF99',
                  to: '#ffffff',
                  duration: '0.5'
               }
            });
            anim.animate();
         };
         //if not visible in viewport
         if (!(targetContentElRegion.intersect(Dom.getClientRegion()))) {
            if (this.scrollAnimation) {
               this.scrollAnimation.stop();
            }
            //set up animation
            this.scrollAnimation = new YAHOO.util.Scroll((YAHOO.env.ua.gecko) ? document.documentElement : document.body, {
               scroll: {
                  to: [0, Math.max(0, targetContentElRegion.top - 50)]
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
      addToolbar: function WEF_UI_Ribbon_addToolbar(config, location)
      {
         var location = location || YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR, tbar = null, toolbarContainer = (location && location === YAHOO.org.wef.ui.Ribbon.SECONDARY_TOOLBAR) ? this.widgets.ribbonFooter : this.widgets.ribbonBody;
         //add primary toolbar
         if (this.widgets.toolbars[location]) // destroy existing toolbar first
         {
            this.widgets.toolbars[location].destroy();
         }
         tbar = this.widgets.toolbars[location] = new YAHOO.widget.Toolbar(toolbarContainer.appendChild(document.createElement('div')), config);
         
         tbar.on('buttonClick', function WEF_UI_Ribbon_generic_button_handler(args)
         {
            //strip out button id (namespace) from value
            if (YAHOO.lang.isString(args.button.value)) {
               args.button.value = args.button.value.split(YAHOO.org.wef.SEPARATOR).pop();
            }
            Bubbling.fire(args.button.id + args.type.replace('button', ''), args);
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
         if (location===YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR)
         {
            //add mouseove behaviour
            console.log('TODO: move mouseover handlre somewehre else')
            this.widgets.toolbars[YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR].getButtonById(this.config.name + YAHOO.org.wef.SEPARATOR + 'quickedit').getMenu().subscribe('mouseover', this.onQuickEditMouseOver, this, true);            
         }
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
      addButton: function WEF_UI_Ribbon_addButton(buttonConfig, buttonGroup, location)
      {
         var location = location || org.wef.ui.Ribbon.PRIMARY_TOOLBAR;
         if (!this.widgets.toolbars[location]) {
            return false;
         }
         // add 'orphaned' button
         if (!buttonGroup) {
            this.widgets.toolbars[location].addButton(buttonConfig);
         }
         // add button to group
         else {
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
      addButtons: function WEF_UI_Ribbon_addButtons(buttonConfigs, buttonGroup, location)
      {
         for (var i = 0, len = i; i < len; i++) {
            this.addButton(buttonConfigs[i], buttonGroup, location);
         }
      },
      
      onQuickEditClick: function WEF_UI_Ribbon_onQuickEditClick(e, args)
      {
         org.wef.ui.loadForm(args[1].button.value);
      },
      
      onShowHideClick: function WEF_UI_Ribbon_onShowHideClick(e, args)
      {
         var editMarkers = Selector.query('span.alfresco-content-marker');
         this.onShowHideClick.isHidden = this.onShowHideClick.isHidden || false;
         if (this.onShowHideClick.isHidden) {
            Dom.setStyle(editMarkers, 'display', '');
            this.onShowHideClick.isHidden = false;
         }
         else {
            Dom.setStyle(editMarkers, 'display', 'none');
            this.onShowHideClick.isHidden = true;
         }
      },
      
      onRibbonPlacementClick: function WEF_UI_Ribbon_onRibbonPlacementClick(e, args)
      {
         this.set('position', args[1].button.value);
      },
      
      
      onHelpClick: function WEF_UI_Ribbon_onHelpClick(e, args)
      {
         alert('help');
      },
      
      onLogoutClick: function WEF_UI_Ribbon_onLogoutClick(e, args)
      {
         var ribbonObj = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: 'Logout?',
            text: 'Are you sure you want to log out?',
            buttons: [{
               text: 'OK',
               handler: function()
               {
                  var config = {
                     url: '/awe/page/dologout',
                     method: "GET",
                     successCallback: {
                        fn: function logoutSuccess(e)
                        {
                           ribbonObj.onLoggedOut.call(ribbonObj, e, [null, {
                              loggedIn: false
                           }]);
                           this.hide();
                           this.destroy();
                        },
                        scope: this
                     },
                     failureCallback: {
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
            }, {
               text: 'Cancel',
               handler: function()
               {
                  this.hide();
                  this.destroy();
               }
            }]
         });
      //show logout dialog
      },
    
      /*
    * Change handler for position attribute. Moves the ribbon around
    * the screen adjusting the margin certain elements appropiately
    *
    * @param e {Object} Object literal describing previous and new value of
    *                   attribute
    */
      onPositionChangeEvent: function WEF_UI_Ribbon_onPositionChangeEvent(e)
      {
         var container = this.widgets.ribbonContainer, bodyEl = document.getElementsByTagName('body')[0];
         
         //if position has changed, change class
         if (e.prevValue !== e.newValue) {
            container.removeClass(('wef-ribbon-orientation-' + e.prevValue));
            container.addClass('wef-ribbon-orientation-' + e.newValue);
         }
         
         if (e.newValue === org.wef.ui.Ribbon.POSITION_TOP && !this._originalBodyMarginTop) {
            //reset body height
            this.widgets.ribbonBody.setStyle('height', 'inherit');
            
            // save original margin as any position changes to ribbon *might*
            // change the margins value
            this._originalBodyMarginTop = Dom.getStyle(bodyEl, 'margin-top');
            
            // offset body element by height of ribbon if position is at top.
            Dom.setStyle(bodyEl, 'margin-top', parseInt(Dom.getStyle(bodyEl, 'margin-top'), 10) +
            (parseInt(Dom.getStyle(this.config.id, 'height'), 10)) * 1.5 +
            'px');
            
            // reset any padding (left or right) on body
            this._originalBodyMarginLeft = this._originalBodyMarginRight = null;
            Dom.setStyle(bodyEl, 'margin-left', this._originalBodyMarginLeft);
            Dom.setStyle(bodyEl, 'margin-right', this._originalBodyMarginRight);
            this.widgets.ribbonContainer.setStyle('margin-right', 0);
            this.widgets.ribbonContainer.setStyle('margin-left', 0);
         }
         else 
            if (e.newValue !== org.wef.ui.Ribbon.POSITION_TOP) {
               //reset body margin-top
               Dom.setStyle(bodyEl, 'margin-top', this._originalBodyMarginTop);
               this._originalBodyMarginTop = null;
               
               //resize toolbar to viewport height minus header and footer heights
               this.widgets.ribbonBody.setStyle('height', (Dom.getViewportHeight() -
               (this.widgets.ribbonHeader.get('offsetHeight') +
               this.widgets.ribbonFooter.get('offsetHeight')) -
               11) +
               'px');
               
               // offset body element by width of ribbon if position is not at top.
               if (e.newValue === org.wef.ui.Ribbon.POSITION_RIGHT && !this._originalBodyMarginRight) {
                  //save original margin right
                  this._originalBodyMarginRight = Dom.getStyle(bodyEl, 'margin-right');
                  Dom.setStyle(bodyEl, 'margin-right', parseInt(Dom.getStyle(bodyEl, 'margin-right'), 10) +
                  parseInt(Dom.getStyle(this.config.id, 'width'), 10) +
                  'px');
                  // set negative margin
                  this.widgets.ribbonContainer.setStyle('margin-right', 1 - parseInt(Dom.getStyle(this.widgets.ribbonContainer.getElementsByTagName('div')[0], 'width'), 10) + 'px');
                  //reset
                  this.widgets.ribbonContainer.setStyle('margin-left', 0);
                  Dom.setStyle(bodyEl, 'margin-left', this._originalBodyMarginLeft);
                  this._originalBodyMarginLeft = null;
               }
               if (e.newValue === org.wef.ui.Ribbon.POSITION_LEFT && !this._originalBodyMarginLeft) {
                  //save original margin left
                  this._originalBodyMarginLeft = Dom.getStyle(bodyEl, 'margin-left');
                  
                  Dom.setStyle(bodyEl, 'margin-left', parseInt(Dom.getStyle(bodyEl, 'margin-left'), 10) +
                  parseInt(Dom.getStyle(this.config.id, 'width'), 10) +
                  'px');
                  
                  // set negative margin
                  this.widgets.ribbonContainer.setStyle('margin-left', 1 - parseInt(Dom.getStyle(this.widgets.ribbonContainer.getElementsByTagName('div')[0], 'width'), 10) + 'px');
                  //reset
                  this.widgets.ribbonContainer.setStyle('margin-right', 0);
                  Dom.setStyle(bodyEl, 'margin-right', this._originalBodyMarginRight);
                  this._originalBodyMarginRight = null;
               }
            }
         WEF.setCookieValue(this.config.name,'ribbon-position', e.newValue);
      }
   });
   
   YAHOO.org.wef.ui.Ribbon.POSITION_LEFT = 'left';
   YAHOO.org.wef.ui.Ribbon.POSITION_RIGHT = 'right';
   YAHOO.org.wef.ui.Ribbon.POSITION_TOP = 'top';
   YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR = 'primary';
   YAHOO.org.wef.ui.Ribbon.SECONDARY_TOOLBAR = 'secondary';
})();

WEF.register("org.wef.ui.ribbon", YAHOO.org.wef.ui.Ribbon, {version: "1.0", build: "1"});