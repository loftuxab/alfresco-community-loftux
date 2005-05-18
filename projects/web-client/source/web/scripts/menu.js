//
// Helper functions for common components
// Kevin Roast 12-04-2005
//

// Menu component functions
var _lastMenu = null;
var _timeoutId = null;

// toggle a dynamic menu dropping down
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
      document.getElementById(menuId).style.display = 'block';
      _lastMenu = menuId;
   }
   else
   {
      document.getElementById(menuId).style.display = 'none';
   }
}

// Hide the specified menu DIV
function _hideMenu(id)
{
   document.getElementById(id).style.display = 'none';
}

// menu DIV onmouseover handler
function _menuIn(id)
{
   if (_timeoutId != null)
   {
      clearTimeout(_timeoutId);
      _timeoutId = null;
   }
}

// menu DIV onmouseout handler
function _menuOut(id)
{
   if (_timeoutId != null)
   {
      clearTimeout(_timeoutId);
      _timeoutId = null;
   }
   
   // hide the menu after a seconds delay
   _timeoutId = window.setTimeout("_hideMenu('" + id + "')", 1000);
}
