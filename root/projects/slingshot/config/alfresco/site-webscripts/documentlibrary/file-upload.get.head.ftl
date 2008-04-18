<!-- File upload Assets -->
<link rel="stylesheet" type="text/css" href="${url.context}/components/documentlibrary/file-upload.css" />
<script type="text/javascript" src="${url.context}/js/flash/AC_OETags.js"></script>

<script type="text/javascript" src="${url.context}/components/documentlibrary/mootools-core.js"></script>
<script type="text/javascript" src="${url.context}/components/documentlibrary/Swiff.Base.js"></script>
<script type="text/javascript" src="${url.context}/components/documentlibrary/Swiff.Uploader.js"></script>
<script type="text/javascript" src="${url.context}/components/documentlibrary/FancyUpload.js"></script>

<script type="text/javascript" src="${url.context}/components/documentlibrary/file-upload.js"></script>
<script type="text/javascript">//<![CDATA[
   Alfresco.FileUpload.ID = "${htmlid}";
//]]></script>


<script type="text/javascript">//<![CDATA[

/**
 * Sample Data
 */

window.addEvent('load', function()
{

    /**
     * We take the first input with this class we can find ...
     */
    var input = $('photoupload-filedata-1');

    /**
     * Simple and easy
     *
     * swf: the path to the swf
     * container: the object is embedded in this container (default: document.body)
     *
     * NOTE: container is only used for the first uploader u create, all others depend
     * on the same swf in that container, so the container option for the other uploaders
     * will be ignored.
     *
     */
    var uplooad = new FancyUpload(input, {
        swf: '${url.context}/components/documentlibrary/Swiff.Uploader.swf',
        queueList: 'photoupload-queue',
        container: $E('h1')
    });

    /**
     * We create the clear-queue link on-demand, since we don't know if the user has flash/javascript.
     *
     * You can also create the complete xhtml structure thats needed for the queue here, to be sure
     * that its only in the document when the user has flash enabled.
     */
    $('photoupload-status').adopt(new Element('a', {
        href: 'javascript:void(null);',
        events: {
            click: uplooad.clearList.bind(uplooad, [false])
        }
    }).setHTML('Clear Completed'));
});

/*
window.addEvent('load', function()
{
    // wait to make suer the flash movie has been loaded
    setTimeout("initializeFlashUpload()", 2000);
});
*/

//]]></script>
