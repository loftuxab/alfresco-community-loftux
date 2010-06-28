<#macro doclibUrl doc>
   <#if ((doc.location.site??) && (doc.location.site != ""))>
   <a href="${url.context}/page/site/${doc.location.site}/documentlibrary?file=${doc.fileName?url}#path=${doc.location.path?url}">${doc.displayName?html}</a>
   <#else>
   ${doc.displayName?html}
   </#if>
</#macro>
<#macro siteUrl site>
   <a href="${url.context}/page/site/${site.id}/dashboard">${site.title?html}</a>
</#macro>
<#macro avatarImg theUser>
   <#if (theUser.avatarRef??)>
      <#assign avatarUrl = url.context + "/proxy/alfresco/api/node/" + theUser.avatarRef?replace('://', '/') + "/content/thumbnails/avatar?c=queue&amp;ph=true">
   <#else>
      <#assign avatarUrl = url.context + "/components/images/no-user-photo-64.png">
   </#if>
   <img src="${avatarUrl}" alt="${theUser.userName}" width="16" height="16" />
</#macro>
<#macro userProfile theUser>
   <a href="${url.context}/page/user/${theUser.userName}/profile">${theUser.fullName?html}</a>
</#macro>
<#if myTasks?? && myTasks.tasks?size &gt; 0>
   <#list myTasks.tasks?sort_by("dueDate")?sort_by("priority") as task>
      <#assign overdue = ((task.dueDate != "") && (dateCompare(date?date, task.dueDate?date("yyyy-MM-dd")) == 1))>
      <#assign dueDate = "" />
      <#if (task.dueDate != "" && task.dueDate?substring(0, 4) != "9999") >
         <#assign dueDate = task.dueDate?date("yyyy-MM-dd")?string(msg("date-format.mediumDateFTL")) />
      </#if>
   <div class="detail-list-item <#if task_index = 0>first-item<#elseif !task_has_next>last-item</#if>">
      <div class="task-icon">
         <span class="priority<#if overdue> overdue</#if> theme-color-1 theme-border-2 theme-bg-color-3">${task.priority}</span>
      </div>
      <div class="task-details">
         <h4>${task.description?html}<#if dueDate != ""> <span class="light">${msg('label.due-on', dueDate)} <#if overdue> (${msg("status.overdue")})</span></#if></#if></h4>
         <span class="task-status">${task.type}</span>
         <div class="task-resources">
      <#if task.resources??>
         <#list task.resources as resource>
               <div class="task-resource<#if resource_has_next> resource-spacer</#if>">
                  <span class="task-resource-link">
                     <img src="${url.context}/components/images/generic-file-16.png" alt="icon" />
                     <span><@doclibUrl resource /></span>
                  </span>
               </div>
         </#list>
      </#if>
      <#if task.invitation??>
         <#assign invite = task.invitation>
         <#if invite.type == "moderated">
            <#assign initiator = invite.invitee>
         <#elseif invite.type == "nominated">
            <#assign initiator = invite.inviter>
         </#if>
         <#assign htmlInitiator>
            <div class="task-resource resource-spacer">
               <span class="task-resource-link">
                  <@avatarImg initiator />
                  <span><@userProfile initiator /></span>
               </span>
            </div>
         </#assign>
         <#assign htmlSite>
            <div class="task-resource resource-spacer">
               <span class="task-resource-link">
                  <img src="${url.context}/components/images/site-16.png" alt="site" />
                  <span><@siteUrl invite.site /></span>
               </span>
            </div>
         </#assign>
         <#assign htmlRole>
            <div class="task-resource resource-spacer">
               <span class="task-resource-link">
                  <img src="${url.context}/components/images/users-16.png" alt="role" />
                  <span>${msg("role." + invite.inviteeRole)}</span>
               </span>
            </div>
         </#assign>
         ${msg("label." + (invite.type) + "-invite-join", htmlInitiator, htmlSite, htmlRole)}
      </#if>
         </div>
         <div class="task-transitions">
      <#list task.transitions as transition>
            <span class="${task.id} ${transition.id}"><a href="#" class="task-transition theme-color-1">${transition.label?html}</a></span>
         <#if transition_has_next><span class="separator">|</span></#if>
      </#list>
         </div>
      </div>
   </div>
   </#list>
<#else>
   <div class="detail-list-item first-item last-item">
      <span>${msg("label.no-tasks")}</span>
   </div>
</#if>