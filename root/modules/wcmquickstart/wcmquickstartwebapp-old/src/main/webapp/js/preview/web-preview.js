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

var WebPreview = function() {
	
	function onWebPreviewerEvent(event)
	{
	    if (event.error)
	    {
	        // Inform the user about the failure
	        var message = "Error";
	        if (event.error.code)
	        {
	            message = messages["error." + event.error.code];
	        }
	        alert(message);
	    }
	}
	
	return {
		render : function (context, id, uri, name, messages) 
		{
			/*if (<check if flash player greater than 9.0.45 is installed >)
			{*/
				// Change the class of the containing div to be taller etc
		        var container = document.getElementById("web-preview-container");
		        container.className = "web-preview-content";

		        // Create flash web preview by using swfobject
		        var swfId = "WebPreviewer_" + id;
		        
		        var flashvarsObj = {"fileName" : name,
		        					"paging" : true,
		        					"url" : uri,
		        					"i18n_actualSize" : messages["preview.actualSize"],
		        					"i18n_fitPage" : messages["preview.fitPage"],
		        					"i18n_fitWidth" : messages["preview.fitWidth"],
		        					"i18n_fitHeight" : messages["preview.fitHeight"],
		        					"i18n_fullscreen" : messages["preview.fullscreen"],
		        					"i18n_fullwindow" : messages["preview.fullwindow"],
		        					"i18n_fullwindow_escape" : messages["preview.fullwindowEscape"],
		        					"i18n_page" : messages["preview.page"],
		        					"i18n_pageOf" : messages["preview.pageOf"],
		        					"show_fullscreen_button" : false,
		        					"show_fullwindow_button" : false};
		        
		        var parObj = {"allowScriptAccess" : "sameDomain", 
		        		      "allowFullScreen" : "true", 
		        		      "wmode" : "transparent"};
		        
		        swfobject.embedSWF(context+"/swf/preview/WebPreviewer.swf", 
		        				   "web-preview",
		        		           "100%", "100%", "9.0.45", null, flashvarsObj, parObj, null, onWebPreviewerEvent);	        
			//}
		}
	};
}();
