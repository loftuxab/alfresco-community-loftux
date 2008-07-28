<script type="text/javascript">//<![CDATA[
   new Alfresco.CalendarToolbar("${args.htmlid}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
   );
//]]></script>
<div id="${args.htmlid}-body" class="toolbar calendar-toolbar">
   <div class="yui-g calendar-bar">
       <div id="${args.htmlid}-viewButtons" class="yui-u first addEvent">
         <button id="${args.htmlid}-addEvent-button" name="addEvent">${msg("button.add-event")}</button>
       </div>
       <div class="yui-u align-right">
          <button id="${args.htmlid}-prev-button">${msg("button.previous")}</button>
          <span class="separator">|</span>
          <button id="${args.htmlid}-next-button">${msg("button.next")}</button>
          <span class="separator">|</span>
           <button id="${args.htmlid}-current-button">${msg("button.today")}</button>
       </div>
   </div>
    <div id="${args.htmlid}-addEvent"></div>
</div>