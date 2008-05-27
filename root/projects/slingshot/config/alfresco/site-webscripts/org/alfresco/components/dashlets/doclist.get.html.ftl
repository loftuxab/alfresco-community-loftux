<div class="dashlet">
   <div class="title">Doclist</div>
   <div class="menu">
      <a href="#">Recently created</a>
   </div>
   <div class="body scrollableList">
      <#list docs as doc>
         <div>
            <img src="${url.context}/${doc.icon}">
            <a href="#">${doc.name}</a>
         </div>
      </#list>
   </div>
</div>