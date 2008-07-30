<script type="text/javascript">//<![CDATA[
   new Alfresco.MiniCalendar("${args.htmlid}").setSiteId("${page.url.templateArgs.site!""}");
//]]></script>

<div class="dashlet">
   <div class="title">Calendar</div>
   <div class="body scrollableList">
      <#-- Events go here -->
      <div id="${args.htmlid}-eventsContainer"></div>
   </div>
</div>