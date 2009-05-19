<#include "documents.ftl" />
<script type="text/javascript" charset="utf-8">
<#if (workflowResult?string='true')>
   var workflowResult = '${msg("label.workflowAssigned")}';
<#else>
   var workflowResult = '${msg("label.workflowAssigned")}';
</#if>
App.addMessage(workflowResult);
window.addEventListener('DOMContentLoaded',function(){
   App.showMessage();
});
</script>
<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <@toolbar title="${msg('Documents')}"/>
      <div class="content">
         <@panelContent/>
      </div>
   </div>
</div>