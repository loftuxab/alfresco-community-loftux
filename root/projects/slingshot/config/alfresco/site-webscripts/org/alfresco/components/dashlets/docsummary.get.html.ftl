<#macro doclibUrl doc>
   <a href="${url.context}/page/site/${doc.location.site}/document-details?nodeRef=${doc.nodeRef}">${doc.displayName?html}</a>
</#macro>
<div class="dashlet">
   <div class="title">${msg("header.docSummary")}</div>
   <div class="body scrollableList">
      <#if docs.message?exists>
         <span class="error">${docs.message}</span>
      <#else>
         <#if docs.items?size == 0>
            <span>${msg("label.noItems")}</span>
         <#else>
            <#list docs.items as doc>
               <#assign modifiedBy><a href="${url.context}/page/user/${doc.modifiedByUser}/profile">${doc.modifiedBy}</a></#assign>
               <div class="detail-list-item  <#if (!doc_has_next)>last</#if>">
                  <div>
                     <div class="icon">
                        <img src="${url.context}/components/images/generic-file-32.png" alt="${doc.displayName?html}" />
                     </div>
                     <div class="details">
                        <h4><@doclibUrl doc /></h4>
                        <div>
                           ${msg("text.modified-by", modifiedBy)} ${msg("text.modified-on", doc.modifiedOn)}
                        </div>
                     </div>
                  </div>
               </div>
            </#list>
         </#if>
      </#if>
   </div>
</div>