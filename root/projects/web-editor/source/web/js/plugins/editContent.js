(function(){
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener,
      Selector = YAHOO.util.Selector,
      Bubbling = YAHOO.Bubbling;

      AWE.Toolbar.plugins.EditContent = function EditContent_constructor(name)
      {
         this.name = name || 'EditContent';
         this.name = 'EditContent';
         this.editMarkers = null;
         this.scrollAnimation = null;
         this.docBody = (YAHOO.env.ua.gecko) ? document.documentElement : document.body;
         AWE.Toolbar.plugins.EditContent.superclass.constructor.call(this, this.name);
      };

      YAHOO.extend(AWE.Toolbar.plugins.EditContent, AWE.Toolbar.Plugin,
      {
         onMenuItemMouseOver: function EditContent_onMenuItemMouseOver(evtName, e, o)
         {
            var menuItem = o.menuItem;
            var pluginObj = o.pluginObj;
            var el = pluginObj.editMarkers[menuItem.value].elem;
            if (pluginObj.scrollAnimation)
            {
               pluginObj.scrollAnimation.stop();
            }

            var posY = Dom.getRegion(el).top;
            //set up animation
            pluginObj.scrollAnimation = new YAHOO.util.Scroll(pluginObj.docBody,
               {
                  scroll:
                  {
                     to: [0, Math.max(0,posY-50)]
                  }
               },
               1,
               YAHOO.util.Easing.easeOut
             );
             pluginObj.scrollAnimation.animate();
         },

         render: function EditContent_render()
         {
            this.editMarkers = AWE.getEditableContentMarkers();
            var menuConfig = [];
            //need documentElement for gecko
            
            for (var p in this.editMarkers)
            {
                 var elem = this.editMarkers[p].elem;
                 var config = this.editMarkers[p].config;
                 var altText = Selector.query('img', elem, true).alt;
                 menuConfig.push({
                    text: altText,
                    value: p,
                     onclick:
                     {
                        fn: function(o)
                        {
                           return function(e)
                           {
                              Event.preventDefault(e);
                              Bubbling.fire(AWE.constants.AWE_EDIT_CONTENT_CLICK_EVENT, o.config);
                           };
                        }(this.editMarkers[p])
                    }
                 });
            }
            return [
               {
                  id:this.name+'-action1',
                  label:'<img src="' + Alfresco.constants.URL_CONTEXT + '/themes/default/images/edit.png" alt="' +this.msg('label-action1') + '" />',
                  value:'action1',
                  type:'menu',
                  focusmenu:false,
                  menu: menuConfig,
                  onMenuItemMouseOver: {
                     fn: this.onMenuItemMouseOver,
                     scope: this
                  }
               }
            ];
         }
      });
})();