<#macro doclibUrl doc>
   <#if ((doc.location.site?exists) && (doc.location.site != ""))>
   <a href="${url.context}/page/site/${doc.location.site}/documentlibrary?file=${doc.fileName?url}#path=${doc.location.path?url}">${doc.displayName?html}</a>
   <#else>
   ${doc.displayName?html}
   </#if>
</#macro>
<#if myTasks?exists && myTasks.tasks?size &gt; 0>
   <#list myTasks.tasks?sort_by("dueDate") as task>
      <#assign overdue = ((task.dueDate != "") && (dateCompare(date?date, task.dueDate?date) == 1))>
      <div class="detail-list-item">
         <div class="task-icon">
            <span class="priority<#if overdue> overdue</#if>">${task.priority}</span>
         </div>
         <div class="task-details">
            <h4>${task.description}<#if overdue> <span class="light">(${msg("status.overdue")})</span></#if></h4>
            <span class="task-status">${task.type}, ${task.status}</span>
            <div class="task-resources">
      <#list task.resources as resource>
               <div class="task-resource<#if resource_has_next> resource-spacer</#if>">
                  <span class="task-resource-link">
                     <img src="${url.context}/components/images/generic-file-16.png" alt="icon" />
                     <span><@doclibUrl resource /></span>
                  </span>
               </div>
      </#list>
            </div>
            <div class="task-transitions">
      <#list task.transitions as transition>
               <span class="${task.id} ${transition.id}"><a href="#" class="task-transition">${transition.label?html}</a></span>
         <#if transition_has_next><span class="separator">|</span></#if>
      </#list>
            </div>
         </div>
      </div>
   </#list>
<#else>
      <span>${msg("label.no-tasks")}</span>
</#if>
