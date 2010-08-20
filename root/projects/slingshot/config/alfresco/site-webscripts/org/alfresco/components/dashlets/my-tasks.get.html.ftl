<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.MyTasks("${args.htmlid}").setOptions(
   {
      hiddenTaskTypes: [<#list hiddenTaskTypes as type>"${type}"<#if type_has_next>, </#if></#list>]
   }).setMessages(
      ${messages}
   );
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>

<div class="dashlet my-tasks">
   <div class="title">${msg("header")}</div>
   <div class="toolbar">
      <a href="${page.url.context}/page/start-workflow" class="theme-color-1">${msg("link.startWorkflow")}</a>
   </div>
   <div id="${args.htmlid}-tasks" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
   </div>
</div>
