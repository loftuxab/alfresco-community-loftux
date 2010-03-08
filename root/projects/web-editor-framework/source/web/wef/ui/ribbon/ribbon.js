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
         this.widgets.toolbars = [];
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
            this.resizeRibbon();
         }, this, true);
      },
      
      /**
       * Renders ribbon
       *
       */
      renderRibbon: function WEF_UI_Ribbon_renderRibbon()
      {
         if (!Dom.get(this.config.id)) {
            Dom.get('wef').innerHTML+='<div id="wef-ribbon-container" class="wef-ribbon-container"><div id="wef-ribbon" class="wef-ribbon wef-hide" role="toolbar"><div class="hd"><h6>Web Editor</h6></div><div class="bd"><div id="wef-toolbar-container"></div></div><div class="ft"><div id="wef-toolbar-secondary-container"></div></div></div></div>';
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
         //set correct width  
         this.resizeRibbon()
         
         //get ribbon position from cookie if available otherwise reset to initial config value
         this.set('position', YAHOO.org.wef.getCookieValue(this.config.name,'ribbon-position') || this.get('position'));
         ribbon.render();
         var name = 'WEF-'+YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR+'Toolbar';
         this.addToolbar(
            YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR,
            {
               id:  YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR,
               name: name,
               element: 'wef-toolbar-container'
            },
            YAHOO.org.wef.ui.TabbedToolbar
         );
         name = 'WEF-Ribbon'+YAHOO.org.wef.ui.Ribbon.SECONDARY_TOOLBAR+'Toolbar';
         this.addToolbar(
            YAHOO.org.wef.ui.Ribbon.SECONDARY_TOOLBAR,
            {
               id:  YAHOO.org.wef.ui.Ribbon.SECONDARY_TOOLBAR,
               name: name,
               element: 'wef-toolbar-secondary-container',
               buttons:
               {
                  buttons: 
                  [
                     {
                        type: 'menu',
                        label: 'Orientation',
                        value: name+ YAHOO.org.wef.SEPARATOR + 'ribbon-placement',
                        id: this.config.name + YAHOO.org.wef.SEPARATOR + 'ribbon-placement',
                        icon: true,
                        menu: 
                        [
                           {
                              text: 'top',
                              value: YAHOO.org.wef.ui.Ribbon.POSITION_TOP
                           }, 
                           {
                              text: 'left',
                              value: YAHOO.org.wef.ui.Ribbon.POSITION_LEFT
                           }, 
                           {
                              text: 'right',
                              value: YAHOO.org.wef.ui.Ribbon.POSITION_RIGHT
                           }
                        ]
                     },
                     {
                        type: 'push',
                        label: 'Help',
                        value: 'http://www.alfresco.com/help/32/labs/sharehelp/',
                        id: this.config.name + YAHOO.org.wef.SEPARATOR + 'help',
                        icon: true
                     } 
                  ]
               }
            },
            YAHOO.org.wef.ui.Toolbar);
         // Refresh any attributes here
         Bubbling.on(this.config.name + YAHOO.org.wef.SEPARATOR + 'ribbon-placementClick', this.onRibbonPlacementClick, this, true);
         Bubbling.on(this.config.name + YAHOO.org.wef.SEPARATOR + 'helpClick', this.onHelpClick, this, true);
      },
      
      resizeRibbon: function WEF_UI_Ribbon_resizeRibbon()
      {
        var newWidth = Dom.getRegion(this.widgets.ribbonContainer).width-parseInt(Dom.getStyle(this.widgets.ribbonHeader,'width'),10);
        if (!YAHOO.env.ua.ie)
        {
           newWidth+=2;
        }
        
        this.widgets.ribbonBody.setStyle('width', newWidth+'px'); 
      },
      
      
      getToolbar: function WEF_UI_Ribbon_getToolbar(toolbarId)
      {
         if (toolbarId === YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR | toolbarId === YAHOO.org.wef.ui.Ribbon.SECONDARY_TOOLBAR)
         {
            return this.widgets.toolbars[toolbarId];            
         }
         else 
         {
            return this.widgets.toolbars[YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR].getToolbar(toolbarId);
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
      addToolbar: function WEF_UI_Ribbon_addToolbar(id, config, toolbarType)
      {
         var tbar = null;  
         
         if (!toolbarType)
         {
            throw new Error('Unable to add toolbar of specified type')
         }
         //add primary/secondary toolbars
         if (id === YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR | id === YAHOO.org.wef.ui.Ribbon.SECONDARY_TOOLBAR)
         {
            if(!this.widgets.toolbars[id])
            {
               tbar = new toolbarType(config);      
            }   
         }
         else //add toolbars as tabs of tabbed toolbars.
         {
            tbar = this.widgets.toolbars[YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR].addToolbar(id, config);            
         }
         this.widgets.toolbars.push(tbar)
         this.widgets.toolbars[id] = tbar;
         tbar.init();   
         tbar.render();

         return tbar;
      },
      
      /**
       * Add button to specified button group and specified toolbar
       *
       * @param id {String} The name of the toolbar to add the button to.
       * @param buttonConfig {Object} An object literal for the button config
       *
       * @return {Boolean} Success/failure of addition
       */
      addButtons: function WEF_UI_Ribbon_addButton(id, buttonConfig)
      {

        if (!this.widgets.toolbars[id])
        {
           throw new Error('Toolbar ' + id + ' not found');
           return false;   
        }
        else
        {
           return this.widgets.toolbars[id].addButtons(buttonConfig);
        }
      },
      
      onRibbonPlacementClick: function WEF_UI_Ribbon_onRibbonPlacementClick(e, args)
      {
         this.set('position', args[1]);
      },
      
      
      onHelpClick: function WEF_UI_Ribbon_onHelpClick(e, args)
      {
        window.location=args[1]
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
         
         if (e.newValue === YAHOO.org.wef.ui.Ribbon.POSITION_TOP && !this._originalBodyMarginTop) {
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
            if (e.newValue !== YAHOO.org.wef.ui.Ribbon.POSITION_TOP) {
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