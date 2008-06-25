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
		parse: function(text)
		{
			var text = this._renderLinks(text);
			return text;
		},
		
		/**
		 * Looks for instance of [[ ]] in the text and replaces
		 * them as appropriate.
		 * 
		 * @method _renderLinks
		 * @param text {String} The text to render
		 */
		_renderLinks: function(s)
		{
			var result = s.split("[[");
			var text = result[0];
			
			if (result.length > 1)
			{
				var re = /^([^\|\]]+)(?:\|([^\]]+))?\]\]/;
				var uri;
				for (var i=1; i < result.length; i++)
				{
					var str = result[i];
					if (re.test(str))
					{
						var matches = re.exec(str);
						uri = '<a href="' + this.URL + matches[1].replace(/\s+/g, "_") + '">';
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