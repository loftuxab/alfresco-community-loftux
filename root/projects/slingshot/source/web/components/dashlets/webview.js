/**
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
 
/*
 *** Alfresco WebView Dashlet
 *
 * @namespace Alfresco
 * @class Alfresco.WebView
 *
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   Alfresco.WebView = function(htmlId)
   {
      Alfresco.WebView.superclass.constructor.call(this, "Alfresco.WebView", htmlId, []);

      // Initialise prototype properties
      this.configDialog = null;

      /**
       * Decoupled event listeners
       */
      YAHOO.Bubbling.on("showPanel", this.onShowPanel, this);
      YAHOO.Bubbling.on("hidePanel", this.onHidePanel, this);

      return this;
   };

   YAHOO.extend(Alfresco.WebView, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         componentId: "",
         webviewURI: "",
         webviewTitle: "",
         webviewHeight: 600,
         isDefault: 'true'
      },
      
      /**
       * Configuration dialog instance
       *
       * @property configDialog
       * @type object
       */
      configDialog: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */
      onReady: function WebView_onReady()
      {
         var configWebViewLink = Dom.get(this.id + "-configWebView-link");
         Event.addListener(configWebViewLink, "click", this.onConfigWebViewClick, this, true);

         /**
          * Save reference to iframe wrapper so we can hide and show it depending
          * on how well the browser handles flash movies.
          */
         this.widgets.iframeWrapper = Dom.get(this.id + "-iframeWrapper");
      },

      /**
       * Event listener for configuration link click.
       *
       * @method onConfigWebViewClick
       */
      onConfigWebViewClick: function WebView_onConfigWebViewClick(event)
      {
         var actionUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/webview/config/" + encodeURIComponent(this.options.componentId);

         if (!this.configDialog)
         {
            this.configDialog = new Alfresco.module.SimpleDialog(this.id + "-configDialog").setOptions(
            {
               width: "50em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/webview/config",
               actionUrl: actionUrl,
               onSuccess:
               {
                  fn: function WebView_onConfigWebView_callback(response)
                  {
                     // MSIE6 doesn't redraw the IFRAME correctly, so tell it to refresh the page
                     if (YAHOO.env.ua.ie === 6)
                     {
                        window.location.reload(true);
                     }
                     else
                     {
                        var div = Dom.get(this.id + "-iframeWrapper");
                        div.innerHTML = response.serverResponse.responseText;
                        var iframe = Dom.getFirstChildBy(div, function(node)
                        {
                           return (node.tagName.toUpperCase() == "IFRAME");
                        });
                        if (iframe)
                        {
                           if (iframe.attributes["name"])
                           {
                              var titleLink = Dom.get(this.id + "-title-link");
                              // update iframe and internal config
                              titleLink.href.value = this.options.webviewURI = iframe.attributes["src"].value;
                              this.options.webviewTitle = iframe.attributes["name"].value;
                              titleLink.innerHTML = $html(this.options.webviewTitle);
                           }
                           if (iframe.attributes["theHeight"])
                           {
                              var theHeight = this.options.webviewHeight = iframe.attributes["theHeight"].value;
                              Dom.setStyle(div, "height", theHeight + "px");
                           }
                           this.options.isDefault = 'false';
                        }
                     }
                  },
                  scope: this
               },
               doSetupFormsValidation:
               {
                  fn: function WebView_doSetupForm_callback(form)
                  {
                     form.addValidation(this.configDialog.id + "-webviewTitle", Alfresco.forms.validation.mandatory, null, "keyup");
                     form.addValidation(this.configDialog.id + "-url", Alfresco.forms.validation.mandatory, null, "blur");
                     form.addValidation(this.configDialog.id + "-url", Alfresco.forms.validation.url, null, "keyup");
                     form.setShowSubmitStateDynamically(true, false);

                     /* Get the link title */
                     var elem = Dom.get(this.configDialog.id + "-webviewTitle");
                     if (elem)
                     {
                        elem.value = this.options.webviewTitle;
                     }

                     /* Get the url value */
                     elem = Dom.get(this.configDialog.id + "-url");
                     if (elem)
                     {
                        elem.value = (this.options.isDefault=='false') ? this.options.webviewURI : 'http://';
                     }

                     /* Get the height value */
                     elem = Dom.get(this.configDialog.id + "-height");
                     if (elem)
                     {
                        elem.value = this.options.webviewHeight;
                     }
                  },
                  scope: this
               }
            });
         }
         else
         {
            this.configDialog.setOptions(
            {
               actionUrl: actionUrl
            });
         }
         this.configDialog.show();
         Event.stopEvent(event);
      },

      /**
       * Called when any Panel in share created with createYUIPanel is shown.
       * Will hide the content for browsers that can't handle a flash movies properly,
       * since the flash movie could hide parts of the the panel.
       *
       * @method onShowPanel
       * @param p_layer {object} Event fired (unused)
       * @param p_args {array} Event parameters (unused)
       */
      onShowPanel: function WW_onShowPanel(p_layer, p_args)
      {
         if (this._browserDestroysPanel())
         {
            Dom.setStyle(this.widgets.iframeWrapper, "visibility", "hidden");
         }
      },

      /**
       * Called when any Panel in share created with createYUIPanel is hidden.
       * Will display the content again if it was hidden before.
       *
       * @method onHidePanel
       * @param p_layer {object} Event fired (unused)
       * @param p_args {array} Event parameters (unused)
       */
      onHidePanel: function WW_onHidePanel(p_layer, p_args)
      {
         if (this._browserDestroysPanel())
         {
            Dom.setStyle(this.widgets.iframeWrapper, "visibility", "visible");
         }
      },

      /**
       * Returns true if browser will make flash movie hide parts of a panel
       *
       * @method _browserDestroysPanel
       * @return {boolean} True if browser will let flash movie mess up panel
       */
      _browserDestroysPanel: function WW__browserDestroysPanel()
      {
         // All browsers on Windows (tested w FP 10) and FF2 and below on Mac
         return (navigator.userAgent.indexOf("Windows") != -1 ||
                 (navigator.userAgent.indexOf("Macintosh") != -1 && YAHOO.env.ua.gecko > 0 && YAHOO.env.ua.gecko < 1.9));
      }

   });
})();