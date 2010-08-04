<div class="share-toolbar theme-bg-2">
   <div class="navigation-bar">
      <div>
         <#assign running = (page.url.args.runningWorkflowsLinkBack! == "true")>
         <#assign completed = (page.url.args.completedWorkflowsLinkBack! == "true")>
         <#assign specific = (running || completed)>
         <#if !specific || running>
         <span class="<#if running>backLink<#else>forwardLink</#if>">
            <a href="${url.context}/page/running-workflows">${msg("link.runningWorkflows")}</a>
         </span>
         </#if>
         <#if !specific || completed>
         <#-- Use code below when "Completed workflows" page has been created -->
         <#--
         <span class="<#if completed>backLink<#else>forwardLink</#if>">
            <a href="${url.context}/page/completed-workflows">${msg("link.completedWorkflows")}</a>
         </span>
         -->
         </#if>
      </div>
   </div>
</div>
