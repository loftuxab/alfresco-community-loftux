<script type="text/javascript">//<![CDATA[
   new Alfresco.CalendarToolbar("${args.htmlid}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
   );
//]]></script>
<div id="${args.htmlid}-body" class="toolbar calendar-toolbar">
   <div class="yui-gf calendar-bar">
      <div class="yui-u first">
<#if role = "SiteCollaborator" || role = "SiteManager">
         <div id="${args.htmlid}-viewButtons" class="addEvent">
            <button id="${args.htmlid}-addEvent-button" name="addEvent">${msg("button.add-event")}</button>
         </div>
         <div class="separator">|</div>
</#if>
         <div class="ical-feed">
            <a href="${page.url.context}/proxy/alfresco/calendar/eventList?site=${page.url.templateArgs["site"]}&amp;format=calendar" target="_blank" id="${args.htmlid}-publishEvents-button" name="publishEvent">${msg("button.ical")}</a>
         </div>
      </div> 
      <div class="yui-u align-right">
         <button id="${args.htmlid}-today-button">${msg("button.today")}</button>
         <span class="separator">|</span>
         <button id="${args.htmlid}-prev-button">&lt; ${msg("button.previous")}</button>
         <span class="separator"></span>
         <div id="${args.htmlid}-navigation" class="yui-buttongroup inline">
            <#-- Don't insert linefeeds between these <input> tags -->
            <input id="${args.htmlid}-day" type="radio" name="navigation" value="${msg("button.day")}" /><input id="${args.htmlid}-week" type="radio" name="navigation" value="${msg("button.week")}" /><input id="${args.htmlid}-month" type="radio" name="navigation" value="${msg("button.month")}" checked /><input id="${args.htmlid}-agenda" type="radio" name="navigation" value="${msg("button.agenda")}" />
         </div>
         <span class="separator"></span>
         <button id="${args.htmlid}-next-button">${msg("button.next")} &gt;</button>
      </div>
   </div>
   <div id="${args.htmlid}-addEvent"></div>
</div>