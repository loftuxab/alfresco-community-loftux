<#if activities?exists && activities?size &gt; 0>
   <#assign mode = args.mode!"">
   <#assign lastDate = "3000-01-01"?date("yyyy-MM-dd") lastHour = -1>
   <#list activities as activity>
      <#assign userLink="<a href=\"${activity.userProfile}\" class=\"theme-color-1\">${activity.fullName?html}</a>">
      <#assign itemLink="<a href=\"${activity.itemPage}\">${activity.title?html}</a>">
      <#assign siteLink="<a href=\"${activity.sitePage}\" class=\"theme-color-1\">${activity.siteId}</a>">
      <#if dateCompare(lastDate?date, activity.date.fullDate?date) == 1>
         <#assign lastDate = activity.date.fullDate lastHour = activity.date.hour>
<div class="new-day <#if activity_index = 0>first</#if>"><div class="ruler"></div><span>${lastHour?string("00")}:00, ${lastDate?date?string.medium}</span></div>
      <#elseif lastHour != activity.date.hour>
         <#assign lastHour = activity.date.hour>
<div class="new-hour"><div class="ruler"></div><span>${lastHour?string("00")}:00</span></div>
      </#if>
      <#assign detail = msg(activity.type, itemLink, userLink, activity.custom0, activity.custom1)>
      <#if mode = "user" && !activity.suppressSite><#assign detail = msg("in.site", detail, siteLink)></#if>
<div class="activity <#if !activity_has_next>last</#if>">
   <div class="time">${activity.date.fullDate?time?string("HH:mm")}</div>
   <div class="detail">${detail}</div>
</div>
   </#list>
<#else>
<div class="detail-list-item first-item last-item">
   <span>${msg("label.no-activities")}</span>
</div>
</#if>