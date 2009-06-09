<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentActions("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!"documentLibrary"}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="document-actions">

   <div class="heading">${msg("document-actions.heading")}</div>

   <div class="doclist">
      <div id="${args.htmlid}-actionSet-document" class="action-set ${type}">
<#list actionSet as action>
   <#assign domId><#if action.domId?? && action.domId != "">id="${action.domId?replace("{htmlid}", args.htmlid)}"</#if></#assign>
         <div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="${action.type}" title="${msg(action.label)}" ${domId}><span>${msg(action.label)}</span></a></div>
</#list>
      </div>
   </div>

</div>