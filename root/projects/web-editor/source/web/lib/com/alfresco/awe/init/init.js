(function() {
    
    YAHOO.namespace('com.alfresco.awe');

    YAHOO.com.alfresco.awe.init = function com_alfresco_awe_init(registerEditableContent)
    {
       AWE.init().registerEditableContent(registerEditableContent);
    };
    
    YAHOO.com.alfresco.awe.render = function com_alfresco_awe_render()
    {
      AWE.module.Ribbon = new AWE.Ribbon("awe-ribbon");
      AWE.module.Ribbon.init({
         position: "top"
      });
      AWE.module.Ribbon.render();
    };
})();

YAHOO.register("com.alfresco.awe.init", YAHOO.com.alfresco.awe.init, {version: "1.0.0", build: "1"});