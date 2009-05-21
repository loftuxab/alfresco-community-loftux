<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.DispositionActions("${controlId}").setOptions(
   {
      currentValue: "${field.value}"
   }).setMessages(
      ${messages}
   );
})();
//]]></script>

<div class="viewmode-field">
   <span class="viewmode-label">${field.label?html}:</span>
   <div id="${controlId}" class="disposition-actions">
      <div id="${controlId}-results"></div>
   </div>
</div> 