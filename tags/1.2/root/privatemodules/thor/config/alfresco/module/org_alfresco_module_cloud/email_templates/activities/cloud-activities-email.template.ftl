<#include "../email-macros.ftl">
<html>
<@emailtemplate "${label_activities_title}" "${generationTime?date?string.full}">
   <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
         <td>
            <table style="${activities_table}">
               <tr style="${activities_numbers_row}">
                  <td style="${activities_number_cell}${activities_total_cell}">${new?number + updates?number + comments?number + likes?number + joins?number + others?number}</td>
                  <td style="${activities_number_cell}">${new}</td>
                  <td style="${activities_number_cell}">${updates}</td>
                  <td style="${activities_number_cell}">${comments}</td>
                  <td style="${activities_number_cell}">${likes}</td>
                  <td style="${activities_number_cell}">${joins}</td>
                  <td style="${activities_number_cell}">${others}</td>
               </tr>
               <tr style="${activities_labels_row}">
                  <td style="${activities_total_cell}${activities_number_cell}">${label_assign}</td>
                  <td style="${activities_number_cell}">${label_new}</td>
                  <td style="${activities_number_cell}">${label_updates}</td>
                  <td style="${activities_number_cell}">${label_comments}</td>
                  <td style="${activities_number_cell}">${label_liked}</td>
                  <td style="${activities_number_cell}">${label_joins}</td>
                  <td style="${activities_number_cell}">${label_others}</td>
               </tr>
            </table>
            <div style="${standard_text}">
               <#assign shareUrlCtx="${shareUrl}">
               <#if (activitiesBySite?exists && activitiesBySite?size > 0)>
                  <#assign firstSite=true>
                  <#list activitiesBySite?keys as site>
                     <#if !firstSite><div style="padding: 0px 0px 12px 0px;"></div></#if>
                     <#assign activities=activitiesBySite[site]>
                     <#if siteNetwork[site]?exists && siteNetwork[site] != "">
                        <#assign shareUrlCtx="${shareUrl}/${siteNetwork[site]}">
                     </#if>
                     <div style="site-name"><a style="${site_link}" href="${shareUrlCtx}/page/site/${site?html}/dashboard">${siteTitles[site]!site?html}</a></div>
                     <#assign firstSite=false>
                     <#assign activityType="" userName="" firstEntry=true>
                     <#list activities as activity>
                        <#assign itemLink="<a href=\"${shareUrlCtx}/page/site/${activity.siteNetwork?html}/${activity.activitySummary.page!\"\"}\">${activity.activitySummary.title!\"\"}</a>">
                        <#if userName != activity.postUserId || activityType != activity.activityType>
                           <#if !firstEntry><div style="padding: 0px 0px 12px 0px;"></div></#if>
                           <#assign userLink="<a href=\"${shareUrlCtx}/page/user/${activity.postUserId?html}/profile\">${activity.activitySummary.firstName!\"\"} ${activity.activitySummary.lastName!\"\"}</a>">
                           <#assign secondUserLink="">
                           <#assign siteLink="<a href=\"${shareUrlCtx}/page/site/${activity.siteNetwork?html}/dashboard\">${siteTitles[activity.siteNetwork]!activity.siteNetwork?html}</a>">

                           <#assign suppressSite=false>

                           <#switch activity.activityType>
                              <#case "org.alfresco.site.user-joined">
                              <#case "org.alfresco.site.user-left">
                                 <#assign suppressSite=true>
                              <#case "org.alfresco.site.user-role-changed">
                                 <#assign custom0=message("role."+activity.activitySummary.role)!"">
                                 <#assign userLink="<a href=\"${shareUrlCtx}/page/user/${activity.activitySummary.memberUserName?html}/profile\">${activity.activitySummary.memberFirstName!\"\"} ${activity.activitySummary.memberLastName!\"\"}</a>">
                                 <#break>
                              <#case "org.alfresco.site.group-added">
                              <#case "org.alfresco.site.group-removed">
                                 <#assign suppressSite=true>
                              <#case "org.alfresco.site.group-role-changed">
                                 <#assign custom0=message("role."+activity.activitySummary.role)!"">
                                 <#assign userLink=activity.activitySummary.groupName?replace("GROUP_", "")>
                                 <#break>
                              <#case "org.alfresco.subscriptions.followed">
                                 <#assign userLink="<a href=\"${shareUrlCtx}/page/user/${activity.activitySummary.followerUserName?html}/profile\">${activity.activitySummary.followerFirstName!\"\"} ${activity.activitySummary.followerLastName!\"\"}</a>">
                                 <#assign secondUserLink="<a href=\"${shareUrlCtx}/page/user/${activity.activitySummary.userUserName?html}/profile\">${activity.activitySummary.userFirstName!\"\"} ${activity.activitySummary.userLastName!\"\"}</a>">
                                 <#assign suppressSite=true>
                                 <#break>
                              <#case "org.alfresco.subscriptions.subscribed">
                                 <#assign userLink="<a href=\"${shareUrlCtx}/page/user/${activity.activitySummary.subscriberUserName?html}/profile\">${activity.activitySummary.subscriberFirstName!\"\"} ${activity.activitySummary.subscriberLastName!\"\"}</a>">
                                 <#assign custom0=(activity.activitySummary.node!"")?html>
                                 <#assign suppressSite=true>
                                 <#break>
                              <#case "org.alfresco.profile.status-changed">
                                 <#assign custom0=(activity.activitySummary.status!"")?html>
                                 <#assign suppressSite=true>
                                 <#break>
                              <#default>
                           </#switch>
                         
                           <#assign detail=message(activity.activityType?html, itemLink, userLink, custom0, activity.activitySummary.custom1!"", siteLink, secondUserLink)!"">
                         
                           <div class="activity">
                              <#if suppressSite>${detail}<#else>${message("in.site", detail, siteLink)}</#if>
                           </div>
                           <#assign firstEntry=false>
                        </#if>

                        <#assign activityType = activity.activityType userName = activity.postUserId>
                        <div style="font-size: 11px; padding: 4px 0px 0px 0px;">
                           ${activity.postDate?datetime?string.medium}  ${itemLink}
                        </div>
                     </#list>
                  </#list>
               </#if>
             </div>
         </td>
      </tr>
      <@footerText ["${label_legal}",
                     "${label_settings}"] />
   </table>
</@>
</html>
