
if (typeof WEF == "undefined" || !WEF) 
{
    WEF = function WEF()
    {
        var config = {},
           loader = (function()
           {
           var loadingModules = [], unloadedModules = [], loadedModules = [], registry = {}, repositories = {
               lib: '/lib',
               plugin: '/plugin',
               core: '/js'
           }, bootloaderConfig = {}, YUILoader = null;
           
           /**
            * Initialises WEF
            *
            * @param config {Object} Config to initialize with
            *
            */
           var init = function WEF_initialise(config, callback)
           {
               bootloaderConfig = config;
           };
           /**
            * boot
            *
            * @param config {Object} Config to initialize with
            *
            */
           var boot = function WEF_initialise(callback)
           {
               var loadYUILoader = function loadYUILoader(yuiloaderPath, callback)
               {
                   loadYUILoader._counter = (loadYUILoader._counter === undefined) ? 0 : ++loadYUILoader._counter;
                   var node = document.createElement('script');
                   node.id = 'wef-yuiloader' + loadYUILoader._counter;
                   node.src = bootloaderConfig.serverPort + yuiloaderPath;
                   if (!+"\v1") // true only in IE
                   {
                       var id = node.id;
                       node.onreadystatechange = function()
                       {
                           var rs = this.readyState;
                           if ("loaded" === rs || "complete" === rs) 
                           {
                               node.onreadystatechange = null;
                               callback();
                           }
                       };
                   }
                   else {
                       node.onload = function()
                       {
                           callback();
                       };
                   }
                   document.getElementsByTagName('head')[0].appendChild(node);
               };
               
               try {
                   var y = YAHOO;
               } 
               catch (e) 
               {
                   //if no YAHOO on page assume YUILoader isn't too so let's load it up.
                   return loadYUILoader(bootloaderConfig.yuiloaderPath, callback);
               }
               
               return this;
           };
           
           /**
            * @private
            * Internal method for converting a simple configuration to a fully fledged YUI module config
            *
            * @param config {Object} A simple config which looks like this:
            *
            * {
            *    name: <module_id> (required),
            *    type: <type_id> (optional - "js" or "css", defaults to "js"),
            *    repo: <repo_id> (optional - "plugin", "lib", "root" or a custom repo, defaults to "plugin"),
            *    path: <path> (optional - if supplied, overrides repo)
            *    requires: <array of ids> (optional - additional requirements)
            *    varName: <variable name> (optional - if not supplied, will be auto-generated)
            * }
            *
            * @return {Object} YUI Loader compatible module
            */
           var convertConfigToYUIModuleConfig = function WEF_convertConfigToYUIModuleConfig(config)
           {
               var yuiConfig = {
                   name: config.name,
                   type: (config.type && config.type==='css') ? 'css' : 'js',
                   //type: config.type || 'js', 
                   requires: config.requires || null
               };
   
               if (config.varName)
               {
                   yuiConfig.varName = config.varName;
               }
               else {
                   var n = config.name, x = config.name.indexOf('.');
                   if (x > -1) 
                   {
                       n = config.name.substring(0, x);
                   }
                   // use internal function counter
                   convertConfigToYUIModuleConfig._counter = (convertConfigToYUIModuleConfig._counter === undefined) ? 0 : ++convertConfigToYUIModuleConfig._counter;
                   yuiConfig.varName = "WEF_Loader_Variable_" + n + convertConfigToYUIModuleConfig._counter;
               }
               var repoRootPath = (config.repo) ? repositories[config.repo] : repositories.core;
               if (config.path) 
               {
                   //absolute path
                   if (config.path.match(/^http:/) || config.path.match(/^https:/)) 
                   {
                       yuiConfig.fullpath = config.path;
                   }
                   //relative path so make it absolute
                   else {
                       if (!config.path.match(/^\//)) 
                       {
                           config.path = "/" + config.path;
                       }
                       yuiConfig.fullpath = bootloaderConfig.serverPort + repoRootPath + config.path;
                   }
               }
               else {
                   var modulePath = config.name.replace(/\./g, "/");
                   if (!modulePath.match(/^\//)) 
                   {
                       modulePath = '/' + modulePath;
                   }
                   //append filename - this is assumed using last part of path.
                   yuiConfig.fullpath = bootloaderConfig.serverPort + repoRootPath + modulePath + '/' + modulePath.substring(modulePath.lastIndexOf('/') + 1) + '.' + yuiConfig.type;
               }
               console.log('s', yuiConfig.fullpath);
               return yuiConfig;
           };
           
           /**
            * Adds a module to be loaded. If module declares dependencies then also sets those modules to be loaded too
            *
            * @param config {Object} Module to setup for loading
            * @param isYUILoaderCompatible {Boolean} if config object is YUI Loader compatible then no conversion is performed.
            *
            */
           var require = function WEF_require(config, isYUILoaderCompatible)
           {
               //convert to object if string (id only)
               if (typeof(config) == 'string') 
               {
                   config = registry[config];
               }
               if (YAHOO.lang.isObject(config) && config.requires) 
               {
                   for (var i = 0, len = config.requires.length; i < len; i++) 
                   {
                       var modName = config.requires[i];
                       //non yui files
                       if (modName.indexOf('.') !== -1 && registry[modName]) 
                       {
                           unloadedModules.push(registry[modName]);
                           // add any dependencies
                           if (registry[modName].requires) 
                           {
                               for (var j = 0, jlen = registry[modName].requires.length; j < jlen; j++) 
                               {
                                   var depModName = registry[modName].requires[j];
                                   this.require(registry[depModName], true);
                               }
                           }
                       }
                   }
               }
               
               var moduleConfig = (isYUILoaderCompatible) ? config : convertConfigToYUIModuleConfig(config);
               unloadedModules.push(moduleConfig);
               return this;
           };
           
           /**
            * Starts the WEF loader
            *
            * @param successCallback {Object} Object literal describing callback and scope for onSuccess of loading process
            * @param failureCallback {Object} Object literal describing callback and scope for onFailure of loading process
            */
           var load = function WEF_load(successCallback, failureCallback)
           {
               var loaderConfig = {};
               if (!YAHOO.util.YUILoader) 
               {
                   throw new Error('YUI Loader unavailable; Unable to load assets.');
               }
               if (!YUILoader) 
               {
                   YUILoader = new YAHOO.util.YUILoader();
               }
               for (var i = 0, len = unloadedModules.length; i < len; i++) 
               {
                   YUILoader.addModule(unloadedModules[i]);
                   loadingModules.push(unloadedModules[i]);
               }
               unloadedModules = [];
               
               var requires = [];
               for (var i = 0, len = loadingModules.length; i < len; i++) 
               {
                   if (YAHOO.lang.isObject(loadingModules[i])) 
                   {
                       requires.push(loadingModules[i].name);
                   }
               }
               
               loaderConfig.onFailure = function(o)
               {
                   var yuiloader_fail = function WEF_YUILoader_failure(msg, obj)
                   {
                       if (failureCallback) 
                       {
                           failureCallback.fn.call(failureCallback.scope || window, msg, obj);
                       }
                       throw new Error('WEF_YUI_Loader failure', this.report());
                   };
                   return function(msg, obj)
                   {
                       yuiloader_fail.call(o, msg, obj);
                   };
               }(this);
               loaderConfig.onSuccess = function(o)
               {
                   var yuiloader_success = function WEF_YUILoader_success(msg, obj)
                   {
                       for (var i = 0, len = loadingModules.length; i < len; i++)
                       {
                           loadedModules.push(loadingModules[i]);
                       }
                       
                       loadingModules = [];
                       if (unloadedModules.length > 0) 
                       {
                           load(successCallback, failureCallback);
                       }
                       else 
                       {
                           if (successCallback) 
                           {
                               successCallback.fn.call(successCallback.scope || window, msg, obj);
                           }
                       }
                   };
                   return function(msg, obj)
                   {
                       yuiloader_success.call(o, msg, obj);
                   };
               }(this);

               if (bootloaderConfig.debugMode===true)
               {
                  loaderConfig.onProgress = function onProgress()
                  {
                      if (console && console.log) 
                      {
                          console.log(arguments.callee.name, arguments);
                      }
                  };                  
               }

               
               if (bootloaderConfig.useSandboxLoader) 
               {
                   loaderConfig.require = requires;
                   loaderConfig.base = bootloaderConfig.yuibase;
                   loaderConfig.filter = bootloaderConfig.filter || 'min';
                   loaderConfig.loadOptional = bootloaderConfig.loadOptional || true;;
                   loaderConfig.skin = bootloaderConfig.skin || null;
                   YUILoader.sandbox(loaderConfig);
               }
               else {
                   YUILoader.base = bootloaderConfig.yuibase;
                   YUILoader.require(requires);
                   YUILoader.onSuccess = loaderConfig.onSuccess;
                   YUILoader.onFailure = loaderConfig.onFailure;
                   if (bootloaderConfig.debugMode)
                   {
                      YUILoader.onProgress = loaderConfig.onProgress;                      
                   }
                   YUILoader.filter = bootloaderConfig.filter || 'min';
                   YUILoader.loadOptional = bootloaderConfig.loadOptional || true;
                   YUILoader.skin = bootloaderConfig.skin || null;
                   YUILoader.insert();
               }
           };
           /**
            * Adds a module so YUILoader knows where to find it. If required, coverts descriptor to YUILoader compatible format
            *
            * @param o {Object|Array} An object or array of object descriptor describing modules to add
            * @param isYUILoaderCompatible {Boolean} Flag denoting whether descriptor is using YUILoader format. Defaults to false.
            *
            */
           var addResource = function addResource(o, isYUILoaderCompatible)
           {
               var isYUILoaderCompatible = isYUILoaderCompatible || false;
               //is an array
               if (Object.prototype.toString.apply(o) === ['[object Array]']) 
               {
                   for (var i = 0, len = o.length; i < len; i++) 
                   {
                       addResource(o[i]);
                   }
               }
               else {
                   if (!isYUILoaderCompatible) 
                   {
                       o = convertConfigToYUIModuleConfig(o);
                       
                   }
                   registry[o.name] = o;
               }
           };
           
           var report = function WEF_Loader_report()
           {
              var loaderReport = {
                 loaded: loadedModules,
                 unloaded: unloadedModules
              };
              return loaderReport;
           };
           
           return {
               init: init,
               require: require,
               load: load,
               addResource: addResource,
               boot: boot,
               report: report
           };
        })();
        
        return {
           /**
            * Initialises the WEF instance.
            * 
            * @param {Object} wefConfig
            */
            init: function WEF_Init(wefConfig)
            {
                config = wefConfig;
                config.loaderConfig.debugMode = wefConfig.debugMode || false;
                loader.init(config.loaderConfig);
            },
            module: {},
            /**
             * Run the specified app, initialising the boot process first. 
             * Loads any required modules and fires specified callback when finished.
             * 
             * @param {Function} callback
             */
            run: function WEF_Run(appName)//, appSuccessCallback, appFailureCallback)
            {
                loader.boot(function (o)
                   {
                      return function WEF_Boot_Callback() 
                      {
                         o.require(appName);
                         o.load(
                         {
                            fn:function WEF_load_success(obj)
                            {
                               var y = (obj.reference) ? obj.reference : YAHOO,
                                   app = y.env.getVersion(appName).mainClass;
                               if (app)
                               {
                                  //initialise ribbon
                                  y.org.wef.module.Ribbon = new YAHOO.org.wef.ui.Ribbon(
                                  {
                                     id : 'wef-ribbon',
                                     name : 'WEF-Ribbon',
                                     element: 'wef-ribbon'
                                  });
                                  y.org.wef.module.Ribbon.init({
                                     position:'top'
                                  });
                                  //show ribbon after it is rendered.
                                  YAHOO.Bubbling.on('WEF-Ribbon--afterRender', function(e, args)
                                  {
                                    y.org.wef.module.Ribbon.show();
                                  });
                                  //run app
                                  app();
                                  y.Bubbling.fire('WEF'+y.org.wef.SEPARATOR+'afterInit');   
                               }
                               o.render(y);
                            }
                         });
                         //, 
                         //appFailureCallback);
                      };
                   }(this)
                );
            },
            
            render: function WEF_render(y)
            {
               
               var Dom = y.util.Dom,
                   Bubbling = y.Bubbling;
               // init core awe divs
               var body = document.getElementsByTagName('body')[0];
               if (!Dom.get('wef'))
               {
                  var el = document.createElement('div');
                  el.id="wef";
                  el.className='wef';
                  body.insertBefore(el, body.firstChild);
               }
               if (!Dom.hasClass(body, 'wef-root-body'))
               {
                  Dom.addClass(body, 'wef-root-body');
               }
               if (body.className.indexOf('yui-skin')==-1)
               {
                  Dom.addClass(body, 'yui-skin-default');
               }
               Bubbling.fire('WEF'+y.org.wef.SEPARATOR+'afterRender');
               
               //appSuccessCallback.fn.call(appSuccessCallback.scope||window, o);
            },
            /**
             * Retrieve a config property 
             * 
             * @param {String} name
             */
            get: function WEF_get(name)
            {
                return config[name] || null;
            },
            
            /**
             * Set a config property
             * 
             * @param {String} name
             * @param {Object} value
             */
            set: function WEF_set(name, value)
            {
                config[name] = value;
            },
            
            /**
             * Registers a resource to WEF for the loader to use.
             * 
             */
            addResource: function WEF_addResource(config)
            {
               loader.addResource(config);
            },
            
            require: function WEF_require(o)
            {
               loader.require(o);
            },
            
            load: function WEF_load(callbackObject)
            {
               loader.load(callbackObject);
            },
            
            register: function WEF_register(name, o, metadata)
            {
               YAHOO.register(name, o, metadata);
            },
            
            report: function WEF_report()
            {
               console.log('FIXME: loader setup duplicate modules to reload so strip dups. this fix should be in loader (' + arguments.callee.name +')');
               var loaded = loader.report().loaded,
                   index = '',
                   uniqs = [];
               for (var i = 0;i<loaded.length;i++)
               {
                   if (loaded[i])
                   {
                       var name = loaded[i].name;
                       if (index.indexOf(name + ',')==-1)
                       {
                           index+=name+',';
                           uniqs.push(loaded[i]);
                       }
                   }
               }
               return uniqs;
            }
        };
    }();
    
   /**
    * Registry object to store configuration for components.
    * 
    * @class WEF.ConfigRegistry
    * @constructor
    * 
    */
   WEF.ConfigRegistry = (function WEF_Config_Registry()
   {
       var configs = {},
           name = 'WEF_Config_Registry';
       /**
        * Registers config object against specified name
        * 
        * @param configName {String} name of object that the config is for
        * @param config {Object} Object literal describing configuration
        * 
        */
       var registerConfig = function WEF_Config_Registry_registerConfig(configName, config)
       {
          configs[configName] = config;
       };
       
       /**
        * Retrieves config for specified object
        * 
        * @param configName {String} Name of object to specify config for
        * @return config {Object}
        * 
        */
       var getConfig = function WEF_Config_Registry_getConfig(configName)
       {
          return configs[configName] || null;
       };
       
       return {
          registerConfig : registerConfig,
          getConfig : getConfig
       }; 
   })();
}



