<div class="dashlet">
   <div class="title">${msg("label.header")}</div>
   <div class="body scrollableList">
<#if eventList??>
   <#if eventList?size &gt; 0>
      <#list eventList as event>
      <div class="detail-list-item <#if event_index = 0>first-item<#elseif !event_has_next>last-item</#if>">
         <div class="icon"><img src="${url.context}/components/calendar/images/calendar-16.png" alt="event" /></div>
         <div class="details2">
            <h4><a href="${url.context}/${event.url}" class="theme-link-1">${event.title?html}</a></h4>
            <div>${event.when} (${event.start} - ${event.end})</div>
         <#assign siteLink><a href='${url.context}/page/site/${event.site}/dashboard' class="theme-link-1">${event.siteTitle?html}</a></#assign>
            <div>${msg("label.in-site", siteLink)}</div>
         </div>
      </div>
      </#list>
   <#else>
      <div class="detail-list-item first-item last-item">
 	      <span>${msg("label.noEvents")}</span>
      </div>
   </#if>
<#else>
      <div class="detail-list-item first-item last-item">
 	      <span>${msg("label.error")}</span>
      </div>
</#if>
   </div>
</div>