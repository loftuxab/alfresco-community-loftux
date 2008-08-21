<div class="path-nav">
   <span class="heading">${msg("path.title")}:</span>
   
   <#if folders?exists && folders?size &gt; 0>
      <#list folders as folder>
         <img src="${page.url.context}/components/documentlibrary/images/folder-closed-16.png" />
         <span class="path-link">${folder}</span>
         <#if folder_has_next>&nbsp;>&nbsp;</#if>
      </#list>
   </#if>
</div>