/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

(function() 
{
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       KeyListener = YAHOO.util.KeyListener,
       Selector = YAHOO.util.Selector,
       Element = YAHOO.util.Element,
       Bubbling = YAHOO.Bubbling,
       Cookie = YAHOO.util.Cookie,
       WebEditor = YAHOO.org.springframework.extensions.webeditor;

   YAHOO.namespace('org.springframework.extensions.webeditor.ui.Toolbar');

   WebEditor.ui.Toolbar = function WEF_UI_Toolbar_constructor(config)
   {
      WebEditor.ui.Toolbar.superclass.constructor.apply(this, Array.prototype.slice.call(arguments));
   };

   YAHOO.extend(WebEditor.ui.Toolbar, WEF.Widget,
   {
      init: function WEF_UI_Toolbar_init()
      {
         WebEditor.ui.Toolbar.superclass.init.apply(this);
         this.widgets.buttons = [];
         var el = this.element.get('element');
         el.innerHTML= '<div class="wef-toolbar"><fieldset><legend>leg</legend><div class="wef-toolbar-subcont"><div class="wef-toolbar-group"><h3></h3><ul></ul></div></div></fieldset></div>';
         this.widgets.buttonContainer = this.element.getElementsByTagName('ul')[0];
         var buttons = this.config.buttons || {};
         if (buttons.buttons) 
         {
            this.addButtons(buttons.buttons); 
         }

         //event handler
         this.element.on('click', function(e)
         {
            var targetEl = Event.getTarget(e),
                id = null,
                btn = null,
                value,
                evtTypeStr = e.type.charAt(0).toUpperCase()+e.type.substring(1);
            if ((targetEl.nodeName.toLowerCase()==='img'))
            {
               targetEl = Dom.getAncestorByTagName(targetEl,'button');
               if (!targetEl)
               {
                  return false;
               }
            }

            if (targetEl.id)
            {
               id = targetEl.id.replace(/-button$/,'');
               btn = this.widgets.buttons[id];
            }

            if (btn) 
            {
               this.currentButton = btn;
               if (btn.get('type') != 'menu') 
               {
                  value = btn.get('value');
               }
            }
            // click is not on button el but a menu item
            else
            {
               if (Dom.hasClass(targetEl, 'yuimenuitemlabel-selected'))
               {
                  value = this.currentButton.getMenu().activeItem.value;
                  //rest id to button id so correct event is fired
                  id = this.currentButton.get('id');
               }
            }
            if (!YAHOO.lang.isUndefined(id) && !YAHOO.lang.isUndefined(value))
            {
               Bubbling.fire(id+evtTypeStr, value);
            }
         },this,true);
         this.initAttributes(this.config);
      },

      addButtons: function WEF_UI_Toolbar_addButtons(buttons)
      {
         for (var i = 0, len = buttons.length; i < len; i++) 
         {
            var btnConfig = buttons[i],
                li;
            btnConfig.icon = btnConfig.icon || false;
            // create container
            li = document.createElement('li');
            li.className = 'wef-toolbar-groupitem';
            this.widgets.buttonContainer.appendChild(li);
            btnConfig.container = li;
            var but = new YAHOO.widget.Button(btnConfig);
            if(btnConfig.icon)
            {
               but.addClass('icon-button');
            }
            this.widgets.buttons.push(but);
            this.widgets.buttons[btnConfig.id] = but;
         };

         Dom.addClass(this.widgets.buttonContainer.childNodes[0],'first');
      },
      
      getButtonById: function WEF_UI_Toolbar_getButtonById(buttonId)
      {
         return this.widgets.buttons[buttonId];
      }
   });
})();

WEF.register("org.springframework.extensions.webeditor.ui.Toolbar", YAHOO.org.springframework.extensions.webeditor.ui.Toolbar, {version: "1.0", build: "1"});