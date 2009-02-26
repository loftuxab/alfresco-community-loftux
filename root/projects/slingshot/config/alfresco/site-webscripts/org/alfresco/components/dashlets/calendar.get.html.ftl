<script type="text/javascript">//<![CDATA[
   new Alfresco.MiniCalendar("${args.htmlid}").setSiteId("${page.url.templateArgs.site!""}");
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>
<div class="dashlet calendar">
   <div class="title">${msg("label.header")}</div>
   <div id="${args.htmlid}-eventsContainer" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
   </div>
</div>