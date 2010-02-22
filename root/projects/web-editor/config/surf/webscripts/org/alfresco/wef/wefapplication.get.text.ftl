(function()
{
   /**
    * WEFApplication top level object
    * @namespace
    * @constructor WEFApplication
    */
   WEFApplication = (function()
   {
      var init = function WEFApplication_init()
      {
      <#list plugins as plugin>
         var ${plugin.variableName?html} = WEF.getPlugin("${plugin.name?html}");
         ${plugin.variableName?html}.init();
      </#list>
         
         return this;
      }
   }
})();

//WEF.register("${appName?html}", YAHOO.${appName?html}, {version: "1.0.0", build: "1"});
