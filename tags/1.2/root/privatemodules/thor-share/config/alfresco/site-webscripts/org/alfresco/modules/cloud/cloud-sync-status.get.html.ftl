<#include "../../include/alfresco-macros.lib.ftl" />
<#assign el=args.htmlid?html>
<div class="cloud-sync-status" data-sync-owner-fullname="${syncOwnerFullName!""}">
   {title}
   <div class="cloud-sync-details">
      <div class="cloud-sync-details-success"></div>
      <div class="cloud-sync-details-info">
         <div class="cloud-sync-status-heading">
            <h4>${msg("sync.status.heading.status")}</h4>
            {showMoreInfoLink}
         </div>
         <#if synced>
            <p>
               <#--Rendered client side, since we've got the data in hand there & it involves a date. -->
               {status}
            </p>
         <#else>
            <p>${msg("sync.status.message.not-synced")}</p>
         </#if>

         <#-- Root folder for indirectly synced nodes -->
         <#if isDirectSync?? && isDirectSync == "false">
            <div class="cloud-sync-indirect-root location">
               <h4>${msg("sync.status.heading.synced-folder")}</h4>
               <span class="document-root-link document-link">
                  <img src="${url.context}/res/components/images/filetypes/generic-folder-32.png" width="32" /><a href="folder-details?nodeRef=${rootNodeRef}" class="view-in-cloud">${rootNodeName}</a>
                </span>
             </div>
          </#if>

      </div>
   </div>
</div>
