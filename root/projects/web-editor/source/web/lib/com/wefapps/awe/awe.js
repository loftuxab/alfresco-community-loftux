(function() {
    
   YAHOO.namespace('com.wefapps.awe');

   YAHOO.com.wefapps.awe = function com_alfresco_awe_init()
   {
    //start awe app after WEF has initialised
    YAHOO.Bubbling.on('WEF'+WEF.SEPARATOR+'afterInit',
      function (e, args)
      {
         WEF.module.AWE = new YAHOO.com.alfresco.awe.app(
         {
            id:'awe',
            name:'awe',
            editables:this.editables
         });
         WEF.module.AWE.init();
      }, 
      {
         editables: WEF.ConfigRegistry.getConfig('com.wefapps.awe')
      });
   };
   YAHOO.Bubbling.on('WEF-Ribbon'+WEF.SEPARATOR+'afterRender',
   function(e, args)
   {
      var ribbon = args[1].obj;
      var aweName = WEF.module.AWE.config.name;
      ribbon.addToolbar(
      {
         buttonType: 'advanced',
         buttons: [{
            group: YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR,
            buttons: [{
               type: 'menu',
               label: 'Quick Edit',
               value: aweName + YAHOO.org.wef.SEPARATOR + 'quickedit',
               id: aweName + YAHOO.org.wef.SEPARATOR + 'quickedit',
               menu: function renderEditableContentMenu(markers)
               {
                  
                  var menuConfig = [];
                  for (var p in markers) 
                  {
                     menuConfig.push(
                     {
                        text: markers[p].title,
                        value: markers[p]
                     });
                  }

                  return menuConfig;
               }(WEF.module.AWE.getEditableContentMarkers())
            }, {
               type: 'push',
               label: 'Show/Hide edit markers',
               value: aweName + YAHOO.org.wef.SEPARATOR + 'show-hide-edit-markers',
               id: aweName + YAHOO.org.wef.SEPARATOR + 'show-hide-edit-markers'
            }]
         }]
      }, YAHOO.org.wef.ui.Ribbon.PRIMARY_TOOLBAR); // add to main body
      
      ribbon.addToolbar(
      {
         buttonType: 'advanced',
         buttons: [{
            group: YAHOO.org.wef.ui.Ribbon.SECONDARY_TOOLBAR,
            buttons: [{
               type: 'menu',
               label: 'Ribbon placement',
               value: aweName+ YAHOO.org.wef.SEPARATOR + 'ribbon-placement',
               id: aweName + YAHOO.org.wef.SEPARATOR + 'ribbon-placement',
               menu: [{
                  text: 'top',
                  value: YAHOO.org.wef.ui.Ribbon.POSITION_TOP
               }, {
                  text: 'left',
                  value: YAHOO.org.wef.ui.Ribbon.POSITION_LEFT
               }, {
                  text: 'right',
                  value: YAHOO.org.wef.ui.Ribbon.POSITION_RIGHT
               }]
            }, {
               type: 'push',
               label: 'Help',
               value: aweName + YAHOO.org.wef.SEPARATOR + 'help',
               id: aweName + YAHOO.org.wef.SEPARATOR + 'help'
            }, {
               type: 'push',
               label: 'Logout',
               value: aweName + YAHOO.org.wef.SEPARATOR + 'logout',
               id: aweName + YAHOO.org.wef.SEPARATOR + 'logout',
               disabled: true
            }]
         }]
      }, YAHOO.org.wef.ui.Ribbon.SECONDARY_TOOLBAR); // add to footer.
      
   });
})();

WEF.register("com.wefapps.awe", YAHOO.com.wefapps.awe, {version: "1.0.0", build: "1"});