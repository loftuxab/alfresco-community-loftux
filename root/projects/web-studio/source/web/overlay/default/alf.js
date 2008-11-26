if (typeof Alf == "undefined" || !Alf)
{
	Alf = {};
}

Alf.clone = function(el)
{
	var clonedEl = el.cloneNode(true);
	
	/*
	* if this is MSIE 6/7, then we need to copy the innerHTML to
	* fix a bug related to some form field elements
	*/
	if( jQuery.browser.msie && (jQuery.browser.version <= 7) )
	{
		//$(clonedEl).setHTML(el.innerHTML);
	   //clonedEl.innerHTML = el.innerHTML;
	   
	   Alf.setHTML(clonedEl, el.innerHTML);
	}	
	
	return clonedEl;
};

Alf.setHTML = function(el, html)
{
	jQuery(el).html(html);
};

Alf.injectInside = function(parentElement, childElement)
{
	jQuery(parentElement).append(childElement);
};

Alf.generateID = function()
{
	if(!Alf.guidCounter)
	{
		Alf.guidCounter = 1;
	}
	Alf.guidCounter = Alf.guidCounter + 1;
	
	return "wsguid" + Alf.guidCounter;
};

Alf.createElement = function(tag, id)
{
	var el = null;
	if(!id)
	{
		el = new Element(tag);
	}
	else
	{
		el = new Element(tag, { id : id } );
	}
	
	return el;
};

Alf.getScrollerSize = function()
{
	if(!this.scrollerSize)
	{
		var inner = document.createElement("p");
		inner.style.width = "100%";
		inner.style.height = "200px";
	
		var outer = document.createElement("div");
		outer.style.position = "absolute";
		outer.style.top = "0px";
		outer.style.left = "0px";
		outer.style.visibility = "hidden";
		outer.style.width = "200px";
		outer.style.height = "150px";
		outer.style.overflow = "hidden";
		outer.appendChild(inner);
	
		document.body.appendChild (outer);
		var w1 = inner.offsetWidth;
		var h1 = inner.offsetHeight;
		outer.style.overflow = 'scroll';
		var w2 = inner.offsetWidth;
		var h2 = inner.offsetHeight;
		if (w1 == w2)
		{
			w2 = outer.clientWidth;
		}
		if (h1 == h2)
		{
			h2 = outer.clientHeight;
		}
	
		document.body.removeChild(outer);
	
		var scrollbarWidth = w1 - w2;
		var scrollbarHeight = h1 - h2;
	
		this.scrollerSize = { "w" : scrollbarWidth, "h" : scrollbarWidth };
	}
	
	return this.scrollerSize;
};

Alf.openBrowser = function(name, url)
{
	if(!name)
	{
		name = "_blank";
	}
	
	var args = "fullscreen";
	if (window.screen) 
	{
		w = window.screen.availWidth;
		h = window.screen.availHeight;
		args = "width="+w+",height="+h+",top=0,left=0";
	}
	args += ",location=1,status=1,scrollbars=1,toolbar=1,menubar=1";
	window.open(url, name, args);
};

Alf.getStyle = function(targetElement, styleName)
{
	var style = null;
		
	if(window.ie)
	{
		style = targetElement.style[styleName];
	}
	else
	{
		style = targetElement.getStyle(styleName);
	}
	
	return style;
};

Alf.parseInt = function(value)
{
	return parseInt(value, 10);
};

Alf.resizeToChildren = function(el)
{
	// recompute size of dom element
	for(var a = 0; a < el.childNodes.length; a++)
	{
		var tag = el.childNodes[a].nodeName;
		if(tag)
		{
			if( (tag != "SCRIPT") && (tag != "LINK") )
			{
				if(el.childNodes[a].offsetWidth)
				{
					el.style.width = el.childNodes[a].offsetWidth;
					el.style.height = el.childNodes[a].offsetHeight;
				}
			}
		}
	}			
};