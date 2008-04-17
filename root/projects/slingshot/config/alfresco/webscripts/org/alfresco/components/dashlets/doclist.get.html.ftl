<div class="component">
  <div class="component-title">Doclist</div>
  <div class="component-links">
    <a href="javascript:alert('Not implemented');">Recently created (v)</a>
  </div>
  <div class="component-list">
<#list docs as doc>
      <div>
        <!--<img src="/SOME-IMAGE-PROXY-PATH/${doc.icon}">-->
        <a href="${url.context}/page/collaboration/details?doc=${doc.name}">${doc.name}</a>
      </div>
</#list>
  </div>
</div>