<script type="text/javascript">//<![CDATA[
   new Alfresco.MiniCalendar("${args.htmlid}").setSiteId("${page.url.templateArgs.site!""}");
//]]></script>

<div class="dashlet">
   <div class="title">${msg("label.header")}</div>
   <div class="body scrollableList">
      <div id="${args.htmlid}-eventsContainer"></div>
   </div>
</div>