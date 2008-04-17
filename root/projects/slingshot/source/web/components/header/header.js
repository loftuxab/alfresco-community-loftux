/*
 *** Alfresco.Header
*/

Alfresco.Header = function()
{
    /* Shortcuts */
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

    return {
        ID: null,

        init: function()
        {
            Event.onDOMReady(this.start, this, true);
        },

        start: function()
        {
            /* Splitbuttons */

            var ssButton = Dom.getElementsByClassName("header-sitesSelect-button", "input", this.ID)[0];
            var ssMenu = Dom.getElementsByClassName("header-sitesSelect-menu", "select", this.ID)[0];
            var sitesSelectButton = new YAHOO.widget.Button(ssButton,
            {
               type: "split",
               menu: ssMenu
            });

            var csButton = Dom.getElementsByClassName("header-colleaguesSelect-button", "input", this.ID)[0];
            var csMenu = Dom.getElementsByClassName("header-colleaguesSelect-menu", "select", this.ID)[0];
            var colleaguesSelectButton = new YAHOO.widget.Button(csButton,
            {
               type: "split",
               menu: csMenu
            });
        }

    }
}();

Alfresco.Header.init();

