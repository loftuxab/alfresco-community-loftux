<div class="dashlet">
   <div class="title">Doclist</div>
   <div class="menu">
      <a href="#" onclick="alert('Not implemented');">Recently created (v)</a>
   </div>
   <div class="body scrollableList">
      <#list docs as doc>
         <div>
            <!--<img src="/SOME-IMAGE-PROXY-PATH/${doc.icon}">-->
            <a href="${url.context}/page/collaboration/details?doc=${doc.name?url}">${doc.name}</a>
         </div>
      </#list>
   </div>
</div>