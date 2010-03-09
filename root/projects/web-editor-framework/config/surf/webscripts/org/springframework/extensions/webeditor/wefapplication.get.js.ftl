(function()
{
   YAHOO.namespace("${appName}");

   YAHOO.${appName} = function ${appName?replace(".", "_")}()
   {
      // initialise each plugin after WEF has rendered
      YAHOO.Bubbling.on(
         'WEF-Ribbon'+WEF.SEPARATOR+'afterRender',
         function(e, args)
         {
            <#list plugins as plugin>
            var plugin = WEF.PluginRegistry.getPlugin("${plugin.name?html}"),
                pluginInstance;
            
            // retrieve or create plugin config
            var config = YAHOO.org.springframework.extensions.webeditor.ConfigRegistry.getConfig("${plugin.name?html}");
            if (config == null)
            {
               config = 
               {
                  id: "${plugin.name?html}",
                  name: "${plugin.name?html}"
               }
            }
            //create instance of plugin
            pluginInstance = new plugin(config);
            //store instance
            WEF.PluginRegistry.registerInstance(config.id, pluginInstance);
            // initialize the plugin
            pluginInstance.init();
            </#list>
         }
      );
   };
})();

WEF.register("${appName}", YAHOO.${appName}, {version: "1.0.0", build: "1"});
