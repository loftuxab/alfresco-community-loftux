(function()
{
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener,
      Selector = YAHOO.util.Selector,
      Bubbling = YAHOO.Bubbling;
   
   YAHOO.namespace('com.alfresco.awe.ui.Panel');
   
   YAHOO.com.alfresco.awe.ui.Panel = function AWE_Panel_constructor(name, containerId, components)
   {
      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null) {
         throw new Error("An instance of ' + name + ' already exists.");
      }
      
      YAHOO.com.alfresco.awe.ui.Panel.superclass.constructor.call(this, name, containerId, ["button", "container", "connection", "selector", "json"]);
      this.init();
      return this;
   };
   
   YAHOO.extend(YAHOO.com.alfresco.awe.ui.Panel, Alfresco.component.Base, {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       * @default {}
       */
      options: {
         /**
          * Flag denoting whether to destroy panel after panel is hidden,
          * forcing a reload of panel template if shown again
          *
          * @type boolean
          *
          */
         destroyPanelOnHide: true,
         /**
          * Flag denoting whether to grab focus on first input element when
          * panel is shown
          *
          * @type boolean
          */
         focusFirstInputElement: true
      },
      
      /**
       *
       */
      init: function AWE_Panel_init()
      {
      
      },
      /**
       * Shows the login dialog to the user.
       *
       * @method show
       */
      show: function AWE_Panel_show(callback)
      {
         //set callback object reference if specified
         //it will be called on success.
         if (callback) {
            this.callback = callback;
         }
         
         if (this.widgets.panel) {
            /**
             * The panel gui has been showed before and its gui has already
             * been loaded and created
             */
            this._showPanel();
         }
         else {
            /**
             * Load the gui from the server and let the templateLoaded() method
             * handle the rest.
             */
            Alfresco.util.Ajax.request({
               url: this.options.templateUrl,
               dataObj: {
                  htmlid: this.id
               },
               successCallback: {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               execScripts: true,
               failureMessage: "Could not load create " + this.name + " template"
            });
         }
      },
      
      /**
       * function AwePanel_hide
       *
       */
      hide: function Awe_Panel_hide()
      {
         this.widgets.panel.hide();
         if (this.options.destroyPanelOnHide) {
            this.widgets.panel.destroy();
            this.widgets.panel = null;
         }
      },
      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function Awe_Panel__showPanel()
      {
         // Show the upload panel
         this.widgets.panel.show();
         
         // Firefox insertion caret fix
         Alfresco.util.caretFix(this.id + "-form");
         
         // Register the ESC key to close the dialog
         var escapeListener = new KeyListener(document, {
            keys: KeyListener.KEY.ESCAPE
         }, {
            fn: function(id, keyEvent)
            {
               this.hide();
            },
            scope: this,
            correctScope: true
         });
         escapeListener.enable();
         
         // Set the focus on the first field
         var firstInputEl = Selector.query('input', this.id, true);
         
         if (firstInputEl) {
            firstInputEl.focus();
         }
      }
   });
})();
WEF.register("com.alfresco.awe.ui.panel", YAHOO.com.alfresco.awe.ui.Panel, {version: "1.0", build: "1"});