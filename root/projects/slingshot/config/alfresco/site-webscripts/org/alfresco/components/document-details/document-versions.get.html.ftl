<div id="${args.htmlid}-body" class="document-versions">

   <div class="info-section">

      <div class="heading">${msg("header.versionHistory")}</div>

      <#list versions as version>
         <#if version_index == 0>
            <div class="info-sub-section">
               <span class="meta-heading">${msg("section.currentVersion")}</span>
            </div>
         </#if>
         <#if version_index == 1>
            <div class="info-sub-section">
               <span class="meta-heading">${msg("section.olderVersion")}</span>
            </div>
         </#if>
         <div class="info">
            <span class="meta-value">${version.name}</span>
         </div>
         <div class="info">
            <span class="meta-label">${msg("label.label")}</span>
            <span class="meta-value">${version.label}</span>
         </div>
         <div class="info">
            <span class="meta-label">${msg("label.creator")}</span>
            <span class="meta-value">${version.creator.firstName} ${version.creator.lastName}</span>
         </div>
         <div class="info">
            <span class="meta-label">${msg("label.createdDate")}</span>
            <span class="meta-value">${version.createdDate}</span>
         </div>
         <div class="info">&nbsp;</div>
      </#list>

   </div>

</div>