(function() {
    
   YAHOO.namespace('org.wefapps.awe');

   YAHOO.org.wefapps.awe = function org_alfresco_awe_app_run()
   {
    //start awe app after WEF has rendered
     YAHOO.Bubbling.on(
         'WEF-Ribbon'+WEF.SEPARATOR+'afterRender',
         function(e, args)
         {
            var ribbon = args[1].obj;
            WEF.module.AWE = new YAHOO.org.alfresco.awe.app(
            {
               id:'awe',
               name:'awe',
               editables:this.editables
            });
            WEF.module.AWE.init();
         },
         {
            editables: WEF.ConfigRegistry.getConfig('org.wefapps.awe')
         }
     );
   };
})();

WEF.register("org.wefapps.awe", YAHOO.org.wefapps.awe, {version: "1.0.0", build: "1"});