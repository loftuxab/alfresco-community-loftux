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
         var text = this._renderLinks(text, pages);
         text = this._renderEmphasizedText(text);
         return text;
      },
      
      /**
       * Takes any text contained between "*" and generates an <em> tag in HTML.
       * e.g. *example* is rendered as <em>example</em>
       * Gets applied to all instances in the text.
       *
       * @method _renderEmphasizedText
       * @param s {String} The text to apply formatting to 
       */
      _renderEmphasizedText: function(s)
      {
         var re = /\*([^\*]+)\*/g;
         return s.replace(re, "<em>$1</em>");
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
         var result = s.split("[[");
         
         if (result.length > 1)
         {
            var re = /^([^\|\]]+)(?:\|([^\]]+))?\]\]/;
            var uri, text = result[0], i, ii, str, matches, page, exists;
            
            for (i = 1, ii = result.length; i < ii; i++)
            {
               str = result[i];
               if (re.test(str))
               {
                  matches = re.exec(str);
                  // Replace " " charcter in page URL with "_"
                  page = matches[1].replace(/\s+/g, "_");
                  exists = Alfresco.util.arrayContains(pages, page);
                  uri = '<a href="' + this.URL + page + '"' + (!exists ? ' class="wiki-missing-page"' : '') + '>';
                  uri += (matches[2] ? matches[2] : matches[1]);
                  uri += '</a>';
                  
                  text += uri;
                  text += str.substring(matches[0].length);
               }
            }
         }   
         return text;
      }
      
   };
   
})();