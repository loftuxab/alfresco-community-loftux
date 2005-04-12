//
// Helper functions for common components
// Kevin Roast 12-04-2005
//

// Menu component functions
var _lastMenu = null;
function _toggleMenu(menuId)
{
   // hide any open menu
   if (_lastMenu != null && _lastMenu != menuId)
   {
      document.getElementById(_lastMenu).style.display = 'none';
      _lastMenu = null;
   }
   
   // toggle visibility of the specified element id
   if (document.getElementById(menuId).style.display == 'none')
   {
      document.getElementById(menuId).style.display = '';
      _lastMenu = menuId;
   }
   else
   {
      document.getElementById(menuId).style.display = 'none';
   }
}
