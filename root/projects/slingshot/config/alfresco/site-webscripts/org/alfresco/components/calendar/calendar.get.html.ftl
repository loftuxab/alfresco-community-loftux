<script type="text/javascript">//<![CDATA[
   new Alfresco.Calendar("${args.htmlid}").setSiteId("${page.url.args["site"]!""}");
//]]></script>

<div id="${args.htmlid}">
<div id="calendar"></div>

<div class="yui-u">
    <div id="${args.htmlid}-viewButtons" class="doclist-viewButtons"><a href="#" id="${args.htmlid}-addEvent-button">Add Event</a></div>
</div>

<div id="${args.htmlid}-addEvent"></div>
</div>