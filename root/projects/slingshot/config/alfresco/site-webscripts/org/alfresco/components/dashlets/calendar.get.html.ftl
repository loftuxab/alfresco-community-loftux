<script type="text/javascript">//<![CDATA[
   new Alfresco.MiniCalendar("${args.htmlid}").setSiteId("${page.url.templateArgs.site!""}");
//]]></script>        
<div class="dashlet calendar">
   <div class="title">${msg("label.header")}</div>
   <div id="${args.htmlid}-eventsContainer" class="body scrollableList">
   </div>
</div>