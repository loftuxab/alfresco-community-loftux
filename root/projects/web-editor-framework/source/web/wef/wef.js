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
       Element = YAHOO.util.Element,
       Cookie = YAHOO.util.Cookie;
      
   if (typeof WEF == "undefined" || !WEF)
   {
      throw new Error('WEF not found');   
   }
   var Bubbling = YAHOO.Bubbling;
   
   YAHOO.namespace('org.springframework.extensions.webeditor');
   /**
    * Provides AOP style functionality (before,after,around)
    * @class WEF.Do
    *  
    */
   WEF.Do = function() 
   {
      var aAspects = {
         
         /**
          * Decorates method wth supplied function. Stores a reference to the
          * original function on the new function object 
          *  
          * @param {Object} oTarget Object that contains method to override
          * @param {String} sMethod Name of function to override
          * @param {Function} fAdvice Function to run before specified object method          
          */
         before: function WEF_before(oTarget,sMethodName,fn) 
         {
            var fOrigMethod = oTarget[sMethodName];

            oTarget[sMethodName] = function()
            {
               fn.apply(oTarget, arguments);
               return fOrigMethod.apply(oTarget, arguments);
            };
            oTarget[sMethodName]['fOrigMethod_before'] = fOrigMethod;
         },
         
         /**
          * Decorates method wth supplied function. Stores a reference to the
          * original function on the new function object 
          *  
          * @param {Object} oTarget Object that contains method to override
          * @param {String} sMethod Name of function to override
          * @param {Function} fAdvice Function to run before specified object method          
          */         
         after: function WEF_after(oTarget,sMethodName,fn)
         {
            var fOrigMethod = oTarget[sMethodName];
            oTarget[sMethodName] = function () 
            {
               var rv = fOrigMethod.apply(oTarget, arguments);
               return fn.apply(oTarget, [rv]);
            };
            oTarget[sMethodName]['fOrigMethod_after'] = fOrigMethod;      
         },
         
         /**
          * Decorates method wth supplied function. Stores a reference to the
          * original function on the new function object 
          *  
          * @param {Object} oTarget Object that contains method to override
          * @param {String} sMethod Name of function to override
          * @param {Array} aFn Array of functions to run before and after 
          * specified object method          
          */         
         around: function WEF_around(oTarget,sMethodName,aFn)
         {
            var fOrigMethod = oTarget[sMethodName];
            oTarget[sMethodName] = function() 
            {
               if (aFn && aFn.length==2) 
               {
                  //before
                  aFn[0].apply(oTarget, arguments);
                  //original
                  var rv = fOrigMethod.apply(oTarget, arguments);
                  //after
                  return aFn[1].apply(oTarget, [rv]);
               }
               else {
                  return fOrigMethod.apply(oTarget, arguments);
               }
            };
            if (aFn && aFn.length==2) 
            {
               oTarget[sMethodName]['fOrigMethod_around'] = fOrigMethod;
            }
         }
      };

      /**
       * 
       * @param {Object} oTarget Object that contains method to override
       * @param {Object} sAspect Name of aspect to advise
       * @param {String} sMethod Name of function to override
       * @param {Function} fAdvice Function to run before specified object method
       */
      var advise = function(oTarget,sAspect,sMethod,fAdvice) 
      {
         if (oTarget && sAspect && sMethod && fAdvice && aAspects[sAspect]) 
         {
             //decorate specified method
             aAspects[sAspect](oTarget,sMethod,fAdvice);
         }
         
         return oTarget;
      };
      
      /**
       * Decorates supplied object method with supplied function so that the 
       * function is run before the object method.
       * 
       * @param {Object} oTarget Object that contains method to override
       * @param {String} sMethod Name of function to override
       * @param {Function} fAdvice Function to run before specified object method
       * 
       * @return function 
       */
      var before = function WEF_Do_before(oTarget, sMethod, fAdvice)
      {
        return advise(oTarget,WEF.Do.BEFORE,sMethod,fAdvice);
      };
      
      /**
       * Decorates supplied object method with supplied function so that the 
       * function is run after the object method.
       * 
       * @param {Object} oTarget Object that contains method to override
       * @param {String} sMethod Name of function to override
       * @param {Function} fAdvice Function to run after specified object method
       * 
       * @return function 
       */
      var after = function WEF_Do_after(oTarget,sMethod,fAdvice)
      {
        return advise(oTarget,WEF.Do.AFTER,sMethod,fAdvice);
      };
      
      /**
       * Decorates supplied object method with supplied function so that the 
       * function is run after the object method.
       * 
       * @param {Object} oTarget Object that contains method to override
       * @param {String} sMethod Name of function to override
       * @param {Array} fAdvice Array of functions, first of which is the before
       * function and the second is the after function
       *
       * @return function
       */
      var around = function WEF_Do_around(oTarget,sMethod,aAdvices)
      {
        return advise(oTarget,WEF.Do.AROUND,sMethod,aAdvices);
      };

      /**
       * Unbinds (removes) the advice given to the specified object method
       * 
       * @param {Object} oTarget Object that contains method to override
       * @param {String} sMethod Name of function to override
       * @param {String} type Name of advice to unbind ('before', 'after' or
       * 'around')
       */
      var unbind = function WEF_Do_unbind(oTarget, sMethod, type)
      {
         var resolvedName = 'fOrigMethod_'+type;
         if (oTarget[sMethod] && oTarget[sMethod][resolvedName])
         {
            oTarget[sMethod] = oTarget[sMethod][resolvedName];
         }
      };
      return {
         before: before,
         after : after,
         around: around,
         unbind: unbind
      };
   }();
   
   WEF.Do.BEFORE = 'before';
   WEF.Do.AFTER  = 'after';
   WEF.Do.AROUND = 'around';
   
   WEF.SEPARATOR = '--';
   WEF.DEBUG = 'DEBUG';
   WEF.BEFORE_EVENT = WEF.SEPARATOR + WEF.Do.BEFORE;
   WEF.AFTER_EVENT =  WEF.SEPARATOR + WEF.Do.AFTER;

   /**
    * Sets value of specified sub cookie
    *
    * @param name {String} Name of specified sub cookie to set
    * @param value Value of specified sub-cookie
    *
    * @return
    */
   WEF.setCookieValue = function WEF_setCookieValue(rootName, name, value)
   {
      var data = Cookie.getSubs(rootName) || {};
      data[name] = value;
      return Cookie.setSubs(rootName, data);
   };
   
   /**
    * Returns value of specified sub-cookie
    *
    * @param name {String} (Optional) Name of specified sub cookie to retrieve. If no name specified then returns full cookie
    * @return Value of specified sub-cookie or full cookie if no name specified.
    */
   WEF.getCookieValue = function WEF_UI_getCookieValue(rootName, name)
   {
      return (YAHOO.lang.isUndefined(name)) ? Cookie.getSubs(rootName) : Cookie.getSub(rootName, name);
   };
   
   /**
    * Base object of all AAF UI components. Automatically fires before and after
    * Bubbling events for init() and destroy().
    * 
    * @param {Object} config Configuration object. If config contains a field
    * called setUpCustomEvents (Array) which point to object methods then those
    * methods are automatically given an around aspect which fires a before and 
    * after event for that method. The actual event name is namespaced eg
    * 'objName::beforeInit'. 'name' is a required property of the config parameter. 
    */
   WEF.Base = function WEF_Base(config)
   {
      if (config)
      {
         this.config = config;
         this.config.setUpCustomEvents = this.config.setUpCustomEvents || [];
         
         function _setupEvent(mth) 
         {
            if (!this[mth])
            {
               return;
            }
            
            var capitalizedMthName = mth.slice(0,1).toUpperCase() + mth.slice(1),
                beforeMthdName = WEF.BEFORE_EVENT + capitalizedMthName,
                afterMthdName = WEF.AFTER_EVENT + capitalizedMthName;
                
            WEF.Do.around(this,mth,
            [
               //before
               function()
               {
                  if (WEF.get('debugMode'))
                  {
                     Bubbling.fire(WEF.DEBUG + beforeMthdName,{
                        name:this.config.name,
                        obj: this
                     });                     
                  }
                  Bubbling.fire(this.config.name + beforeMthdName,{
                     name:this.config.name,
                     obj: this
                  });
                  return this;
               },
               //after
               function()
               {
                  if (WEF.get('debugMode'))
                  {
                     Bubbling.fire(WEF.DEBUG + afterMthdName,{
                        name:this.config.name,
                        obj: this
                     });                     
                  }
                  Bubbling.fire(this.config.name + afterMthdName,
                  {
                     name:this.config.name,
                     obj: this
                  });
                  return this;
               }
            ]);
         }
         
         var evts = (['init','destroy']).concat(this.config.setUpCustomEvents);
         
         for (var i=0,len=evts.length;i<len;i++)
         {
          _setupEvent.apply(this,[evts[i]]);
         }
      }
      return this;
   };

   WEF.Base.prototype = 
   {
      
      /**
       * Initialises object
       * 
       * Fires a beforeInit and afterInit event
       * @return this
       */
      init : function init()
      {
         return this;
      },
      
      /**
       * Destroys object
       * 
       * Fires a beforeDestroy and afterDestory event
       * @return this
       */      
      destroy : function destroy()
      {         
         return this;
      },
      
      /**
       * Add i18n messages to the global message store.
       * 
       * @param {String} name Name of key to use for message. Actual name is
       *                      stored with the name of the component as a prefix
       * @param {String} msg  Message value
       */
      setMessages: function(name, msg)
      {
         var name = this.config.name+'.'+ name,
             container = YAHOO.lang.isUndefined(Alfresco) ? SpringSurf.messages.global : Alfresco.messages.global;
         
         container[name] = msg;
      },
      
      /**
       * Retrieves i18n message
       * 
       * @param {String} name Key of message. Fully qualified name is prefixed 
       *                      with name of component
       */
      getMessage: function(name, namespace)
      {
         var name = (YAHOO.lang.isUndefined(namespace)) ? this.config.name+'.'+ name : namespace+'.'+name,
             container = YAHOO.lang.isUndefined(Alfresco) ? SpringSurf.messages.global : Alfresco.messages.global;
         
         return container[name] || name;
      }
   };
   YAHOO.augment(WEF.Base, YAHOO.util.AttributeProvider);
   /**
    * The Plugin object constructor. Automatically fires before and after
    * Bubbling events for activate() and deactivate() in additional to those
    * fired by WEF.Base.
    * 
    * @class WEF.Plugin
    * @constructor
    * @extends WEF.Base 
    */
   WEF.Plugin = function WEF_Plugin(config)
   {
      config.setUpCustomEvents = (['activate','deactivate']).concat(config.setUpCustomEvents || []);
      WEF.Plugin.superclass.constructor.apply(this, Array.prototype.slice.call(arguments));
   };
   
   YAHOO.extend(WEF.Plugin, WEF.Base, {
      /**
       * Activates plugin
       * 
       * Fires a beforeActivate and afterActivate event
       * @return this
       */
      activate : function activate()
      {         
         return this;
      },

      /**
       * Deactivates object
       * 
       * Fires a beforeDeactivate and afterDeActivate event
       * @return this
       */      
      deactivate : function deactivate()
      {
         return this;
      },
      
      /**
       * Container for any service instances
       */
      services: {},
      
      /**
       * Container for widget instances
       */
      widgets: {}
   });
   
   /**
    * The App widget constructor. Automatically fires before and after
    * Bubbling events for render(), show() and hide(), in additional to those
    * fired by WEF.Base.
    * 
    * @class WEF.Widget
    * @constructor
    * @extends WEF.Plugin 
    */  
   
   WEF.Widget = function WEF_Widget(config)
   {
      config.setUpCustomEvents = (['render','show','hide']).concat(config.setUpCustomEvents || []);
      WEF.Widget.superclass.constructor.apply(this, Array.prototype.slice.call(arguments));
      
      this.services = {};
      this.widgets = {};
   };
   
   YAHOO.extend(WEF.Widget, WEF.Plugin, {
      
      init: function init()
      {
         //if no element on config then widget must assign one at some point.
         if (this.config.element)
         {
            this.element = new Element(this.config.element);            
         }
      },
      
      initAttributes: function initAttributes()
      {
         
      }, 
      
     /**
       * Renders object
       * 
       * Fires a beforeRender and afterRender event
       * @return this
       */
      render : function WEF_Widget_render() 
      {
         return this;
      },
     
     
     /**
       * Shows widget
       * 
       * Fires a beforeShow and afterShow event
       * @return this
       */      
      show : function WEF_Widget_show()
      {
         Dom.addClass(this.config.id, 'wef-show');
         Dom.removeClass(this.config.id, 'wef-hide');
         return this;
      },
      
      /**
       * Hides Widget
       * 
       * Fires a beforeHide and afterHide event
       * @return this
       *
       */
      hide: function WEF_Widget_hide()
      {
         Dom.addClass(this.config.id, 'wef-hide');
         Dom.removeClass(this.config.id, 'wef-show');         
         return this;
      }      
   });
   
   /**
    * The App object constructor
    * 
    * @class WEF.App
    * @constructor
    * @extends WEF.Plugin 
    */
   WEF.App = function WEF_App(config)
   {
      WEF.App.superclass.constructor.apply(this, arguments);  
   };
   
   YAHOO.extend(WEF.App, WEF.Plugin);
   

   YAHOO.org.springframework.extensions.webeditor = WEF;   
})();
WEF.register("org.springframework.extensions.webeditor", YAHOO.org.springframework.extensions.webeditor, {version: "1.0", build: "1"});