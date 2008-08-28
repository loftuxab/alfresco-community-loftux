<script type="text/javascript">//<![CDATA[
   new Alfresco.CalendarToolbar("${args.htmlid}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
   );
//]]></script>
<div id="${args.htmlid}-body" class="toolbar calendar-toolbar">
   <div class="yui-g calendar-bar">
      <div class="yui-u first">
         <#if role = "SiteCollaborator" || role = "SiteManager">
          <div id="${args.htmlid}-viewButtons" class="addEvent">
            <button id="${args.htmlid}-addEvent-button" name="addEvent">${msg("button.add-event")}</button>
          </div>
          <div class="separator">|</div>
          </#if>
           <div class="ical-feed">
           <a href="${page.url.context}/proxy/alfresco/calendar/eventList?site=${page.url.templateArgs["site"]}&format=calendar" target="_blank" id="${args.htmlid}-publishEvents-button" name="publishEvent">${msg("button.ical")}</a>
           </div>
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