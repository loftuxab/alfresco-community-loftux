<script type="text/javascript">//<![CDATA[
   new Alfresco.CalendarToolbar("${args.htmlid}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
   );
//]]></script>
<div id="${args.htmlid}-body" class="toolbar calendar-toolbar theme-bg-2">
   <div class="yui-ge calendar-bar">
      <div class="yui-u first theme-bg-1">
         <button id="${args.htmlid}-today-button">${msg("button.today")}</button>
         <span class="separator">&nbsp;</span>
         <button id="${args.htmlid}-prev-button">&lt; ${msg("button.previous")}</button>
         <div id="${args.htmlid}-navigation" class="yui-buttongroup inline">
            <#-- Don't insert linefeeds between these <input> tags -->
            <input id="${args.htmlid}-day" type="radio" name="navigation" value="${msg("button.day")}" /><input id="${args.htmlid}-week" type="radio" name="navigation" value="${msg("button.week")}" /><input id="${args.htmlid}-month" type="radio" name="navigation" value="${msg("button.month")}" /><input id="${args.htmlid}-agenda" type="radio" name="navigation" value="${msg("button.agenda")}" />
         </div>
         <button id="${args.htmlid}-next-button">${msg("button.next")} &gt;</button>
      </div> 
      <div class="yui-u flat-button">
         <#if role = "SiteCollaborator" || role = "SiteManager">
         <div id="${args.htmlid}-viewButtons" class="addEvent">
            <button id="${args.htmlid}-addEvent-button" name="addEvent">${msg("button.add-event")}</button>
         </div>
         <div class="separator">&nbsp;</div>
         </#if>
         <div class="ical-feed">
            <a id="${args.htmlid}-publishEvents-button" href="${page.url.context}/proxy/alfresco-feed/calendar/eventList?site=${page.url.templateArgs["site"]}&amp;format=calendar" rel="_blank">${msg("button.ical")}</a>
         </div>
      </div>
   </div>
   <div id="${args.htmlid}-addEvent"></div>
</div>
<script type="text/javascript">//<![CDATA[
(function()
{
   Alfresco.util.relToTarget("${args.htmlid}-body");
})();
//]]></script>