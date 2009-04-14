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
      this.name = "Alfresco.WebView";
      this.id = htmlId;

      this.configDialog = null;

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.WebView.prototype = 
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
         webviewHeight: 600
      },
      
      /**
       * Configuration dialog instance
       *
       * @property configDialog
       * @type object
       */
      configDialog: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function WebView_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function WebView_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

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
                              var titleLink = Dom.get(this.id + "-title-link"),
                                 linkHref = iframe.attributes["name"].value;
                              titleLink.attributes["href"].value = linkHref;
                              titleLink.innerHTML = $html(linkHref);
                           }
                           if (iframe.attributes["theHeight"])
                           {
                              var theHeight = iframe.attributes["theHeight"].value;
                              Dom.setStyle(div, "height", theHeight + "px");
                           }
                        }
                     }
                  },
                  scope: this
               },
               doSetupFormsValidation:
               {
                  fn: function WebView_doSetupForm_callback(form)
                  {
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
                        elem.value = this.options.webviewURI;
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
      }
   };
})();