/*
 * @class Ext.ux.ManagedIFrame
 * Version:  RC2.01 (Release Candidate 2.01)
 * Author: Doug Hendricks. doug[always-At]theactivegroup.com
 * Copyright 2007-2008, Active Group, Inc.  All rights reserved.
 *
 ************************************************************************************
 *   This file is distributed on an AS IS BASIS WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE.
 ************************************************************************************

 License: ux.ManagedIFrame and ux.ManagedIFramePanel are licensed under the terms of
 the Open Source LGPL 3.0 license.  Commercial use is permitted to the extent
 that the code/component(s) do NOT become part of another Open Source or Commercially
 licensed development library or toolkit without explicit permission.

 Donations are welcomed: http://donate.theactivegroup.com

 License details: http://www.gnu.org/licenses/lgpl.html

 * <p> An Ext harness for iframe elements.

  Adds Ext.UpdateManager(Updater) support and a compatible 'update' method for
  writing content directly into an iFrames' document structure.

  Signals various DOM/document states as the frames content changes with 'domready',
  'documentloaded', and 'exception' events.  The domready event is only raised when
  a proper security context exists for the frame's DOM to permit modification.
  (ie, Updates via Updater or documents retrieved from same-domain servers).

  Frame sand-box permits eval/script-tag writes of javascript source.
  (See execScript, writeScript, and loadFunction methods for more info.)

  * Usage:<br>
   * <pre><code>
   * // Harnessed from an existing Iframe from markup:
   * var i = new Ext.ux.ManagedIFrame("myIframe");
   * // Replace the iFrames document structure with the response from the requested URL.
   * i.load("http://myserver.com/index.php", "param1=1&amp;param2=2");

   * // Notes:  this is not the same as setting the Iframes src property !
   * // Content loaded in this fashion does not share the same document namespaces as it's parent --
   * // meaning, there (by default) will be no Ext namespace defined in it since the document is
   * // overwritten after each call to the update method, and no styleSheets.
  * </code></pre>
  * <br>
   * @cfg {Boolean/Object} autoCreate True to auto generate the IFRAME element, or a {@link Ext.DomHelper} config of the IFRAME to create
   * @cfg {String} html Any markup to be applied to the IFRAME's document content when rendered.
   * @cfg {Object} loadMask An {@link Ext.LoadMask} config or true to mask the iframe while using the update or setSrc methods (defaults to false).
   * @cfg {Object} src  The src attribute to be assigned to the Iframe after initialization (overrides the autoCreate config src attribute)
   * @constructor

    * @param {Mixed} el, Config object The iframe element or it's id to harness or a valid config object.

 */

Ext.ux.ManagedIFrame = function(){
    var args=Array.prototype.slice.call(arguments, 0)
        ,el = Ext.get(args[0])
        ,config = args[0];


    if(el && el.dom && el.dom.tagName == 'IFRAME'){
            config = args[1] || {};
    }else{
            config = args[0] || args[1] || {};
            el = config.autoCreate?
            Ext.get(Ext.DomHelper.append(config.autoCreate.parent||document.body, Ext.apply({tag:'iframe', src:(Ext.isIE&&Ext.isSecure)?Ext.SSL_SECURE_URL:''},config.autoCreate))):null;
    }

    if(!el || el.dom.tagName != 'IFRAME') return el;

    !!el.dom.name.length || (el.dom.name = el.dom.id); //make sure there is a valid frame name

    this.addEvents({
       /**
         * @event domready
         * Fires ONLY when an iFrame's Document(DOM) has reach a state where the DOM may be manipulated (ie same domain policy)
         * @param {Ext.ux.ManagedIFrame} this
         * Note: This event is only available when overwriting the iframe document using the update method and to pages
         * retrieved from a "same domain".
         * Returning false from the eventHandler stops further event (documentloaded) processing.
         */
        "domready"       : true,

       /**
         * @event documentloaded
         * Fires when the iFrame has reached a loaded/complete state.
         * @param {Ext.ux.ManagedIFrame} this
         */
        "documentloaded" : true,

        /**
         * @event exception
         * Fires when the iFrame raises an error
         * @param {Ext.ux.ManagedIFrame} this
         * @param {Object/string} exception
         */
        "exception" : true
    });

    if(config.listeners){
        this.listeners=config.listeners;
        Ext.ux.ManagedIFrame.superclass.constructor.call(this);
    }

    Ext.apply(el,this);  // apply this class interface ( pseudo Decorator )

    el.addClass('x-managed-iframe');
    if(config.style){
        el.applyStyles(config.style);
    }

    el.loadMask = Ext.apply({msg:'Loading..',msgCls:'x-mask-loading',maskEl:null, hideOnReady:true, enabled:!!config.loadMask},config.loadMask);

    //Hook the Iframes loaded state handler
    el._eventName = Ext.isIE?'onreadystatechange':'onload';
    el._windowContext = null;
    el.dom[el._eventName] = el.loadHandler.createDelegate(el);

    if(document.addEventListener){  //for Gecko and Opera and any who might support it soon.
       Ext.EventManager.on(window,"DOMFrameContentLoaded", el.dom[el._eventName]);
    }

    var um = el.updateManager=new Ext.UpdateManager(el,true);
    um.showLoadIndicator= config.showLoadIndicator || false;


    if(config.src){
        el.setSrc(config.src);
    }else{
        el.src = el.dom.src||null;
        var content = config.html || config.content || false;

        if(content){
            el.update(content);
        }
    }

    return el;
};

Ext.extend(Ext.ux.ManagedIFrame , Ext.util.Observable,
    {
          /**
      * Sets the embedded Iframe src property.

      * @param {String/Function} url (Optional) A string or reference to a Function that returns a URI string when called
      * @param {Boolean} discardUrl (Optional) If not passed as <tt>false</tt> the URL of this action becomes the default SRC attribute for
      * this iframe, and will be subsequently used in future setSrc calls (emulates autoRefresh by calling setSrc without params).
      * Note:  invoke the function with no arguments to refresh the iframe based on the current defaultSrc value.
     */
    setSrc : function(url, discardUrl, callback){
          var reset = Ext.isIE&&Ext.isSecure?Ext.SSL_SECURE_URL:''
          var src = url || this.src || reset;

          if(Ext.isOpera){
              this.dom.src=reset;
           }
          this._windowContext = null;
          this._hooked = this._domReady = this._domFired = false;
          this._callBack = callback || false;

          this.showMask();

          (function(){
                var s = typeof src == 'function'?src()||'':src;
                try{
                    this._frameAction = true;//signal listening now
                    this.dom.src = s;
                    this.checkDOM();
                }catch(ex){ this.fireEvent('exception', this, ex); }

          }).defer(10,this);

          if(discardUrl !== true){ this.src = src; }

          return this;

    },
    //Private: script removal RegeXp
    scriptRE  : /(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/gi
    ,
    /*
     * Write(replacing) string content into the IFrames document structure
     * @param {String} content The new content
     * @param {Boolean} loadScripts (optional) true to also render and process embedded scripts
     * @param {Function} callback (optional) Callback when update is complete.
     */
    update : function(content,loadScripts,callback){

        loadScripts = loadScripts || this.getUpdateManager().loadScripts || false;

        content = Ext.DomHelper.markup(content||'');

        var doc = this.getDocument();
        if(doc){
            this._windowContext = null;
            this._callBack = callback || false;
            this._frameAction = !!content.length;
            this._hooked = this._domReady = this._domFired = false;
            this.showMask();

            doc.open();
            doc.write(loadScripts===true ? content:content.replace(this.scriptRE , ""));
            doc.close();

            if(this._frameAction){
                this.checkDOM();
            } else if(callback){
                callback();
            }

        }
        return this;
    },
    //Private execScript sandbox
    _renderHook : function(){

        this._windowContext = null;
        this._hooked = false;
        try{
           return this.writeScript( String.format('(function(){parent.Ext.get("{0}")._windowContext={1};})();'
                                 ,this.dom.id,(Ext.isIE?'window':'{eval:function(s){return eval(s);}}')))
                                 && this.domWritable();
          }catch(ex){}
        return false;

    },
    _windowContext : null,
    /*
      Return the Iframes document object
    */
    getDocument:function(){
        return this.getWindow()?this.getWindow().document:null;
    },

    /*
     Return the Iframes window object
    */
    getWindow:function(){
        var dom= this.dom;
        return dom?dom.contentWindow||window.frames[dom.name]:null;
    },

    /*
     Print the contents of the Iframes (if we own the document)
    */
    print:function(){
        try{
            var win = this.getWindow();
            if(Ext.isIE){win.focus();}
            win.print();
        } catch(ex){
            throw 'print exception: ' + (ex.description || ex.message || ex);
        }
    },
    //private
    destroy:function(){
        this.removeAllListeners();

        if(this.dom){
             //unHook the Iframes loaded state handlers
             if(document.addEventListener){ //Gecko/Opera
                Ext.EventManager.un(window,"DOMFrameContentLoaded", this.dom[this._eventName]);
               }
             this.dom[this._eventName]=null;

            //IE Iframe cleanup
             if(this.dom.src){
                this.dom.src = 'javascript:false';
             }
             Ext.removeNode(this.dom);
        }
        this._windowContext = null;
        Ext.apply(this.loadMask,{masker :null ,maskEl : null});
    }
    /* Returns the general DOM modification capability of the frame. */
    ,domWritable  : function(){
        return !!this._windowContext;
    }
    /*
     *  eval a javascript code block(string) within the context of the Iframes window object.
     * @param {String} block A valid ('eval'able) script source block.
     * @param {Boolean} useDOM - if true inserts the fn into a dynamic script tag,
     *                           false does a simple eval on the function definition. (useful for debugging)
     * <p> Note: will only work after a successful iframe.(Updater) update
     *      or after same-domain document has been hooked, otherwise an exception is raised.
     */
    ,execScript: function(block, useDOM){
      try{
        if(this.domWritable()){
            return useDOM?
            this.writeScript(block) :
            this._windowContext.eval(block);

        }else{ throw 'execScript:non-secure context' }
       }catch(ex){
            this.fireEvent('exception', this, ex);
        }
    }
    /*
     *  write a <script> block into the iframe's document
     * @param {String} block A valid (executable) script source block.
     * @param {object} attributes Additional Script tag attributes to apply to the script Element (for other language specs [vbscript, Javascript] etc.)
     * <p> Note: writeScript will only work after a successful iframe.(Updater) update
     *      or after same-domain document has been hooked, otherwise an exception is raised.
     */
    ,writeScript  : function(block, attributes) {
         attributes = Ext.apply({},attributes||{},{type :"text/javascript",text:block });

         try{
            var head,script, doc = this.getDocument();
            if(doc && doc.getElementsByTagName){
                if((head = doc.getElementsByTagName("head")[0]) && (script = doc.createElement("script"))){
                    head.appendChild(script);
                    for(var attrib in attributes){
                        if(attributes.hasOwnProperty(attrib)){
                            script[attrib] = attributes[attrib];
                        }
                    }
                    return true;
                }
            }
         }catch(ex){ this.fireEvent('exception', this, ex);}
         return false;
    }
    /*
     * Eval a function definition into the iframe window context.
     * args:
     * @param {String/Object} name of the function or
                              function map object: {name:'encodeHTML',fn:Ext.util.Format.htmlEncode}
     * @param {Boolean} useDOM - if true inserts the fn into a dynamic script tag,
                                    false does a simple eval on the function definition,
     * examples:
     * var trim = function(s){
     *     return s.replace( /^\s+|\s+$/g,'');
     *     };
     * iframe.loadFunction('trim');
     * iframe.loadFunction({name:'myTrim',fn:String.prototype.trim || trim});
     */
    ,loadFunction : function(fn, useDOM, invokeIt){

       var name  =  fn.name || fn;
       var    fn =  fn.fn   || window[fn];
       this.execScript(name + '=' + fn, useDOM); //fn.toString coercion
       if(invokeIt){
           this.execScript(name+'()') ; //no args only
        }
    }
    ,loadMask: {msg:'Loading..',msgCls:'x-mask-loading',maskEl:null, enabled:false}

    //Private
    ,showMask: function(msg,msgCls,forced){

          var lmask;
          if((lmask = this.loadMask) && (lmask.enabled || forced)){
               if(lmask._vis)return;

               lmask.masker || (lmask.masker = Ext.get(lmask.maskEl||this.dom.parentNode||this.wrap({tag:'div',style:{position:'relative'}})));
               //lmask.masker.repaint();
               lmask._vis = true;
               lmask.masker.mask(msg||lmask.msg , msgCls||lmask.msgCls );
           }
       }
    //Private
    ,hideMask: function(forced){
           var tlm ;

           if((tlm = this.loadMask) && (tlm.enabled || forced) && tlm.masker ){
               if(!tlm._vis){return;}
               if(!forced && (tlm.hideOnReady!==true && this._domReady)){return;}
               tlm._vis = false;
               tlm.masker.unmask();
           }
    }

    /* Private
      Evaluate the Iframes readyState/load event to determine its 'load' state,
      and raise the 'domready/documentloaded' event when applicable.
    */
    ,loadHandler : function(e){

        if(!this._frameAction ){return;}
        var rstatus = (e && typeof e.type !== 'undefined'?e.type:this.dom.readyState );

        switch(rstatus){
            case 'loading':  //IE
              break;
            case 'interactive': //IE
              this.showMask();
              break;
            case 'DOMFrameContentLoaded': //Gecko

              if(this._domFired || (e && e.target !== this.dom)){ return;} //not this frame.

            case 'domready': //MIF
              if(this._domFired)return;
              if(this._domFired = this._hooked = this._renderHook() ){
                 this._frameAction = (this.fireEvent("domready",this) === false?false:this._frameAction);  //Only raise if sandBox injection succeeded (same domain)
              }
            case 'domfail': //MIF
              this._domReady = true;
              this.hideMask();
              break;
            case 'load': //Gecko, Opera
            case 'complete': //IE
              if(!this._domFired ){  // one last try for slow DOMS.
                  this.loadHandler({type:'domready'});
              }
              if(this._frameAction){
                this.fireEvent("documentloaded",this);
              }
              this._frameAction = false;
              this.hideMask(true);

              if(this._callBack){
                   this._callBack(this);
              }

              break;
            default:
        }
    }
    /* Private
      Poll the Iframes document structure to determine DOM ready state,
      and raise the 'domready' event when applicable.
    */
    ,checkDOM : function(win){

        if(Ext.isOpera || Ext.isGecko){ return;}

        //initialise the counter
        var n = 0
            ,win = win||this.getWindow()
            ,manager = this
            ,domReady = false;

            var max = 100;

            var poll =  function(){  //DOM polling for IE and others
               try{
                 domReady  =false;
                 var doc = win.document,body;
                 domReady = (doc && doc.getElementsByTagName);
                 domReady = domReady && (body = doc.getElementsByTagName('body')[0]) && !!body.innerHTML.length;
               }catch(ex){
                     n = max; //likely same-domain policy violation
               }

                //if the timer has reached 100 (timeout after 3 seconds)
                //in practice, shouldn't take longer than 7 iterations [in kde 3
                //in second place was IE6, which takes 2 or 3 iterations roughly 5% of the time]

                if(!manager._frameAction || manager._domReady)return;

                if(n++ < max && !domReady )
                {
                    //try again
                    setTimeout(arguments.callee, 30);
                    return;
                }
                manager.loadHandler ({type:domReady?'domready':'domfail'});

            };

            poll.defer(50);


    }

 });


 /*
  * @class Ext.ux.ManagedIFramePanel
  * Version:  RC 2.01 checkDom defer adjustment.
  *     Improved domready,documentloaded, exception(new) event handling
  * Version:  RC 2
  *     Improved domready,documentloaded, exception(new) event handling
  *     Added getFrame, getFrameWindow, getFrameDocument, loadFunction, writeScript, and domWritable members to MIF
  * Version:  RC1.1
  *     Modified default bodyCfg property for IE6 secure pages
  *     Added getFrame, getFrameWindow, and getFrameDocument members to MIFP
  * Version:  RC1
  *     Adds unsupportedText property to render an element/text indicating lack of Iframe support
  *     Improves el visibility/display support when hiding panels (FF does not reload iframe if using visibility mode)
  *     Adds custom renderer definition to autoLoad config.
  * Version:  0.16
  *     fixed (inherited)panel destroy bugs and iframe cleanup. (now, no orphans/leaks for IE).
  *     added loadMask.enabled = (true/false) toggle
  *     Requesting the Panel.getUpdater now returns the Updater for the Iframe.
  *     MIP.load modified to load content into panel.iframe (rather than panel.body)
  * Version:  0.15
  *     enhanced loadMask.maskEl support to support panel element names ie: 'body, bwrap' etc
  * Version:  0.13
  *     Added loadMask support and refactored domready/documentloaded events
  * Version:  0.11
  *     Made Panel state-aware.
  * Version:  0.1
  * Author: Doug Hendricks 12/2007 doug[always-At]theactivegroup.com
  *
  *
 */
 Ext.ux.ManagedIframePanel = Ext.extend(Ext.Panel, {

    /**
    * Cached Iframe.src url to use for refreshes. Overwritten every time setSrc() is called unless "discardUrl" param is set to true.
    * @type String/Function (which will return a string URL when invoked)
     */
    defaultSrc  :null,
    bodyStyle   :{height:'100%',width:'100%',overflow:'visible'},
    /**
    * @cfg {String/Object} iframeStyle
    * Custom CSS styles to be applied to the ux.ManagedIframe element in the format expected by {@link Ext.Element#applyStyles}
    * (defaults to {overflow:'auto'}).
    */
    frameStyle  : {overflow:'auto'},
    loadMask    : false,
    animCollapse: false,
    autoScroll  : false,
    closable    : true, /* set True by default in the event a site times-out while loadMasked */
    ctype       : "Ext.ux.ManagedIframePanel",
    showLoadIndicator : false,

    /**
    *@cfg {String/Object} unsupportedText Text (or Ext.DOMHelper config) to display within the rendered iframe tag to indicate the frame is not supported
    */
    unsupportedText : {tag:'span'
                      ,cls:'x-error-noframes'
                      ,html:'Inline frames are NOT enabled\/supported by your browser.'
    },

    initComponent : function(){

        var unsup =this.unsupportedText?{cn:this.unsupportedText}:false;

        this.bodyCfg ||
           (this.bodyCfg =
               {tag:'div'
               ,cls:'x-panel-body'
               ,children:[Ext.apply(
                          {tag          :'iframe',
                           frameborder  : 0,
                           cls          : 'x-managed-iframe',
                           style        : Ext.apply({height:'100%',width:'100%'},this.frameStyle || this.iframeStyle || {})
                          },unsup , Ext.isIE&&Ext.isSecure?{src:Ext.SSL_SECURE_URL}:false )
                          ]
           });

         Ext.ux.ManagedIframePanel.superclass.initComponent.call(this);
         this.addEvents({documentloaded:true, domready:true});

         if(this.defaultSrc){
            this.on('render', this.setSrc.createDelegate(this,[this.defaultSrc],0), this, {single:true});
        }
    },

      // private
    beforeDestroy : function(){

        if(this.rendered){

             if(this.tools){
                for(var k in this.tools){
                      Ext.destroy(this.tools[k]);
                }
             }

             if(this.header && this.headerAsText){
                var s;
                if( s=this.header.child('span')) s.remove();
                this.header.update('');
             }

             Ext.each(['iframe','header','topToolbar','bottomToolbar','footer','loadMask','body','bwrap'],
                function(elName){
                  if(this[elName]){
                    if(typeof this[elName].destroy == 'function'){
                         this[elName].destroy();
                    } else { Ext.destroy(this[elName]); }

                    this[elName] = null;
                    delete this[elName];
                  }
             },this);
        }

        Ext.ux.ManagedIframePanel.superclass.beforeDestroy.call(this);
    },
    onDestroy : function(){
        //Yes, Panel.super (Component), since we're doing Panel cleanup beforeDestroy instead.
        Ext.Panel.superclass.onDestroy.call(this);
    },
    // private
    onRender : function(ct, position){
        Ext.ux.ManagedIframePanel.superclass.onRender.call(this, ct, position);

        if(this.iframe = this.body.child('iframe.x-managed-iframe')){

            // Set the Visibility Mode for el, bwrap for collapse/expands/hide/show
            Ext.each(
                [this[this.collapseEl],this.el,this.iframe]
                ,function(el){
                     el.setVisibilityMode(Ext.Element[(this.hideMode||'display').toUpperCase()] || 1).originalDisplay = (this.hideMode != 'display'?'visible':'block');
            },this);

            if(this.loadMask){
                this.loadMask = Ext.apply({enabled  :true
                                          ,maskEl   :this.body
                                          ,hideOnReady:true}
                                          ,this.loadMask);
             }

            if(this.iframe = new Ext.ux.ManagedIFrame(this.iframe, {
                    loadMask           :this.loadMask
                   ,showLoadIndicator  :this.showLoadIndicator
                   //,style              :(this.iframeStyle || this.frameStyle)
                   })){

                this.loadMask = this.iframe.loadMask;
                this.iframe.ownerCt = this;

                this.relayEvents(this.iframe, ["documentloaded","domready","exception"]);
            }
            this.body.repaint();
            this.getUpdater().showLoadIndicator = this.showLoadIndicator || false;

        }
    },
        // private
    afterRender : function(container){
        var html = this.html;
        delete this.html;
        Ext.ux.ManagedIframePanel.superclass.afterRender.call(this);
        if(html && this.iframe){
            this.iframe.update(typeof html == 'object' ? Ext.DomHelper.markup(html) : html);
        }

    },
    /**
    * Sets the embedded Iframe src property.
    * @param {String/Function} url (Optional) A string or reference to a Function that returns a URI string when called
    * @param {Boolean} discardUrl (Optional) If not passed as <tt>false</tt> the URL of this action becomes the default URL for
    * this panel, and will be subsequently used in future setSrc calls.
    * Note:  invoke the function with no arguments to refresh the iframe based on the current defaultSrc value.
    */
    setSrc : function(url, discardUrl,callback){
         var u = url || this.defaultSrc;

         if(typeof u == 'object'){
            url = u.url || false;
            callback = u.callback || false;
         }
         var src = url || (Ext.isIE&&Ext.isSecure?Ext.SSL_SECURE_URL:'');

         if(this.rendered && this.iframe){
              this.iframe.setSrc(src,discardUrl,callback);
           }
         if(discardUrl !== true){ this.defaultSrc = {url:src,callback:callback}; } //normalize
         this.saveState();
         return this;
    },

    //Make it state-aware
    getState: function(){

         var f = (this.defaultSrc.url||this.defaultSrc );
         return Ext.apply(Ext.ux.ManagedIframePanel.superclass.getState.call(this) || {},
             {defaultSrc  : typeof f == 'function'?f():f}
             );

    },
    /**
     * Get the {@link Ext.Updater} for this panel's iframe/or body. Enables you to perform Ajax-based document replacement of this panel's iframe document.
     * @return {Ext.Updater} The Updater
     */
    getUpdater : function(){
        return this.rendered?(this.iframe||this.body).getUpdater():null;
    },
    /**
     * Get the embedded iframe Ext.Element for this panel
     * @return {Ext.Element} The Panels ux.ManagedIFrame instance.
     */
    getFrame : function(){
        return this.rendered?this.iframe:null
    },
    /**
     * Get the embedded iframe's window object
     * @return {Object} or Null if unavailable
     */
    getFrameWindow : function(){
        return this.rendered && this.iframe?this.iframe.getWindow():null
    },
    /**
     * Get the embedded iframe's document object
     * @return {Object} or null if unavailable
     */
    getFrameDocument : function(){
        return this.rendered && this.iframe?this.iframe.getDocument():null
    },
     /**
      * Loads this panel's iframe immediately with content returned from an XHR call.
      * @param {Object/String/Function} config A config object containing any of the following options:
    <pre><code>
    panel.load({
        url: "your-url.php",
        params: {param1: "foo", param2: "bar"}, // or a URL encoded string
        callback: yourFunction,
        scope: yourObject, // optional scope for the callback
        discardUrl: false,
        nocache: false,
        text: "Loading...",
        timeout: 30,
        scripts: false,
        renderer:{render:function(el, response, updater, callback){....}}  //optional custom renderer
    });
    </code></pre>
         * The only required property is url. The optional properties nocache, text and scripts
         * are shorthand for disableCaching, indicatorText and loadScripts and are used to set their
         * associated property on this panel Updater instance.
         * @return {Ext.Panel} this
         */
    load : function(loadCfg){
         var um;
         if(um = this.getUpdater()){
            if (loadCfg && loadCfg.renderer) {
                 um.setRenderer(loadCfg.renderer);
                 delete loadCfg.renderer;
            }
            um.update.apply(um, arguments);
         }
         return this;
    }
     // private
    ,doAutoLoad : function(){
        this.load(
            typeof this.autoLoad == 'object' ?
                this.autoLoad : {url: this.autoLoad});
    }
    // private
    ,onShow : function(){
        if(this.iframe)this.iframe.setVisible(true);
        Ext.ux.ManagedIframePanel.superclass.onShow.call(this);
    }

    // private
    ,onHide : function(){
        if(this.iframe)this.iframe.setVisible(false);
        Ext.ux.ManagedIframePanel.superclass.onHide.call(this);
    }
});

Ext.reg('iframepanel', Ext.ux.ManagedIframePanel);

Ext.ux.ManagedIframePortlet = Ext.extend(Ext.ux.ManagedIframePanel, {
     anchor: '100%',
     frame:true,
     collapseEl:'bwrap',
     collapsible:true,
     draggable:true,
     cls:'x-portlet'
 });
Ext.reg('iframeportlet', Ext.ux.ManagedIframePortlet);