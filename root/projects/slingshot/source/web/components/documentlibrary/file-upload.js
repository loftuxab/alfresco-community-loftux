/*
 *** Alfresco.FileUpload
*/

Alfresco.FileUpload = function()
{
    /* Shortcuts */
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

    return {
        ID: null,

        htmlPanel: null,
        flashPanel: null,
        modalPanelConfig: {
                fixedcenter: true,
                close:false,
                draggable:false,
                zindex:4,
                modal:true,
                visible:true
            },

        init: function()
        {
            Event.onDOMReady(this.start, this, true);       
        },

        start: function()
        {
            /* Html Dialog */
            var htmlDiv = Dom.getElementsByClassName("fileupload-htmldialog-panel", "div", this.ID)[0];
            this.htmlPanel = new YAHOO.widget.Panel(htmlDiv, this.modalPanelConfig);

            /* Flash Dialog */
            var flashDiv = Dom.getElementsByClassName("fileupload-flashdialog-panel", "div", this.ID)[0];
            this.flashPanel = new YAHOO.widget.Panel(flashDiv, this.modalPanelConfig);
        },

        show: function()
        {
            /* Show the appropriate dialog */

            var hasRequestedVersion = DetectFlashVer(8, 0, 0); // majorVersion, minorVersions, revisionVersion
            p = hasRequestedVersion ? this.flashPanel : this.htmlPanel;
            p.render(document.body);
            p.show();
            
        }

    }
}();

Alfresco.FileUpload.init();
