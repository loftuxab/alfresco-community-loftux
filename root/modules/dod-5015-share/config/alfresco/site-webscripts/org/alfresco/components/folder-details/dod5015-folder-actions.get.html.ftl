<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsFolderActions("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!"documentLibrary"}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="folder-actions">

   <div class="heading">${msg("heading")}</div>

   <div class="doclist">
      <div id="${args.htmlid}-actionSet-folder" class="action-set ${type}">
<#list actionSet as action>
   <#assign domId><#if action.domId?? && action.domId != "">id="${action.domId?replace("{htmlid}", args.htmlid)}"</#if></#assign>
         <div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="${action.type}" title="${msg(action.label)}" ${domId}><span>${msg(action.label)}</span></a></div>
</#list>
      </div>
   </div>

</div>