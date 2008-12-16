<#macro doclibUrl doc>
   <a href="${url.context}/page/site/${doc.location.site}/documentlibrary?file=${doc.fileName?url}&amp;filter=editingMe">${doc.displayName?html}</a>
</#macro>
<div class="dashlet">
   <div class="title">${msg("header")}</div>
   <div class="body scrollableList">
   <#if docs.message?exists>
      <div class="detail-list-item first-item last-item">
         <span class="error">${docs.message}</span>
      </div>
   <#else>
      <#if docs.items?size == 0>
      <div class="detail-list-item first-item last-item">
         <span>${msg("label.noItems")}</span>
      </div>
      <#else>
         <#list docs.items?sort_by("modifiedOn") as doc>
            <#assign modifiedBy><a href="${url.context}/page/user/${doc.modifiedByUser?url}/profile">${doc.modifiedBy?html}</a></#assign>
      <div class="detail-list-item <#if doc_index = 0>first-item<#elseif !doc_has_next>last-item</#if>">
         <div>
            <div class="icon">
               <img src="${url.context}/components/images/generic-file-32.png" alt="${doc.displayName?html}" />
            </div>
            <div class="details">
               <h4><@doclibUrl doc /></h4>
               <div>
                  ${msg("text.editing-since", doc.modifiedOn?datetime("dd MMM yyyy HH:mm:ss 'GMT'Z '('zzz')'")?string("dd MMM, yyyy HH:mm"))}
               </div>
            </div>
         </div>
      </div>
         </#list>
      </#if>
   </#if>
   </div>
</div>