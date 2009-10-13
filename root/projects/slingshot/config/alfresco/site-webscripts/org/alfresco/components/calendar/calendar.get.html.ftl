<script type="text/javascript">//<![CDATA[
   new Alfresco.Calendar("${args.htmlid}").setSiteId("${page.url.templateArgs.site!""}").setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body">
   <div id="calendar"></div>
   <div>
      <div id="${args.htmlid}-viewButtons" class="calendar-currentMonth"><a href="#" id="${args.htmlid}-thisMonth-button">${msg("button.this-month")}</a></div>
   </div>
</div>