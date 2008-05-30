<div class="dashlet">
   <div class="title">Documents Modified In Last 7 Days</div>
   <div class="body scrollableList">
      <#if docs.error?exists>
         <span class="error">${docs.error}</span>
      <#else>
         <#if docs.items?size == 0>
            <span>No items to display</span>
         <#else>
            <#list docs.items as doc>
               <div class="detail-list-item">
                  <div>
                     <div class="icon">
                        <img src="${url.context}${doc.icon32}" />
                     </div>
                     <div class="details">
                        <h4>${doc.name}</h4>
                        <div>modified by ${doc.modifiedBy} on ${doc.modifiedOn}</div>
                     </div>
                  </div>
               </div>
            </#list>
         </#if>
      </#if>
   </div>
</div>