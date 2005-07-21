/*
  Alfresco Extension 1.0
*/

var alfrescoext =
{

  loaded: null,
  currentversion: "1.0",

  init: function () 
  {
    if (alfrescoext.loaded)
      return; 
    else
    {
      alfrescoext.loaded = true;
    }
    getBrowser().addEventListener("mousedown", alfrescoext.mousedown, true);
  },

  mousedown: function (aEvent)
  {
    var linkHref, linkClick;

    if (!aEvent)
      return;
    if (aEvent.button != 0)
      return;

    if (aEvent.target)
      var targ = aEvent.originalTarget;
  
    if (targ.tagName.toUpperCase() != "A")
    {
      // Recurse until reaching root node
      while (targ.parentNode) 
      {
        targ = targ.parentNode;
        // stop if an anchor is located
        if (targ.tagName && targ.tagName.toUpperCase() == "A")
        break;
      }
      if (!targ.tagName || targ.tagName.toUpperCase() != "A")
        return;
    }

    linkHref = targ.getAttribute("href");
    if (linkHref)
    {
      if (linkHref.substring(0,5) == "file:")
      {
			try {
				var len = 1;
				var exargs = new Array();
                                exargs[0] = linkHref;

				var lfile = Components.classes['@mozilla.org/file/local;1'].createInstance(Components.interfaces.nsILocalFile);
				lfile.initWithPath("c:\\windows\\explorer.exe");

				if (lfile.isFile() && lfile.isExecutable()) {
					try {
						var process = Components.classes['@mozilla.org/process/util;1'].createInstance(Components.interfaces.nsIProcess);
						process.init(lfile);
						exargs = process.run(false, exargs, len);
						return false;
					} catch (e) {
						// foobar!
					}
				}
			}
			catch(e) {
				// foobar!
			}
			
                              }
                              
    }
  }
}

window.addEventListener("load",alfrescoext.init,false);
