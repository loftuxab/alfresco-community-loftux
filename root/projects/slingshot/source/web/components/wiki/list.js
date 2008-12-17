/*
 *** Alfresco.WikiList
*/
(function()
{
   Alfresco.WikiList = function(containerId)
   {
	   this.name = "Alfresco.WikiList";
      this.id = containerId;
      this.options = {};

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "editor", "tabview"], this.componentsLoaded, this);
      return this;
   };
   
   Alfresco.WikiList.prototype = 
   {
      _selectedTag: "",

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * The pages on this sites wiki.
          *
          * @property pages
          * @type Array
          */
         pages: []

      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DocListToolbar} returns 'this' for method chaining
       */
      setOptions: function DLTB_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);

         // Make sure the parser is using the current site
         this.$parser = new Alfresco.WikiParser();
         this.$parser.URL = Alfresco.constants.URL_CONTEXT + "page/site/" + this.options.siteId + "/wiki-page?title=";

         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
       componentsLoaded: function()
       {
          YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
       },
       
       /**
        * Fired by YUI when parent element is available for scripting.
        * Initialises components, including YUI widgets.
        *
        * @method init
        */
       init: function()
       {
          this._initMouseOverListeners();
          
          // Render any mediawiki markup
          // TODO: look at doing this on the server
          var divs = YAHOO.util.Dom.getElementsByClassName('pageCopy', 'div');
          var div;
          for (var i=0; i < divs.length; i++)
          {
             div = divs[i];
             div.innerHTML = this.$parser.parse(div.innerHTML, this.options.pages);
          }
          
          YAHOO.Bubbling.addDefaultAction('delete-link', function(layer, args)
          {
             var link = args[1].target;
             if (link)
             {
                var title, node;
                // Search for the "title" attribute as that has the page title
                for (var i = 0, ii = link.attributes.length; i < ii; i++)
                {
                   node = link.attributes[i];
                   if (node.nodeName.toLowerCase() === 'title')
                   {
                      title = node.nodeValue;
                      break; 
                   }
                }
                
                if (title)
                {
                   // Trigger the delete dialog in the toolbar
                   YAHOO.Bubbling.fire('deletePage',
                   {
                      title: title
                   });
                }
             }
             
             return true;
          });          

          YAHOO.Bubbling.addDefaultAction('wiki-tag-link', function(layer, args)
          {
             var link = args[1].target;
             if (link)
             {
                var tagName = link.firstChild.nodeValue;
                YAHOO.Bubbling.fire("tagSelected", {
                   "tagname": tagName
                });
             }
             return true;
          });

          YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);
       },
       
       onTagSelected: function(e, args)
       {
          var Dom = YAHOO.util.Dom;
          var tagname = args[1].tagname;
          
          // TODO: do something with the tag name for all tags
          if (tagname === "All Tags")
          {
             var divs = Dom.getElementsByClassName('wikiPageDeselect', 'div');
             for (var i=0; i < divs.length; i++)
             {
                Dom.removeClass(divs[i], 'wikiPageDeselect');
             }
             
             this._tagSelected = "";
          }
          else if (this._tagSelected !== tagname) 
          {
             var divs = Dom.getElementsByClassName('wikipage', 'div');
             var div;
             for (var x=0; x < divs.length; x++) {
                div = divs[x];
             
                if (Dom.hasClass(div, 'wikiPageDeselect'))
                {
                   Dom.removeClass(div, 'wikiPageDeselect');
                }
             
                if (!Dom.hasClass(div, 'wp-' + tagname)) 
                {
                   Dom.addClass(divs[x], 'wikiPageDeselect');
                }
             }
          
             this._tagSelected = tagname;
          }
       },       
       
       _initMouseOverListeners: function()
       {
          var divs = YAHOO.util.Dom.getElementsByClassName('wikipage', 'div');
          for (var x=0; x < divs.length; x++) {
             YAHOO.util.Event.addListener(divs[x], 'mouseover', this.mouseOverHandler);
             YAHOO.util.Event.addListener(divs[x], 'mouseout', this.mouseOutHandler);
          }
       },
       
       mouseOverHandler: function(e)
       {
          var currentTarget = e.currentTarget;
          YAHOO.util.Dom.addClass(currentTarget, 'wikiPageSelected');
       },
       
       mouseOutHandler: function(e)
       {
          var currentTarget = e.currentTarget;
          YAHOO.util.Dom.removeClass(currentTarget, 'wikiPageSelected');
       }
      
      
   };
   
})();   
