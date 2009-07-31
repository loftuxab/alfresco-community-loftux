/**
 * Wiki markup parser. 
 * Very simple parser that converts a subset of wiki markup
 * to HTML.
 * 
 * @namespace Alfresco
 * @class Alfresco.WikiParser
 */
(function()
{
   /**
    * WikiParser constructor.
    * 
    * @return {Alfresco.WikiParser} The new parser instance
    * @constructor
    */
   Alfresco.WikiParser = function()
   {
      return this;
   };

   Alfresco.WikiParser.prototype =
   {
      /**
       * The url to use when rewriting links.
       * 
       * @property URL
       * @type String
       */
      URL : null,
      
      /**
       * Renders wiki markup.
       *
       * @method parse
       * @param test {String} The text to render
       */
      parse: function(text, pages)
      {
         pages = pages == null ? [] : pages;
         text = this._renderLinks(text, pages);
         return text;
      },
      
      /**
       * Looks for instance of [[ ]] in the text and replaces
       * them as appropriate.
       * 
       * @method _renderLinks
       * @param s {String} The text to render
       * @param pages {Array} The existing pages on the current site
       */
      _renderLinks: function(s, pages)
      {
         if (typeof s == "string")
         {
            var result = s.split("[["), text = s;
         
            if (result.length > 1)
            {
               var re = /^([^\|\]]+)(?:\|([^\]]+))?\]\]/;
               var uri, i, ii, str, matches, page, exists;
               text = result[0];
            
               for (i = 1, ii = result.length; i < ii; i++)
               {
                  str = result[i];
                  if (re.test(str))
                  {
                     matches = re.exec(str);
                     // Replace " " character in page URL with "_"
                     page = matches[1].replace(/\s+/g, "_");
                     exists = Alfresco.util.arrayContains(pages, page);
                     uri = '<a href="' + this.URL + page + '" class="' + (exists ? 'theme-color-1' : 'wiki-missing-page') + '">';
                     uri += (matches[2] ? matches[2] : matches[1]);
                     uri += '</a>';
                  
                     text += uri;
                     text += str.substring(matches[0].length);
                  }
               }
            }   
            return text;
         }
         return s;
      }
      
   };
   
})();