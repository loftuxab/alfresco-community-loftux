<#if activities?exists && activities?size &gt; 0>
   <#assign lastDate = "3000-01-01"?date("yyyy-MM-dd") lastHour = -1>
   <#list activities as activity>
      <#assign userLink="<a href=\"${activity.userProfile}\">${activity.fullName}</a>">
      <#assign siteLink="<a href=\"${activity.sitePage}\">${activity.title}</a>">
      <#if dateCompare(lastDate?date, activity.date.fullDate?date) == 1>
         <#assign lastDate = activity.date.fullDate lastHour = activity.date.hour>
<div class="new-day <#if activity_index = 0>first</#if>"><div class="ruler"></div><span>${lastHour?string("00")}:00, ${lastDate?date?string.medium}</span></div>
      <#elseif lastHour != activity.date.hour>
         <#assign lastHour = activity.date.hour>
<div class="new-hour"><div class="ruler"></div><span>${lastHour?string("00")}:00</span></div>
      </#if>
<div class="activity <#if !activity_has_next>last</#if>">
   <div class="time">${activity.date.fullDate?time?string("HH:mm")}</div>
   <div class="detail">${msg(activity.type, siteLink, userLink, activity.custom0, activity.custom1)}</div>
</div>
   </#list>
<#else>
<div class="detail-list-item first last">
   <span>${msg("label.no-activities")}</span>
</div>
</#if>