/* Gutter Plugin */
Alfresco.gutter = function(myEditor) {
   var Dom = YAHOO.util.Dom,
          Event = YAHOO.util.Event;
   
   return {
         status: false,
         gutter: null,
         
         createGutter: function() {
             this.gutter = new YAHOO.widget.Overlay('gutter1', {
                 height: '425px',
                 width: '300px',
                 context: [myEditor.get('element_cont').get('element'), 'tl', 'tr'],
                 position: 'absolute',
                 visible: false
             });
             this.gutter.hideEvent.subscribe(function() {
                 myEditor.toolbar.deselectButton('alfresco');
                 Dom.setStyle('gutter1', 'visibility', 'visible');                
                 var anim = new YAHOO.util.Anim('gutter1', {
                     width: {
                         from: 300,
                         to: 0
                     },
                     opacity: {
                         from: 1,
                         to: 0
                     }
                 }, 1);
                 anim.onComplete.subscribe(function() {  
                     Dom.setStyle('gutter1', 'visibility', 'hidden');
                 });
                 anim.animate();
             }, this, true);
             this.gutter.showEvent.subscribe(function() {
                 myEditor.toolbar.selectButton('alfresco');
                 this.gutter.cfg.setProperty('context', [myEditor.get('element_cont').get('element'), 'tl', 'tr']);
                 Dom.setStyle(this.gutter.element, 'width', '0px');
                 var anim = new YAHOO.util.Anim('gutter1', {
                     width: {
                         from: 0,
                         to: 300
                     },
                     opacity: {
                         from: 0,
                         to: 1
                     }
                 }, 1);
                 anim.animate();
             }, this, true);
             var warn = '';
             if (myEditor.browser.webkit || myEditor.browser.opera) {
                 warn = myEditor.STR_IMAGE_COPY;
             }
             this.gutter.setBody('<h2>Library Images</h2><div id="image_results"></div>' + warn);
             this.gutter.render(document.body);
         },
         
         open: function() {
             this.gutter.show();
             this.status = true;
         },
         
         close: function() {
             this.gutter.hide();
             this.status = false;
         },
         
         toggle: function() {
             if (this.status) {
                 this.close();
             } else {
                 this.open();
             }
         }
     }
}

/**
 * Alfresco top-level util namespace.
 * 
 * @namespace Alfresco
 * @class Alfresco.util
 */
Alfresco.util = Alfresco.util || {};
 
Alfresco.util.createImageEditor = function(id, options)
{
   var editor = new YAHOO.widget.SimpleEditor(id, options);

   editor.on('toolbarLoaded', function() {
 	   var gutter = new Alfresco.gutter(editor);

 	   // Image Library button config
 	   var libraryConfig = {
 	      type: 'push',
 	      label: 'Insert Library Image',
 	      value: 'alfresco'
 	   };

 	   // Add the button to the "insertitem" group
 	   editor.toolbar.addButtonToGroup(libraryConfig, 'insertitem');

 	   // Handle the button's click
 	   editor.toolbar.on('alfrescoClick', function(ev) {
 	      if (ev && ev.img)
 	      {
 	         var html = '<img src="' + ev.img + '" title="' + ev.title + '"/>';
 	         Alfresco.logger.debug(html, this);
            editor.execCommand('inserthtml', html);
 	      }
 	      gutter.toggle();
 	   });
 	   // Create the gutter control
 	   gutter.createGutter();
 	});

   YAHOO.util.Event.onAvailable('image_results', function() {
 	   YAHOO.util.Event.on('image_results', 'mousedown', function(ev) {
 	      YAHOO.util.Event.stopEvent(ev);
 	      var target = YAHOO.util.Event.getTarget(ev);
          if (target.tagName.toLowerCase() == 'img') {
             if (target.getAttribute('src')) {
                var img = target.getAttribute('src'),
                title = target.getAttribute('title');
                this.toolbar.fireEvent('alfrescoClick', { type: 'alfrescoClick', img: img, title: title });
             }
          }
 	   }, editor, true);
 	   // Load the "images"
      Alfresco.util.Ajax.request(
 		{
 		   method: Alfresco.util.Ajax.GET,
 		   url: Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/images/site/" + options.siteId + "/documentLibrary?filter=node",
 			successCallback:
 			{
 				fn: function(e) {
 				   Alfresco.logger.debug(e.serverResponse.responseText, this);
 				   var result = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
 				   if (result)
 				   {
 				      var div = YAHOO.util.Dom.get('image_results');
 				      var items = result.items;
 				      for (var i=0; i < items.length; i++)
 				      {
 				         var src = items[i].contentUrl,
 				         title = items[i].title;
 				         div.innerHTML += '<img src="' + Alfresco.constants.PROXY_URI + src + '" title="' + title + '"/>';
 				      }
 				   } 
 				},
 				scope: this
 			},
 		   failureMessage: "Could not retrieve version information"
 		});
   });

   return editor;
}
 
    
