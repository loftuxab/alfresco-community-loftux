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

/**
 * RulesHeader template.
 *
 * @namespace Alfresco
 * @class Alfresco.RulesHeader
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;
   
   /**
    * RulesHeader constructor.
    *
    * @param {String} el The HTML id of the parent element
    * @return {Alfresco.RulesHeader} The new RulesHeader instance
    * @constructor
    */
   Alfresco.Location = function Location_constructor(el)
   {
      if (YAHOO.lang.isString(el))
      {
         el = Dom.get(el);
      }
      else if (!el.getAttribute("id"))
      {
         Alfresco.uti.generateDomId(el);
      }

      Alfresco.Location.superclass.constructor.call(this, "Alfresco.Location", el.getAttribute("id"), ["json"]);

      // Save references to dom object
      this.widgets.spanEl = el;

      return this;
   };

   YAHOO.extend(Alfresco.Location, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Repository's rootNode
          *
          * @property rootNode
          * @type Alfresco.util.NodeRef
          */
         rootNode: null,

         /**
          * Current siteId (if any).
          *
          * @property siteId
          * @type string
          */
         siteId: ""
      },

      /**
       * The locations object representing the current location
       *
       * @property _locations
       * @type object
       */
      _locations: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Location_onReady()
      {
      },

      /**
       * Set nodeRef, will lookup the cntext of the nodeRef and display the result depending
       * on the scope of the options (site and rootNode).
       *
       * @method displayByNodeRef
       * @param nodeRef {Alfresco.util.NodeRef|string}
       */
      displayByNodeRef: function Location_displayByNodeRef(nodeRef)
      {
         // Find the path for the nodeRef
         if (YAHOO.lang.isString(nodeRef))
         {
            nodeRef = Alfresco.util.NodeRef(nodeRef);
         }
         if (nodeRef)
         {
            var url = Alfresco.constants.PROXY_URI + "slingshot/doclib/node/" + nodeRef.uri + "/path";
            if (this.options.siteId == "" && this.options.rootNode)
            {
               // Repository mode
               url += "?libraryRoot=" + encodeURIComponent(this.options.rootNode.toString());
            }
            Alfresco.util.Ajax.jsonGet(
            {
               url: url,
               successCallback:
               {
                  fn: function(response)
                  {
                     if (response.json !== undefined)
                     {
                        var locations = response.json;
                        this._locations = locations;
                        if (locations.site)
                        {
                           this.displayByPath($combine(locations.site.path, locations.site.file), locations.site.site, locations.site.siteTitle);
                        }
                        else
                        {
                           this.displayByPath($combine(locations.repo.path, locations.repo.file));
                        }

                        YAHOO.Bubbling.fire("itemLocationLoaded",
                        {
                           eventGroup: this,
                           locations: locations
                        });

                     }
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function(response)
                  {
                     if (this.widgets.spanEl)
                     {
                        this.widgets.spanEl.innerHTML = '<span class="location error">' + this.msg("message.failure") + '</span>';
                     }
                  },
                  scope: this
               }
            });
         }
         else
         {
            this.widgets.spanEl.innerHTML = '<span class="location-none">' + this.msg("location.label.none") + '</span>';
         }
      },

      /**
       * Will render the location
       *
       * @method displayByPath
       * @param fullPath {string}
       * @param siteId {string}
       * @param siteTitle {string}
       */
      displayByPath: function Location_displayByPath(fullPath, siteId, siteTitle)
      {
         this._locations = null;
         if (this.widgets.spanEl)
         {
            this.widgets.spanEl.innerHTML = this.generateHTML(fullPath, siteId, siteTitle);
         }
      },
      
      /**
       * Create html that represent a path and site
       *
       * @method generateHTML
       * @param fullPath
       * @param siteId
       * @param siteTitle
       * @return {string} html respresenting path and site as span elements
       */
      generateHTML: function Location_generateHTML(fullPath, siteId, siteTitle)
      {
         var i = fullPath.lastIndexOf("/"),
            path = i >= 0 ? fullPath.substring(0, i + 1) : "",
            name = i >= 0 ? fullPath.substring(i + 1) : fullPath;
         if (siteId)
         {
            if (Alfresco.util.arrayContains(["/", ""], name + path))
            {
               fullPath = this.msg("location.path.documents");
               name = this.msg("location.path.documents");
            }
            else
            {
               fullPath = this.msg("location.path.documents") + fullPath;
               name = ".../" + name;
            }
         }
         else
         {
            if (Alfresco.util.arrayContains(["/", ""], name + path))
            {
               fullPath = this.msg("location.path.repository");
               name = this.msg("location.path.repository");
            }
            else
            {
               fullPath = this.msg("location.path.repository") + fullPath;
               name = ".../" + name;
            }
         }
         var pathHtml = '<span class="location-path" title="' + this.msg("location.tooltip.path", fullPath) + '">' + $html(name) + '</span>';
         if (siteId)
         {
            if (siteId && siteId != this.options.siteId)
            {
               var siteHtml = '<span class="location-site" title="' + this.msg("location.tooltip.site", siteTitle ? siteTitle : siteId) + '">' + $html(siteTitle ? siteTitle : siteId) + '</span>';
               return this.msg("location.label.site", pathHtml, siteHtml);
            }
            else
            {
               return this.msg("location.label.local", pathHtml);
            }
         }
         else
         {
            return this.msg("location.label.repository", pathHtml);
         }
      }

   });
})();
