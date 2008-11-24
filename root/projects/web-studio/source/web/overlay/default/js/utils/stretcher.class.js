if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.Stretcher = function(font)
{
    this.cache = this._getCache(font);
};

WebStudio.Stretcher.cache = [];

WebStudio.Stretcher.prototype._getCache = function(font)
{
    var key = font ? "font_s" + font.size + "_w" + font.weight + "_f" + font.family + "_s" + font.style: "font_default";
    var c = WebStudio.Stretcher.cache[key];
    if(!c) 
    {
        c = [];
        c._font = font;
        WebStudio.Stretcher.cache[key] = c;
    }
    return c;
};

WebStudio.Stretcher.prototype._measureChar = function(c, chars)
{
    var cname = (c == " " ? "space" : c);
    var w = chars["_" + cname];
    if(!w) 
    {
        var f = chars._font;
        var tc = c == " " ? "&nbsp;" : c;
        
        if(!f)
        {
        	w = this._getWidth(tc);
        }
        else
        {
        	w = this._getWidth(tc, f.size, f.weight, f.family, f.style);
        }
        
        chars["_" + cname] = w;
    }
    return w;
};

WebStudio.Stretcher.prototype._getWidth = function(text, fontSize, fontWeight, fontFamily, fontStyle){
    var Span = document.createElement("span");
    Span.style.visibility = "hidden";
    Span.style.position = "absolute";
    Span.style.left = "0px";
    Span.style.top = "0px";
    Span.innerHTML = text;
    if (fontSize)
    {
		Span.style.fontSize = fontSize;
	}
    if (fontWeight)
    {
		Span.style.fontWeight = fontWeight;
	}
    if (fontFamily)
    {
		Span.style.fontFamily = fontFamily;
	}
    if (fontStyle)
    {
		Span.style.fontStyle = fontStyle;
	}
	
    document.body.appendChild(Span);
    var w = Span.offsetWidth;
    document.body.removeChild(Span);
    return w;
};

WebStudio.Stretcher.prototype.getTextWidth = function(str)
{
    if (!str)
    {
        return 0;
	}
    var w = 0;
    for(var i=0; i<str.length; i++) 
    {
        w += this._measureChar(str.charAt(i), this.cache);
    }
    return w;
};

WebStudio.Stretcher.prototype.cutText = function(str, width)
{
    return this._doCutStretch(str, width, true, true);
};

WebStudio.Stretcher.prototype.stretchText = function(str, width)
{
    return this._doCutStretch(str, width, false, true);
};

WebStudio.Stretcher.prototype._doCutStretch = function(str, width, cut, skipSpaces)
{
	var e = 0;
	var w = 0;
	var ew = cut ? 0 : this.getTextWidth("...");
	var spw = this.getTextWidth(" ");

	var textWidth = this.getTextWidth(str);
	if (textWidth <= width)
	{
		return str;
	}

	while(e < str.length) 
	{
		var c = str.charAt(e);
		if(!skipSpaces && (c == ' ' || c == '\n'))
		{
			w += spw;
		}
		w += this._measureChar(str.charAt(e), this.cache);
		if(w + ew >= width)
		{
			break;
		}

		e++;
	}

	return str.substring(0, e) + (cut || e == str.length ? "" : "...");
};

WebStudio.Stretcher.prototype.wrapText = function(str, width) 
{
    var w = this.getTextWidth(str);
    if(w < width)
    {
    	return str;
    }

    var res = "";
    while(str.length > 0) 
    {
        var s = this._doCutStretch(str, width, true, false);
        res += s + " ";
        str = str.substring(str.indexOf(s) + s.length);
    }
    
    return res;
};
