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
            var plugin = WEF.getPlugin("${plugin.name?html}");
            
            // retrieve or create plugin config
            var config = YAHOO.org.wef.ConfigRegistry.getConfig("${plugin.name?html}");
            if (config == null)
            {
               config = 
               {
                  id: "${plugin.name?html}",
                  name: "${plugin.name?html}"
               }
            }
            
            // initialise the plugin
            plugin.init(config);
            </#list>
         }
      );
   };
})();

WEF.register("${appName}", YAHOO.${appName}, {version: "1.0.0", build: "1"});
