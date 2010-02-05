/**
 *
 *
 * @module AWE.Toolbar
 * @constructor
 *
 * @param htmlid {String} Id of element to use as toolbar. Defaults to
 *                        awe-toolbar if not specified.
 */
(function () {
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener,
      Selector = YAHOO.util.Selector,
      Bubbling = YAHOO.Bubbling;
   AWE.Toolbar = (function AWE_Toolbar(htmlid)
   {
      var id = htmlid || 'awe-toolbar';
      var name = 'AWE.Toolbar';
      var widgets = {};
      var plugins = {};
      var services = {};
      var pluginOrder = [];

      /**
       * Toolbar init function. Sets up any required services or events.
       *
       *
       **/
      var init = function AWE_Toolbar()
      {
         //loadPreferences()
         if (!services.prefs)
         {
            //initialise services
            services.prefs = new Alfresco.service.Preferences();
         }
         // initialise events
         Bubbling.on(AWE.Toolbar.AWE_PLUGIN_COMPONENTSLOADED_EVENT, function AWE_Toolbar_Plugins_componentsloaded_event() {
         }, this);
      };



      var loadPreferences = function AWE_load_Preferences()
      {
         // attempt to get toolbar config. if failure due to not logged in,
         // then login
         var successCallback = {
            fn: function success_get(e)
            {
                console.log(arguments.callee.name, arguments);
            },
            scope: this
         };

         var failureCallback =
         {
            fn: function failure_get(args)
            {
               if (args.serverResponse.status===401)
               {
                  AWE.login({
                     fn: function() {
                        loadPreferences();
                     },
                     scope:this
                  });
               }
            },
            scope:this
         };

         services.prefs.request('AWE.Toolbar', {
            failureCallback: failureCallback,
            successCallback: successCallback,
            noReloadOnAuthFailure:true
         });
      }

      var setPreferences = function AWE_setPreferences(prefName, value)
      {
         prefName  = (name.charAt(0)=='.') ? name + prefName : name + "." + prefName;
         var setMethod = (value===null) ? 'remove' : 'set';
         services.prefs[setMethod](prefName, value, {
            failureCallback:
            {
               fn: function failure_set(args)
               {
                  if (args.serverResponse.status===401)
                  {
                     AWE.login({
                        fn: function() {
                           console.log('resetting');
                           setPreferences(prefName, value);
                        },
                        scope:this
                     });
                  }
               },
               scope:this
            },
            successCallback:
            {
               fn: function success_set(e)
               {
                   console.log(arguments.callee.name, arguments);
                   loadPreferences()
               },
               scope: this
            },
            noReloadOnAuthFailure:true
         });
      };

      /**
       * Registers plugin with toolbar.
       *
       * @param pluginName {String} or Identifier
       * @param plugin {AWE.Toolbar.Plugin}
       * @param override {Boolean} Overide an existing plugin
       *
       */
      var registerPlugin = function AWE_Toolbar_registerPlugin(pluginName, plugin, override)
      {
         if (!(plugins[pluginName]) | (plugins[pluginName] && override))
         {
            plugins[pluginName] = plugin;
            pluginOrder.push(pluginName);
            return this;
         }
         throw new Error('A plugin with that name (' + pluginName + ') is already registered. Rename plugin or set override to true to force registration.');

      };

      /**
       * Sets plugin state
       *
       * @param plugInName {String} Name of plugin
       * @param state {Boolean} State of plugin (enabled or disabled)
       *
       */
      var setPluginState = function AWE_Toolbar_setPluginState(pluginName, state)
      {
         if (plugins[pluginName])
         {
            plugins[pluginName].setEnabled(state);
         }
      };


      /**
       * Renders toolbar and its plugins
       *
       */
      var render = function AWE_Toolbar_render()
      {
         renderToolbar();
         renderPlugins();
         widgets.toolbar.show();
      };

      /**
       * Renders toolbar
       *
       */
      var renderToolbar = function AWE_Toolbar_renderToolbar()
      {
         if (!Dom.get(id))
         {
            console.log('render toolbar');
         }
         else
         {
            var panelConfig = {
               visible: false,
               draggable: true,
               underlay:'none',
               close:false
            };
            // only make it center for ie browsers that don't support fixed
            // positioning. Note: center() method of panel needs to be
            // overridden for toolbar instance (see below)
            if (YAHOO.env.ua.ie && YAHOO.env.ua.ie<7)
            {
               panelConfig.fixedCenter = true;
            }

            var toolbar = widgets.toolbar = new YAHOO.widget.Panel(id, panelConfig);

            // override panel.center() so that toolbar is redrawn on page scroll
            // at top of viewport.
            if (YAHOO.env.ua.ie && YAHOO.env.ua.ie<7)
            {
               widgets.toolbar.center = function toolbar_center() {
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

            Dom.addClass([toolbar.header,toolbar.body,toolbar.footer],'awe-toolbar-module');
            toolbar.render();
         }
      };

      /**
       * Renders child plugins
       *
       * As plugin.render methods always return an array of button configurations
       * this method simply renders the button(s) and adds to toolbar.
       *
       */
      var renderPlugins = function AWE_Toolbar_renderPlugins()
      {
         for (var i=0,len=pluginOrder.length;i<len;i++)
         {
            var pluginName = pluginOrder[i];
            if (plugins[pluginName])
            {
               var buts = plugins[pluginName].render();
               if (buts && YAHOO.lang.isArray(buts))
               {
                  for (var i=0,len = buts.length;i<buts.length;i++)
                  {
                       var defaultButtonConfig = {
                          container:widgets.toolbar.body,
                          usearia:true,
                          menualignment:['tl','tr']
                       };
                       var buttonConfig = YAHOO.lang.merge(buts[i], defaultButtonConfig);
                       //TODO: decide whether we save ref to these buttons somewhere.
                       var btn = new YAHOO.widget.Button(buttonConfig);

                       if (YAHOO.lang.isArray(buttonConfig.menu))
                       {
                          var menu = btn.getMenu();
                          menu.subscribe("itemAdded", function (e, args)
                          {
                             var menuItem = args[0];
                             var handler = buttonConfig.onMenuItemMouseOver;
                             menuItem.subscribe('mouseover', handler.fn, {
                                menuItem: args[0],
                                pluginObj: handler.scope
                             }, true);
                             menuItem.subscribe('focus', handler.fn, {
                                menuItem: args[0],
                                pluginObj: handler.scope
                             }, true);
                          });
                       }
                  }
               }
            }
         }
      };

      return {
         init: init,
         registerPlugin: registerPlugin,
         setPluginState: setPluginState,
         render: render,
         plugins: {},
         loadPreferences: loadPreferences,
         setPreferences: setPreferences
      };
   }());

   /**
    *
    * @module AWE.Toolbar
    * @name AWE_PLUGIN_COMPONENTSLOADED_EVENT
    * @event Fires when all components required by plugin are loaded
    *
    */
   AWE.Toolbar.AWE_PLUGIN_COMPONENTSLOADED_EVENT = 'AWE_Plugin_ComponentsLoaded';


   /**
    * Toolbar Plugin object constructor
    *
    * @module AWE.Toolbar.Plugin
    *
    */
   AWE.Toolbar.Plugin = function AWE_Toolbar_Plugin(name, id, components)
   {
      AWE.Toolbar.Plugin.superclass.constructor.call(this, name, id, components);
   };

   YAHOO.extend(AWE.Toolbar.Plugin, Alfresco.component.Base,
   {
      options: {
         'dependencies':[]
      },
      init: function AWE_Toolbar_Plugin_init()
      {
      },

      /**
       * Fires an event when components (dependencies) have been loaded
       *
       * @module AWE.Toolbar.Plugin
       *
       * @name onComponentsLoaded
       *
       */
      onComponentsLoaded : function AWE_Toolbar_Plugin_onComponentsLoaded()
      {
         Bubbling.fire(AWE.Toolbar.AWE_PLUGIN_COMPONENTSLOADED_EVENT, {
            plugInName: this.name
         });
      },

      /**
       * Renders all html required by plugin and returns. Needs to be overiddden
       *
       * @module AWE.Toolbar.Plugin
       * @name render
       *
       * @return {Object} config Returns a configuration object that consists of
       *                         an array of button configs, handlers(?), and
       *                         dialog html. E.G
       *                         {
       *                            buttonConfigs:
       *                            [
       *                               {
       *                                  id:'pluginName-actionid',//-but gets added by yui button
       *                                  label: "Radio 2",
       *                                  value: "2",
       *                                  onclick:{
       *                                     fn:function(){},
       *                                     scope:this
       *                                  }
       *                               },
       *                            ],
       *                            html: '<div id='plugin-actionpanel'>lots of html</div>
       *                         }
       *
       */
      render: function AWE_Toolbar_Plugin_render()
      {
        return {};
      }
   });
})();