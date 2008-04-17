/*
 *** Alfresco.Footer
*/

Alfresco.Footer = function()
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

        }

    }
}();

Alfresco.Footer.init();
