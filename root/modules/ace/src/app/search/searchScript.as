// Search Action Script 

import mx.controls.Alert;
import util.searchservice.SearchService;
import app.*;

/**
 * Event handler for logout button link.  
 * 
 * Makes call to authenitcation service and redirects user to login dialog.
 */
private function onSearchButtonLinkClick():void
{
	SearchService.instance.search(searchTxt.text);	
}
