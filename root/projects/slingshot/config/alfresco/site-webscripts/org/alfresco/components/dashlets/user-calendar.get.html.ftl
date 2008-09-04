<div class="dashlet">
   <div class="title">${msg("label.header")}</div>
   <div class="body scrollableList">
 	   <#if eventList?size &gt; 0>
 	   <#list eventList as event>
 	      <div class="detail-list-item">
 	         <div class="icon"><img src="${url.context}/components/calendar/images/calendar-16.png" alt="event" /></div>
 	         <div class="details2">
 	            <h4><a href="${url.context}/${event.url}">${event.title}</a></h4>
 	            <div>${event.when} (${event.start} - ${event.end})</div>
 	      <#assign siteLink><a href='${url.context}/page/site/${event.site}/dashboard'>${event.site}</a></#assign>
 	            <div>${msg("label.in-site", siteLink)}</div>
 	         </div>
 	      </div>
 	   </#list>
 	   <#else>
 	      <div>${msg("label.noEvents")}</div>
 	   </#if>
   </div>
</div>
