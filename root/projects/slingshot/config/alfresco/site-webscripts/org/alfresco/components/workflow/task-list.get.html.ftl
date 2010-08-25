<#assign el=args.htmlid?js_string>
<!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />
<div id="${el}-body" class="task-list">
   <div id="${el}-taskListBar" class="yui-gc task-list-bar flat-button">
      <div class="yui-u first">
         <div class="task-select">
            &nbsp;
         </div>
         <div id="${el}-paginator" class="paginator"></div>
      </div>
      <div class="yui-u align-right">
         <div class="task-sorting">
         </div>
      </div>
   </div>
   <div id="${el}-tasks" class="tasks"></div>
</div>
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.TaskList("${el}").setOptions(
   {
   }).setMessages(
      ${messages}
   );
//]]></script>
