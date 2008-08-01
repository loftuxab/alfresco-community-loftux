<#--
<script type="text/javascript">//<![CDATA[
   new Alfresco.UserCalendar("${args.htmlid}");
//]]></script>
-->
<div class="dashlet">
   <div class="title">Calendar</div>
   <div class="body scrollableList">
 	   <#if eventList?size &gt; 0>
 	   <#list eventList as event>
 	      <table class='cal-events-dashlet'>
 	      <tr class='cal-event'>
 	         <td class='cal-icon'><img src='${url.context}/components/calendar/images/calendar-16.png'/></td>
 	         <td>
 	            <div class='cal-header'><a href='${url.context}/${event.url}'>${event.title}</a></div>
 	            <div>${event.when} (${event.start} - ${event.end})</div>
 	            <div>In: <a href='${url.context}/page/site/${event.site}/dashboard'>${event.site}</a></div>
 	         </td>
 	      </tr>   
 	      </table>
 	   </#list>
 	   <#else>
 	      <div>No upcoming events</div>
 	   </#if>
   </div>
</div>