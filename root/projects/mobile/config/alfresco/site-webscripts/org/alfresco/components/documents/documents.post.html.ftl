<#include "documents.ftl" />

<script type="text/javascript" charset="utf-8">
<#if (workflowResult)>
   alert('${msg("Workflow Assigned")}');
<#else>
   alert('${msg("Workflow Not Assigned")}');
</#if>
</script>
<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <@toolbar title="${msg('Documents')}"/>
      <div class="content">
         <@panelContent/>
      </div>
   </div>
</div>