<script type="text/javascript">//<![CDATA[
new Alfresco.DocumentVersions("${args.htmlid}").setOptions(
{
   versions: [
<#list versions as version>
   {
      label: "${version.label}",
      createdDate: "${version.createdDate}"
   }<#if (version_has_next)>,</#if>
</#list>
   ],
   filename: "${filename!}",
   nodeRef: "${nodeRef!}"
}).setMessages(
      ${messages}
);
//]]></script>

<div id="${args.htmlid}-body" class="document-versions">

   <div class="info-section">

      <div class="heading">${msg("header.versionHistory")}</div>

      <#list versions as version>
         <#if version_index == 1>
            <div class="info-sub-section">
               <span class="meta-heading">${msg("section.olderVersion")}</span>
            </div>
         </#if>
         <div id="${args.htmlid}-expand-div-${version_index}" class="info more <#if version_index != 0>collapsed<#else>expanded</#if>">
            <span class="meta-section-label">${msg("label.label")} ${version.label}</span>
            <span id="${args.htmlid}-createdDate-span-${version_index}" class="meta-value">&nbsp;</span>
         </div>
         <div id="${args.htmlid}-moreVersionInfo-div-${version_index}" class="moreInfo" <#if version_index != 0>style="display: none;"</#if>>
            <div class="info">
               <span class="meta-label">${msg("label.creator")}</span>
               <span class="meta-value">${version.creator.firstName?html} ${version.creator.lastName?html}</span>
            </div>
            <div class="info">
               <span class="meta-label">${msg("label.description")}</span>
               <span class="meta-value">${version.description?html}</span>
            </div>
            <div class="actions">
               <span class="download"><a href="${url.context}/proxy/alfresco${version.downloadURL}">${msg("link.download")}</a></span>
               <#if version_index != 0>
                  <span id="${args.htmlid}-revert-span-${version_index}" class="revert"><a>${msg("link.revert")}</a></span>
               </#if>
            </div>
         </div>
      </#list>

   </div>

</div>